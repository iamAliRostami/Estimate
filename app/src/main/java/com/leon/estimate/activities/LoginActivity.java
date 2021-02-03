package com.leon.estimate.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Debug;
import android.provider.Settings;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.location.LocationManagerCompat;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.leon.estimate.BuildConfig;
import com.leon.estimate.Enums.DialogType;
import com.leon.estimate.Enums.ProgressType;
import com.leon.estimate.Enums.SharedReferenceKeys;
import com.leon.estimate.Enums.SharedReferenceNames;
import com.leon.estimate.Infrastructure.IAbfaService;
import com.leon.estimate.Infrastructure.ICallback;
import com.leon.estimate.Infrastructure.ICallbackError;
import com.leon.estimate.Infrastructure.ICallbackIncomplete;
import com.leon.estimate.MyApplication;
import com.leon.estimate.R;
import com.leon.estimate.Utils.CustomDialog;
import com.leon.estimate.Utils.CustomErrorHandlingNew;
import com.leon.estimate.Utils.HttpClientWrapper;
import com.leon.estimate.Utils.NetworkHelper;
import com.leon.estimate.Utils.SharedPreferenceManager;
import com.leon.estimate.databinding.LoginActivityBinding;

import java.util.ArrayList;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

import static com.leon.estimate.Utils.Constants.REQUEST_LOCATION_CODE;

