package com.leon.estimate.fragments;


import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
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
import com.leon.estimate.Tables.MotherChild;
import com.leon.estimate.Tables.MyDatabase;
import com.leon.estimate.Tables.NoeVagozariDictionary;
import com.leon.estimate.Tables.QotrEnsheabDictionary;
import com.leon.estimate.Tables.TaxfifDictionary;
import com.leon.estimate.activities.FormActivity;
import com.leon.estimate.adapters.MotherChildAdapter;
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
    FormFragmentBinding binding;
    private MyDatabase dataBase;
    private List<KarbariDictionary> karbariDictionaries;
    private List<QotrEnsheabDictionary> qotrEnsheabDictionaries;
    private List<NoeVagozariDictionary> noeVagozariDictionaries;
    private List<TaxfifDictionary> taxfifDictionaries;
    MotherChildAdapter motherChildAdapter;

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
        ((FormActivity) Objects.requireNonNull(getActivity())).setActionBarTitle(
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
        setOnEditTextTedadTejariTextChangeListener();
        setOnImageViewPlusClickListener();
        FormActivity.motherChildren = new ArrayList<>();
        //FormActivity.motherChildren.add(new MotherChild("s","s",1,2,3));

    }

    void setOnImageViewPlusClickListener() {
        binding.imageViewPlus.setOnClickListener(v -> {
            //todo check empty
            if (checkIsNoEmpty(binding.editTextVahed) && checkIsNoEmpty(binding.editTextNoeShoql)
                    && checkIsNoEmpty(binding.editTextVahedMohasebe) && checkIsNoEmpty(binding.editTextA2)) {
                String karbari = karbariDictionaries.get(binding.spinner5.getSelectedItemPosition()).getTitle();
                String noeShoql = binding.editTextNoeShoql.getText().toString();
                int tedadVahed = Integer.parseInt(binding.editTextVahed.getText().toString());
                String vahedMohasebe = binding.editTextVahedMohasebe.getText().toString();
                String a = binding.editTextA2.getText().toString();
                MotherChild motherChild = new MotherChild(karbari, noeShoql, tedadVahed, vahedMohasebe, a);
                FormActivity.motherChildren.add(motherChild);
                if (FormActivity.motherChildren.size() == 1) {
                    motherChildAdapter = new MotherChildAdapter(context);
                    binding.recyclerViewMotherChild.setAdapter(motherChildAdapter);
                    binding.recyclerViewMotherChild.setLayoutManager(new LinearLayoutManager(getActivity()) {
                        @Override
                        public boolean requestChildRectangleOnScreen(@NonNull RecyclerView parent,
                                                                     @NonNull View child,
                                                                     @NonNull Rect rect, boolean immediate) {
                            return false;
                        }
                    });
                }
                motherChildAdapter.notifyDataSetChanged();
                binding.editTextA2.setText("");
                binding.editTextNoeShoql.setText("");
                binding.editTextVahed.setText("");
                binding.editTextVahedMohasebe.setText("");
            }
        });
    }

    public CalculationUserInput setOnButtonNextClickListener() {
        if (prepareForm()) {
            return prepareField();
        }
        return null;
    }

    void setOnEditTextTedadTejariTextChangeListener() {
        binding.editTextTedadTejari.addTextChangedListener(new TextWatcher() {
            int tejari;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0)
                    tejari = Integer.parseInt(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.e("tejari", String.valueOf(tejari));
                binding.linearLayoutTejari.setVisibility(View.GONE);
                if (tejari > 0) {
                    binding.linearLayoutTejari.setVisibility(View.VISIBLE);
                }
            }
        });
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
        calculationUserInput.sifoon100 = Integer.parseInt(binding.editTextSifoon100.getText().toString());
        calculationUserInput.sifoon125 = Integer.parseInt(binding.editTextSifoon125.getText().toString());
        calculationUserInput.sifoon150 = Integer.parseInt(binding.editTextSifoon150.getText().toString());
        calculationUserInput.sifoon200 = Integer.parseInt(binding.editTextSifoon200.getText().toString());
        calculationUserInput.arse = Integer.parseInt(binding.editTextArese.getText().toString());
        calculationUserInput.aianMaskooni = Integer.parseInt(binding.editTextAianMaskooni.getText().toString());
        calculationUserInput.aianTejari = Integer.parseInt(binding.editTextAianNonMaskooni.getText().toString());
        calculationUserInput.aianKol = Integer.parseInt(binding.editTextAianKol.getText().toString());
        calculationUserInput.tedadMaskooni = Integer.parseInt(binding.editTextTedadMaskooni.getText().toString());
        calculationUserInput.tedadTejari = Integer.parseInt(binding.editTextTedadTejari.getText().toString());
        calculationUserInput.tedadSaier = Integer.parseInt(binding.editTextTedadSaier.getText().toString());
        calculationUserInput.tedadTaxfif = Integer.parseInt(binding.editTextTedadTakhfif.getText().toString());
        calculationUserInput.zarfiatQarardadi = Integer.parseInt(binding.editTextZarfiatQaradadi.getText().toString());
        calculationUserInput.arzeshMelk = Integer.parseInt(binding.editTextArzeshMelk.getText().toString());
        calculationUserInput.parNumber = binding.editTextPariNumber.getText().toString();

        calculationUserInput.karbariId = karbariDictionaries.get(binding.spinner1.getSelectedItemPosition()).getId();
        calculationUserInput.noeVagozariId = noeVagozariDictionaries.get(binding.spinner2.getSelectedItemPosition()).getId();
        calculationUserInput.qotrEnsheabId = qotrEnsheabDictionaries.get(binding.spinner3.getSelectedItemPosition()).getId();
        calculationUserInput.taxfifId = taxfifDictionaries.get(binding.spinner4.getSelectedItemPosition()).getId();
        calculationUserInput.ensheabQeireDaem = binding.checkbox1.isChecked();
        FormActivity.karbari = karbariDictionaries.get(binding.spinner1.getSelectedItemPosition()).getTitle();
        FormActivity.noeVagozari = noeVagozariDictionaries.get(binding.spinner2.getSelectedItemPosition()).getTitle();
        FormActivity.estelamShahrdari = binding.checkbox3.isChecked();
        FormActivity.parvane = binding.checkbox4.isChecked();
        FormActivity.motaqazi = binding.checkbox2.isChecked();
        return calculationUserInput;
    }

    private boolean prepareForm() {
        return checkIsNoEmpty(binding.editTextSifoon100)
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
                && checkIsNoEmpty(binding.editTextPariNumber);
//                && checkIsNoEmpty(binding.editTextSodurDate)
//                && checkIsNoEmpty(binding.editTextPelak);
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
        List<String> arrayListSpinner = new ArrayList<>();
        int selected = 0, counter = 0;
        for (KarbariDictionary karbariDictionary : karbariDictionaries) {
            arrayListSpinner.add(karbariDictionary.getTitle());
            if (karbariDictionary.getId() == FormActivity.examinerDuties.getKarbariId()) {
                selected = counter;
            }
            counter = counter + 1;
        }
        binding.spinner1.setAdapter(createArrayAdapter(arrayListSpinner));
        binding.spinner5.setAdapter(createArrayAdapter(arrayListSpinner));
//        binding.spinner1.setSelection(FormActivity1.examinerDuties.getKarbariId());
        binding.spinner1.setSelection(selected);
    }

    private void initializeTaxfifSpinner() {
        DaoTaxfifDictionary daoTaxfifDictionary = dataBase.daoTaxfifDictionary();
        taxfifDictionaries = daoTaxfifDictionary.getTaxfifDictionaries();
        List<String> arrayListSpinner = new ArrayList<>();
        int selected = 0, counter = 0;
        for (TaxfifDictionary taxfifDictionary : taxfifDictionaries) {
            arrayListSpinner.add(taxfifDictionary.getTitle());
            if (taxfifDictionary.getId() == FormActivity.examinerDuties.getTaxfifId()) {
                selected = counter;
            }
            counter = counter + 1;
        }
        binding.spinner4.setAdapter(createArrayAdapter(arrayListSpinner));
//        binding.spinner4.setSelection(FormActivity1.examinerDuties.getTaxfifId());
        binding.spinner4.setSelection(selected);
    }

    private void initializeNoeVagozariSpinner() {
        DaoNoeVagozariDictionary daoNoeVagozariDictionary = dataBase.daoNoeVagozariDictionary();
        noeVagozariDictionaries = daoNoeVagozariDictionary.getNoeVagozariDictionaries();
        List<String> arrayListSpinner = new ArrayList<>();
        int select = 0, counter = 0;
        for (NoeVagozariDictionary noeVagozariDictionary : noeVagozariDictionaries) {
            arrayListSpinner.add(noeVagozariDictionary.getTitle());
            if (noeVagozariDictionary.isSelected()) {
                select = counter;
            }
            counter++;
        }
        binding.spinner2.setAdapter(createArrayAdapter(arrayListSpinner));
        binding.spinner2.setSelection(select);
    }

    private void initializeQotrEnsheabSpinner() {
        DaoQotrEnsheabDictionary daoQotrEnsheabDictionary = dataBase.daoQotrEnsheabDictionary();
        qotrEnsheabDictionaries = daoQotrEnsheabDictionary.getQotrEnsheabDictionaries();
        List<String> arrayListSpinner = new ArrayList<>();
        int counter = 0, selected = 0;
        for (QotrEnsheabDictionary qotrEnsheabDictionary : qotrEnsheabDictionaries) {
            arrayListSpinner.add(qotrEnsheabDictionary.getTitle());
            if (FormActivity.examinerDuties.getQotrEnsheabId() == qotrEnsheabDictionary.getId()) {
                selected = counter;
            }
            counter = counter + 1;
        }
        binding.spinner3.setAdapter(createArrayAdapter(arrayListSpinner));
//        binding.spinner3.setSelection(FormActivity1.examinerDuties.getQotrEnsheabId());
        binding.spinner3.setSelection(selected);
    }

    private void initializeField() {
        binding.editTextSifoon100.setText(String.valueOf(FormActivity.examinerDuties.getSifoon100()));
        binding.editTextSifoon125.setText(String.valueOf(FormActivity.examinerDuties.getSifoon125()));
        binding.editTextSifoon150.setText(String.valueOf(FormActivity.examinerDuties.getSifoon150()));
        binding.editTextSifoon200.setText(String.valueOf(FormActivity.examinerDuties.getSifoon200()));
        binding.editTextArese.setText(String.valueOf(FormActivity.examinerDuties.getArse()));
        binding.editTextAianKol.setText(String.valueOf(FormActivity.examinerDuties.getAianKol()));
        binding.editTextAianMaskooni.setText(String.valueOf(FormActivity.examinerDuties.getAianMaskooni()));
        binding.editTextAianNonMaskooni.setText(String.valueOf(FormActivity.examinerDuties.getAianNonMaskooni()));
        binding.editTextTedadMaskooni.setText(String.valueOf(FormActivity.examinerDuties.getTedadMaskooni()));
        binding.editTextTedadTejari.setText(String.valueOf(FormActivity.examinerDuties.getTedadTejari()));
        binding.editTextTedadSaier.setText(String.valueOf(FormActivity.examinerDuties.getTedadSaier()));
        binding.editTextArzeshMelk.setText(String.valueOf(FormActivity.examinerDuties.getArzeshMelk()));
        binding.editTextTedadTakhfif.setText(String.valueOf(FormActivity.examinerDuties.getTedadTaxfif()));
        binding.editTextZarfiatQaradadi.setText(String.valueOf(FormActivity.examinerDuties.getZarfiatQarardadi()));
        binding.editTextPariNumber.setText(FormActivity.examinerDuties.getParNumber());
        binding.editTextSodurDate.setText(FormActivity.examinerDuties.getExaminationDay());
        binding.editTextPelak.setText(FormActivity.examinerDuties.getPostalCode());

        binding.checkbox1.setChecked(FormActivity.examinerDuties.isEnsheabQeirDaem());
    }

    ArrayAdapter<String> createArrayAdapter(List<String> arrayListSpinner) {
        return new ArrayAdapter<String>(context,
                R.layout.dropdown_menu_popup_item, arrayListSpinner) {
            @NotNull
            @Override
            public View getView(int position, View convertView, @NotNull ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                final CheckedTextView textView = view.findViewById(android.R.id.text1);
                textView.setChecked(true);
                textView.setTextSize(context.getResources().getDimension(R.dimen.textSizeSmall));
                textView.setTextColor(getResources().getColor(R.color.black));
                return view;
            }
        };
    }

    @SuppressLint("StaticFieldLeak")
    class initializeSpinners extends AsyncTask<Integer, Integer, Integer> {
        ProgressDialog dialog;

        @Override
        protected Integer doInBackground(Integer... integers) {
            Objects.requireNonNull(getActivity()).runOnUiThread(FormFragment.this::initializeSpinner);
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
