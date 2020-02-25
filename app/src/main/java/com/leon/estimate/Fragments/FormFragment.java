package com.leon.estimate.Fragments;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
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
import com.leon.estimate.Enums.BundleEnum;
import com.leon.estimate.R;
import com.leon.estimate.Tables.CalculationUserInput;
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
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ir.smartlab.persiandatepicker.util.PersianCalendar;

public class FormFragment extends Fragment {
    private static final String ARG_PARAM2 = "param2";

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
    private ArrayList<CheckBox> checkBoxes = new ArrayList<>();
    private Typeface typeface;
    private MyDatabase dataBase;
    private List<KarbariDictionary> karbariDictionaries;
    private List<QotrEnsheabDictionary> qotrEnsheabDictionaries;
    private List<NoeVagozariDictionary> noeVagozariDictionaries;
    private List<TaxfifDictionary> taxfifDictionaries;
    private List<ServiceDictionary> serviceDictionaries;
    private List<RequestDictionary> requestDictionaries;
    private ExaminerDuties examinerDuties;
    PersianCalendar persianCalendar = new PersianCalendar();

    public FormFragment() {

    }

    public static FormFragment newInstance(ExaminerDuties examinerDuties, String param2) {
        FormFragment fragment = new FormFragment();
        Bundle args = new Bundle();
        Gson gson = new Gson();
        String json = gson.toJson(examinerDuties);
        args.putString(BundleEnum.REQUEST.getValue(), json);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            String json = getArguments().getString(BundleEnum.REQUEST.getValue());
            Gson gson = new GsonBuilder().create();
            examinerDuties = gson.fromJson(json, ExaminerDuties.class);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        context = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        findViewById = inflater.inflate(R.layout.form_fragment, container, false);
        ButterKnife.bind(this, findViewById);
        initialize();
        return findViewById;
    }

    private void initialize() {
        FontManager fontManager = new FontManager(context);
        fontManager.setFont(relativeLayout);
        typeface = Typeface.createFromAsset(context.getAssets(), "font/BYekan_3.ttf");
        initializeSpinner();
        buttonNext.setOnClickListener(v -> {
            if (prepareForm()) {
                CalculationUserInput calculationUserInput = prepareField();
                prepareServices(calculationUserInput);
                Log.e("services", calculationUserInput.selectedServicesString);
                ((FormActivity) getActivity()).nextPage(null, calculationUserInput);
            }
        });
    }

    private CalculationUserInput prepareField() {
        CalculationUserInput calculationUserInput = new CalculationUserInput();
        calculationUserInput.trackNumber = editTextTrackNumber.getText().toString();
        calculationUserInput.billId = editTextBillId.getText().toString();
        calculationUserInput.sifoon100 = Integer.valueOf(editTextSifoon100.getText().toString());
        calculationUserInput.sifoon125 = Integer.valueOf(editTextSifoon125.getText().toString());
        calculationUserInput.sifoon150 = Integer.valueOf(editTextSifoon150.getText().toString());
        calculationUserInput.sifoon200 = Integer.valueOf(editTextSifoon200.getText().toString());
        calculationUserInput.arse = Integer.valueOf(editTextArese.getText().toString());
        calculationUserInput.aianKol = Integer.valueOf(editTextAianKol.getText().toString());
        calculationUserInput.aianMaskooni = Integer.valueOf(editTextAianMaskooni.getText().toString());
        calculationUserInput.aianTejari = Integer.valueOf(editTextAianNonMaskooni.getText().toString());
        calculationUserInput.tedadMaskooni = Integer.valueOf(editTextTedadMaskooni.getText().toString());
        calculationUserInput.tedadTejari = Integer.valueOf(editTextTedadTejari.getText().toString());
        calculationUserInput.tedadSaier = Integer.valueOf(editTextTedadSaier.getText().toString());
        calculationUserInput.arzeshMelk = Integer.valueOf(editTextArzeshMelk.getText().toString());
        calculationUserInput.tedadTaxfif = Integer.valueOf(editTextTedadTakhfif.getText().toString());
        calculationUserInput.zarfiatQarardadi = Integer.valueOf(editTextZarfiatQaradadi.getText().toString());
        calculationUserInput.parNumber = editTextPariNumber.getText().toString();
        calculationUserInput.karbariId = karbariDictionaries.get(spinner1.getSelectedItemPosition()).getId();
        calculationUserInput.noeVagozariId = noeVagozariDictionaries.get(spinner2.getSelectedItemPosition()).getId();
        calculationUserInput.qotrEnsheabId = qotrEnsheabDictionaries.get(spinner3.getSelectedItemPosition()).getId();
        calculationUserInput.taxfifId = taxfifDictionaries.get(spinner4.getSelectedItemPosition()).getId();
        calculationUserInput.adamTaxfifAb = checkBox1.isChecked();
        calculationUserInput.adamTaxfifFazelab = checkBox2.isChecked();
        calculationUserInput.ensheabQeireDaem = checkBox3.isChecked();
        return calculationUserInput;
    }

