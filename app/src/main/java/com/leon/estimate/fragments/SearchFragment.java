package com.leon.estimate.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.fragment.app.DialogFragment;

import com.leon.estimate.Enums.BundleEnum;
import com.leon.estimate.Enums.SharedReferenceNames;
import com.leon.estimate.MyApplication;
import com.leon.estimate.Utils.SharedPreferenceManager;
import com.leon.estimate.databinding.SearchFragmentBinding;

import org.jetbrains.annotations.NotNull;

public class SearchFragment extends DialogFragment {
    SearchFragmentBinding binding;
    SharedPreferenceManager sharedPreferenceManager;

    public SearchFragment() {
    }

    public static SearchFragment newInstance(String param1) {
        SearchFragment fragment = new SearchFragment();
        Bundle args = new Bundle();
        args.putString(BundleEnum.TRACK_NUMBER.getValue(), param1);
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
        binding = SearchFragmentBinding.inflate(inflater, container, false);
        initialize();
        return binding.getRoot();
    }

    void initialize() {
        sharedPreferenceManager = new SharedPreferenceManager(MyApplication.getContext(),
                SharedReferenceNames.ACCOUNT.getValue());
        binding.buttonSearch.setOnClickListener(v -> {

        });
        binding.buttonClear.setOnClickListener(v -> dismiss());
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