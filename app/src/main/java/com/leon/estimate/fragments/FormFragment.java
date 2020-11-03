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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.google.gson.Gson;
import com.leon.estimate.Enums.BundleEnum;
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
import com.leon.estimate.Tables.Arzeshdaraei;
import com.leon.estimate.Tables.CalculationUserInput;
import com.leon.estimate.Tables.DaoBlock;
import com.leon.estimate.Tables.DaoFormula;
import com.leon.estimate.Tables.DaoKarbariDictionary;
import com.leon.estimate.Tables.DaoNoeVagozariDictionary;
import com.leon.estimate.Tables.DaoQotrEnsheabDictionary;
import com.leon.estimate.Tables.DaoTaxfifDictionary;
import com.leon.estimate.Tables.DaoZarib;
import com.leon.estimate.Tables.ExaminerDuties;
import com.leon.estimate.Tables.KarbariDictionary;
import com.leon.estimate.Tables.MyDatabase;
import com.leon.estimate.Tables.NoeVagozariDictionary;
import com.leon.estimate.Tables.QotrEnsheabDictionary;
import com.leon.estimate.Tables.TaxfifDictionary;
import com.leon.estimate.Tables.Tejariha;
import com.leon.estimate.Utils.CustomDialog;
import com.leon.estimate.Utils.CustomErrorHandlingNew;
import com.leon.estimate.Utils.HttpClientWrapper;
import com.leon.estimate.Utils.NetworkHelper;
import com.leon.estimate.Utils.SharedPreferenceManager;
import com.leon.estimate.activities.FormActivity;
import com.leon.estimate.adapters.TejarihaAdapter;
import com.leon.estimate.databinding.FormFragmentBinding;
import com.sardari.daterangepicker.customviews.DateRangeCalendarView;
import com.sardari.daterangepicker.dialog.DatePickerDialog;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

import static com.leon.estimate.Utils.Constants.arzeshdaraei;
import static com.leon.estimate.Utils.Constants.examinerDuties;
import static com.leon.estimate.Utils.Constants.karbari;
import static com.leon.estimate.Utils.Constants.noeVagozari;
import static com.leon.estimate.Utils.Constants.qotrEnsheab;
import static com.leon.estimate.Utils.Constants.tejarihas;

