package com.leon.estimate.activities;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.room.Room;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.leon.estimate.Enums.BundleEnum;
import com.leon.estimate.Enums.ErrorHandlerType;
import com.leon.estimate.Enums.SharedReferenceKeys;
import com.leon.estimate.Enums.SharedReferenceNames;
import com.leon.estimate.Infrastructure.IAbfaService;
import com.leon.estimate.Infrastructure.ICallback;
import com.leon.estimate.R;
import com.leon.estimate.Tables.CalculationUserInput;
import com.leon.estimate.Tables.CalculationUserInputSend;
import com.leon.estimate.Tables.DaoCalculationUserInput;
import com.leon.estimate.Tables.DaoExaminerDuties;
import com.leon.estimate.Tables.ExaminerDuties;
import com.leon.estimate.Tables.MyDatabase;
import com.leon.estimate.Tables.RequestDictionary;
import com.leon.estimate.Utils.HttpClientWrapperOld;
import com.leon.estimate.Utils.NetworkHelper;
import com.leon.estimate.Utils.SharedPreferenceManager;
import com.leon.estimate.Utils.SimpleMessage;
import com.leon.estimate.adapters.MyPagerAdapter;
import com.leon.estimate.databinding.FormActivityBinding;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Retrofit;

public class FormActivity extends AppCompatActivity {
    FragmentPagerAdapter adapterViewPager;
    Context context;
    String trackNumber, json;
    MyDatabase dataBase;
    List<RequestDictionary> requestDictionaries;
    ExaminerDuties examinerDuties;
    DaoExaminerDuties daoExaminerDuties;
    CalculationUserInput calculationUserInput, calculationUserInputTemp;
    FormActivityBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        binding = FormActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        context = this;
        if (getIntent().getExtras() != null) {
            trackNumber = getIntent().getExtras().getString(BundleEnum.TRACK_NUMBER.getValue());

            new SerializeJson().execute(getIntent());
        }

