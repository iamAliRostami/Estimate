package com.leon.estimate.fragments;


import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.EditText;

import androidx.fragment.app.Fragment;
import androidx.room.Room;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.leon.estimate.Enums.BundleEnum;
import com.leon.estimate.R;
import com.leon.estimate.Tables.CalculationUserInput;
import com.leon.estimate.Tables.DaoKarbariDictionary;
import com.leon.estimate.Tables.DaoNoeVagozariDictionary;
import com.leon.estimate.Tables.DaoQotrEnsheabDictionary;
import com.leon.estimate.Tables.DaoTaxfifDictionary;
import com.leon.estimate.Tables.ExaminerDuties;
import com.leon.estimate.Tables.KarbariDictionary;
import com.leon.estimate.Tables.MyDatabase;
import com.leon.estimate.Tables.NoeVagozariDictionary;
import com.leon.estimate.Tables.QotrEnsheabDictionary;
import com.leon.estimate.Tables.RequestDictionary;
import com.leon.estimate.Tables.TaxfifDictionary;
import com.leon.estimate.activities.FormActivity;
import com.leon.estimate.adapters.CheckBoxAdapter;
import com.leon.estimate.databinding.FormFragmentBinding;
import com.sardari.daterangepicker.customviews.DateRangeCalendarView;
import com.sardari.daterangepicker.dialog.DatePickerDialog;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class FormOldFragment extends Fragment {
    private static final String ARG_PARAM2 = "param2";
    FormFragmentBinding binding;
    private Context context;
    private ArrayList<CheckBox> checkBoxes = new ArrayList<>();
    //    private Typeface typeface;
    private MyDatabase dataBase;
    private List<KarbariDictionary> karbariDictionaries;
    private List<QotrEnsheabDictionary> qotrEnsheabDictionaries;
    private List<NoeVagozariDictionary> noeVagozariDictionaries;
    private List<TaxfifDictionary> taxfifDictionaries;
    private ExaminerDuties examinerDuties;
    private List<RequestDictionary> requestDictionaries;

    public FormOldFragment() {

    }

    public static FormOldFragment newInstance(ExaminerDuties examinerDuties, String param2) {
        FormOldFragment fragment = new FormOldFragment();
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
        }
        context = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FormFragmentBinding.inflate(inflater, container, false);
        initialize();
        return binding.getRoot();
    }

    private void initialize() {
        initializeSpinner();
        setOnButtonNextClickListener();
        setOnEditText19ClickListener();
    }

    void setOnButtonNextClickListener() {
        binding.buttonNext.setOnClickListener(v -> {
            if (prepareForm()) {
                CalculationUserInput calculationUserInput = prepareField();
                prepareServices(calculationUserInput);
//                Log.e("services", calculationUserInput.selectedServicesString);
                ((FormActivity) Objects.requireNonNull(getActivity())).nextPage(null, calculationUserInput);
            }
        });
    }

    void setOnEditText19ClickListener() {
        binding.editText19.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(context);
            datePickerDialog.setSelectionMode(DateRangeCalendarView.SelectionMode.Single);
            datePickerDialog.setDisableDaysAgo(false);
            datePickerDialog.setTextSizeTitle(10.0f);
            datePickerDialog.setTextSizeWeek(12.0f);
            datePickerDialog.setTextSizeDate(14.0f);
            datePickerDialog.setCanceledOnTouchOutside(true);
            datePickerDialog.setOnSingleDateSelectedListener(date -> binding.editText19.setText(date.getPersianShortDate()));
            datePickerDialog.showDialog();
        });
    }

    private CalculationUserInput prepareField() {
        CalculationUserInput calculationUserInput = new CalculationUserInput();
        calculationUserInput.trackNumber = binding.editTextTrackNumber.getText().toString();
        calculationUserInput.billId = binding.editTextBillId.getText().toString();
        calculationUserInput.sifoon100 = Integer.parseInt(binding.editTextSifoon100.getText().toString());
        calculationUserInput.sifoon125 = Integer.parseInt(binding.editTextSifoon125.getText().toString());
        calculationUserInput.sifoon150 = Integer.parseInt(binding.editTextSifoon150.getText().toString());
        calculationUserInput.sifoon200 = Integer.parseInt(binding.editTextSifoon200.getText().toString());
        calculationUserInput.arse = Integer.parseInt(binding.editTextArese.getText().toString());
        calculationUserInput.aianKol = Integer.parseInt(binding.editTextAianKol.getText().toString());
        calculationUserInput.aianMaskooni = Integer.parseInt(binding.editTextAianMaskooni.getText().toString());
        calculationUserInput.aianTejari = Integer.parseInt(binding.editTextAianNonMaskooni.getText().toString());
        calculationUserInput.tedadMaskooni = Integer.parseInt(binding.editTextTedadMaskooni.getText().toString());
        calculationUserInput.tedadTejari = Integer.parseInt(binding.editTextTedadTejari.getText().toString());
        calculationUserInput.tedadSaier = Integer.parseInt(binding.editTextTedadSaier.getText().toString());
        calculationUserInput.arzeshMelk = Integer.parseInt(binding.editTextArzeshMelk.getText().toString());
        calculationUserInput.tedadTaxfif = Integer.parseInt(binding.editTextTedadTakhfif.getText().toString());
        calculationUserInput.zarfiatQarardadi = Integer.parseInt(binding.editTextZarfiatQaradadi.getText().toString());
        calculationUserInput.parNumber = binding.editTextPariNumber.getText().toString();
        calculationUserInput.karbariId = karbariDictionaries.get(binding.spinner1.getSelectedItemPosition()).getId();
        calculationUserInput.noeVagozariId = noeVagozariDictionaries.get(binding.spinner2.getSelectedItemPosition()).getId();
        calculationUserInput.qotrEnsheabId = qotrEnsheabDictionaries.get(binding.spinner3.getSelectedItemPosition()).getId();
        calculationUserInput.taxfifId = taxfifDictionaries.get(binding.spinner4.getSelectedItemPosition()).getId();
        calculationUserInput.adamTaxfifAb = binding.checkbox1.isChecked();
        calculationUserInput.adamTaxfifFazelab = binding.checkbox2.isChecked();
        calculationUserInput.ensheabQeireDaem = binding.checkbox3.isChecked();
        return calculationUserInput;
    }

    private CalculationUserInput prepareServices(CalculationUserInput calculationUserInput) {
        for (int i = 0; i < checkBoxes.size(); i++) {
            requestDictionaries.get(i).setSelected(checkBoxes.get(i).isSelected());
        }
        calculationUserInput.selectedServicesObject = requestDictionaries;
        Gson gson = new GsonBuilder().create();
        calculationUserInput.selectedServicesString = gson.toJson(requestDictionaries);
        return calculationUserInput;
    }

    private boolean prepareForm() {
        return checkIsNoEmpty(binding.editTextZoneTitle)
                && checkIsNoEmpty(binding.editTextTrackNumber)
                && checkIsNoEmpty(binding.editTextBillId)
                && checkIsNoEmpty(binding.editTextSifoon100)
                && checkIsNoEmpty(binding.editTextSifoon125)
                && checkIsNoEmpty(binding.editTextSifoon150)
                && checkIsNoEmpty(binding.editTextSifoon200)
                && checkIsNoEmpty(binding.editTextArese)
                && checkIsNoEmpty(binding.editTextAianKol)
                && checkIsNoEmpty(binding.editTextAianMaskooni)
                && checkIsNoEmpty(binding.editTextAianNonMaskooni)
                && checkIsNoEmpty(binding.editTextTedadMaskooni)
                && checkIsNoEmpty(binding.editTextTedadTejari)
                && checkIsNoEmpty(binding.editTextTedadSaier)
                && checkIsNoEmpty(binding.editTextArzeshMelk)
                && checkIsNoEmpty(binding.editTextTedadTakhfif)
                && checkIsNoEmpty(binding.editTextZarfiatQaradadi)
                && checkIsNoEmpty(binding.editTextPariNumber)
                && checkIsNoEmpty(binding.editText20)
                && checkIsNoEmpty(binding.editText19)
//                && editText19.getText().length() > 0
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
//                textView.setTypeface(typeface);
                textView.setChecked(true);
                textView.setTextColor(getResources().getColor(R.color.black));
                return view;
            }
        };
        binding.spinner1.setAdapter(arrayAdapter);
        binding.spinner1.setSelection(examinerDuties.getKarbariId());
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
//                textView.setTypeface(typeface);
                textView.setChecked(true);
                textView.setTextColor(getResources().getColor(R.color.black));
                return view;
            }
        };
        binding.spinner4.setAdapter(arrayAdapter);
        binding.spinner4.setSelection(examinerDuties.getTaxfifId());
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
//                textView.setTypeface(typeface);
                textView.setChecked(true);
                textView.setTextColor(getResources().getColor(R.color.black));
                return view;
            }
        };
        binding.spinner2.setAdapter(arrayAdapter);
        binding.spinner2.setSelection(select);
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
//                textView.setTypeface(typeface);
                textView.setChecked(true);
                textView.setTextColor(getResources().getColor(R.color.black));
                return view;
            }
        };
        binding.spinner3.setAdapter(arrayAdapter);
        binding.spinner3.setSelection(examinerDuties.getQotrEnsheabId());
    }

    private void initializeField() {
        binding.editTextZoneTitle.setText(examinerDuties.getZoneTitle());
        binding.editTextTrackNumber.setText(examinerDuties.getTrackNumber());
        binding.editTextBillId.setText(examinerDuties.getBillId());
        binding.editTextSifoon100.setText(String.valueOf(examinerDuties.getSifoon100()));
        binding.editTextSifoon125.setText(String.valueOf(examinerDuties.getSifoon125()));
        binding.editTextSifoon150.setText(String.valueOf(examinerDuties.getSifoon150()));
        binding.editTextSifoon200.setText(String.valueOf(examinerDuties.getSifoon200()));
        binding.editTextArese.setText(String.valueOf(examinerDuties.getArse()));
        binding.editTextAianKol.setText(String.valueOf(examinerDuties.getAianKol()));
        binding.editTextAianMaskooni.setText(String.valueOf(examinerDuties.getAianMaskooni()));
        binding.editTextAianNonMaskooni.setText(String.valueOf(examinerDuties.getAianNonMaskooni()));
        binding.editTextTedadMaskooni.setText(String.valueOf(examinerDuties.getTedadMaskooni()));
        binding.editTextTedadTejari.setText(String.valueOf(examinerDuties.getTedadTejari()));
        binding.editTextTedadSaier.setText(String.valueOf(examinerDuties.getTedadSaier()));
        binding.editTextArzeshMelk.setText(String.valueOf(examinerDuties.getArzeshMelk()));
        binding.editTextTedadTakhfif.setText(String.valueOf(examinerDuties.getTedadTaxfif()));
        binding.editTextZarfiatQaradadi.setText(String.valueOf(examinerDuties.getZarfiatQarardadi()));
        binding.editTextPariNumber.setText(examinerDuties.getParNumber());
        binding.editText19.setText(examinerDuties.getExaminationDay());
        binding.editText20.setText(examinerDuties.getPostalCode());

        binding.checkbox1.setChecked(examinerDuties.isAdamTaxfifAb());
        binding.checkbox2.setChecked(examinerDuties.isAdamTaxfifFazelab());
        binding.checkbox3.setChecked(examinerDuties.isEnsheabQeirDaem());
    }

    @SuppressLint("NewApi")
    private void initializeServicesCheckBox() {
        Gson gson = new GsonBuilder().create();
        requestDictionaries = Arrays.asList(gson.fromJson(examinerDuties.getRequestDictionaryString(),
                RequestDictionary[].class));
        CheckBoxAdapter checkBoxAdapter = new CheckBoxAdapter(context, requestDictionaries);
        binding.gridView.setAdapter(checkBoxAdapter);
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
