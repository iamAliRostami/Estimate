package com.leon.estimate.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.fragment.app.DialogFragment;

import com.leon.estimate.Enums.BundleEnum;
import com.leon.estimate.R;
import com.leon.estimate.activities.DocumentActivity;
import com.leon.estimate.databinding.EnterBillIdFragmentBinding;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class EnterBillIdFragment extends DialogFragment {
    EnterBillIdFragmentBinding binding;

    public EnterBillIdFragment() {

    }

    public static EnterBillIdFragment newInstance() {
        EnterBillIdFragment fragment = new EnterBillIdFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = EnterBillIdFragmentBinding.inflate(inflater, container, false);
        initialize();
        return binding.getRoot();
    }

    void initialize() {
        binding.buttonSearch.setOnClickListener(v -> {
            String s = binding.editTextSearch.getText().toString();
            if (s.length() < 6) {
                View focusView;
                binding.editTextSearch.setError(getString(R.string.error_format));
                focusView = binding.editTextSearch;
                focusView.requestFocus();
            } else {
                Intent intent = new Intent(getContext(), DocumentActivity.class);
                intent.putExtra(BundleEnum.BILL_ID.getValue(), s);
                intent.putExtra(BundleEnum.NEW_ENSHEAB.getValue(), false);
                startActivity(intent);
                dismiss();
            }
        });
    }

    @Override
    public void onResume() {
        WindowManager.LayoutParams params = Objects.requireNonNull(
                Objects.requireNonNull(getDialog()).getWindow()).getAttributes();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        Objects.requireNonNull(getDialog().getWindow()).setAttributes(params);
        super.onResume();
    }
}