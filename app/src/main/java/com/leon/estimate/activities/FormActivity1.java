package com.leon.estimate.activities;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.room.Room;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.leon.estimate.Enums.BundleEnum;
import com.leon.estimate.Enums.ProgressType;
import com.leon.estimate.Enums.SharedReferenceKeys;
import com.leon.estimate.Enums.SharedReferenceNames;
import com.leon.estimate.Infrastructure.IAbfaService;
import com.leon.estimate.Infrastructure.ICallback;
import com.leon.estimate.Infrastructure.ICallbackError;
import com.leon.estimate.Infrastructure.ICallbackIncomplete;
import com.leon.estimate.MyApplication;
import com.leon.estimate.R;
import com.leon.estimate.Tables.CalculationUserInput;
import com.leon.estimate.Tables.CalculationUserInputSend;
import com.leon.estimate.Tables.DaoCalculationUserInput;
import com.leon.estimate.Tables.DaoExaminerDuties;
import com.leon.estimate.Tables.ExaminerDuties;
import com.leon.estimate.Tables.MyDatabase;
import com.leon.estimate.Tables.RequestDictionary;
import com.leon.estimate.Utils.CustomErrorHandlingNew;
import com.leon.estimate.Utils.HttpClientWrapper;
import com.leon.estimate.Utils.NetworkHelper;
import com.leon.estimate.Utils.SharedPreferenceManager;
import com.leon.estimate.Utils.SimpleMessage;
import com.leon.estimate.databinding.FormActivity1Binding;
import com.leon.estimate.fragments.FormFragment;
import com.leon.estimate.fragments.MapFragment;
import com.leon.estimate.fragments.PersonalFragment;
import com.leon.estimate.fragments.ServicesFragment;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

