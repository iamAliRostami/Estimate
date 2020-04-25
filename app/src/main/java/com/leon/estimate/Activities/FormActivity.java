package com.leon.estimate.Activities;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.room.Room;
import androidx.viewpager.widget.ViewPager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.leon.estimate.Enums.BundleEnum;
import com.leon.estimate.Enums.ErrorHandlerType;
import com.leon.estimate.Enums.SharedReferenceKeys;
import com.leon.estimate.Enums.SharedReferenceNames;
import com.leon.estimate.R;
import com.leon.estimate.Tables.CalculationUserInput;
import com.leon.estimate.Tables.CalculationUserInputSend;
import com.leon.estimate.Tables.DaoCalculationUserInput;
import com.leon.estimate.Tables.DaoExaminerDuties;
import com.leon.estimate.Tables.ExaminerDuties;
import com.leon.estimate.Tables.MyDatabase;
import com.leon.estimate.Tables.RequestDictionary;
import com.leon.estimate.Utils.FontManager;
import com.leon.estimate.Utils.HttpClientWrapper;
import com.leon.estimate.Utils.IAbfaService;
import com.leon.estimate.Utils.ICallback;
import com.leon.estimate.Utils.MyPagerAdapter;
import com.leon.estimate.Utils.NetworkHelper;
import com.leon.estimate.Utils.SharedPreferenceManager;
import com.leon.estimate.Utils.SimpleMessage;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Retrofit;

public class FormActivity extends AppCompatActivity {
    FragmentPagerAdapter adapterViewPager;
    @BindView(R.id.view_pager)
    ViewPager viewPager;
    @BindView(R.id.relativeLayout)
    RelativeLayout relativeLayout;
    Context context;
    String trackNumber, json;
    List<RequestDictionary> requestDictionaries;
    ExaminerDuties examinerDuties;
    MyDatabase dataBase;
    DaoExaminerDuties daoExaminerDuties;
    CalculationUserInput calculationUserInput, calculationUserInputTemp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        setContentView(R.layout.form_activity);
        ButterKnife.bind(this);
        context = this;
        if (getIntent().getExtras() != null) {
            trackNumber = getIntent().getExtras().getString(BundleEnum.TRACK_NUMBER.getValue());
            json = getIntent().getExtras().getString(BundleEnum.SERVICES.getValue());
            Gson gson = new GsonBuilder().create();
            requestDictionaries = Arrays.asList(gson.fromJson(json, RequestDictionary[].class));
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
//        examinerDuties = daoExaminerDuties.examinerDutiesByTrackNumber(trackNumber);
        examinerDuties = daoExaminerDuties.unreadExaminerDutiesByTrackNumber(trackNumber);
        adapterViewPager = new MyPagerAdapter(getSupportFragmentManager(), context, examinerDuties);
        viewPager.setAdapter(adapterViewPager);
        viewPager.setOnTouchListener((v, event) -> true);
        FontManager fontManager = new FontManager(getApplicationContext());
        fontManager.setFont(relativeLayout);
        dialog.dismiss();
    }

