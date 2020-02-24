package com.leon.estimate.Activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.location.LocationManagerCompat;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.leon.estimate.Enums.CompanyNames;
import com.leon.estimate.Enums.DialogType;
import com.leon.estimate.Enums.ErrorHandlerType;
import com.leon.estimate.Enums.ProgressType;
import com.leon.estimate.Enums.SharedReferenceKeys;
import com.leon.estimate.Enums.SharedReferenceNames;
import com.leon.estimate.R;
import com.leon.estimate.Utils.Crypto;
import com.leon.estimate.Utils.CustomDialog;
import com.leon.estimate.Utils.DifferentCompanyManager;
import com.leon.estimate.Utils.FontManager;
import com.leon.estimate.Utils.HttpClientWrapper;
import com.leon.estimate.Utils.IAbfaService;
import com.leon.estimate.Utils.ICallback;
import com.leon.estimate.Utils.LoginInfo;
import com.leon.estimate.Utils.NetworkHelper;
import com.leon.estimate.Utils.SharedPreferenceManager;
import com.leon.estimate.Utils.SimpleMessage;

import java.util.ArrayList;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.editTextUsername)
    EditText editTextUsername;
    @BindView(R.id.editTextPassword)
    EditText editTextPassword;
    @BindView(R.id.textViewFooter)
    TextView textViewFooter;
    @BindView(R.id.buttonLogin)
    Button buttonLogin;
    @BindView(R.id.activity_admin_forget_password)
    RelativeLayout relativeLayout;
    @BindView(R.id.linearLayoutUsername)
    LinearLayout linearLayoutUsername;
    @BindView(R.id.linearLayoutPassword)
    LinearLayout linearLayoutPassword;
    @BindView(R.id.imageViewPerson)
    ImageView imageViewPerson;
    @BindView(R.id.imageViewLogo)
    ImageView imageViewLogo;
    @BindView(R.id.imageViewUsername)
    ImageView imageViewUsername;
    @BindView(R.id.imageViewPassword)
    ImageView imageViewPassword;
    int REQUEST_LOCATION_CODE = 1236;
    private SharedPreferenceManager sharedPreferenceManager;
    private FontManager fontManager;
    private String username, password, deviceId;
    private View viewFocus;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        setContentView(R.layout.login_activity);
        ButterKnife.bind(this);
        initialize();
    }

    void initialize() {
        context = this;
        sharedPreferenceManager = new SharedPreferenceManager(getApplicationContext(), SharedReferenceNames.ACCOUNT.getValue());
        fontManager = new FontManager(getApplicationContext());
        fontManager.setFont(relativeLayout);
        imageViewPassword.setImageResource(R.drawable.img_password);
        imageViewLogo.setImageResource(R.drawable.img_bg_logo);
        imageViewPerson.setImageResource(R.drawable.img_profile);
        imageViewUsername.setImageResource(R.drawable.img_user);
        setEditTextUsernameChangedListener();
        setEditTextPasswordChangedListener();
        setEditTextUsernameOnFocusChangeListener();
        setEditTextPasswordOnFocusChangeListener();
        setImageViewOnClickListener();
        setButtonOnClickListener();
        setButtonOnLongClickListener();
    }

    @SuppressLint("HardwareIds")
    void attemptLogin() {
        deviceId = Build.SERIAL;
        Retrofit retrofit = NetworkHelper.getInstance(false, "");
        final IAbfaService loginInfo = retrofit.create(IAbfaService.class);
        Call<com.leon.estimate.Utils.LoginFeedBack> call = loginInfo.login(new LoginInfo(deviceId, username, password));
        LoginFeedBack loginFeedBack = new LoginFeedBack();
        HttpClientWrapper.callHttpAsync(call, loginFeedBack, this, ProgressType.SHOW.getValue(), ErrorHandlerType.login);
    }

    @SuppressLint("HardwareIds")
    void attemptSerial() {
        deviceId = Build.SERIAL;
        Retrofit retrofit;
        if (DifferentCompanyManager.getActiveCompanyName() == CompanyNames.DEBUG) {
            Gson gson = new GsonBuilder()
                    .setLenient()
                    .create();
            String baseUrl = "http://81.90.148.25/";
            retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .client(NetworkHelper.getHttpClient(""))
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        } else {
            retrofit = NetworkHelper.getInstance(true, "");
        }
        final IAbfaService serial = retrofit.create(IAbfaService.class);
        Call<SimpleMessage> call = serial.signSerial(new LoginInfo(deviceId, username, password));
        SignSerialFeedBack signSerialFeedBack = new SignSerialFeedBack();
        HttpClientWrapper.callHttpAsync(call, signSerialFeedBack, this, ProgressType.SHOW.getValue(), ErrorHandlerType.login);
    }


    @Override
    protected void onStart() {
        super.onStart();
        initialize();
    }

    @Override
    protected void onStop() {
        super.onStop();
        imageViewPerson.setImageDrawable(null);
        imageViewPassword.setImageDrawable(null);
        imageViewLogo.setImageDrawable(null);
        imageViewUsername.setImageDrawable(null);
        System.gc();
        Runtime.getRuntime().gc();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        context = null;
        System.gc();
        Runtime.getRuntime().gc();
    }

    class SignSerialFeedBack
            implements ICallback<SimpleMessage> {
        @Override
        public void execute(SimpleMessage simpleMessage) {
            Toast.makeText(context, simpleMessage.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void askPermission() {
        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                Intent intent = new Intent(getApplicationContext(), Main2Activity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                Toast.makeText(getApplicationContext(), "مجوز رد شد \n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
                forceClose();
            }
        };

        new TedPermission(this)
                .setPermissionListener(permissionlistener)
                .setRationaleMessage("جهت استفاده بهتر از برنامه مجوز های پیشنهادی را قبول فرمایید")
                .setDeniedMessage("در صورت رد این مجوز قادر با استفاده از این دستگاه نخواهید بود" + "\n" +
                        "لطفا با فشار دادن دکمه" + " " + "اعطای دسترسی" + " " + "و سپس در بخش " + " دسترسی ها" + " " + " با این مجوز هاموافقت نمایید")
                .setGotoSettingButtonText("اعطای دسترسی")
                .setPermissions(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                ).check();
    }

    private void GpsEnabled() {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean enabled = LocationManagerCompat.isLocationEnabled(Objects.requireNonNull(locationManager));
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        if (!enabled) {
            alertDialog.setCancelable(false);
            alertDialog.setTitle(R.string.setting_gps);
            alertDialog.setMessage(R.string.gps_question);
            alertDialog.setPositiveButton(R.string.setting_, (dialog, which) -> {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(intent, REQUEST_LOCATION_CODE);
            });
            alertDialog.setNegativeButton(R.string.permission_not_completed, (dialog, which) -> finishAffinity());
            alertDialog.show();
        } else askPermission();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        GpsEnabled();
    }

    private void forceClose() {
        new CustomDialog(DialogType.Red, getApplicationContext(),
                getApplicationContext().getString(R.string.permission_not_completed),
                getApplicationContext().getString(R.string.dear_user),
                getApplicationContext().getString(R.string.call_operator),
                getApplicationContext().getString(R.string.force_close));
        finishAffinity();
    }

    private void setEditTextUsernameChangedListener() {
        editTextUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                editTextUsername.setHint("");
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void setEditTextPasswordChangedListener() {
        editTextPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                editTextPassword.setHint("");
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void setEditTextUsernameOnFocusChangeListener() {
        editTextUsername.setOnFocusChangeListener((view, b) -> {
            editTextUsername.setHint("");
            if (b) {
                linearLayoutUsername.setBackground(getResources().getDrawable(R.drawable.border_3));
                editTextPassword.setTextColor(getResources().getColor(R.color.black));
            } else {
                linearLayoutUsername.setBackground(getResources().getDrawable(R.drawable.border_2));
                editTextPassword.setTextColor(getResources().getColor(R.color.gray2));
            }
        });
    }

    private void setEditTextPasswordOnFocusChangeListener() {
        editTextPassword.setOnFocusChangeListener((view, b) -> {
            editTextPassword.setHint("");
            if (b) {
                linearLayoutPassword.setBackground(getResources().getDrawable(R.drawable.border_3));
                editTextPassword.setTextColor(getResources().getColor(R.color.black));
            } else {
                linearLayoutPassword.setBackground(getResources().getDrawable(R.drawable.border_2));
                editTextPassword.setTextColor(getResources().getColor(R.color.gray2));
            }
        });
    }

    private void setImageViewOnClickListener() {
        imageViewPassword.setOnClickListener(view -> {
            if (editTextPassword.getInputType() != InputType.TYPE_CLASS_NUMBER)
                editTextPassword.setInputType(InputType.TYPE_CLASS_NUMBER);
            else
                editTextPassword.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
            fontManager.setFont(relativeLayout);
        });
    }

    private void setButtonOnClickListener() {
        buttonLogin.setOnClickListener(view -> {
            boolean cancel = false;
            username = editTextUsername.getText().toString();
            password = editTextPassword.getText().toString();
            if (username.length() < 1) {
                viewFocus = editTextUsername;
                viewFocus.requestFocus();
                editTextUsername.setError(getString(R.string.error_empty));
                cancel = true;
            }
            if (!cancel && password.length() < 1) {
                viewFocus = editTextPassword;
                viewFocus.requestFocus();
                editTextPassword.setError(getString(R.string.error_empty));
                cancel = true;
            }
            if (!cancel) {
                attemptLogin();
            }
        });
    }

    private void setButtonOnLongClickListener() {
        buttonLogin.setOnLongClickListener(view -> {
            boolean cancel = false;
            username = editTextUsername.getText().toString();
            password = editTextPassword.getText().toString();
            if (username.length() < 1) {
                viewFocus = editTextUsername;
                viewFocus.requestFocus();
                editTextUsername.setError(getString(R.string.error_empty));
                cancel = true;
            }
            if (!cancel && password.length() < 1) {
                viewFocus = editTextPassword;
                viewFocus.requestFocus();
                editTextPassword.setError(getString(R.string.error_empty));
                cancel = true;
            }
            if (!cancel) {
//                attemptSerial();
//                GpsEnabled();
            }
            return false;
        });
    }

    class LoginFeedBack
            implements ICallback<com.leon.estimate.Utils.LoginFeedBack> {
        @Override
        public void execute(com.leon.estimate.Utils.LoginFeedBack loginFeedBack) {
            if (loginFeedBack.getAccess_token() == null ||
                    loginFeedBack.getRefresh_token() == null ||
                    loginFeedBack.getAccess_token().length() < 1 ||
                    loginFeedBack.getRefresh_token().length() < 1) {
                Toast.makeText(getApplicationContext(), getString(R.string.error_is_not_match), Toast.LENGTH_SHORT).show();
            } else {
                sharedPreferenceManager.putData(SharedReferenceKeys.TOKEN.getValue(), loginFeedBack.getAccess_token());
                sharedPreferenceManager.putData(SharedReferenceKeys.USERNAME.getValue(), username);
                sharedPreferenceManager.putData(SharedReferenceKeys.PASSWORD.getValue(), Crypto.encrypt(password));
                sharedPreferenceManager.putData(SharedReferenceKeys.REFRESH_TOKEN.getValue(), loginFeedBack.getRefresh_token());
                GpsEnabled();
            }
        }
    }
}
