package com.leon.estimate.fragments;


import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.EditText;

import androidx.fragment.app.Fragment;
import androidx.room.Room;

import com.google.gson.Gson;
import com.leon.estimate.Enums.BundleEnum;
import com.leon.estimate.MyApplication;
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
import com.leon.estimate.Tables.TaxfifDictionary;
import com.leon.estimate.activities.FormActivity1;
import com.leon.estimate.databinding.FormFragmentBinding;
import com.sardari.daterangepicker.customviews.DateRangeCalendarView;
import com.sardari.daterangepicker.dialog.DatePickerDialog;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FormFragment extends Fragment {
    private static final String ARG_PARAM2 = "param2";

    private Context context;
    private MyDatabase dataBase;
    private List<KarbariDictionary> karbariDictionaries;
    private List<QotrEnsheabDictionary> qotrEnsheabDictionaries;
    private List<NoeVagozariDictionary> noeVagozariDictionaries;
    private List<TaxfifDictionary> taxfifDictionaries;
    FormFragmentBinding binding;

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
        context = getActivity();
        ((FormActivity1) Objects.requireNonNull(getActivity())).setActionBarTitle(
                context.getString(R.string.app_name).concat(" / ").concat(context.getString(R.string.moshakhasat_melk)));
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FormFragmentBinding.inflate(inflater, container, false);
        initialize();
        return binding.getRoot();
    }

    private void initialize() {
        new initializeSpinners().execute();
        setOnEditTextSodurDateClickListener();
    }

    public CalculationUserInput setOnButtonNextClickListener() {
        if (prepareForm()) {
            return prepareField();
        }
        return null;
    }

    @SuppressLint("ClickableViewAccessibility")
    void setOnEditTextSodurDateClickListener() {
        binding.editTextSodurDate.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(context);
            datePickerDialog.setSelectionMode(DateRangeCalendarView.SelectionMode.Single);
            datePickerDialog.setDisableDaysAgo(false);
            datePickerDialog.setTextSizeTitle(10.0f);
            datePickerDialog.setTextSizeWeek(12.0f);
            datePickerDialog.setTextSizeDate(14.0f);
            datePickerDialog.setCanceledOnTouchOutside(true);
            datePickerDialog.setOnSingleDateSelectedListener(date ->
                    binding.editTextSodurDate.setText(date.getPersianShortDate()));
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
                && checkIsNoEmpty(binding.editTextPelak)
                && checkIsNoEmpty(binding.editTextSodurDate);
    }

    boolean checkIsNoEmpty(EditText editText) {
        View focusView;
        if (editText.getText().toString().length() < 1) {
            editText.setError(getString(R.string.error_empty));
            focusView = editText;
            focusView.requestFocus();
            return false;
        }
        return true;
    }

    private void initializeSpinner() {
        dataBase = Room.databaseBuilder(context, MyDatabase.class, MyApplication.getDBNAME())
                .allowMainThreadQueries().build();
        initializeNoeVagozariSpinner();
        initializeKarbariSpinner();
        initializeQotrEnsheabSpinner();
        initializeTaxfifSpinner();
        initializeField();
    }

    private void initializeKarbariSpinner() {
        DaoKarbariDictionary daoKarbariDictionary = dataBase.daoKarbariDictionary();
        karbariDictionaries = daoKarbariDictionary.getKarbariDictionary();
        List<String> arrayListSpinner1 = new ArrayList<>();
        for (KarbariDictionary karbariDictionary : karbariDictionaries) {
            arrayListSpinner1.add(karbariDictionary.getTitle());
        }
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(context,
                android.R.layout.simple_spinner_dropdown_item, arrayListSpinner1) {
            @NotNull
            @Override
            public View getView(int position, View convertView, @NotNull ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                final CheckedTextView textView = view.findViewById(android.R.id.text1);
                textView.setChecked(true);
                textView.setTextColor(getResources().getColor(R.color.black));
                return view;
            }
        };
        binding.spinner1.setAdapter(arrayAdapter);
        binding.spinner1.setSelection(FormActivity1.examinerDuties.getKarbariId());
    }

    private void initializeTaxfifSpinner() {
        DaoTaxfifDictionary daoTaxfifDictionary = dataBase.daoTaxfifDictionary();
        taxfifDictionaries = daoTaxfifDictionary.getTaxfifDictionaries();
        List<String> arrayListSpinner1 = new ArrayList<>();
        for (TaxfifDictionary taxfifDictionary : taxfifDictionaries) {
            arrayListSpinner1.add(taxfifDictionary.getTitle());
        }
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(context,
                android.R.layout.simple_spinner_dropdown_item, arrayListSpinner1) {
            @NotNull
            @Override
            public View getView(int position, View convertView, @NotNull ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                final CheckedTextView textView = view.findViewById(android.R.id.text1);
                textView.setChecked(true);
                textView.setTextColor(getResources().getColor(R.color.black));
                return view;
            }
        };
        binding.spinner4.setAdapter(arrayAdapter);
        binding.spinner4.setSelection(FormActivity1.examinerDuties.getTaxfifId());
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
        for (QotrEnsheabDictionary qotrEnsheabDictionary : qotrEnsheabDictionaries) {
            arrayListSpinner1.add(qotrEnsheabDictionary.getTitle());
        }
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(context,
                android.R.layout.simple_spinner_dropdown_item, arrayListSpinner1) {
            @NotNull
            @Override
            public View getView(int position, View convertView, @NotNull ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                final CheckedTextView textView = view.findViewById(android.R.id.text1);
                textView.setChecked(true);
                textView.setTextColor(getResources().getColor(R.color.black));
                return view;
            }
        };
        binding.spinner3.setAdapter(arrayAdapter);
        binding.spinner3.setSelection(FormActivity1.examinerDuties.getQotrEnsheabId());
    }

    private void initializeField() {
        binding.editTextZoneTitle.setText(FormActivity1.examinerDuties.getZoneTitle());
        binding.editTextTrackNumber.setText(FormActivity1.examinerDuties.getTrackNumber());
        binding.editTextBillId.setText(FormActivity1.examinerDuties.getBillId());
        binding.editTextSifoon100.setText(String.valueOf(FormActivity1.examinerDuties.getSifoon100()));
        binding.editTextSifoon125.setText(String.valueOf(FormActivity1.examinerDuties.getSifoon125()));
        binding.editTextSifoon150.setText(String.valueOf(FormActivity1.examinerDuties.getSifoon150()));
        binding.editTextSifoon200.setText(String.valueOf(FormActivity1.examinerDuties.getSifoon200()));
        binding.editTextArese.setText(String.valueOf(FormActivity1.examinerDuties.getArse()));
        binding.editTextAianKol.setText(String.valueOf(FormActivity1.examinerDuties.getAianKol()));
        binding.editTextAianMaskooni.setText(String.valueOf(FormActivity1.examinerDuties.getAianMaskooni()));
        binding.editTextAianNonMaskooni.setText(String.valueOf(FormActivity1.examinerDuties.getAianNonMaskooni()));
        binding.editTextTedadMaskooni.setText(String.valueOf(FormActivity1.examinerDuties.getTedadMaskooni()));
        binding.editTextTedadTejari.setText(String.valueOf(FormActivity1.examinerDuties.getTedadTejari()));
        binding.editTextTedadSaier.setText(String.valueOf(FormActivity1.examinerDuties.getTedadSaier()));
        binding.editTextArzeshMelk.setText(String.valueOf(FormActivity1.examinerDuties.getArzeshMelk()));
        binding.editTextTedadTakhfif.setText(String.valueOf(FormActivity1.examinerDuties.getTedadTaxfif()));
        binding.editTextZarfiatQaradadi.setText(String.valueOf(FormActivity1.examinerDuties.getZarfiatQarardadi()));
        binding.editTextPariNumber.setText(FormActivity1.examinerDuties.getParNumber());
        binding.editTextSodurDate.setText(FormActivity1.examinerDuties.getExaminationDay());
        binding.editTextPelak.setText(FormActivity1.examinerDuties.getPostalCode());

        binding.checkbox1.setChecked(FormActivity1.examinerDuties.isAdamTaxfifAb());
        binding.checkbox2.setChecked(FormActivity1.examinerDuties.isAdamTaxfifFazelab());
        binding.checkbox3.setChecked(FormActivity1.examinerDuties.isEnsheabQeirDaem());
    }

    @SuppressLint("StaticFieldLeak")
    class initializeSpinners extends AsyncTask<Integer, Integer, Integer> {
        ProgressDialog dialog;

        @Override
        protected Integer doInBackground(Integer... integers) {
            initializeSpinner();
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(context);
            dialog.setMessage(context.getString(R.string.loading_getting_info));
            dialog.setTitle(context.getString(R.string.loading_connecting));
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected void onPostExecute(Integer s) {
            super.onPostExecute(s);
            dialog.dismiss();
        }
    }
}