    public void nextPage(Bitmap bitmap, CalculationUserInput calculationUserInput) {
        if (viewPager.getCurrentItem() == 0) {
            this.calculationUserInput = calculationUserInput;
            viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
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

    @Override
    protected void onResume() {
        super.onResume();
    }

    void prepareToSend() {
        fillCalculationUserInput();
        DaoCalculationUserInput daoCalculationUserInput = dataBase.daoCalculationUserInput();
        daoCalculationUserInput.deleteByTrackNumber(trackNumber);
        daoCalculationUserInput.insertCalculationUserInput(calculationUserInput);
        updateExamination();

        SharedPreferenceManager sharedPreferenceManager = new SharedPreferenceManager(getApplicationContext(), SharedReferenceNames.ACCOUNT.getValue());
        String token = sharedPreferenceManager.getStringData(SharedReferenceKeys.TOKEN.getValue());
//        Retrofit retrofit = NetworkHelper.getInstance(false, "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJqdGkiOiI4MGI0YjJjNi0zYzQ0LTRlNDMtYWQwMi05ODlhNmFiNTIwNTIiLCJpc3MiOiJodHRwOi8vYXV0aHNlcnZlci8iLCJpYXQiOjE1ODIzNzE1MDEsImh0dHA6Ly9zY2hlbWFzLnhtbHNvYXAub3JnL3dzLzIwMDUvMDUvaWRlbnRpdHkvY2xhaW1zL25hbWVpZGVudGlmaWVyIjoiMmRiNDE3YWYtNmU5My00YmU5LTgyOGEtMDE4ZDE0NjkwZWNmIiwiaHR0cDovL3NjaGVtYXMueG1sc29hcC5vcmcvd3MvMjAwNS8wNS9pZGVudGl0eS9jbGFpbXMvbmFtZSI6ImFwcEV4YW0iLCJkaXNwbGF5TmFtZSI6Itin2b7ZhNuM2qnbjNi02YYg2KfYsdiy24zYp9io24wg2KrYs9iqIiwidXNlcklkIjoiMmRiNDE3YWYtNmU5My00YmU5LTgyOGEtMDE4ZDE0NjkwZWNmIiwidXNlckNvZGUiOiI2NCIsImh0dHA6Ly9zY2hlbWFzLm1pY3Jvc29mdC5jb20vd3MvMjAwOC8wNi9pZGVudGl0eS9jbGFpbXMvc2VyaWFsbnVtYmVyIjoiZDY4NmFmOWY4YzVjNDUzYjk0ZTIwMWIxY2Q0YTRkM2YiLCJodHRwOi8vc2NoZW1hcy5taWNyb3NvZnQuY29tL3dzLzIwMDgvMDYvaWRlbnRpdHkvY2xhaW1zL3VzZXJkYXRhIjoiMmRiNDE3YWYtNmU5My00YmU5LTgyOGEtMDE4ZDE0NjkwZWNmIiwiem9uZUlkIjoiMTMxMzAzIiwiYWN0aW9uIjpbIlByb2ZpbGUuSW5kZXgiLCJFeGFtaW5hdGlvbk1hbmFnZXIuR2V0TXlXb3JrcyJdLCJodHRwOi8vc2NoZW1hcy5taWNyb3NvZnQuY29tL3dzLzIwMDgvMDYvaWRlbnRpdHkvY2xhaW1zL3JvbGUiOiJFeGFtaW5lciIsInJvbGVJZCI6IjQiLCJuYmYiOjE1ODIzNzE1MDEsImV4cCI6MTU4MjQxODMwMSwiYXVkIjoiNDE0ZTE5MjdhMzg4NGY2OGFiYzc5ZjcyODM4MzdmZDEifQ.iCLVExnN_UCqEgMvzGWB1Lw3UI4T-5ey3Z8aNQj_I1Y");
        Retrofit retrofit = NetworkHelper.getInstance(false, token);
        final IAbfaService abfaService = retrofit.create(IAbfaService.class);
        SendCalculation sendCalculation = new SendCalculation();
        ArrayList<CalculationUserInputSend> calculationUserInputSends = new ArrayList<>();
        calculationUserInputSends.add(new CalculationUserInputSend(calculationUserInput));
        Call<SimpleMessage> call = abfaService.setExaminationInfo(calculationUserInputSends);
        HttpClientWrapper.callHttpAsync(call, sendCalculation, ErrorHandlerType.ordinary);
    }

    void updateExamination() {
        DaoExaminerDuties daoExaminerDuties = dataBase.daoExaminerDuties();
        daoExaminerDuties.updateExamination(true, trackNumber);
        daoExaminerDuties.insert(examinerDuties.updateExaminerDuties(calculationUserInput));
    }

    void fillCalculationUserInput() {
        //TODO SELECTED SERVICE
        calculationUserInput.nationalId = calculationUserInputTemp.nationalId;
        calculationUserInput.firstName = calculationUserInputTemp.firstName.trim();
        calculationUserInput.sureName = calculationUserInputTemp.sureName.trim();
        calculationUserInput.fatherName = calculationUserInputTemp.fatherName.trim();
        calculationUserInput.postalCode = calculationUserInputTemp.postalCode;
        calculationUserInput.radif = calculationUserInputTemp.radif;
        calculationUserInput.phoneNumber = calculationUserInputTemp.phoneNumber;
        calculationUserInput.mobile = calculationUserInputTemp.mobile;
        calculationUserInput.address = calculationUserInputTemp.address;
        calculationUserInput.description = calculationUserInputTemp.description;

        calculationUserInput.trackingId = examinerDuties.getTrackingId();
        calculationUserInput.requestType = Integer.valueOf(examinerDuties.getRequestType());
        calculationUserInput.parNumber = examinerDuties.getParNumber();
        calculationUserInput.billId = examinerDuties.getBillId();
        calculationUserInput.neighbourBillId = examinerDuties.getNeighbourBillId();
        calculationUserInput.notificationMobile = examinerDuties.getNotificationMobile();
        calculationUserInput.nationalId = examinerDuties.getNationalId();
        calculationUserInput.identityCode = examinerDuties.getIdentityCode();
        calculationUserInput.trackNumber = examinerDuties.getTrackNumber();
        calculationUserInput.trackingId = examinerDuties.getTrackingId();
        calculationUserInput.setSent(true);
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
}
