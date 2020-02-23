package com.leon.estimate.Fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import androidx.fragment.app.Fragment;
import androidx.room.Room;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.leon.estimate.Activities.FormActivity;
import com.leon.estimate.R;
import com.leon.estimate.Tables.DaoKarbariDictionary;
import com.leon.estimate.Tables.DaoNoeVagozariDictionary;
import com.leon.estimate.Tables.DaoQotrEnsheabDictionary;
import com.leon.estimate.Tables.DaoRequestDictionary;
import com.leon.estimate.Tables.DaoServiceDictionary;
import com.leon.estimate.Tables.DaoTaxfifDictionary;
import com.leon.estimate.Tables.ExaminerDuties;
import com.leon.estimate.Tables.KarbariDictionary;
import com.leon.estimate.Tables.MyDatabase;
import com.leon.estimate.Tables.NoeVagozariDictionary;
import com.leon.estimate.Tables.QotrEnsheabDictionary;
import com.leon.estimate.Tables.RequestDictionary;
import com.leon.estimate.Tables.ServiceDictionary;
import com.leon.estimate.Tables.TaxfifDictionary;
import com.leon.estimate.Utils.FontManager;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class Form1Fragment extends Fragment {

    private static final String ARG_PARAM1 = "requests";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;
    @BindView(R.id.editTextZoneTitle)
    EditText editTextZoneTitle;
    @BindView(R.id.editTextTrackNumber)
    EditText editTextTrackNumber;
    @BindView(R.id.editTextBillId)
    EditText editTextBillId;
    @BindView(R.id.editTextSifoon100)
    EditText editTextSifoon100;
    @BindView(R.id.editTextSifoon125)
    EditText editTextSifoon125;
    @BindView(R.id.editTextSifoon150)
    EditText editTextSifoon150;
    @BindView(R.id.editTextSifoon200)
    EditText editTextSifoon200;
    @BindView(R.id.editTextArese)
    EditText editTextArese;
    @BindView(R.id.editTextAianKol)
    EditText editTextAianKol;
    @BindView(R.id.editTextAianMaskooni)
    EditText editTextAianMaskooni;
    @BindView(R.id.editTextAianNonMaskooni)
    EditText editTextAianNonMaskooni;
    @BindView(R.id.editTextTedadMaskooni)
    EditText editTextTedadMaskooni;
    @BindView(R.id.editTextTedadTejari)
    EditText editTextTedadTejari;
    @BindView(R.id.editTextTedadSaier)
    EditText editTextTedadSaier;
    @BindView(R.id.editTextArzeshMelk)
    EditText editTextArzeshMelk;
    @BindView(R.id.editTextTedadTakhfif)
    EditText editTextTedadTakhfif;
    @BindView(R.id.editTextZarfiatQaradadi)
    EditText editTextZarfiatQaradadi;
    @BindView(R.id.editTextPariNumber)
    EditText editTextPariNumber;
    @BindView(R.id.editText19)
    EditText editText19;
    @BindView(R.id.editText20)
    EditText editText20;
    @BindView(R.id.linearLayout2)
    LinearLayout linearLayout2;
    @BindView(R.id.spinner1)
    Spinner spinner1;
    @BindView(R.id.spinner2)
    Spinner spinner2;
    @BindView(R.id.spinner3)
    Spinner spinner3;
    @BindView(R.id.spinner4)
    Spinner spinner4;
    @BindView(R.id.relativeLayout)
    RelativeLayout relativeLayout;
    @BindView(R.id.button_next)
    Button buttonNext;
    @BindView(R.id.checkbox1)
    CheckBox checkBox1;
    @BindView(R.id.checkbox2)
    CheckBox checkBox2;
    @BindView(R.id.checkbox3)
    CheckBox checkBox3;
    private View findViewById;
    private Context context;
    ArrayList<CheckBox> checkBoxes = new ArrayList<>();
    private Typeface typeface;
    private MyDatabase dataBase;
    List<KarbariDictionary> karbariDictionaries;
    List<QotrEnsheabDictionary> qotrEnsheabDictionaries;
    List<NoeVagozariDictionary> noeVagozariDictionaries;
    List<TaxfifDictionary> taxfifDictionaries;

    List<ServiceDictionary> serviceDictionaries;
    List<RequestDictionary> requestDictionaries;

    ExaminerDuties examinerDuties;

    public Form1Fragment() {

    }

    public static Form1Fragment newInstance(ExaminerDuties examinerDuties, String param2) {
        Form1Fragment fragment = new Form1Fragment();
        Bundle args = new Bundle();

        Gson gson = new Gson();
        String json = gson.toJson(examinerDuties);
        args.putString(ARG_PARAM1, json);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);

            Gson gson = new GsonBuilder().create();
            examinerDuties = Arrays.asList(gson.fromJson(mParam1, ExaminerDuties[].class)).get(0);

            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        context = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        findViewById = inflater.inflate(R.layout.form1_fragment, container, false);
        ButterKnife.bind(this, findViewById);
        initialize();
        return findViewById;
    }

    private void initialize() {
        FontManager fontManager = new FontManager(context);
        fontManager.setFont(relativeLayout);
        typeface = Typeface.createFromAsset(context.getAssets(), "font/BYekan_3.ttf");
        buttonNext.setOnClickListener(v -> ((FormActivity) getActivity()).nextPage(null));
        initializeSpinner();
    }

    private void initializeSpinner() {
        dataBase = Room.databaseBuilder(context, MyDatabase.class, "MyDatabase")
                .allowMainThreadQueries().build();
        initializeNoeVagozariSpinner();
        initializeKarbariSpinner();
        initializeQotrEnsheabSpinner();
        initializeTaxfifSpinner();
        initializeServicesCheckBox();
        initializeField();
    }

    private void initializeKarbariSpinner() {
        DaoKarbariDictionary daoKarbariDictionary = dataBase.daoKarbariDictionary();
        karbariDictionaries = daoKarbariDictionary.getKarbariDictionary();
        List<String> arrayListSpinner1 = new ArrayList<>();
        int select = 0, counter = 0;
        for (KarbariDictionary karbariDictionary : karbariDictionaries) {
            arrayListSpinner1.add(karbariDictionary.getTitle());
            if (karbariDictionary.isSelected()) {
                select = counter;
            }
            counter++;
        }
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(context,
                android.R.layout.simple_spinner_dropdown_item, arrayListSpinner1) {
            @NotNull
            @Override
            public View getView(int position, View convertView, @NotNull ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                final CheckedTextView textView = view.findViewById(android.R.id.text1);
                textView.setTypeface(typeface);
                textView.setChecked(true);
                textView.setTextColor(getResources().getColor(R.color.black));
                return view;
            }
        };
        spinner1.setAdapter(arrayAdapter);
        spinner1.setSelection(select);
    }

    private void initializeTaxfifSpinner() {
        DaoTaxfifDictionary daoTaxfifDictionary = dataBase.daoTaxfifDictionary();
        taxfifDictionaries = daoTaxfifDictionary.getTaxfifDictionaries();
        List<String> arrayListSpinner1 = new ArrayList<>();
        int select = 0, counter = 0;
        for (TaxfifDictionary taxfifDictionary : taxfifDictionaries) {
            arrayListSpinner1.add(taxfifDictionary.getTitle());
            if (taxfifDictionary.isSelected()) {
                select = counter;
            }
            counter++;
        }
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(context,
                android.R.layout.simple_spinner_dropdown_item, arrayListSpinner1) {
            @NotNull
            @Override
            public View getView(int position, View convertView, @NotNull ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                final CheckedTextView textView = view.findViewById(android.R.id.text1);
                textView.setTypeface(typeface);
                textView.setChecked(true);
                textView.setTextColor(getResources().getColor(R.color.black));
                return view;
            }
        };
        spinner4.setAdapter(arrayAdapter);
        spinner4.setSelection(select);
    }

    private void initializeNoeVagozariSpinner() {
        DaoNoeVagozariDictionary daoNoeVagozariDictionary = dataBase.daoNoeVagozariDictionary();
        noeVagozariDictionaries = daoNoeVagozariDictionary.getNoeVagozariDictionaries();
        List<String> arrayListSpinner1 = new ArrayList<>();
        int select = 0, counter = 0;
        for (NoeVagozariDictionary taxfifdictionary : noeVagozariDictionaries) {
            arrayListSpinner1.add(taxfifdictionary.getTitle());
            if (taxfifdictionary.isSelected()) {
                select = counter;
            }
            counter++;
        }
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(context,
                android.R.layout.simple_spinner_dropdown_item, arrayListSpinner1) {
            @NotNull
            @Override
            public View getView(int position, View convertView, @NotNull ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                final CheckedTextView textView = view.findViewById(android.R.id.text1);
                textView.setTypeface(typeface);
                textView.setChecked(true);
                textView.setTextColor(getResources().getColor(R.color.black));
                return view;
            }
        };
        spinner2.setAdapter(arrayAdapter);
        spinner2.setSelection(select);
    }

    private void initializeQotrEnsheabSpinner() {
        DaoQotrEnsheabDictionary daoQotrEnsheabDictionary = dataBase.daoQotrEnsheabDictionary();
        qotrEnsheabDictionaries = daoQotrEnsheabDictionary.getQotrEnsheabDictionaries();
        List<String> arrayListSpinner1 = new ArrayList<>();
        int select = 0, counter = 0;
        for (QotrEnsheabDictionary qotrEnsheabDictionary : qotrEnsheabDictionaries) {
            arrayListSpinner1.add(qotrEnsheabDictionary.getTitle());
            if (qotrEnsheabDictionary.isSelected()) {
                select = counter;
            }
            counter++;
        }
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(context,
                android.R.layout.simple_spinner_dropdown_item, arrayListSpinner1) {
            @NotNull
            @Override
            public View getView(int position, View convertView, @NotNull ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                final CheckedTextView textView = view.findViewById(android.R.id.text1);
                textView.setTypeface(typeface);
                textView.setChecked(true);
                textView.setTextColor(getResources().getColor(R.color.black));
                return view;
            }
        };
        spinner3.setAdapter(arrayAdapter);
        spinner3.setSelection(select);
    }

    private void initializeField() {
        editTextZoneTitle.setText(examinerDuties.getZoneTitle());
        editTextTrackNumber.setText(examinerDuties.getTrackNumber());
        editTextBillId.setText(examinerDuties.getBillId());
        editTextSifoon100.setText(examinerDuties.getSifoon100());
        editTextSifoon125.setText(examinerDuties.getSifoon125());
        editTextSifoon150.setText(examinerDuties.getSifoon150());
        editTextSifoon200.setText(examinerDuties.getSifoon200());
        editTextArese.setText(examinerDuties.getArse());
        editTextAianKol.setText(examinerDuties.getAianKol());
        editTextAianMaskooni.setText(examinerDuties.getAianMaskooni());
        editTextAianNonMaskooni.setText(examinerDuties.getAianNonMaskooni());
        editTextTedadMaskooni.setText(examinerDuties.getTedadMaskooni());
        editTextTedadTejari.setText(examinerDuties.getTedadTejari());
        editTextTedadSaier.setText(examinerDuties.getTedadSaier());
        editTextArzeshMelk.setText(examinerDuties.getArzeshMelk());
        editTextTedadTakhfif.setText(examinerDuties.getTedadTaxfif());
        editTextZarfiatQaradadi.setText(examinerDuties.getZarfiatQarardadi());
        editTextPariNumber.setText(examinerDuties.getParNumber());
        editText19.setText(examinerDuties.getExaminationDay());
        editText20.setText(examinerDuties.getPostalCode());

        checkBox1.setChecked(examinerDuties.isAdamTaxfifAb());
        checkBox2.setChecked(examinerDuties.isAdamTaxfifFazelab());
        checkBox3.setChecked(examinerDuties.isEnsheabQeirDaem());
    }

    @SuppressLint("NewApi")
    private void initializeServicesCheckBox() {
        DaoServiceDictionary daoServiceDictionary = dataBase.daoServiceDictionary();
        serviceDictionaries = daoServiceDictionary.getServiceDictionaries();

        DaoRequestDictionary daoRequestDictionary = dataBase.daoRequestDictionary();
        requestDictionaries = daoRequestDictionary.getRequestDictionaries();

        LinearLayout linearLayout = new LinearLayout(context);
        String tag;
        int padding = (int) context.getResources().getDimension(R.dimen.activity_mid_padding);
        int margin = (int) context.getResources().getDimension(R.dimen.activity_mid_margin);
        int textSize = (int) context.getResources().getDimension(R.dimen.textSizeSmall);

        for (int i = 0; i < serviceDictionaries.size(); i++) {
            if (i % 5 == 0) {
                tag = "linearLayout".concat(String.valueOf(i));
                linearLayout = new LinearLayout(context);
                linearLayout.setTag(tag);
                linearLayout.setGravity(1);
                linearLayout2.addView(linearLayout);
            }
            CheckBox checkBox = new CheckBox(context);
            checkBox.setTag("checkBox".concat(String.valueOf(i)));
            checkBox.setGravity(1);
            checkBox.setText(serviceDictionaries.get(i).getTitle());
            checkBox.setTextSize(textSize);
            checkBox.setPadding(padding, padding, padding, padding);
            checkBox.setTypeface(typeface);
            checkBox.setTextColor(context.getColor(R.color.blue4));
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(margin, margin, margin, margin);
            checkBox.setLayoutParams(params);
            if (serviceDictionaries.get(i).isSelected())
                checkBox.setChecked(true);
            checkBoxes.add(checkBox);
            linearLayout.addView(checkBox);
        }
    }

    boolean checkIsNoEmpty(EditText editText) {
        View focusView;
        if (editText.getText().toString().length() < 1) {
            focusView = editText;
            focusView.requestFocus();
            return true;
        }
        return false;
    }

    private void sendData() {
        //INTENT OBJ
        Intent i = new Intent(getActivity().getBaseContext(), FormActivity.class);
        i.putExtra("SENDER_KEY", "name");
        i.putExtra("NAME_KEY", "slm");
        getActivity().startActivity(i);
    }

    public interface test {
    }

    @Override
    public void onAttach(@NotNull Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
