package com.leon.estimate.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;

import com.leon.estimate.Enums.DialogType;
import com.leon.estimate.Enums.ProgressType;
import com.leon.estimate.Enums.SharedReferenceKeys;
import com.leon.estimate.Enums.SharedReferenceNames;
import com.leon.estimate.Infrastructure.IAbfaService;
import com.leon.estimate.Infrastructure.ICallback;
import com.leon.estimate.Infrastructure.ICallbackError;
import com.leon.estimate.Infrastructure.ICallbackIncomplete;
import com.leon.estimate.R;
import com.leon.estimate.Tables.AddDocument;
import com.leon.estimate.Utils.Constants;
import com.leon.estimate.Utils.CustomErrorHandlingNew;
import com.leon.estimate.Utils.HttpClientWrapper;
import com.leon.estimate.Utils.NetworkHelper;
import com.leon.estimate.Utils.SharedPreferenceManager;
import com.leon.estimate.Utils.custom_dialogue.CustomDialog;
import com.leon.estimate.databinding.AddDocumentFragmentBinding;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

public class AddDocumentFragment extends DialogFragment {
    AddDocumentFragmentBinding binding;
    SharedPreferenceManager sharedPreferenceManager;

    public AddDocumentFragment() {
    }

    public static AddDocumentFragment newInstance() {
        return new AddDocumentFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = AddDocumentFragmentBinding.inflate(inflater, container, false);
        initialize();
        return binding.getRoot();
    }

    void initialize() {
        binding.editTextAddress.setText(Constants.examinerDuties.getAddress());
        binding.editTextFamily.setText(Constants.examinerDuties.getSureName());
        binding.editTextName.setText(Constants.examinerDuties.getFirstName());
        binding.editTextTrackNumber.setText(Constants.examinerDuties.getTrackNumber());
        sharedPreferenceManager = new SharedPreferenceManager(getContext(), SharedReferenceNames.ACCOUNT.getValue());
        setButtonSendClickListener();
    }

    void setButtonSendClickListener() {
        binding.buttonSend.setOnClickListener(v -> {
            Retrofit retrofit = NetworkHelper.getInstance("");
            IAbfaService iAbfaService = retrofit.create(IAbfaService.class);
            Call<AddDocument> call = iAbfaService.addDocument(
                    sharedPreferenceManager.getStringData(SharedReferenceKeys.TOKEN_FOR_FILE.getValue()),
                    new AddDocument(Constants.examinerDuties.getTrackNumber(),
                            Constants.examinerDuties.getFirstName(),
                            Constants.examinerDuties.getSureName(),
                            Constants.examinerDuties.getAddress(),
                            Constants.examinerDuties.getZoneId()));

            HttpClientWrapper.callHttpAsync(call, ProgressType.SHOW.getValue(), getContext(),
                    new addDocument(), new addDocumentIncomplete(), new GetError());
        });
    }

    @Override
    public void onResume() {
        WindowManager.LayoutParams params = getDialog().getWindow().getAttributes();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        getDialog().getWindow().setAttributes(params);
        super.onResume();
    }

    class addDocument implements ICallback<AddDocument> {
        @Override
        public void execute(AddDocument responseBody) {
            if (responseBody.isSuccess()) {
                Toast.makeText(getActivity(),
                        getActivity().getString(R.string.add_successful), Toast.LENGTH_LONG).show();
                dismiss();
            } else {
                Log.e("error", responseBody.getError());
                new CustomDialog(DialogType.Yellow, getActivity(),
                        getActivity().getString(R.string.error_add_document).concat("\n")
                                .concat(responseBody.getError()),
                        getActivity().getString(R.string.dear_user),
                        getActivity().getString(R.string.add_document),
                        getActivity().getString(R.string.accepted));
            }
        }
    }

    class addDocumentIncomplete implements ICallbackIncomplete<AddDocument> {
        @Override
        public void executeIncomplete(Response<AddDocument> response) {
            CustomErrorHandlingNew customErrorHandlingNew = new CustomErrorHandlingNew(getActivity());
            String error = customErrorHandlingNew.getErrorMessageDefault(response);
            new CustomDialog(DialogType.Yellow, getActivity(), error,
                    getActivity().getString(R.string.dear_user),
                    getActivity().getString(R.string.add_document),
                    getActivity().getString(R.string.accepted));
        }
    }

    class GetError implements ICallbackError {
        @Override
        public void executeError(Throwable t) {
            Log.e("Error", Objects.requireNonNull(t.getMessage()));
            CustomErrorHandlingNew customErrorHandlingNew = new CustomErrorHandlingNew(getActivity());
            String error = customErrorHandlingNew.getErrorMessageTotal(t);
            new CustomDialog(DialogType.YellowRedirect, getActivity(), error,
                    getActivity().getString(R.string.dear_user),
                    getActivity().getString(R.string.add_document),
                    getActivity().getString(R.string.accepted));
        }
    }
}