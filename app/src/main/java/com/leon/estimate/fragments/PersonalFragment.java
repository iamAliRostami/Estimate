package com.leon.estimate.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.leon.estimate.Enums.BundleEnum;
import com.leon.estimate.R;
import com.leon.estimate.Tables.CalculationUserInput;
import com.leon.estimate.Tables.ExaminerDuties;
import com.leon.estimate.activities.FormActivity;
import com.leon.estimate.databinding.PersonalFragmentBinding;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;


public class PersonalFragment extends Fragment {
    private static final String ARG_PARAM2 = "param2";
    PersonalFragmentBinding binding;

    public PersonalFragment() {
    }

    public static PersonalFragment newInstance(ExaminerDuties examinerDuties, String param2) {
        PersonalFragment fragment = new PersonalFragment();
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
        Context context = getActivity();
        if (context != null) {
            ((FormActivity) Objects.requireNonNull(getActivity())).setActionBarTitle(
                    context.getString(R.string.app_name).concat(" / ").concat("صفحه اول"));
        }
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = PersonalFragmentBinding.inflate(inflater, container, false);
        initialize();
        return binding.getRoot();
    }

    private void initialize() {
        initializeField();
    }

    public CalculationUserInput setOnButtonNextClickListener() {
        if (prepareForm()) {
            return prepareField();
        }
        return null;
    }

    private CalculationUserInput prepareField() {
        CalculationUserInput calculationUserInput = new CalculationUserInput();
        calculationUserInput.nationalId = binding.editTextNationNumber.getText().toString();
        calculationUserInput.firstName = binding.editTextName.getText().toString();
        calculationUserInput.sureName = binding.editTextFamily.getText().toString();
        calculationUserInput.fatherName = binding.editTextFatherName.getText().toString();
        calculationUserInput.postalCode = binding.editTextPostalCode.getText().toString();
        calculationUserInput.radif = binding.editTextRadif.getText().toString();
        calculationUserInput.phoneNumber = binding.editTextPhone.getText().toString();
        calculationUserInput.mobile = binding.editTextMobile.getText().toString();
        calculationUserInput.address = binding.editTextAddress.getText().toString();
        calculationUserInput.description = binding.editTextDescription.getText().toString();
        calculationUserInput.shenasname = binding.editTextShenasname.getText().toString();
        return calculationUserInput;
    }

    private boolean prepareForm() {
        return checkIsNoEmpty(binding.editTextName)
                && checkIsNoEmpty(binding.editTextFamily)
                && checkIsNoEmpty(binding.editTextAddress)
                && checkOtherIsNoEmpty();
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

    private boolean checkOtherIsNoEmpty() {
        View focusView;
        if (binding.editTextNationNumber.getText().toString().length() < 10) {
            binding.editTextNationNumber.setError(getString(R.string.error_format));
            focusView = binding.editTextNationNumber;
            focusView.requestFocus();
            return false;
        } else if (binding.editTextPostalCode.getText().toString().length() > 0 &&
                binding.editTextPostalCode.getText().toString().length() < 10) {
            binding.editTextPostalCode.setError(getString(R.string.error_format));
            focusView = binding.editTextPostalCode;
            focusView.requestFocus();
            return false;
        } else if (binding.editTextMobile.getText().toString().length() < 11) {
            binding.editTextMobile.setError(getString(R.string.error_format));
            focusView = binding.editTextMobile;
            focusView.requestFocus();
            return false;
        }
        return true;
    }

    private void initializeField() {
        binding.editTextAddress.setText(FormActivity.examinerDuties.getAddress());
        if (FormActivity.examinerDuties.getFirstName() != null)
            binding.editTextName.setText(FormActivity.examinerDuties.getFirstName().trim());
        if (FormActivity.examinerDuties.getSureName() != null)
            binding.editTextFamily.setText(FormActivity.examinerDuties.getSureName().trim());
        binding.editTextNationNumber.setText(FormActivity.examinerDuties.getNationalId());
        binding.editTextFatherName.setText(FormActivity.examinerDuties.getFatherName());
        binding.editTextDescription.setText(FormActivity.examinerDuties.getDescription().trim());
        binding.editTextPhone.setText(FormActivity.examinerDuties.getPhoneNumber());
        binding.editTextMobile.setText(FormActivity.examinerDuties.getMobile());
        binding.editTextEshterak.setText(Objects.requireNonNull(
                FormActivity.examinerDuties.getEshterak().trim()));
        binding.editTextPostalCode.setText(FormActivity.examinerDuties.getPostalCode());
        binding.editTextRadif.setText(FormActivity.examinerDuties.getRadif());

        binding.textViewZone.setText(FormActivity.examinerDuties.getZoneTitle());
        if (FormActivity.examinerDuties.getBillId() != null && FormActivity.examinerDuties.getBillId().length() > 0)
            binding.textViewBillId.setText(FormActivity.examinerDuties.getBillId());
        else {
            binding.textViewBillId.setText(FormActivity.examinerDuties.getNeighbourBillId());
            binding.textViewBillIdTitle.setText(getString(R.string.neighbour_bill_id));
        }
        binding.textViewTrackNumber.setText(FormActivity.examinerDuties.getTrackNumber());
        binding.editTextShenasname.setText(FormActivity.examinerDuties.getShenasname());//TODO
    }

    @Override
    public void onAttach(@NotNull Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onResume() {
        super.onResume();
        initializeField();
    }
}
