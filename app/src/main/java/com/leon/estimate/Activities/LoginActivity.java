package com.leon.estimate.Activities;

import android.content.Context;
import android.os.Bundle;
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

import androidx.appcompat.app.AppCompatActivity;

import com.leon.estimate.R;
import com.leon.estimate.Utils.FontManager;

import butterknife.BindView;
import butterknife.ButterKnife;

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

    //    private SharedPreferenceManager sharedPreferenceManager;
//    private ConnectingManager connectingManager;
    private FontManager fontManager;
    private String username, password, deviceId;
    private View viewFocus;
    private Context context;
    private int failureCount = 0;

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
        editTextUsername.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                editTextUsername.setHint("");
                if (b) {
                    linearLayoutUsername.setBackground(getResources().getDrawable(R.drawable.border_3));
                    editTextPassword.setTextColor(getResources().getColor(R.color.black));
                } else {
                    linearLayoutUsername.setBackground(getResources().getDrawable(R.drawable.border_2));
                    editTextPassword.setTextColor(getResources().getColor(R.color.gray2));
                }
            }
        });
    }

    private void setEditTextPasswordOnFocusChangeListener() {
        editTextPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                editTextPassword.setHint("");
                if (b) {
                    linearLayoutPassword.setBackground(getResources().getDrawable(R.drawable.border_3));
                    editTextPassword.setTextColor(getResources().getColor(R.color.black));
                } else {
                    linearLayoutPassword.setBackground(getResources().getDrawable(R.drawable.border_2));
                    editTextPassword.setTextColor(getResources().getColor(R.color.gray2));
                }
            }
        });
    }

    private void setImageViewOnClickListener() {
        imageViewPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editTextPassword.getInputType() != InputType.TYPE_CLASS_NUMBER)
                    editTextPassword.setInputType(InputType.TYPE_CLASS_NUMBER);
                else
                    editTextPassword.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
                fontManager.setFont(relativeLayout);
            }
        });
    }

}
