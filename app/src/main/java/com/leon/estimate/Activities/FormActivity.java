package com.leon.estimate.Activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.room.Room;

import com.leon.estimate.Enums.BundleEnum;
import com.leon.estimate.Enums.ProgressType;
import com.leon.estimate.R;
import com.leon.estimate.Tables.CalculationInfo;
import com.leon.estimate.Tables.CalculationUserInputSend;
import com.leon.estimate.Tables.DaoCalculation;
import com.leon.estimate.Tables.DaoCalculationUserInput;
import com.leon.estimate.Tables.KarbarDictionary;
import com.leon.estimate.Tables.MyDatabase;
import com.leon.estimate.Tables.NoeVagozariDictionary;
import com.leon.estimate.Tables.QotrEnsheabDictionary;
import com.leon.estimate.Tables.ServiceDictionary;
import com.leon.estimate.Tables.TaxfifDictionary;
import com.leon.estimate.Utils.FontManager;
import com.leon.estimate.Utils.HttpClientWrapper;
import com.leon.estimate.Utils.IAbfaService;
import com.leon.estimate.Utils.ICallback;
import com.leon.estimate.Utils.NetworkHelper;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Retrofit;

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


    @BindView(R.id.checkbox1)
    CheckBox checkBox1;
    @BindView(R.id.checkbox2)
    CheckBox checkBox2;
    @BindView(R.id.checkbox3)
    CheckBox checkBox3;

    @BindView(R.id.spinner1)
    Spinner spinner1;
    @BindView(R.id.spinner2)
    Spinner spinner2;
    @BindView(R.id.spinner3)
    Spinner spinner3;
    @BindView(R.id.spinner4)
    Spinner spinner4;

    @BindView(R.id.button)
    Button button;

    @BindView(R.id.constraintLayout1)
    ConstraintLayout constraintLayout;
    @BindView(R.id.linearLayout1)
    LinearLayout linearLayout1;
    @BindView(R.id.linearLayout2)
    LinearLayout linearLayout2;
    View view;
    Context context;
    String trackNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        setContentView(R.layout.form_activity);
        if (getIntent().getExtras() != null) {
            trackNumber = getIntent().getExtras().getString(BundleEnum.TRACK_NUMBER.getValue());
        }
        ButterKnife.bind(this);
        initialize();
    }

    void initialize() {
        context = this;
        FontManager fontManager = new FontManager(getApplicationContext());
        fontManager.setFont(constraintLayout);
        downloadDetails();
        setOnButtonClickListener();
//        setOnEditTextOnFocusChangeListener();
    }

    @SuppressLint("NewApi")
    void initializeCheckBox(List<ServiceDictionary> serviceDictionaries) {
        LinearLayout linearLayout = new LinearLayout(this);
        String tag;
        int padding = (int) context.getResources().getDimension(R.dimen.activity_mid_padding);
        int margin = (int) context.getResources().getDimension(R.dimen.activity_mid_margin);
        int textSize = (int) context.getResources().getDimension(R.dimen.textSizeSmall);
        Typeface typeface = Typeface.createFromAsset(getAssets(), "font/BYekan_3.ttf");

        for (int i = 0; i < serviceDictionaries.size(); i++) {
            if (i % 5 == 0) {
                tag = "linearLayout".concat(String.valueOf(i));
                linearLayout = new LinearLayout(this);
                linearLayout.setTag(tag);
                linearLayout.setGravity(1);
                linearLayout2.addView(linearLayout);
            }
            CheckBox checkBox = new CheckBox(this);
            checkBox.setTag("checkBox".concat(String.valueOf(i)));
            checkBox.setGravity(1);
            checkBox.setText(serviceDictionaries.get(i).getTitle());
            checkBox.setTextSize(textSize);
            checkBox.setPadding(padding, padding, padding, padding);
            checkBox.setTypeface(typeface);
            checkBox.setTextColor(getColor(R.color.blue4));
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(margin, margin, margin, margin);
            checkBox.setLayoutParams(params);
            if (serviceDictionaries.get(i).isSelected())
                checkBox.setChecked(true);
            linearLayout.addView(checkBox);
        }
    }

    void downloadDetails() {
        Retrofit retrofit = NetworkHelper.getInstance(true, "header");
        final IAbfaService getKardex = retrofit.create(IAbfaService.class);
        Call<CalculationInfo> call = getKardex.getMyWorksDetails(trackNumber);
        DownloadDetails downloadDetails = new DownloadDetails();
        HttpClientWrapper.callHttpAsync(call, downloadDetails, context, ProgressType.SHOW.getValue());
    }

    void initializeSpinner(List<KarbarDictionary> karbarDictionaries,
                           List<NoeVagozariDictionary> noeVagozariDictionaries,
                           List<QotrEnsheabDictionary> qotrEnsheabDictionaries,
                           List<TaxfifDictionary> taxfifDictionaries) {
        List<String> arrayListSpinner1 = new ArrayList<>();
        List<String> arrayListSpinner2 = new ArrayList<>();
        List<String> arrayListSpinner3 = new ArrayList<>();
        List<String> arrayListSpinner4 = new ArrayList<>();
        int select1 = 0, select2 = 0, select3 = 0, select4 = 0, counter = 0;
        for (KarbarDictionary karbarDictionary : karbarDictionaries) {
            arrayListSpinner1.add(karbarDictionary.getTitle());
            if (karbarDictionary.isSelected()) {
                select1 = counter;
            }
            counter++;
        }
        counter = 0;
        for (NoeVagozariDictionary noeVagozariDictionary : noeVagozariDictionaries) {
            arrayListSpinner2.add(noeVagozariDictionary.getTitle());
            if (noeVagozariDictionary.isSelected()) {
                select2 = counter;
            }
            counter++;
        }
        counter = 0;
        for (QotrEnsheabDictionary qotrEnsheabDictionary : qotrEnsheabDictionaries) {
            arrayListSpinner3.add(qotrEnsheabDictionary.getTitle());
            if (qotrEnsheabDictionary.isSelected()) {
                select3 = counter;
            }
            counter++;
        }
        counter = 0;
        for (TaxfifDictionary taxfifDictionary : taxfifDictionaries) {
            arrayListSpinner4.add(taxfifDictionary.getTitle());
            if (taxfifDictionary.isSelected()) {
                select4 = counter;
            }
            counter++;
        }
        ArrayAdapter<String> arrayAdapter1 = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item, arrayListSpinner1) {
            @NotNull
            @Override
            public View getView(int position, View convertView, @NotNull ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                final CheckedTextView textView = view
                        .findViewById(android.R.id.text1);
                Typeface typeface = Typeface.createFromAsset(getAssets(), "font/BYekan_3.ttf");
                textView.setTypeface(typeface);
                textView.setChecked(true);
                textView.setTextColor(getResources().getColor(R.color.black));
                return view;
            }
        };
        ArrayAdapter<String> arrayAdapter2 = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item, arrayListSpinner2) {
            @NotNull
            @Override
            public View getView(int position, View convertView, @NotNull ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                final CheckedTextView textView = view
                        .findViewById(android.R.id.text1);
                Typeface typeface = Typeface.createFromAsset(getAssets(), "font/BYekan_3.ttf");
                textView.setTypeface(typeface);
                textView.setChecked(true);
                textView.setTextColor(getResources().getColor(R.color.black));
                return view;
            }
        };
        ArrayAdapter<String> arrayAdapter3 = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item, arrayListSpinner3) {
            @NotNull
            @Override
            public View getView(int position, View convertView, @NotNull ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                final CheckedTextView textView = view
                        .findViewById(android.R.id.text1);
                Typeface typeface = Typeface.createFromAsset(getAssets(), "font/BYekan_3.ttf");
                textView.setTypeface(typeface);
                textView.setChecked(true);
                textView.setTextColor(getResources().getColor(R.color.black));
                return view;
            }
        };
        ArrayAdapter<String> arrayAdapter4 = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item, arrayListSpinner4) {
            @NotNull
            @Override
            public View getView(int position, View convertView, @NotNull ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                final CheckedTextView textView = view
                        .findViewById(android.R.id.text1);
                Typeface typeface = Typeface.createFromAsset(getAssets(), "font/BYekan_3.ttf");
                textView.setTypeface(typeface);
                textView.setChecked(true);
                textView.setTextColor(getResources().getColor(R.color.black));
                return view;
            }
        };
        spinner1.setAdapter(arrayAdapter1);
        spinner1.setSelection(select1);
        spinner2.setAdapter(arrayAdapter2);
        spinner2.setSelection(select2);
        spinner3.setAdapter(arrayAdapter3);
        spinner3.setSelection(select3);
        spinner4.setAdapter(arrayAdapter4);
        spinner4.setSelection(select4);
    }

    void setOnButtonClickListener() {
        button.setOnClickListener(view -> {
            MyDatabase dataBase = Room.databaseBuilder(context, MyDatabase.class, "MyDatabase")
                    .allowMainThreadQueries().build();
            DaoCalculation daoCalculation = dataBase.daoCalculateCalculation();
            daoCalculation.updateCalculation(true, trackNumber);


            DaoCalculationUserInput daoCalculationUserInput = dataBase.daoCalculationUserInput();
            daoCalculationUserInput.insertCalculationUserInput();

            CalculationUserInputSend calculationUserInputSend = new CalculationUserInputSend()
        });
    }

    void setCheckBox(CalculationInfo calculationInfo) {
        checkBox1.setChecked(calculationInfo.isAdamTaxfifAb());
        checkBox1.setChecked(calculationInfo.isAdamTaxfifFazelab());
        checkBox1.setChecked(calculationInfo.isEnsheabQeirDaem());
    }

    void setEditText(CalculationInfo calculationInfo) {
        editText1.setText(calculationInfo.getZoneTitle());
        editText2.setText(calculationInfo.getTrackNumber());
        editText3.setText(calculationInfo.getBillId());
        editText4.setText(calculationInfo.getSifoon100());
        editText5.setText(calculationInfo.getSifoon125());
        editText6.setText(calculationInfo.getSifoon150());
        editText7.setText(calculationInfo.getSifoon200());
        editText8.setText(calculationInfo.getArzeshMelk());
        editText9.setText(calculationInfo.getArse());
        editText10.setText(calculationInfo.getAianMaskooni());
        editText11.setText(calculationInfo.getAianNonMaskooni());
        editText12.setText(calculationInfo.getAianKol());
        editText13.setText(calculationInfo.getTedadMaskooni());
        editText14.setText(calculationInfo.getTedadTejari());
        editText15.setText(calculationInfo.getTedadSaier());
        editText16.setText(calculationInfo.getZarfiatQarardadi());
        editText17.setText(calculationInfo.getTedadTaxfif());
        editText18.setText(calculationInfo.getNationalId());
        editText19.setText(calculationInfo.getIdentityCode());
        editText20.setText(calculationInfo.getPostalCode());
        editText21.setText(calculationInfo.getFirstName());
        editText22.setText(calculationInfo.getSureName());
        editText23.setText(calculationInfo.getFatherName());
        editText24.setText(calculationInfo.getPhoneNumber());
        editText25.setText(calculationInfo.getMobile());
        editText26.setText(calculationInfo.getNotificationMobile());
        editText27.setText(calculationInfo.getDescription());
        editText28.setText(calculationInfo.getAddress());
    }

    class DownloadDetails implements ICallback<CalculationInfo> {
        @Override
        public void execute(CalculationInfo calculationsInfo) {
            initializeCheckBox(calculationsInfo.getServiceDictionary());
            initializeSpinner(calculationsInfo.getKarbarDictionary(),
                    calculationsInfo.getNoeVagozariDictionary(),
                    calculationsInfo.getQotrEnsheabDictionary(),
                    calculationsInfo.getTaxfifDictionary());
            setEditText(calculationsInfo);
            setCheckBox(calculationsInfo);
        }
    }
}
