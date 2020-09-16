package com.leon.estimate.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;
import androidx.room.Room;

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
import com.leon.estimate.Tables.CalculationUserInputSend;
import com.leon.estimate.Tables.DaoCalculationUserInput;
import com.leon.estimate.Tables.DaoExaminerDuties;
import com.leon.estimate.Tables.MyDatabase;
import com.leon.estimate.Utils.CustomDialog;
import com.leon.estimate.Utils.CustomErrorHandlingNew;
import com.leon.estimate.Utils.HttpClientWrapper;
import com.leon.estimate.Utils.NetworkHelper;
import com.leon.estimate.Utils.SharedPreferenceManager;
import com.leon.estimate.Utils.SimpleMessage;
import com.leon.estimate.databinding.SignFragmentBinding;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

import static com.leon.estimate.activities.FormActivity.calculationUserInput;

public class SignFragment extends DialogFragment {
    SignFragmentBinding binding;
    MyDatabase dataBase;
    SharedPreferenceManager sharedPreferenceManager;
    private String trackNumber;

    public SignFragment() {
    }

    public static SignFragment newInstance(String param1) {
        SignFragment fragment = new SignFragment();
        Bundle args = new Bundle();
        args.putString(BundleEnum.TRACK_NUMBER.getValue(), param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            trackNumber = getArguments().getString(BundleEnum.TRACK_NUMBER.getValue());
        }
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = SignFragmentBinding.inflate(inflater, container, false);
        initialize();
        return binding.getRoot();
    }

    void initialize() {
        dataBase = Room.databaseBuilder(MyApplication.getContext(),
                MyDatabase.class, MyApplication.getDBNAME())
                .allowMainThreadQueries().build();

        sharedPreferenceManager = new SharedPreferenceManager(MyApplication.getContext(),
                SharedReferenceNames.ACCOUNT.getValue());
        binding.buttonAccepted.setOnClickListener(v -> {
            String token = sharedPreferenceManager.getStringData(SharedReferenceKeys.TOKEN.getValue());
            Retrofit retrofit = NetworkHelper.getInstance(true, token);
            final IAbfaService abfaService = retrofit.create(IAbfaService.class);

            DaoExaminerDuties daoExaminerDuties = dataBase.daoExaminerDuties();
            daoExaminerDuties.updateExamination(true, trackNumber);

            SendCalculation sendCalculation = new SendCalculation();
            SendCalculationIncomplete incomplete = new SendCalculationIncomplete();
            GetError error = new GetError();

            ArrayList<CalculationUserInputSend> calculationUserInputSends = new ArrayList<>();
            calculationUserInputSends.add(new CalculationUserInputSend(calculationUserInput));
            Call<SimpleMessage> call = abfaService.setExaminationInfo(calculationUserInputSends);
            HttpClientWrapper.callHttpAsync(call, ProgressType.NOT_SHOW.getValue(), getActivity(),
                    sendCalculation, incomplete, error);
        });
        binding.buttonDenial.setOnClickListener(v -> dismiss());
    }

    @Override
    public void onResume() {
        WindowManager.LayoutParams params = Objects.requireNonNull(
                Objects.requireNonNull(getDialog()).getWindow()).getAttributes();
        params.width = ViewGroup.LayoutParams.WRAP_CONTENT;
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        Objects.requireNonNull(getDialog().getWindow()).setAttributes(params);
        super.onResume();
    }

    class SendCalculation implements ICallback<SimpleMessage> {
        @Override
        public void execute(SimpleMessage simpleMessage) {
            DaoCalculationUserInput daoCalculationUserInput = dataBase.daoCalculationUserInput();
            daoCalculationUserInput.updateCalculationUserInput(true, trackNumber);

        }
    }

    class SendCalculationIncomplete implements ICallbackIncomplete<SimpleMessage> {
        @Override
        public void executeIncomplete(Response<SimpleMessage> response) {
            CustomErrorHandlingNew customErrorHandlingNew = new CustomErrorHandlingNew(getActivity());
            String error = customErrorHandlingNew.getErrorMessageDefault(response);
            new CustomDialog(DialogType.Yellow, getActivity(), error,
                    MyApplication.getContext().getString(R.string.dear_user),
                    MyApplication.getContext().getString(R.string.login),
                    MyApplication.getContext().getString(R.string.accepted));
        }
    }

    class GetError implements ICallbackError {
        @Override
        public void executeError(Throwable t) {
            CustomErrorHandlingNew customErrorHandlingNew = new CustomErrorHandlingNew(MyApplication.getContext());
            String error = customErrorHandlingNew.getErrorMessageTotal(t);
            Toast.makeText(MyApplication.getContext(), error, Toast.LENGTH_LONG).show();
            ((Activity) MyApplication.getContext()).finish();
        }
    }
}