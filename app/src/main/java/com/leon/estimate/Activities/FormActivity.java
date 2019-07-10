package com.leon.estimate.Activities;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.leon.estimate.R;
import com.leon.estimate.Utils.FontManager;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FormActivity extends AppCompatActivity {
    @BindView(R.id.editText1)
    EditText editText1;
    @BindView(R.id.editText2)
    EditText editText2;
    @BindView(R.id.editText3)
    EditText editText3;
    @BindView(R.id.editText4)
    EditText editText4;
    @BindView(R.id.editText5)
    EditText editText5;
    @BindView(R.id.editText6)
    EditText editText6;
    @BindView(R.id.editText7)
    EditText editText7;
    @BindView(R.id.editText8)
    EditText editText8;
    @BindView(R.id.editText9)
    EditText editText9;
    @BindView(R.id.editText10)
    EditText editText10;
    @BindView(R.id.editText11)
    EditText editText11;
    @BindView(R.id.editText12)
    EditText editText12;
    @BindView(R.id.editText13)
    EditText editText13;
    @BindView(R.id.editText14)
    EditText editText14;
    @BindView(R.id.editText15)
    EditText editText15;
    @BindView(R.id.editText16)
    EditText editText16;
    @BindView(R.id.editText17)
    EditText editText17;
    @BindView(R.id.editText18)
    EditText editText18;
    @BindView(R.id.editText19)
    EditText editText19;
    @BindView(R.id.editText20)
    EditText editText20;
    @BindView(R.id.editText21)
    EditText editText21;
    @BindView(R.id.editText22)
    EditText editText22;
    @BindView(R.id.editText23)
    EditText editText23;
    @BindView(R.id.editText24)
    EditText editText24;
    @BindView(R.id.editText25)
    EditText editText25;
    @BindView(R.id.editText26)
    EditText editText26;
    @BindView(R.id.editText27)
    EditText editText27;
    @BindView(R.id.editText28)
    EditText editText28;
    @BindView(R.id.editText29)
    EditText editText29;
    @BindView(R.id.editText30)
    EditText editText30;

    @BindView(R.id.checkbox1)
    CheckBox checkBox1;
    @BindView(R.id.checkbox2)
    CheckBox checkBox2;
    @BindView(R.id.checkbox3)
    CheckBox checkBox3;
    @BindView(R.id.checkbox4)
    CheckBox checkBox4;
    @BindView(R.id.checkbox5)
    CheckBox checkBox5;
    @BindView(R.id.checkbox6)
    CheckBox checkBox6;
    @BindView(R.id.checkbox7)
    CheckBox checkBox7;
    @BindView(R.id.checkbox8)
    CheckBox checkBox8;

    @BindView(R.id.spinner1)
    Spinner spinner1;
    @BindView(R.id.spinner2)
    Spinner spinner2;

    @BindView(R.id.button)
    Button button;

    @BindView(R.id.constraintLayout)
    ConstraintLayout constraintLayout;

    View view;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        setContentView(R.layout.form_activity);
        ButterKnife.bind(this);
        initialize();
    }

    void initialize() {
        context = this;
        FontManager fontManager = new FontManager(getApplicationContext());
        fontManager.setFont(constraintLayout);
        setOnEditTextOnFocusChangeListener();
        initializeSpinner();
        setOnButtonClickListener();
    }

    void initializeSpinner() {
        ArrayList<String> arrayListSpinner = new ArrayList<>();
        arrayListSpinner.add("1");
        arrayListSpinner.add("2");
        ArrayAdapter arrayAdapter = new ArrayAdapter<>(context,
                android.R.layout.simple_spinner_dropdown_item, arrayListSpinner);
        spinner1.setAdapter(arrayAdapter);
        spinner2.setAdapter(arrayAdapter);
    }

    void setOnButtonClickListener() {
        button.setOnClickListener(view -> {

        });
    }

    void setOnEditTextOnFocusChangeListener() {
        setOnEdit1TextOnFocusChangeListener();
        setOnEdit2TextOnFocusChangeListener();
        setOnEdit3TextOnFocusChangeListener();
        setOnEdit4TextOnFocusChangeListener();
        setOnEdit5TextOnFocusChangeListener();
        setOnEdit6TextOnFocusChangeListener();
        setOnEdit7TextOnFocusChangeListener();
        setOnEdit8TextOnFocusChangeListener();
        setOnEdit9TextOnFocusChangeListener();
        setOnEdit10TextOnFocusChangeListener();
        setOnEdit11TextOnFocusChangeListener();
        setOnEdit12TextOnFocusChangeListener();
        setOnEdit13TextOnFocusChangeListener();
        setOnEdit14TextOnFocusChangeListener();
        setOnEdit15TextOnFocusChangeListener();
        setOnEdit16TextOnFocusChangeListener();
        setOnEdit17TextOnFocusChangeListener();
        setOnEdit18TextOnFocusChangeListener();
        setOnEdit19TextOnFocusChangeListener();
        setOnEdit20TextOnFocusChangeListener();
        setOnEdit21TextOnFocusChangeListener();
        setOnEdit22TextOnFocusChangeListener();
        setOnEdit23TextOnFocusChangeListener();
        setOnEdit24TextOnFocusChangeListener();
        setOnEdit25TextOnFocusChangeListener();
        setOnEdit26TextOnFocusChangeListener();
        setOnEdit27TextOnFocusChangeListener();
        setOnEdit28TextOnFocusChangeListener();
        setOnEdit29TextOnFocusChangeListener();
        setOnEdit30TextOnFocusChangeListener();
    }

    void setOnEdit1TextOnFocusChangeListener() {
        editText1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
//                view = editText2;
//                view.requestFocus();
            }
        });
    }

    void setOnEdit2TextOnFocusChangeListener() {
        editText2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                view = editText3;
                view.requestFocus();
            }
        });
    }

    void setOnEdit3TextOnFocusChangeListener() {
        editText3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                view = spinner1;
                view.requestFocus();
            }
        });
    }

    void setOnEdit4TextOnFocusChangeListener() {
        editText4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                view = editText5;
                view.requestFocus();
            }
        });
    }

    void setOnEdit5TextOnFocusChangeListener() {
        editText5.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                view = editText6;
                view.requestFocus();
            }
        });
    }

    void setOnEdit6TextOnFocusChangeListener() {
        editText6.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                view = editText7;
                view.requestFocus();
            }
        });
    }

    void setOnEdit7TextOnFocusChangeListener() {
        editText7.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                view = editText8;
                view.requestFocus();
            }
        });
    }

    void setOnEdit8TextOnFocusChangeListener() {
        editText8.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                view = editText9;
                view.requestFocus();
            }
        });
    }

    void setOnEdit9TextOnFocusChangeListener() {
        editText9.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                view = editText10;
                view.requestFocus();
            }
        });
    }

    void setOnEdit10TextOnFocusChangeListener() {
        editText10.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                view = editText11;
                view.requestFocus();
            }
        });
    }

    void setOnEdit11TextOnFocusChangeListener() {
        editText11.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                view = editText12;
                view.requestFocus();
            }
        });
    }

    void setOnEdit12TextOnFocusChangeListener() {
        editText12.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                view = editText13;
                view.requestFocus();
            }
        });
    }

    void setOnEdit13TextOnFocusChangeListener() {
        editText13.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                view = editText14;
                view.requestFocus();
            }
        });
    }

    void setOnEdit14TextOnFocusChangeListener() {
        editText14.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                view = editText15;
                view.requestFocus();
            }
        });
    }

    void setOnEdit15TextOnFocusChangeListener() {
        editText15.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                view = editText16;
                view.requestFocus();
            }
        });
    }

    void setOnEdit16TextOnFocusChangeListener() {
        editText16.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                view = editText17;
                view.requestFocus();
            }
        });
    }

    void setOnEdit17TextOnFocusChangeListener() {
        editText17.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                view = editText18;
                view.requestFocus();
            }
        });
    }

    void setOnEdit18TextOnFocusChangeListener() {
        editText18.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                view = editText19;
                view.requestFocus();
            }
        });
    }

    void setOnEdit19TextOnFocusChangeListener() {
        editText19.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                view = checkBox6;
                view.requestFocus();
            }
        });
    }

    void setOnEdit20TextOnFocusChangeListener() {
        editText20.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                view = editText21;
                view.requestFocus();
            }
        });
    }

    void setOnEdit21TextOnFocusChangeListener() {
        editText21.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                view = editText22;
                view.requestFocus();
            }
        });
    }

    void setOnEdit22TextOnFocusChangeListener() {
        editText22.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                view = editText23;
                view.requestFocus();
            }
        });
    }

    void setOnEdit23TextOnFocusChangeListener() {
        editText23.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                view = editText24;
                view.requestFocus();
            }
        });
    }

    void setOnEdit24TextOnFocusChangeListener() {
        editText24.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                view = editText25;
                view.requestFocus();
            }
        });
    }

    void setOnEdit25TextOnFocusChangeListener() {
        editText25.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                view = editText26;
                view.requestFocus();
            }
        });
    }

    void setOnEdit26TextOnFocusChangeListener() {
        editText26.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                view = editText27;
                view.requestFocus();
            }
        });
    }

    void setOnEdit27TextOnFocusChangeListener() {
        editText27.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                view = editText28;
                view.requestFocus();
            }
        });
    }

    void setOnEdit28TextOnFocusChangeListener() {
        editText28.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                view = editText29;
                view.requestFocus();
            }
        });
    }

    void setOnEdit29TextOnFocusChangeListener() {
        editText29.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                view = editText30;
                view.requestFocus();
            }
        });
    }

    void setOnEdit30TextOnFocusChangeListener() {
        editText30.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                view = button;
                view.requestFocus();
            }
        });
    }
}