        initialize();
    }

    @SuppressLint("ClickableViewAccessibility")
    void initialize() {
        final ProgressDialog dialog = new ProgressDialog(context);
        dialog.setMessage(context.getString(R.string.loading_getting_info));
        dialog.setTitle(context.getString(R.string.loading_connecting));
        dialog.setCancelable(false);
        dialog.show();

        calculationUserInput = new CalculationUserInput();
        calculationUserInputTemp = new CalculationUserInput();
        dataBase = Room.databaseBuilder(context, MyDatabase.class, "MyDatabase")
                .allowMainThreadQueries().build();
        daoExaminerDuties = dataBase.daoExaminerDuties();
        examinerDuties = daoExaminerDuties.unreadExaminerDutiesByTrackNumber(trackNumber);
        adapterViewPager = new MyPagerAdapter(getSupportFragmentManager(), context, examinerDuties);
        binding.viewPager.setAdapter(adapterViewPager);
        binding.viewPager.setOnTouchListener((v, event) -> true);
        dialog.dismiss();
    }

    public void nextPage(Bitmap bitmap, CalculationUserInput calculationUserInput) {
        if (binding.viewPager.getCurrentItem() == 0) {
            this.calculationUserInput = calculationUserInput;
            binding.viewPager.setCurrentItem(binding.viewPager.getCurrentItem() + 1);
        } else {
            this.calculationUserInputTemp = calculationUserInput;
            prepareToSend();
            Intent intent = new Intent(getApplicationContext(), DocumentActivity.class);
            intent.putExtra(BundleEnum.IMAGE_BITMAP.getValue(), convertBitmapToByte(bitmap));
            context.startActivity(intent);
            finish();
        }
    }

    private byte[] convertBitmapToByte(Bitmap bitmap) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
        return bos.toByteArray();
    }


    void prepareToSend() {
        fillCalculationUserInput();
        DaoCalculationUserInput daoCalculationUserInput = dataBase.daoCalculationUserInput();
        daoCalculationUserInput.deleteByTrackNumber(trackNumber);
        daoCalculationUserInput.insertCalculationUserInput(calculationUserInput);
        updateExamination();

        SharedPreferenceManager sharedPreferenceManager = new SharedPreferenceManager(
                getApplicationContext(), SharedReferenceNames.ACCOUNT.getValue());
        String token = sharedPreferenceManager.getStringData(SharedReferenceKeys.TOKEN.getValue());
        Retrofit retrofit = NetworkHelper.getInstance(false, token);
        final IAbfaService abfaService = retrofit.create(IAbfaService.class);
        SendCalculation sendCalculation = new SendCalculation();
        ArrayList<CalculationUserInputSend> calculationUserInputSends = new ArrayList<>();
        calculationUserInputSends.add(new CalculationUserInputSend(calculationUserInput));
        Call<SimpleMessage> call = abfaService.setExaminationInfo(calculationUserInputSends);
        HttpClientWrapperOld.callHttpAsync(call, sendCalculation, ErrorHandlerType.ordinary);
    }

    void updateExamination() {
        DaoExaminerDuties daoExaminerDuties = dataBase.daoExaminerDuties();
        daoExaminerDuties.updateExamination(true, trackNumber);
        daoExaminerDuties.insert(examinerDuties.updateExaminerDuties(calculationUserInput));
    }

    void fillCalculationUserInput() {
        //TODO SELECTED SERVICE
//        calculationUserInput.nationalId = calculationUserInputTemp.nationalId;
//        calculationUserInput.firstName = calculationUserInputTemp.firstName.trim();
//        calculationUserInput.sureName = calculationUserInputTemp.sureName.trim();
//        calculationUserInput.fatherName = calculationUserInputTemp.fatherName.trim();
//        calculationUserInput.postalCode = calculationUserInputTemp.postalCode;
//        calculationUserInput.radif = calculationUserInputTemp.radif;
//        calculationUserInput.phoneNumber = calculationUserInputTemp.phoneNumber;
//        calculationUserInput.mobile = calculationUserInputTemp.mobile;
//        calculationUserInput.address = calculationUserInputTemp.address;
//        calculationUserInput.description = calculationUserInputTemp.description;
//
//        calculationUserInput.trackingId = examinerDuties.getTrackingId();
//        calculationUserInput.requestType = Integer.parseInt(examinerDuties.getRequestType());
//        calculationUserInput.parNumber = examinerDuties.getParNumber();
//        calculationUserInput.billId = examinerDuties.getBillId();
//        calculationUserInput.neighbourBillId = examinerDuties.getNeighbourBillId();
//        calculationUserInput.notificationMobile = examinerDuties.getNotificationMobile();
//        calculationUserInput.nationalId = examinerDuties.getNationalId();
//        calculationUserInput.identityCode = examinerDuties.getIdentityCode();
//        calculationUserInput.trackNumber = examinerDuties.getTrackNumber();
//        calculationUserInput.trackingId = examinerDuties.getTrackingId();
//        calculationUserInput.setSent(true);
    }

    class SendCalculation implements ICallback<SimpleMessage> {
        @Override
        public void execute(SimpleMessage simpleMessage) {
            MyDatabase dataBase = Room.databaseBuilder(context, MyDatabase.class, "MyDatabase")
                    .allowMainThreadQueries().build();
            DaoCalculationUserInput daoCalculationUserInput = dataBase.daoCalculationUserInput();
            daoCalculationUserInput.updateCalculationUserInput(true, trackNumber);
        }
    }

    public void setActionBarTitle(String title) {
        Objects.requireNonNull(getSupportActionBar()).setTitle(title);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @SuppressLint("StaticFieldLeak")
    class SerializeJson extends AsyncTask<Intent, String, String> {
        @Override
        protected String doInBackground(Intent... intents) {
            json = getIntent().getExtras().getString(BundleEnum.SERVICES.getValue());
            Gson gson = new GsonBuilder().create();
            requestDictionaries = Arrays.asList(gson.fromJson(json, RequestDictionary[].class));
            Log.e("data", json);
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }

    }
}
