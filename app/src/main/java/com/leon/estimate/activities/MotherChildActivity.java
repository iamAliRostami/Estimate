package com.leon.estimate.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.leon.estimate.R;
import com.leon.estimate.databinding.MotherChildActivityBinding;

public class MotherChildActivity extends AppCompatActivity {
    MotherChildActivityBinding binding;
    boolean isNew = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = MotherChildActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initialize();
    }

    void initialize() {
        setOnRadioGroupClickListener();
        setOnButtonClickListener();
    }

    void setOnButtonClickListener() {
        binding.buttonSendRequest.setOnClickListener(v -> {
            boolean cancel = false;
            String billId = binding.editTextBillId.getText().toString();
            String nationNumber = binding.editTextNationNumber.getText().toString();
            if (billId.length() < 6) {
                View focusView;
                binding.editTextBillId.setError(getString(R.string.error_format));
                focusView = binding.editTextBillId;
                focusView.requestFocus();
                cancel = true;
            }
            if (!cancel && nationNumber.length() < 10) {
                View focusView;
                binding.editTextNationNumber.setError(getString(R.string.error_format));
                focusView = binding.editTextNationNumber;
                focusView.requestFocus();
                cancel = true;
            }
            if (!cancel && !isNew)
                if ((checkIsNoEmpty(binding.editTextAddress) ||
                        checkIsNoEmpty(binding.editTextNationNumber) ||
                        checkIsNoEmpty(binding.editTextFamily) ||
                        checkIsNoEmpty(binding.editTextName))) {
                    cancel = true;

                }
            if (!cancel) {
                if (isNew)
                    sendNewRequest();
                else senAfterSaleRequest();
            }
        });
    }

    void sendNewRequest() {
    }

    void senAfterSaleRequest() {
    }

    void setOnRadioGroupClickListener() {
        binding.radioGroupRequestType.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radioButtonNew) {
                isNew = true;
                binding.linearLayoutName.setVisibility(View.GONE);
                binding.linearLayoutNation.setVisibility(View.GONE);
            } else {
                isNew = false;
                binding.linearLayoutName.setVisibility(View.VISIBLE);
                binding.linearLayoutNation.setVisibility(View.VISIBLE);
            }
        });
    }

    boolean checkIsNoEmpty(EditText editText) {
        View focusView;
        if (editText.getText().toString().length() < 1) {
            editText.setError(getString(R.string.error_empty));
            focusView = editText;
            focusView.requestFocus();
            return true;
        }
        return false;
    }
}