public class FormActivity1 extends AppCompatActivity {
    public static List<RequestDictionary> requestDictionaries;
    public static ExaminerDuties examinerDuties;
    public static CalculationUserInput calculationUserInput, calculationUserInputTemp;
    Context context;
    String trackNumber, json;
    int pageNumber = 1;
    MyDatabase dataBase;
    DaoExaminerDuties daoExaminerDuties;
    FormActivity1Binding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        binding = FormActivity1Binding.inflate(getLayoutInflater());
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
        calculationUserInput = new CalculationUserInput();
        calculationUserInputTemp = new CalculationUserInput();
        new GetDBData().execute();
        setOnButtonClickListener();
    }

    void setOnButtonClickListener() {
        binding.buttonNext.setOnClickListener(view -> {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            FragmentManager fragmentManager = getSupportFragmentManager();
            switch (pageNumber) {
                case 1:
                    PersonalFragment personalFragment = (PersonalFragment)
                            fragmentManager.findFragmentById(R.id.fragment);
                    if (personalFragment != null) {
                        calculationUserInputTemp = personalFragment.setOnButtonNextClickListener();
                        if (calculationUserInputTemp != null) {
                            prepareFromPersonal();
                            fragmentTransaction = getSupportFragmentManager().beginTransaction();
                            fragmentTransaction.replace(R.id.fragment, new ServicesFragment());
                            fragmentTransaction.commit();
                            pageNumber = pageNumber + 1;
                        }
                    }
                    break;
                case 2:
                    calculationUserInputTemp = ServicesFragment.prepareServices();
                    if (calculationUserInputTemp != null) {
                        calculationUserInput.selectedServicesObject =
                                calculationUserInputTemp.selectedServicesObject;
                        calculationUserInput.selectedServicesString =
                                calculationUserInputTemp.selectedServicesString;
                        fragmentTransaction.replace(R.id.fragment, new FormFragment());
                        fragmentTransaction.commit();
                        pageNumber = pageNumber + 1;
                    } else
                        Toast.makeText(context, R.string.select_service, Toast.LENGTH_LONG).show();
                    break;
                case 3:
                    FormFragment formFragment = (FormFragment) fragmentManager.findFragmentById(R.id.fragment);
                    if (formFragment != null)
                        calculationUserInputTemp = formFragment.setOnButtonNextClickListener();
                    if (calculationUserInputTemp != null) {
                        prepareFromForm();
                        fragmentTransaction = getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.fragment, new MapFragment());
                        fragmentTransaction.commit();
                        pageNumber = pageNumber + 1;
                    }
                    break;
                case 4:
                    MapFragment mapFragment = (MapFragment) fragmentManager.findFragmentById(R.id.fragment);
                    Intent intent = new Intent(getApplicationContext(), DocumentActivity1.class);
                    if (mapFragment != null) {
                        intent.putExtra(BundleEnum.IMAGE_BITMAP.getValue(),
                                convertBitmapToByte(mapFragment.convertMapToBitmap()));
                    }
                    intent.putExtra(BundleEnum.TRACK_NUMBER.getValue(), trackNumber);
                    intent.putExtra(BundleEnum.BILL_ID.getValue(), examinerDuties.getBillId());
                    intent.putExtra(BundleEnum.NEW_ENSHEAB.getValue(), examinerDuties.isNewEnsheab());
                    prepareToSend();
                    startActivity(intent);
                    finish();
                    break;
            }
        });
        binding.buttonPrevious.setOnClickListener(view -> {
            FragmentTransaction fragmentTransaction;
            switch (pageNumber) {
                case 1:
                    finish();
                    break;
                case 2:
                    fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.fragment, new PersonalFragment());
                    fragmentTransaction.commit();
                    break;
                case 3:
                    fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.fragment, new ServicesFragment());
                    fragmentTransaction.commit();
                    break;
                case 4:
                    fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.fragment, new FormFragment());
                    fragmentTransaction.commit();
                    break;
            }
            pageNumber = pageNumber - 1;
        });
    }

    void prepareFromForm() {
        calculationUserInput.sifoon100 = calculationUserInputTemp.sifoon100;
        calculationUserInput.sifoon125 = calculationUserInputTemp.sifoon125;
        calculationUserInput.sifoon150 = calculationUserInputTemp.sifoon150;
        calculationUserInput.sifoon200 = calculationUserInputTemp.sifoon200;
        calculationUserInput.arse = calculationUserInputTemp.arse;
        calculationUserInput.aianKol = calculationUserInputTemp.aianKol;
        calculationUserInput.aianMaskooni = calculationUserInputTemp.aianMaskooni;
        calculationUserInput.aianTejari = calculationUserInputTemp.aianTejari;
        calculationUserInput.tedadMaskooni = calculationUserInputTemp.tedadMaskooni;
        calculationUserInput.tedadTejari = calculationUserInputTemp.tedadTejari;
        calculationUserInput.tedadSaier = calculationUserInputTemp.tedadSaier;
        calculationUserInput.arzeshMelk = calculationUserInputTemp.arzeshMelk;
        calculationUserInput.tedadTaxfif = calculationUserInputTemp.tedadTaxfif;
        calculationUserInput.zarfiatQarardadi = calculationUserInputTemp.zarfiatQarardadi;
        calculationUserInput.parNumber = calculationUserInputTemp.parNumber;
        calculationUserInput.karbariId = calculationUserInputTemp.karbariId;
        calculationUserInput.noeVagozariId = calculationUserInputTemp.noeVagozariId;
        calculationUserInput.qotrEnsheabId = calculationUserInputTemp.qotrEnsheabId;
        calculationUserInput.taxfifId = calculationUserInputTemp.taxfifId;
        calculationUserInput.adamTaxfifAb = calculationUserInputTemp.adamTaxfifAb;
        calculationUserInput.adamTaxfifFazelab = calculationUserInputTemp.adamTaxfifFazelab;
        calculationUserInput.ensheabQeireDaem = calculationUserInputTemp.ensheabQeireDaem;

        examinerDuties.setSifoon100(calculationUserInputTemp.sifoon100);
        examinerDuties.setSifoon125(calculationUserInputTemp.sifoon125);
        examinerDuties.setSifoon150(calculationUserInputTemp.sifoon150);
        examinerDuties.setSifoon200(calculationUserInputTemp.sifoon200);
        examinerDuties.setArse(calculationUserInputTemp.arse);
        examinerDuties.setAianMaskooni(calculationUserInputTemp.aianMaskooni);
        examinerDuties.setAianNonMaskooni(calculationUserInputTemp.aianTejari);
        examinerDuties.setAianKol(calculationUserInputTemp.aianKol);
        examinerDuties.setTedadMaskooni(calculationUserInputTemp.tedadMaskooni);
        examinerDuties.setTedadTejari(calculationUserInputTemp.tedadTejari);
        examinerDuties.setTedadSaier(calculationUserInputTemp.tedadSaier);
        examinerDuties.setTedadTaxfif(calculationUserInputTemp.tedadTaxfif);
        examinerDuties.setZarfiatQarardadi(calculationUserInputTemp.zarfiatQarardadi);
        examinerDuties.setArzeshMelk(calculationUserInputTemp.arzeshMelk);
        examinerDuties.setParNumber(calculationUserInputTemp.parNumber);

        examinerDuties.setKarbariId(calculationUserInputTemp.karbariId);
        examinerDuties.setQotrEnsheabId(calculationUserInputTemp.qotrEnsheabId);
        examinerDuties.setTaxfifId(calculationUserInputTemp.taxfifId);

        examinerDuties.setEnsheabQeirDaem(calculationUserInputTemp.ensheabQeireDaem);
    }

    void prepareFromPersonal() {
        calculationUserInput.nationalId = calculationUserInputTemp.nationalId;
        calculationUserInput.firstName = calculationUserInputTemp.firstName;
        calculationUserInput.sureName = calculationUserInputTemp.sureName;
        calculationUserInput.fatherName = calculationUserInputTemp.fatherName;
        calculationUserInput.postalCode = calculationUserInputTemp.postalCode;
        calculationUserInput.radif = calculationUserInputTemp.radif;
        calculationUserInput.phoneNumber = calculationUserInputTemp.phoneNumber;
        calculationUserInput.mobile = calculationUserInputTemp.mobile;
        calculationUserInput.address = calculationUserInputTemp.address;
        calculationUserInput.description = calculationUserInputTemp.description;

        examinerDuties.setNationalId(calculationUserInputTemp.nationalId);
        examinerDuties.setFirstName(calculationUserInputTemp.firstName);
        examinerDuties.setSureName(calculationUserInputTemp.sureName);
        examinerDuties.setNameAndFamily(calculationUserInputTemp.firstName.concat(" ")
                .concat(calculationUserInputTemp.sureName));
        examinerDuties.setFatherName(calculationUserInputTemp.fatherName);
        examinerDuties.setPostalCode(calculationUserInput.postalCode);
        examinerDuties.setPhoneNumber(calculationUserInput.phoneNumber);
        examinerDuties.setMobile(calculationUserInputTemp.mobile);//TODO 3 mobile
        examinerDuties.setAddress(calculationUserInputTemp.address);
        examinerDuties.setDescription(calculationUserInputTemp.description);
    }

    private byte[] convertBitmapToByte(Bitmap bitmap) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
        return bos.toByteArray();
    }

    void prepareToSend() {
        fillCalculationUserInput();
        updateCalculationUserInput();
        updateExamination();

        SharedPreferenceManager sharedPreferenceManager =
                new SharedPreferenceManager(getApplicationContext(),
                        SharedReferenceNames.ACCOUNT.getValue());
        String token = sharedPreferenceManager.getStringData(SharedReferenceKeys.TOKEN.getValue());
        Retrofit retrofit = NetworkHelper.getInstance(true, token);
        final IAbfaService abfaService = retrofit.create(IAbfaService.class);
        SendCalculation sendCalculation = new SendCalculation();
        SendCalculationIncomplete incomplete = new SendCalculationIncomplete();
        GetError error = new GetError();
        ArrayList<CalculationUserInputSend> calculationUserInputSends = new ArrayList<>();
        calculationUserInputSends.add(new CalculationUserInputSend(calculationUserInput));
        Call<SimpleMessage> call = abfaService.setExaminationInfo(calculationUserInputSends);
        HttpClientWrapper.callHttpAsync(call, ProgressType.NOT_SHOW.getValue(), this,
                sendCalculation, incomplete, error);
    }

    void updateCalculationUserInput() {
        DaoCalculationUserInput daoCalculationUserInput = dataBase.daoCalculationUserInput();
        daoCalculationUserInput.deleteByTrackNumber(trackNumber);
        daoCalculationUserInput.insertCalculationUserInput(calculationUserInput);
    }

    void updateExamination() {
        DaoExaminerDuties daoExaminerDuties = dataBase.daoExaminerDuties();
        daoExaminerDuties.updateExamination(true, trackNumber);
        daoExaminerDuties.insert(examinerDuties.updateExaminerDuties(calculationUserInput));
    }

    void fillCalculationUserInput() {
        calculationUserInput.trackingId = examinerDuties.getTrackingId();
        calculationUserInput.requestType = Integer.parseInt(examinerDuties.getRequestType());
        calculationUserInput.parNumber = examinerDuties.getParNumber();
        calculationUserInput.billId = examinerDuties.getBillId();
        calculationUserInput.neighbourBillId = examinerDuties.getNeighbourBillId();
        calculationUserInput.notificationMobile = examinerDuties.getNotificationMobile();
        calculationUserInput.identityCode = examinerDuties.getIdentityCode();
        calculationUserInput.trackNumber = examinerDuties.getTrackNumber();
        calculationUserInput.setSent(false);
    }

    public void setActionBarTitle(String title) {
        Objects.requireNonNull(getSupportActionBar()).setTitle(title);
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
        }
    }

    @SuppressLint("StaticFieldLeak")
    class SerializeJson extends AsyncTask<Intent, String, String> {
        ProgressDialog dialog;

        @Override
        protected String doInBackground(Intent... intents) {
            json = Objects.requireNonNull(getIntent().getExtras()).getString(BundleEnum.SERVICES.getValue());
            Gson gson = new GsonBuilder().create();
            requestDictionaries = Arrays.asList(gson.fromJson(json, RequestDictionary[].class));
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(context);
            dialog.setMessage(context.getString(R.string.loading_getting_info));
            dialog.setTitle(context.getString(R.string.loading_connecting));
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            dialog.dismiss();
        }

    }

    class GetError implements ICallbackError {
        @Override
        public void executeError(Throwable t) {
            CustomErrorHandlingNew customErrorHandlingNew = new CustomErrorHandlingNew(context);
            String error = customErrorHandlingNew.getErrorMessageTotal(t);
            Toast.makeText(FormActivity1.this, error, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @SuppressLint("StaticFieldLeak")
    class GetDBData extends AsyncTask<Integer, String, String> {
        ProgressDialog dialog;

        @Override
        protected String doInBackground(Integer... integers) {
            dataBase = Room.databaseBuilder(context, MyDatabase.class, MyApplication.getDBNAME())
                    .allowMainThreadQueries().build();
            daoExaminerDuties = dataBase.daoExaminerDuties();
            examinerDuties = daoExaminerDuties.unreadExaminerDutiesByTrackNumber(trackNumber);
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment, new PersonalFragment());
            fragmentTransaction.commit();
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(context);
            dialog.setMessage(context.getString(R.string.loading_getting_info));
            dialog.setTitle(context.getString(R.string.loading_connecting));
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            dialog.dismiss();
        }
    }
}
