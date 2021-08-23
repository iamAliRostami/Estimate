package com.leon.estimate.fragments;


import static com.leon.estimate.Utils.Constants.requestDictionaries;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.leon.estimate.Enums.BundleEnum;
import com.leon.estimate.R;
import com.leon.estimate.Tables.CalculationUserInput;
import com.leon.estimate.Tables.ExaminerDuties;
import com.leon.estimate.activities.FormActivity;
import com.leon.estimate.adapters.CheckBoxAdapter;
import com.leon.estimate.databinding.ServicesFragmentBinding;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class ServicesFragment extends Fragment {
    private static final String ARG_PARAM2 = "param2";
    @SuppressLint("StaticFieldLeak")
    static CheckBoxAdapter checkBoxAdapter;
    ServicesFragmentBinding binding;
    private Context context;

    public ServicesFragment() {

    }

    public static ServicesFragment newInstance(ExaminerDuties examinerDuties, String param2) {
        ServicesFragment fragment = new ServicesFragment();
        Bundle args = new Bundle();
        Gson gson = new Gson();
        String json = gson.toJson(examinerDuties);
        args.putString(BundleEnum.REQUEST.getValue(), json);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public static CalculationUserInput prepareServices() {
        CalculationUserInput calculationUserInput = new CalculationUserInput();
        int counter = 0;
        for (int i = 0; i < checkBoxAdapter.requestDictionaries.size(); i++) {
            if (checkBoxAdapter.requestDictionaries.get(i).isSelected())
                counter = counter + 1;
            requestDictionaries.get(i).setSelected(
                    checkBoxAdapter.requestDictionaries.get(i).isSelected());
        }
        if (counter > 0) {
            calculationUserInput.selectedServicesObject = requestDictionaries;
            Gson gson = new GsonBuilder().create();
            calculationUserInput.selectedServicesString = gson.toJson(requestDictionaries);

            return calculationUserInput;
        } else return null;
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = ServicesFragmentBinding.inflate(inflater, container, false);
        initialize();
        return binding.getRoot();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        ((FormActivity) Objects.requireNonNull(getActivity())).setActionBarTitle(
                context.getString(R.string.app_name).concat(" / ").concat("صفحه دوم"));
    }

    private void initialize() {
        initializeServicesCheckBox();
    }

    @SuppressLint("NewApi")
    private void initializeServicesCheckBox() {
        checkBoxAdapter = new CheckBoxAdapter(context, requestDictionaries);
        binding.gridView.setAdapter(checkBoxAdapter);
    }

    @Override
    public void onAttach(@NotNull Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
