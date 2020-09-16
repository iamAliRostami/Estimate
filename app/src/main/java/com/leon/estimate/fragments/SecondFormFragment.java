package com.leon.estimate.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.RadioButton;

import androidx.fragment.app.Fragment;

import com.leon.estimate.R;
import com.leon.estimate.Tables.SecondForm;
import com.leon.estimate.activities.FormActivity;
import com.leon.estimate.databinding.SecondFormFragmentBinding;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class SecondFormFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    SecondFormFragmentBinding binding;
    Context context;

    public SecondFormFragment() {
    }

    public static SecondFormFragment newInstance(String param1, String param2) {
        SecondFormFragment fragment = new SecondFormFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        context = getActivity();
        ((FormActivity) Objects.requireNonNull(getActivity())).setActionBarTitle(
                context.getString(R.string.app_name).concat(" / ")
                        .concat(context.getString(R.string.second_form)));
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = SecondFormFragmentBinding.inflate(inflater, container, false);
        initialize();
        return binding.getRoot();
    }

    void initialize() {
        binding.spinnerLoole.setAdapter(createArrayAdapter(getResources().getStringArray(R.array.menu_qotr_loole)));
        binding.spinnerLooleJens.setAdapter(createArrayAdapter(getResources().getStringArray(R.array.menu_jens_loole)));
    }

    ArrayAdapter<String> createArrayAdapter(String[] arraySpinner) {
        return new ArrayAdapter<String>(context,
                R.layout.dropdown_menu_popup_item, arraySpinner) {
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

    public SecondForm setOnButtonNextClickListener() {
        if (prepareForm()) {
            return prepareField();
        }
        return null;
    }

    private boolean prepareForm() {
        return checkIsNoEmpty(binding.editTextKhaki) &&
                checkIsNoEmpty(binding.editTextAsphalt) &&
                checkIsNoEmpty(binding.editTextSange) &&
                checkIsNoEmpty(binding.editTextOther) &&
                checkIsNoEmpty(binding.editTextKhakiFazelab) &&
                checkIsNoEmpty(binding.editTextAsphaltFazelab) &&
                checkIsNoEmpty(binding.editTextSangeFazelab) &&
                checkIsNoEmpty(binding.editTextOtherFazelab) &&
                checkIsNoEmpty(binding.editTextOmqZirzamin) &&
                checkIsNoEmpty(binding.editTextOmqFazelab);
    }

    private boolean checkIsNoEmpty(EditText editText) {
        View focusView;
        if (editText.getText().toString().length() < 1) {
            editText.setError(getString(R.string.error_empty));
            focusView = editText;
            focusView.requestFocus();
            return false;
        }
        return true;
    }

    private SecondForm prepareField() {
        String loole = getResources().getStringArray(R.array.menu_qotr_loole)[binding.spinnerLoole.getSelectedItemPosition()];
        String jenseLoole = getResources().getStringArray(R.array.menu_jens_loole)[binding.spinnerLooleJens.getSelectedItemPosition()];
        int noeMasraf = 0;
        RadioButton radioButtonMasraf = binding.radioButtonNormal;
        if (binding.radioButtonSakht.isChecked()) {
            noeMasraf = 1;
            radioButtonMasraf = binding.radioButtonSakht;
        } else if (binding.radioButtonService.isChecked()) {
            noeMasraf = 2;
            radioButtonMasraf = binding.radioButtonService;
        } else if (binding.radioButtonMedical.isChecked()) {
            noeMasraf = 3;
            radioButtonMasraf = binding.radioButtonMedical;
        }
        return new SecondForm(
                Integer.parseInt(binding.editTextKhaki.getText().toString()),
                Integer.parseInt(binding.editTextKhakiFazelab.getText().toString()),
                Integer.parseInt(binding.editTextAsphalt.getText().toString()),
                Integer.parseInt(binding.editTextAsphaltFazelab.getText().toString()),
                Integer.parseInt(binding.editTextSange.getText().toString()),
                Integer.parseInt(binding.editTextSangeFazelab.getText().toString()),
                Integer.parseInt(binding.editTextOther.getText().toString()),
                Integer.parseInt(binding.editTextOtherFazelab.getText().toString()),
                loole, jenseLoole,
                noeMasraf/*TODO*/,
                radioButtonMasraf.getText().toString()/*TODO*/,
                binding.radioButtonNormalPomp.isChecked()/*TODO*/,
                Integer.parseInt(binding.editTextOmqZirzamin.getText().toString()),
                binding.checkBoxChahAbBaran.isSelected()/*TODO*/,
                Integer.parseInt(binding.editTextOmqFazelab.getText().toString()),
                binding.checkBoxEtesalZirzamin.isSelected());
    }
}