public class FormFragment extends Fragment {
    private static final String ARG_PARAM2 = "param2";
    private Context context;
    int saier;
    int tejari;
    FormFragmentBinding binding;
    private MyDatabase dataBase;
    private List<KarbariDictionary> karbariDictionaries;
    private List<QotrEnsheabDictionary> qotrEnsheabDictionaries;
    private List<NoeVagozariDictionary> noeVagozariDictionaries;
    private List<TaxfifDictionary> taxfifDictionaries;
    TejarihaAdapter tejarihaAdapter;

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
                context.getString(R.string.app_name).concat(" / ").concat("صفحه سوم"));
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
        setOnEditTextTedadSaierTextChangeListener();
        setOnTextViewArezeshdaraeiClickListener();
        setOnImageViewPlusClickListener();
        tejarihaAdapter = new TejarihaAdapter(context);
        binding.recyclerViewTejariha.setAdapter(tejarihaAdapter);
        binding.recyclerViewTejariha.setLayoutManager(new LinearLayoutManager(getActivity()) {
            @Override
            public boolean requestChildRectangleOnScreen(@NonNull RecyclerView parent,
                                                         @NonNull View child,
                                                         @NonNull Rect rect, boolean immediate) {
                return false;
            }
        });
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
                Tejariha tejariha = new Tejariha(karbari, noeShoql, tedadVahed, vahedMohasebe, a,
                        examinerDuties.getTrackNumber());
                tejarihas.add(tejariha);
                tejarihaAdapter.notifyDataSetChanged();
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

    void setOnTextViewArezeshdaraeiClickListener() {
        binding.textViewArzeshMelk.setOnClickListener(v -> {
            if (arzeshdaraei != null
                    && arzeshdaraei.blocks != null
                    && arzeshdaraei.blocks.size() > 0
                    && arzeshdaraei.formulas != null
                    && arzeshdaraei.formulas.size() > 0
                    && arzeshdaraei.zaribs != null
                    && arzeshdaraei.zaribs.size() > 0) {
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction;
                if (fragmentManager != null) {
                    fragmentTransaction = fragmentManager.beginTransaction();
                    ValueFragment valueFragment = ValueFragment.newInstance(examinerDuties.getZoneId());
                    valueFragment.show(fragmentTransaction, "");
                }
            } else {
                getArzeshdaraei();
            }
        });
    }

    void setOnEditTextTedadSaierTextChangeListener() {
        binding.editTextTedadSaier.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0)
                    saier = Integer.parseInt(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (tejari < 1) {
                    binding.linearLayoutTejari.setVisibility(View.GONE);
                }
                if (saier > 0) {
                    binding.linearLayoutTejari.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    void setOnEditTextTedadTejariTextChangeListener() {
        binding.editTextTedadTejari.addTextChangedListener(new TextWatcher() {

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
                if (saier < 1) {
                    binding.linearLayoutTejari.setVisibility(View.GONE);
                }
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
        calculationUserInput.arzeshMelk = Integer.parseInt(binding.textViewArzeshMelk.getText().toString());
        calculationUserInput.parNumber = binding.editTextPariNumber.getText().toString();
        calculationUserInput.karbariId = karbariDictionaries.get(binding.spinner1.getSelectedItemPosition()).getId();
        calculationUserInput.noeVagozariId = noeVagozariDictionaries.get(binding.spinner2.getSelectedItemPosition()).getId();
        calculationUserInput.qotrEnsheabId = qotrEnsheabDictionaries.get(binding.spinner3.getSelectedItemPosition()).getId();
        calculationUserInput.taxfifId = taxfifDictionaries.get(binding.spinner4.getSelectedItemPosition()).getId();
        calculationUserInput.ensheabQeireDaem = binding.checkbox1.isChecked();

        karbari = karbariDictionaries.get(binding.spinner1.getSelectedItemPosition()).getTitle();
        noeVagozari = noeVagozariDictionaries.get(binding.spinner2.getSelectedItemPosition()).getTitle();
        qotrEnsheab = qotrEnsheabDictionaries.get(binding.spinner3.getSelectedItemPosition()).getTitle();
        examinerDuties.setEstelamShahrdari(binding.checkbox3.isChecked());
        examinerDuties.setParvane(binding.checkbox4.isChecked());
        examinerDuties.setMotaqazi(binding.checkbox2.isChecked());
        examinerDuties.setPelak(Integer.parseInt(binding.editTextPelak.getText().toString()));
        examinerDuties.setSanad(binding.checkbox5.isChecked());
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
                && checkIsNoEmpty(binding.textViewArzeshMelk)
                && checkIsNoEmpty(binding.editTextTedadTakhfif)
                && checkIsNoEmpty(binding.editTextZarfiatQaradadi)
                && checkIsNoEmpty(binding.editTextPariNumber);
    }

    boolean checkIsNoEmpty(TextView textView) {
        View focusView;
        if (textView.getText().toString().length() < 1) {
            textView.setError(getString(R.string.error_empty));
            focusView = textView;
            focusView.requestFocus();
            return false;
        }
        return true;
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
            if (karbariDictionary.getId() == examinerDuties.getKarbariId()) {
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
            if (taxfifDictionary.getId() == examinerDuties.getTaxfifId()) {
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
        binding.spinner2.setSelection(examinerDuties.noeVagozariId);
    }

    private void initializeQotrEnsheabSpinner() {
        DaoQotrEnsheabDictionary daoQotrEnsheabDictionary = dataBase.daoQotrEnsheabDictionary();
        qotrEnsheabDictionaries = daoQotrEnsheabDictionary.getQotrEnsheabDictionaries();
        List<String> arrayListSpinner = new ArrayList<>();
        int counter = 0, selected = 0;
        for (QotrEnsheabDictionary qotrEnsheabDictionary : qotrEnsheabDictionaries) {
            arrayListSpinner.add(qotrEnsheabDictionary.getTitle());
            if (examinerDuties.getQotrEnsheabId() == qotrEnsheabDictionary.getId()) {
                selected = counter;
            }
            counter = counter + 1;
        }
        binding.spinner3.setAdapter(createArrayAdapter(arrayListSpinner));
//        binding.spinner3.setSelection(FormActivity1.examinerDuties.getQotrEnsheabId());
        binding.spinner3.setSelection(selected);
    }

    private void initializeField() {
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
        binding.textViewArzeshMelk.setText(String.valueOf(examinerDuties.getArzeshMelk()));
        binding.editTextTedadTakhfif.setText(String.valueOf(examinerDuties.getTedadTaxfif()));
        binding.editTextZarfiatQaradadi.setText(String.valueOf(examinerDuties.getZarfiatQarardadi()));
        binding.editTextPariNumber.setText(examinerDuties.getParNumber());
        binding.editTextSodurDate.setText(examinerDuties.getExaminationDay());
        binding.editTextPelak.setText(String.valueOf(examinerDuties.getPelak()));

        binding.checkbox1.setChecked(examinerDuties.isEnsheabQeirDaem());
        binding.checkbox2.setChecked(examinerDuties.isMotaqazi());
        binding.checkbox3.setChecked(examinerDuties.isEstelamShahrdari());
        binding.checkbox4.setChecked(examinerDuties.isParvane());
        binding.checkbox5.setChecked(examinerDuties.isSanad());

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

    void getArzeshdaraei() {
        SharedPreferenceManager sharedPreferenceManager = new SharedPreferenceManager(context,
                SharedReferenceNames.ACCOUNT.getValue());
        Retrofit retrofit = NetworkHelper.getInstance(true,
                sharedPreferenceManager.getStringData(SharedReferenceKeys.TOKEN.getValue()));
        final IAbfaService arzeshdaraei = retrofit.create(IAbfaService.class);
        Log.e("zone", examinerDuties.getZoneId());
        Call<Arzeshdaraei> call = arzeshdaraei.getArzeshDaraii(Integer.parseInt(
                examinerDuties.getZoneId()));
        HttpClientWrapper.callHttpAsync(call, ProgressType.SHOW.getValue(), context,
                new GetArzeshdaraei(), new GetArzeshdaraeiIncomplete(), new GetError());
    }

    class GetArzeshdaraei implements ICallback<Arzeshdaraei> {
        @Override
        public void execute(Arzeshdaraei arzeshdaraeiResponse) {
            arzeshdaraei = arzeshdaraeiResponse;
            if (arzeshdaraei != null && arzeshdaraei.formulas != null &&
                    arzeshdaraei.formulas.size() > 0 && arzeshdaraei.blocks != null
                    && arzeshdaraei.blocks.size() > 0) {
                DaoFormula daoFormula = dataBase.daoFormula();
                DaoBlock daoBlock = dataBase.daoBlock();
                DaoZarib daoZarib = dataBase.daoZarib();
                for (int i = 0; i < arzeshdaraei.formulas.size(); i++)
                    daoFormula.insertFormula(arzeshdaraei.formulas.get(i));
                for (int i = 0; i < arzeshdaraei.blocks.size(); i++)
                    daoBlock.insertBlock(arzeshdaraei.blocks.get(i));
                for (int i = 0; i < arzeshdaraei.zaribs.size(); i++)
                    daoZarib.insertZarib(arzeshdaraei.zaribs.get(i));
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction;
                if (fragmentManager != null) {
                    fragmentTransaction = fragmentManager.beginTransaction();
                    ValueFragment valueFragment = ValueFragment.newInstance(examinerDuties.getZoneId());
                    valueFragment.show(fragmentTransaction, "");
                }
            } else {
                new CustomDialog(DialogType.Yellow, context,
                        context.getString(R.string.error_change_server),
                        getString(R.string.dear_user),
                        getString(R.string.arzesh_mantaghe),
                        getString(R.string.accepted));
            }
        }
    }

    class GetArzeshdaraeiIncomplete implements ICallbackIncomplete<Arzeshdaraei> {
        @Override
        public void executeIncomplete(Response<Arzeshdaraei> response) {
            CustomErrorHandlingNew customErrorHandlingNew = new CustomErrorHandlingNew(context);
            String error = customErrorHandlingNew.getErrorMessageDefault(response);
            new CustomDialog(DialogType.Yellow, context, error,
                    getString(R.string.dear_user),
                    getString(R.string.arzesh_mantaghe),
                    getString(R.string.accepted));
        }
    }

    class GetError implements ICallbackError {
        @Override
        public void executeError(Throwable t) {
            CustomErrorHandlingNew customErrorHandlingNew = new CustomErrorHandlingNew(context);
            String error = customErrorHandlingNew.getErrorMessageTotal(t);
            Toast.makeText(context, error, Toast.LENGTH_LONG).show();
        }
    }
}
