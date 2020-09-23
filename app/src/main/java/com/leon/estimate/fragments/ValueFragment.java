package com.leon.estimate.fragments;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;

import com.leon.estimate.Enums.BundleEnum;
import com.leon.estimate.Enums.ProgressType;
import com.leon.estimate.Enums.SharedReferenceKeys;
import com.leon.estimate.Enums.SharedReferenceNames;
import com.leon.estimate.Infrastructure.IAbfaService;
import com.leon.estimate.Infrastructure.ICallback;
import com.leon.estimate.Infrastructure.ICallbackError;
import com.leon.estimate.Infrastructure.ICallbackIncomplete;
import com.leon.estimate.R;
import com.leon.estimate.Tables.Arzeshdaraei;
import com.leon.estimate.Tables.Block;
import com.leon.estimate.Tables.Formula;
import com.leon.estimate.Tables.Zarib;
import com.leon.estimate.Utils.HttpClientWrapper;
import com.leon.estimate.Utils.NetworkHelper;
import com.leon.estimate.Utils.SharedPreferenceManager;
import com.leon.estimate.databinding.ValueFragmentBinding;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

import static com.leon.estimate.activities.FormActivity.activity;
import static com.leon.estimate.activities.FormActivity.arzeshdaraei;


public class ValueFragment extends DialogFragment {
    ValueFragmentBinding binding;
    Context context;
    TextView textview;
    private String zoneId;
//    Arzeshdaraei arzeshdaraei;

    public ValueFragment() {
    }

    public static ValueFragment newInstance(String param1) {
        ValueFragment fragment = new ValueFragment();
        Bundle args = new Bundle();
        args.putString(BundleEnum.ZONE_ID.getValue(), param1);
        fragment.setArguments(args);
        return fragment;
    }

    TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            counting(false);
        }
    };

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = ValueFragmentBinding.inflate(inflater, container, false);
        initialize();
        return binding.getRoot();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            zoneId = getArguments().getString(BundleEnum.ZONE_ID.getValue());
            Log.e("zone", zoneId);
        }
    }

    void initialize() {
        context = getActivity();
//        getArzeshdaraei();
        initializeSpinners();
        textview = (TextView) activity.findViewById(R.id.textViewArzeshMelk);
        addEditTextsChangedListener();
        binding.buttonCounting.setOnClickListener(v -> {
            if (checkIsNoEmpty(binding.editTextMaskooni) || checkIsNoEmpty(binding.editTextEdari)
                    || checkIsNoEmpty(binding.editTextHotel) || checkIsNoEmpty(binding.editTextOmumi)
                    || checkIsNoEmpty(binding.editTextSanati) || checkIsNoEmpty(binding.editTextTejari))
                counting(true);
            else
                Toast.makeText(context, "حداقل یکی از مقادیر را وارد کنید.", Toast.LENGTH_LONG).show();
        });
    }

    void addEditTextsChangedListener() {
        binding.editTextMaskooni.addTextChangedListener(textWatcher);
        binding.editTextTejari.addTextChangedListener(textWatcher);
        binding.editTextEdari.addTextChangedListener(textWatcher);
        binding.editTextOmumi.addTextChangedListener(textWatcher);
        binding.editTextSanati.addTextChangedListener(textWatcher);
        binding.editTextHotel.addTextChangedListener(textWatcher);
    }

    void counting(boolean dis) {
        int maskooni = 0, tejari = 0, omoomi = 0, sanati = 0, edari = 0, hotel = 0;
        String maskooniS = binding.editTextMaskooni.getText().toString(),
                tejariS = binding.editTextTejari.getText().toString(),
                omoomiS = binding.editTextOmumi.getText().toString(),
                sanatiS = binding.editTextSanati.getText().toString(),
                edariS = binding.editTextEdari.getText().toString(),
                hotelS = binding.editTextHotel.getText().toString();

        if (hotelS.length() > 0)
            hotel = Integer.parseInt(hotelS);
        if (maskooniS.length() > 0)
            maskooni = Integer.parseInt(maskooniS);
        if (tejariS.length() > 0)
            tejari = Integer.parseInt(tejariS);
        if (sanatiS.length() > 0)
            sanati = Integer.parseInt(sanatiS);
        if (edariS.length() > 0)
            edari = Integer.parseInt(edariS);
        if (omoomiS.length() > 0)
            omoomi = Integer.parseInt(omoomiS);
        if (maskooni > 0 || hotel > 0 || tejari > 0 || sanati > 0 || edari > 0 || omoomi > 0) {
            double countMaskooni = 20000, countEdariDolati = 20000, countTejari = 20000,
                    countSanati = 20000, countKhadamati = 20000, countSayer = 20000;
            Block block = arzeshdaraei.blocks.get(binding.spinner1.getSelectedItemPosition());
            Zarib zarib = arzeshdaraei.zaribs.get(0);
            Formula formula = arzeshdaraei.formulas.get(binding.spinner2.getSelectedItemPosition());

            if ((block.maskooni * formula.maskooniZ) > 20000)
                countMaskooni = (block.maskooni * formula.maskooniZ);

            if ((block.tejari * formula.tejariZ) > 20000)
                countTejari = (block.tejari * formula.tejariZ);

            if ((block.edariDolati * formula.edariDolatiZ) > 20000)
                countEdariDolati = (block.edariDolati * formula.edariDolatiZ);

            if ((block.sanati * formula.sanatiZ) > 20000)
                countSanati = (block.sanati * formula.sanatiZ);

            if ((block.khadamati * formula.khadamatiZ) > 20000)
                countKhadamati = (block.khadamati * formula.khadamatiZ);

            if ((block.sayer * formula.sayerZ) > 20000)
                countSayer = (block.sayer * formula.sayerZ);

            countMaskooni = (countMaskooni * maskooni);
            countEdariDolati = (countEdariDolati * edari);
            countTejari = (countTejari * tejari);
            countSanati = (countSanati * sanati);
            countKhadamati = (countKhadamati * omoomi);
            countSayer = (countSayer * hotel);
            int count =
                    (int) ((countMaskooni + countEdariDolati + countTejari + countSanati + countKhadamati + countSayer)
                            / (maskooni + tejari + omoomi + sanati + edari + hotel));
            count = count / 1000;
            count = count * 1000;
            binding.textViewCount.setText(String.valueOf(count));
            Log.e("value", String.valueOf(count));
            if (dis) {
                textview.setText(String.valueOf(count));
                dismiss();
            }
        } else {
            binding.textViewCount.setText(getString(R.string.zero));
            if (dis) {
                Toast.makeText(context, "حداقل یکی از مقادیر را بزرگتر از صفر وارد کنید.", Toast.LENGTH_LONG).show();
            }
        }
    }

    void initializeSpinners() {
        initializeSpinnerBlock();
        initializeSpinnerGozar();
        binding.spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                counting(false);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        binding.spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                counting(false);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    void initializeSpinnerGozar() {
        List<String> arrayListSpinner = new ArrayList<>();
        for (Formula formula : arzeshdaraei.formulas) {
            arrayListSpinner.add(formula.gozarTitle);
        }
        binding.spinner2.setAdapter(createArrayAdapter(arrayListSpinner));
    }

    void initializeSpinnerBlock() {
        List<String> arrayListSpinner = new ArrayList<>();
        for (Block block : arzeshdaraei.blocks) {
            arrayListSpinner.add(block.blockId);
        }

        binding.spinner1.setAdapter(createArrayAdapter(arrayListSpinner));
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

    boolean checkIsNoEmpty(EditText editText) {
        View focusView;
        if (editText.getText().toString().length() < 1) {
            focusView = editText;
            focusView.requestFocus();
            return false;
        }
        return true;
    }

    void getArzeshdaraei() {
        SharedPreferenceManager sharedPreferenceManager = new SharedPreferenceManager(context,
                SharedReferenceNames.ACCOUNT.getValue());
        Retrofit retrofit = NetworkHelper.getInstance(true,
                sharedPreferenceManager.getStringData(SharedReferenceKeys.TOKEN.getValue()));
        final IAbfaService arzeshdaraei = retrofit.create(IAbfaService.class);
        Call<Arzeshdaraei> call = arzeshdaraei.getArzeshDaraii(Integer.parseInt(zoneId));
        HttpClientWrapper.callHttpAsync(call, ProgressType.SHOW.getValue(), context,
                new GetArzeshdaraei(), new GetArzeshdaraeiIncomplete(), new GetError());
    }

    @Override
    public void onResume() {
        WindowManager.LayoutParams params = getDialog().getWindow().getAttributes();
        params.width = ViewGroup.LayoutParams.WRAP_CONTENT;
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        getDialog().getWindow().setAttributes(params);
        super.onResume();
    }

    class GetArzeshdaraei implements ICallback<Arzeshdaraei> {
        @Override
        public void execute(Arzeshdaraei arzeshdaraeiResponse) {
            arzeshdaraei = arzeshdaraeiResponse;
            initializeSpinners();
        }
    }

    class GetArzeshdaraeiIncomplete implements ICallbackIncomplete<Arzeshdaraei> {
        @Override
        public void executeIncomplete(Response<Arzeshdaraei> response) {
            Log.e("Incomplete", String.valueOf(response.errorBody()));
        }
    }

    class GetError implements ICallbackError {
        @Override
        public void executeError(Throwable t) {
            Log.e("Error", String.valueOf(t));

        }
    }
}