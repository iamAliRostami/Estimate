package com.leon.estimate.activities;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import com.google.gson.Gson;
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
import com.leon.estimate.Tables.DaoExaminerDuties;
import com.leon.estimate.Tables.DaoKarbariDictionary;
import com.leon.estimate.Tables.DaoNoeVagozariDictionary;
import com.leon.estimate.Tables.DaoQotrEnsheabDictionary;
import com.leon.estimate.Tables.DaoResultDictionary;
import com.leon.estimate.Tables.DaoServiceDictionary;
import com.leon.estimate.Tables.DaoTaxfifDictionary;
import com.leon.estimate.Tables.ExaminerDuties;
import com.leon.estimate.Tables.Input;
import com.leon.estimate.Tables.MyDatabase;
import com.leon.estimate.Tables.Request;
import com.leon.estimate.Utils.CustomDialog;
import com.leon.estimate.Utils.CustomErrorHandlingNew;
import com.leon.estimate.Utils.HttpClientWrapper;
import com.leon.estimate.Utils.NetworkHelper;
import com.leon.estimate.Utils.SharedPreferenceManager;
import com.leon.estimate.Utils.SimpleMessage;
import com.leon.estimate.databinding.MotherChildActivityBinding;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MotherChildActivity extends AppCompatActivity {
    MotherChildActivityBinding binding;
    boolean isNew = true;
    MyDatabase dataBase;
    Context context;
    String billId, nationNumber, mobile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        binding = MotherChildActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        context = this;
        initialize();
    }

    void initialize() {
        dataBase = Room.databaseBuilder(context, MyDatabase.class, MyApplication.getDBNAME())
                .allowMainThreadQueries().build();
        setOnRadioGroupClickListener();
        setOnButtonClickListener();
    }

    void setOnButtonClickListener() {
        binding.buttonSendRequest.setOnClickListener(v -> {
            boolean cancel = false;
            billId = binding.editTextBillId.getText().toString();
            nationNumber = binding.editTextNationNumber.getText().toString();
            mobile = binding.editTextMobile.getText().toString();
            if (billId.length() < 6) {
                View focusView;
                binding.editTextBillId.setError(getString(R.string.error_format));
                focusView = binding.editTextBillId;
                focusView.requestFocus();
                cancel = true;
            }
            if (!cancel && mobile.length() < 11) {
                View focusView;
                binding.editTextNationNumber.setError(getString(R.string.error_format));
                focusView = binding.editTextMobile;
                focusView.requestFocus();
                cancel = true;
            }
            if (!cancel && isNew) {
                if ((checkIsNoEmpty(binding.editTextAddress) ||
                        checkIsNoEmpty(binding.editTextFamily) ||
                        checkIsNoEmpty(binding.editTextName))) {
                    cancel = true;
                }
                if (!cancel && nationNumber.length() < 10) {
                    View focusView;
                    binding.editTextNationNumber.setError(getString(R.string.error_format));
                    focusView = binding.editTextNationNumber;
                    focusView.requestFocus();
                    cancel = true;
                }
            }
            if (!cancel) {
                if (isNew)
                    sendNewRequest();
                else
                    sendAfterSaleRequest();
            }
        });
    }

    void setOnRadioGroupClickListener() {
        binding.radioGroupRequestType.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radioButtonNew) {
                isNew = true;
                binding.linearLayoutName.setVisibility(View.VISIBLE);
                binding.linearLayoutNation.setVisibility(View.VISIBLE);
            } else {
                isNew = false;
                binding.linearLayoutName.setVisibility(View.GONE);
                binding.linearLayoutNation.setVisibility(View.GONE);
            }
        });
    }

    void sendNewRequest() {
        SharedPreferenceManager sharedPreferenceManager = new SharedPreferenceManager(
                getApplicationContext(), SharedReferenceNames.ACCOUNT.getValue());
        String token = sharedPreferenceManager.getStringData(SharedReferenceKeys.TOKEN.getValue());
        Retrofit retrofit = NetworkHelper.getInstance(token);
        final IAbfaService sendRequest = retrofit.create(IAbfaService.class);
        ArrayList<Integer> selectedServices = new ArrayList<>(Arrays.asList(1, 2));
        Call<SimpleMessage> call = sendRequest.sendRequestNew(new Request(billId, selectedServices,
                binding.editTextName.getText().toString(),
                binding.editTextFamily.getText().toString(), mobile, nationNumber,
                binding.editTextAddress.getText().toString()));
        HttpClientWrapper.callHttpAsync(call, ProgressType.SHOW.getValue(), context,
                new SendRequest(), new SendRequestIncomplete(), new GetError());
    }

    void sendAfterSaleRequest() {
        SharedPreferenceManager sharedPreferenceManager = new SharedPreferenceManager(
                getApplicationContext(), SharedReferenceNames.ACCOUNT.getValue());
        String token = sharedPreferenceManager.getStringData(SharedReferenceKeys.TOKEN.getValue());
        Retrofit retrofit = NetworkHelper.getInstance(token);
        final IAbfaService sendRequest = retrofit.create(IAbfaService.class);
        ArrayList<Integer> selectedServices = new ArrayList<>();
        selectedServices.add(7);
        Call<SimpleMessage> call = sendRequest.sendRequestAfterSale(new Request(selectedServices, billId, mobile));
        HttpClientWrapper.callHttpAsync(call, ProgressType.SHOW.getValue(), context,
                new SendRequest(), new SendRequestIncomplete(), new GetError());
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

    void download() {
        SharedPreferenceManager sharedPreferenceManager = new SharedPreferenceManager(
                getApplicationContext(), SharedReferenceNames.ACCOUNT.getValue());
        String token = sharedPreferenceManager.getStringData(SharedReferenceKeys.TOKEN.getValue());
        Retrofit retrofit = NetworkHelper.getInstance(token);
        final IAbfaService getKardex = retrofit.create(IAbfaService.class);
        Call<Input> call = getKardex.getMyWorks();
        HttpClientWrapper.callHttpAsync(call, ProgressType.SHOW.getValue(), context,
                new Download(), new DownloadIncomplete(), new GetError());
    }

    class GetError implements ICallbackError {
        @Override
        public void executeError(Throwable t) {
            CustomErrorHandlingNew customErrorHandlingNew = new CustomErrorHandlingNew(context);
            String error = customErrorHandlingNew.getErrorMessageTotal(t);
            new CustomDialog(DialogType.YellowRedirect, MotherChildActivity.this, error,
                    MotherChildActivity.this.getString(R.string.dear_user),
                    MotherChildActivity.this.getString(R.string.login),
                    MotherChildActivity.this.getString(R.string.accepted));
        }
    }

    class SendRequest implements ICallback<SimpleMessage> {
        @Override
        public void execute(SimpleMessage simpleMessage) {
            binding.editTextAddress.setText("");
            binding.editTextFamily.setText("");
            binding.editTextName.setText("");
            binding.editTextMobile.setText("");
            binding.editTextNationNumber.setText("");
            binding.editTextBillId.setText("");
            new CustomDialog(DialogType.Green, context, simpleMessage.getMessage(),
                    getString(R.string.dear_user), getString(R.string.request),
                    getString(R.string.accepted));
            download();
        }
    }

    class SendRequestIncomplete implements ICallbackIncomplete<SimpleMessage> {

        @Override
        public void executeIncomplete(Response<SimpleMessage> response) {
            Log.e("error", String.valueOf(response));
            CustomErrorHandlingNew customErrorHandlingNew = new CustomErrorHandlingNew(context);
            String error = customErrorHandlingNew.getErrorMessageDefault(response);
            if (response.code() == 400) {
                if (response.errorBody() != null) {
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        error = jObjError.getString("message");
                    } catch (Exception e) {
                        Log.e("error", e.toString());
                    }
                }
            }
            new CustomDialog(DialogType.Yellow, context, error,
                    getString(R.string.dear_user), getString(R.string.request),
                    getString(R.string.accepted));
        }
    }

    List<ExaminerDuties> prepareExaminerDuties(List<ExaminerDuties> examinerDutiesList) {
        for (int i = 0; i < examinerDutiesList.size(); i++) {
            Gson gson = new Gson();
            examinerDutiesList.get(i).setRequestDictionaryString(
                    gson.toJson(examinerDutiesList.get(i).getRequestDictionary()));
            if (examinerDutiesList.get(i).getZoneId() == null ||
                    examinerDutiesList.get(i).getZoneId().equals("0")) {
                examinerDutiesList.remove(i);
                i--;
            }
        }
        return examinerDutiesList;
    }

    void removeExaminerDuties(List<ExaminerDuties> examinerDutiesList) {
        DaoExaminerDuties daoExaminerDuties = dataBase.daoExaminerDuties();
        List<ExaminerDuties> examinerDutiesListTemp = daoExaminerDuties.getExaminerDuties();
        for (int i = 0; i < examinerDutiesList.size(); i++) {
            examinerDutiesList.get(i).setTrackNumber(
                    examinerDutiesList.get(i).getTrackNumber().replace(".0", ""));
            examinerDutiesList.get(i).setRadif(
                    examinerDutiesList.get(i).getRadif().replace(".0", ""));
            ExaminerDuties examinerDuties = examinerDutiesList.get(i);
            for (int j = 0; j < examinerDutiesListTemp.size(); j++) {
                ExaminerDuties examinerDutiesTemp = examinerDutiesListTemp.get(j);
                if (examinerDuties.getTrackNumber().equals(examinerDutiesTemp.getTrackNumber())
                        || examinerDuties.getZoneId() == null
                        || examinerDuties.getZoneId().equals("0")) {
                    examinerDutiesList.remove(i);
                    j = examinerDutiesListTemp.size();
                    i--;
                }
            }
        }
        daoExaminerDuties.insertAll(examinerDutiesList);
    }

    class Download implements ICallback<Input> {
        @Override
        public void execute(Input input) {
            if (input != null) {
                removeExaminerDuties(prepareExaminerDuties(input.getExaminerDuties()));

                DaoNoeVagozariDictionary daoNoeVagozariDictionary = dataBase.daoNoeVagozariDictionary();
                daoNoeVagozariDictionary.insertAll(input.getNoeVagozariDictionary());

                DaoQotrEnsheabDictionary daoQotrEnsheabDictionary = dataBase.daoQotrEnsheabDictionary();
                daoQotrEnsheabDictionary.insertAll(input.getQotrEnsheabDictionary());

                DaoServiceDictionary daoServiceDictionary = dataBase.daoServiceDictionary();
                daoServiceDictionary.insertAll(input.getServiceDictionary());

                DaoTaxfifDictionary daoTaxfifDictionary = dataBase.daoTaxfifDictionary();
                daoTaxfifDictionary.insertAll(input.getTaxfifDictionary());

                DaoKarbariDictionary daoKarbariDictionary = dataBase.daoKarbariDictionary();
                daoKarbariDictionary.insertAll(input.getKarbariDictionary());

                DaoResultDictionary daoResultDictionary = dataBase.daoResultDictionary();
                daoResultDictionary.insertAll(input.getResultDictionary());
            }
        }
    }

    class DownloadIncomplete implements ICallbackIncomplete<Input> {
        @Override
        public void executeIncomplete(Response<Input> response) {
            Log.e("Download Incomplete", response.toString());
            new CustomDialog(DialogType.Yellow, context, "به صفحه اصلی رفته و بارگیری نمایید.",
                    getString(R.string.dear_user),
                    getString(R.string.request),
                    getString(R.string.accepted));
        }
    }
}