package com.leon.estimate.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.fragment.app.DialogFragment;

import com.leon.estimate.activities.ListActivity;
import com.leon.estimate.databinding.SearchFragmentBinding;

import org.jetbrains.annotations.NotNull;

public class SearchFragment extends DialogFragment {
    SearchFragmentBinding binding;

    public SearchFragment() {
    }

    public static SearchFragment newInstance() {
        return new SearchFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = SearchFragmentBinding.inflate(inflater, container, false);
        initialize();
        return binding.getRoot();
    }

    void initialize() {
        binding.buttonSearch.setOnClickListener(v -> {
            if (ListActivity.customAdapter != null) {
                ListActivity.customAdapter.filter(binding.editTextBillId.getText().toString(),
                        binding.editTextTrackNumber.getText().toString(),
                        binding.editTextName.getText().toString(),
                        binding.editTextFamily.getText().toString(),
                        binding.editTextNationNumber.getText().toString(),
                        binding.editTextMobile.getText().toString()
                );
            }
            dismiss();
        });
    }

    @Override
    public void onResume() {
        WindowManager.LayoutParams params = getDialog().getWindow().getAttributes();
        params.width = ViewGroup.LayoutParams.WRAP_CONTENT;
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        getDialog().getWindow().setAttributes(params);
        super.onResume();
    }
}