public class LoginActivity extends AppCompatActivity {
    private LoginActivityBinding binding;
    private SharedPreferenceManager sharedPreferenceManager;
    private String username, password;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = LoginActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initialize();
    }

    @SuppressLint("HardwareIds")
    void initialize() {
        context = this;
        binding.textViewVersion.setText(getString(R.string.version).concat(" ")
                .concat(BuildConfig.VERSION_NAME).concat(" *** ").concat(getAndroidVersion()));
        loadPreference();
        binding.imageViewPassword.setImageResource(R.drawable.img_password);
        binding.imageViewLogo.setImageResource(R.drawable.img_bg_logo);
        binding.imageViewPerson.setImageResource(R.drawable.img_profile);
        binding.imageViewUsername.setImageResource(R.drawable.img_user);
        setEditTextUsernameChangedListener();
        setEditTextPasswordChangedListener();
        setEditTextUsernameOnFocusChangeListener();
        setEditTextPasswordOnFocusChangeListener();
        setImageViewOnClickListener();
        setButtonLoginOnClickListener();
        setButtonLocalOnClickListener();
    }

    public String getAndroidVersion() {
        String release = Build.VERSION.RELEASE;
        int sdkVersion = Build.VERSION.SDK_INT;
        return "Android SDK: " + sdkVersion + " (" + release + ")";
    }

    void attemptLogin() {
        Retrofit retrofit = NetworkHelper.getInstance("");
        final IAbfaService loginInfo = retrofit.create(IAbfaService.class);
        Call<com.leon.estimate.Tables.LoginFeedBack> call = loginInfo.login1(username, password);
        HttpClientWrapper.callHttpAsync(call, ProgressType.SHOW_CANCELABLE.getValue(), this,
                new LoginFeedBack(), new GetErrorIncomplete(), new GetError());
    }


    public void askPermission() {
        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                Toast.makeText(getApplicationContext(), getString(R.string.access_denialed) +
                        deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
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
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.CAMERA,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
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


    private void setEditTextUsernameChangedListener() {
        binding.editTextUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                binding.editTextUsername.setHint("");
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void setEditTextPasswordChangedListener() {
        binding.editTextPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                binding.editTextPassword.setHint("");
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void setEditTextUsernameOnFocusChangeListener() {
        binding.editTextUsername.setOnFocusChangeListener((view, b) -> {
            binding.editTextUsername.setHint("");
            if (b) {
                binding.linearLayoutUsername.setBackground(getResources().getDrawable(R.drawable.border_3));
                binding.editTextPassword.setTextColor(getResources().getColor(R.color.black));
            } else {
                binding.linearLayoutUsername.setBackground(getResources().getDrawable(R.drawable.border_2));
                binding.editTextPassword.setTextColor(getResources().getColor(R.color.gray2));
            }
        });
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void setEditTextPasswordOnFocusChangeListener() {
        binding.editTextPassword.setOnFocusChangeListener((view, b) -> {
            binding.editTextPassword.setHint("");
            if (b) {
                binding.linearLayoutPassword.setBackground(getResources().getDrawable(R.drawable.border_3));
                binding.editTextPassword.setTextColor(getResources().getColor(R.color.black));
            } else {
                binding.linearLayoutPassword.setBackground(getResources().getDrawable(R.drawable.border_2));
                binding.editTextPassword.setTextColor(getResources().getColor(R.color.gray2));
            }
        });
    }

    private void setImageViewOnClickListener() {
        binding.imageViewPassword.setOnClickListener(view -> {
            if (binding.editTextPassword.getInputType() != InputType.TYPE_CLASS_TEXT) {
                binding.editTextPassword.setInputType(InputType.TYPE_CLASS_TEXT);
            } else
                binding.editTextPassword.setInputType(InputType.TYPE_CLASS_TEXT |
                        InputType.TYPE_TEXT_VARIATION_PASSWORD);
        });
    }

    private void setButtonLoginOnClickListener() {
        binding.buttonLogin.setOnClickListener(view -> {
            View viewFocus;
            boolean cancel = false;
            username = binding.editTextUsername.getText().toString();
            password = binding.editTextPassword.getText().toString();
            if (username.length() < 1) {
                viewFocus = binding.editTextUsername;
                viewFocus.requestFocus();
                binding.editTextUsername.setError(getString(R.string.error_empty));
                cancel = true;
            }
            if (!cancel && password.length() < 1) {
                viewFocus = binding.editTextPassword;
                viewFocus.requestFocus();
                binding.editTextPassword.setError(getString(R.string.error_empty));
                cancel = true;
            }
            if (!cancel) {
                MyApplication.isLocal = false;
                attemptLogin();
            }
        });
    }

    private void setButtonLocalOnClickListener() {
        binding.buttonLoginLocal.setOnClickListener(v -> {
            Log.e("buttonLogin", "Long");
            View viewFocus;
            boolean cancel = false;
            username = binding.editTextUsername.getText().toString();
            password = binding.editTextPassword.getText().toString();
            if (username.length() < 1) {
                viewFocus = binding.editTextUsername;
                viewFocus.requestFocus();
                binding.editTextUsername.setError(getString(R.string.error_empty));
                cancel = true;
            }
            if (!cancel && password.length() < 1) {
                viewFocus = binding.editTextPassword;
                viewFocus.requestFocus();
                binding.editTextPassword.setError(getString(R.string.error_empty));
                cancel = true;
            }
            if (!cancel) {
                MyApplication.isLocal = true;
                attemptLogin();
            }
        });
    }

    void loadPreference() {
        sharedPreferenceManager = new SharedPreferenceManager(getApplicationContext(),
                SharedReferenceNames.ACCOUNT.getValue());
        if (sharedPreferenceManager.checkIsNotEmpty(SharedReferenceKeys.USERNAME.getValue()) &&
                sharedPreferenceManager.checkIsNotEmpty(SharedReferenceKeys.PASSWORD.getValue())) {
            binding.editTextUsername.setText(sharedPreferenceManager.getStringData(
                    SharedReferenceKeys.USERNAME.getValue()));
            binding.editTextPassword.setText(sharedPreferenceManager.getStringData(
                    SharedReferenceKeys.PASSWORD.getValue()));
        }
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

    class LoginFeedBack
            implements ICallback<com.leon.estimate.Tables.LoginFeedBack> {
        @Override
        public void execute(com.leon.estimate.Tables.LoginFeedBack loginFeedBack) {
            if (loginFeedBack.getAccess_token() == null ||
                    loginFeedBack.getRefresh_token() == null ||
                    loginFeedBack.getAccess_token().length() < 1 ||
                    loginFeedBack.getRefresh_token().length() < 1) {
                Toast.makeText(getApplicationContext(), getString(R.string.error_is_not_match), Toast.LENGTH_SHORT).show();
            } else {
                if (MyApplication.isLocal) {
                    Toast.makeText(getApplicationContext(), "شبکه داخلی فعال شد.", Toast.LENGTH_LONG).show();
                } else
                    Toast.makeText(getApplicationContext(), "اینترنت فعال شد.", Toast.LENGTH_LONG).show();
                sharedPreferenceManager.putData(SharedReferenceKeys.USERNAME_TEMP.getValue(), username);
//                sharedPreferenceManager.putData(SharedReferenceKeys.PASSWORD.getValue(), Crypto.encrypt(password));
                sharedPreferenceManager.putData(SharedReferenceKeys.PASSWORD_TEMP.getValue(), password);
                sharedPreferenceManager.putData(SharedReferenceKeys.TOKEN.getValue(), loginFeedBack.getAccess_token());
                sharedPreferenceManager.putData(SharedReferenceKeys.REFRESH_TOKEN.getValue(), loginFeedBack.getRefresh_token());
                if (binding.checkBoxSave.isChecked()) {
                    sharedPreferenceManager.putData(SharedReferenceKeys.USERNAME.getValue(), username);
                    sharedPreferenceManager.putData(SharedReferenceKeys.PASSWORD.getValue(), password);
                }
                GpsEnabled();
            }
        }
    }

    class GetErrorIncomplete implements ICallbackIncomplete<com.leon.estimate.Tables.LoginFeedBack> {
        @Override
        public void executeIncomplete(Response<com.leon.estimate.Tables.LoginFeedBack> response) {
            CustomErrorHandlingNew customErrorHandlingNew = new CustomErrorHandlingNew(context);
            String error = customErrorHandlingNew.getErrorMessageDefault(response);
            if (response.code() == 401) {
                error = LoginActivity.this.getString(R.string.error_is_not_match);
            }
            new CustomDialog(DialogType.Yellow, LoginActivity.this, error,
                    LoginActivity.this.getString(R.string.dear_user),
                    LoginActivity.this.getString(R.string.login),
                    LoginActivity.this.getString(R.string.accepted));
        }
    }

    class GetError implements ICallbackError {
        @Override
        public void executeError(Throwable t) {
            CustomErrorHandlingNew customErrorHandlingNew = new CustomErrorHandlingNew(context);
            String error = customErrorHandlingNew.getErrorMessageTotal(t);
            new CustomDialog(DialogType.YellowRedirect, LoginActivity.this, error,
                    LoginActivity.this.getString(R.string.dear_user),
                    LoginActivity.this.getString(R.string.login),
                    LoginActivity.this.getString(R.string.accepted));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        context = null;
        binding.imageViewPerson.setImageDrawable(null);
        binding.imageViewPassword.setImageDrawable(null);
        binding.imageViewLogo.setImageDrawable(null);
        binding.imageViewUsername.setImageDrawable(null);
        System.gc();
        Runtime.getRuntime().gc();
        Runtime.getRuntime().totalMemory();
        Runtime.getRuntime().freeMemory();
        Runtime.getRuntime().maxMemory();
        Debug.getNativeHeapAllocatedSize();
    }

    @Override
    protected void onStop() {
        super.onStop();
        System.gc();
        Runtime.getRuntime().gc();
        Runtime.getRuntime().totalMemory();
        Runtime.getRuntime().freeMemory();
        Runtime.getRuntime().maxMemory();
        Debug.getNativeHeapAllocatedSize();
    }

    @Override
    protected void onStart() {
        super.onStart();
        initialize();
    }
}