    private CalculationUserInput prepareServices(CalculationUserInput calculationUserInput) {
        for (ServiceDictionary serviceDictionary : serviceDictionaries) {
            RequestDictionary requestDictionary = new RequestDictionary(
                    serviceDictionary.getId(), serviceDictionary.getTitle(),
                    serviceDictionary.isSelected(), serviceDictionary.isDisabled(),
                    serviceDictionary.isHasSms());
            requestDictionaries.add(requestDictionary);
        }
        for (int i = 0; i < checkBoxes.size(); i++) {
            requestDictionaries.get(i).setSelected(checkBoxes.get(i).isSelected());
        }
        calculationUserInput.selectedServices = requestDictionaries;
        Gson gson = new GsonBuilder().create();
        calculationUserInput.selectedServicesString = gson.toJson(requestDictionaries);
        return calculationUserInput;
    }

    private boolean prepareForm() {
        return checkIsNoEmpty(editTextZoneTitle)
                && checkIsNoEmpty(editTextTrackNumber)
                && checkIsNoEmpty(editTextBillId)
                && checkIsNoEmpty(editTextSifoon100)
                && checkIsNoEmpty(editTextSifoon125)
                && checkIsNoEmpty(editTextSifoon150)
                && checkIsNoEmpty(editTextSifoon200)
                && checkIsNoEmpty(editTextArese)
                && checkIsNoEmpty(editTextAianKol)
                && checkIsNoEmpty(editTextAianMaskooni)
                && checkIsNoEmpty(editTextAianNonMaskooni)
                && checkIsNoEmpty(editTextTedadMaskooni)
                && checkIsNoEmpty(editTextTedadTejari)
                && checkIsNoEmpty(editTextTedadSaier)
                && checkIsNoEmpty(editTextArzeshMelk)
                && checkIsNoEmpty(editTextTedadTakhfif)
                && checkIsNoEmpty(editTextZarfiatQaradadi)
                && checkIsNoEmpty(editTextPariNumber)
                && checkIsNoEmpty(editText19)
                && checkIsNoEmpty(editText20)
                ;
    }

    boolean checkIsNoEmpty(EditText editText) {
        View focusView;
        if (editText.getText().toString().length() < 1) {
            focusView = editText;
            focusView.requestFocus();
            return false;
        }
        return true;
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
        spinner1.setSelection(examinerDuties.getKarbariId());
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
        spinner4.setSelection(examinerDuties.getTaxfifId());
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
        spinner3.setSelection(examinerDuties.getQotrEnsheabId());
    }

    private void initializeField() {
        editTextZoneTitle.setText(examinerDuties.getZoneTitle());
        editTextTrackNumber.setText(examinerDuties.getTrackNumber());
        editTextBillId.setText(examinerDuties.getBillId());
        editTextSifoon100.setText(String.valueOf(examinerDuties.getSifoon100()));
        editTextSifoon125.setText(String.valueOf(examinerDuties.getSifoon125()));
        editTextSifoon150.setText(String.valueOf(examinerDuties.getSifoon150()));
        editTextSifoon200.setText(String.valueOf(examinerDuties.getSifoon200()));
        editTextArese.setText(String.valueOf(examinerDuties.getArse()));
        editTextAianKol.setText(String.valueOf(examinerDuties.getAianKol()));
        editTextAianMaskooni.setText(String.valueOf(examinerDuties.getAianMaskooni()));
        editTextAianNonMaskooni.setText(String.valueOf(examinerDuties.getAianNonMaskooni()));
        editTextTedadMaskooni.setText(String.valueOf(examinerDuties.getTedadMaskooni()));
        editTextTedadTejari.setText(String.valueOf(examinerDuties.getTedadTejari()));
        editTextTedadSaier.setText(String.valueOf(examinerDuties.getTedadSaier()));
        editTextArzeshMelk.setText(String.valueOf(examinerDuties.getArzeshMelk()));
        editTextTedadTakhfif.setText(String.valueOf(examinerDuties.getTedadTaxfif()));
        editTextZarfiatQaradadi.setText(String.valueOf(examinerDuties.getZarfiatQarardadi()));
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
            CheckBox checkBox = new CheckBox(context);
            checkBox.setGravity(1);
            checkBox.setText(serviceDictionaries.get(i).getTitle());
            checkBox.setTextSize(textSize);
            checkBox.setTypeface(typeface);
            checkBox.setTextColor(context.getColor(R.color.blue4));
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(margin, margin, margin, margin);
//            checkBox.setLayoutParams(params);
//            checkBox.setPadding(padding, padding, padding, padding);
            if (serviceDictionaries.get(i).isSelected())
                checkBox.setChecked(true);
            checkBoxes.add(checkBox);
            linearLayout.addView(checkBox);
            if (i % 3 == 2) {
                tag = "linearLayout".concat(String.valueOf(i));
                linearLayout.setTag(tag);
                linearLayout.setGravity(1);
                linearLayout2.addView(linearLayout);
                linearLayout = new LinearLayout(context);
            } else if (i == serviceDictionaries.size() - 1) {
                tag = "linearLayout".concat(String.valueOf(i));
                linearLayout.setTag(tag);
                linearLayout.setGravity(1);
                linearLayout2.addView(linearLayout);
            }
        }
    }


    private void sendData() {
        //INTENT OBJ
        Intent i = new Intent(getActivity().getBaseContext(), FormActivity.class);
        i.putExtra("SENDER_KEY", "name");
        i.putExtra("NAME_KEY", "slm");
        getActivity().startActivity(i);
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
