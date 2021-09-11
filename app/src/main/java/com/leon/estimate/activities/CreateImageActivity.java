package com.leon.estimate.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Debug;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
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
import com.leon.estimate.Tables.DaoResultDictionary;
import com.leon.estimate.Tables.MyDatabase;
import com.leon.estimate.Tables.RequestDictionary;
import com.leon.estimate.Tables.ResultDictionary;
import com.leon.estimate.Tables.Tejariha;
import com.leon.estimate.Tables.UploadImage;
import com.leon.estimate.Utils.Constants;
import com.leon.estimate.Utils.CustomDialog;
import com.leon.estimate.Utils.CustomErrorHandlingNew;
import com.leon.estimate.Utils.CustomFile;
import com.leon.estimate.Utils.CustomProgressBar;
import com.leon.estimate.Utils.GPSTracker;
import com.leon.estimate.Utils.HttpClientWrapper;
import com.leon.estimate.Utils.NetworkHelper;
import com.leon.estimate.Utils.SharedPreferenceManager;
import com.leon.estimate.Utils.SimpleMessage;
import com.leon.estimate.databinding.CreateImageActivityBinding;
import com.leon.estimate.fragments.HighQualityFragment;
import com.sardari.daterangepicker.utils.PersianCalendar;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

import static com.leon.estimate.Utils.Constants.calculationUserInput;
import static com.leon.estimate.Utils.Constants.examinerDuties;
import static com.leon.estimate.Utils.Constants.karbari;
import static com.leon.estimate.Utils.Constants.noeVagozari;
import static com.leon.estimate.Utils.Constants.others;
import static com.leon.estimate.Utils.Constants.qotrEnsheab;
import static com.leon.estimate.Utils.Constants.secondForm;

public class CreateImageActivity extends AppCompatActivity {
    Context context;
    CreateImageActivityBinding binding;
    Bitmap bitmap, bitmapDescription = null;
    List<ResultDictionary> resultDictionaries;
    MyDatabase dataBase;
    SharedPreferenceManager sharedPreferenceManager;
    String trackNumber, billId;
    boolean isNew;
    int docId, docIdDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        binding = CreateImageActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getExtra();
        initialize();
    }

    void initialize() {
        context = this;
        sharedPreferenceManager = new SharedPreferenceManager(context,
                SharedReferenceNames.ACCOUNT.getValue());
        dataBase = Room.databaseBuilder(context, MyDatabase.class, MyApplication.getDBNAME())
                .allowMainThreadQueries().build();
        DaoResultDictionary daoResultDictionary = dataBase.daoResultDictionary();
        resultDictionaries = daoResultDictionary.getResults();
        initializeSpinner();
        new CreateImage(context).execute();
        binding.buttonDenial.setOnClickListener(v -> finish());
        setOnAcceptedButtonClickListener();
        binding.imageViewRefresh1.setOnClickListener(v -> binding.signatureView1.clearCanvas());
        binding.imageViewRefresh2.setOnClickListener(v -> binding.signatureView2.clearCanvas());
        binding.imageViewExport.setOnClickListener(v -> {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            HighQualityFragment highQualityFragment = HighQualityFragment.newInstance(
                    bitmap, "imageSign");
            highQualityFragment.show(fragmentTransaction, "imageSign");
        });
    }

    void initializeSpinner() {
        List<String> arrayListSpinner = new ArrayList<>();
        for (ResultDictionary resultDictionary : resultDictionaries) {
            arrayListSpinner.add(resultDictionary.getTitle());
        }
        binding.spinner1.setAdapter(createArrayAdapter(arrayListSpinner));
    }

    ArrayAdapter<String> createArrayAdapter(List<String> arrayListSpinner) {
        return new ArrayAdapter<String>(context,
                R.layout.dropdown_menu_popup_item, arrayListSpinner) {
            @NotNull
            @Override
            public View getView(int position, View convertView, @NotNull ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                final CheckedTextView textView = view.findViewById(android.R.id.text1);
                textView.setChecked(true);
                textView.setTextSize(context.getResources().getDimension(R.dimen.textSizeSmall));
                textView.setTextColor(getResources().getColor(R.color.black));
                return view;
            }
        };
    }

    void setOnAcceptedButtonClickListener() {
        binding.buttonAccepted.setOnClickListener(v -> {
            if (binding.signatureView1.isBitmapEmpty() || binding.signatureView2.isBitmapEmpty()) {
                Toast.makeText(context, R.string.request_sign, Toast.LENGTH_LONG).show();
            } else {
                addImageSign();
                if (binding.editTextDescription.getText().toString().length() > 0) {
                    bitmapDescription =
                            createImageDescription(binding.editTextDescription.getText().toString());
                }
//                uploadImage();
//                if (bitmapDescription != null)
//                    uploadDescriptionImage();
                send();
            }
        });
    }

    void addImageSign() {
        Bitmap bitmap1 = binding.signatureView1.getSignatureBitmap();
        Bitmap bitmap2 = binding.signatureView2.getSignatureBitmap();
        Bitmap dest = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas cs = new Canvas(dest);

        Paint tPaint = new Paint();
        tPaint.setStyle(Paint.Style.FILL);
        tPaint.setColor(Color.BLACK);

        float yCoordinate = (float) bitmap.getHeight() * 62 / 72;
        float xCoordinate = (float) bitmap.getWidth() * 20 / 36;

        cs.drawBitmap(bitmap, 0f, 0f, null);
        cs.drawBitmap(bitmap2, xCoordinate, yCoordinate, tPaint);
        xCoordinate = (float) bitmap.getWidth() * 5 / 36;
        cs.drawBitmap(bitmap1, xCoordinate, yCoordinate, tPaint);
        bitmap = dest;
        binding.imageViewExport.setImageBitmap(bitmap);
        binding.imageViewExport.setVisibility(View.VISIBLE);
        binding.signatureView1.setVisibility(View.GONE);
        binding.signatureView2.setVisibility(View.GONE);
        binding.imageViewRefresh1.setVisibility(View.GONE);
        binding.imageViewRefresh2.setVisibility(View.GONE);
    }

    void uploadImage() {
        Retrofit retrofit = NetworkHelper.getInstance("");
        final IAbfaService getImage = retrofit.create(IAbfaService.class);
        MultipartBody.Part body = CustomFile.bitmapToFile(bitmap, context, null);
        Call<UploadImage> call;
        if (isNew)
            call = getImage.uploadDocNew(sharedPreferenceManager.getStringData(
                    SharedReferenceKeys.TOKEN_FOR_FILE.getValue()), body, docId, trackNumber);
        else
            call = getImage.uploadDoc(sharedPreferenceManager.getStringData(
                    SharedReferenceKeys.TOKEN_FOR_FILE.getValue()), body, docId, billId);
        HttpClientWrapper.callHttpAsync(call, ProgressType.SHOW.getValue(), this,
                new UploadImageDoc(), new UploadImageDocIncomplete(), new GetError());
    }

    void uploadDescriptionImage() {
        Retrofit retrofit = NetworkHelper.getInstance("");
        final IAbfaService getImage = retrofit.create(IAbfaService.class);
        MultipartBody.Part body = CustomFile.bitmapToFile(bitmapDescription, context, null);
        Call<UploadImage> call;
        if (isNew)
            call = getImage.uploadDocNew(sharedPreferenceManager.getStringData(
                    SharedReferenceKeys.TOKEN_FOR_FILE.getValue()), body, docIdDescription, trackNumber);
        else
            call = getImage.uploadDoc(sharedPreferenceManager.getStringData(
                    SharedReferenceKeys.TOKEN_FOR_FILE.getValue()), body, docIdDescription, billId);
        HttpClientWrapper.callHttpAsync(call, ProgressType.SHOW.getValue(), this,
                new UploadDescriptionImageDoc(), new UploadDescriptionImageDocIncomplete(), new GetError());
    }

    void send() {
        String token = sharedPreferenceManager.getStringData(SharedReferenceKeys.TOKEN.getValue());
        Retrofit retrofit = NetworkHelper.getInstance(token);
        final IAbfaService abfaService = retrofit.create(IAbfaService.class);

        DaoExaminerDuties daoExaminerDuties = dataBase.daoExaminerDuties();
        daoExaminerDuties.updateExamination(true, trackNumber);
        GPSTracker gpsTracker = new GPSTracker(this);
        calculationUserInput.accuracy = gpsTracker.getAccuracy();
        calculationUserInput.y2 = gpsTracker.getLatitude();
        calculationUserInput.x2 = gpsTracker.getLongitude();
        calculationUserInput.resultId = resultDictionaries.get(binding.spinner1.getSelectedItemPosition()).getId();
        calculationUserInput.ready = true;
        DaoCalculationUserInput daoCalculationUserInput = dataBase.daoCalculationUserInput();
        daoCalculationUserInput.deleteByTrackNumber(trackNumber);
        daoCalculationUserInput.insertCalculationUserInput(calculationUserInput);

        ArrayList<CalculationUserInputSend> calculationUserInputSends = new ArrayList<>();
        calculationUserInputSends.add(new CalculationUserInputSend(calculationUserInput, examinerDuties));

        Call<SimpleMessage> call = abfaService.setExaminationInfo(calculationUserInputSends);
        HttpClientWrapper.callHttpAsync(call, ProgressType.SHOW.getValue(), context,
                new SendCalculation(), new SendCalculationIncomplete(), new GetError());
    }

    void getExtra() {
        if (getIntent().getExtras() != null) {
            billId = Objects.requireNonNull(
                    getIntent().getExtras()).getString(BundleEnum.BILL_ID.getValue());
            trackNumber = getIntent().getExtras().getString(BundleEnum.TRACK_NUMBER.getValue());
            docId = getIntent().getExtras().getInt(BundleEnum.TITLE.getValue());
            docIdDescription = getIntent().getExtras().getInt(BundleEnum.OTHER_TITLE.getValue());
            isNew = getIntent().getExtras().getBoolean(BundleEnum.NEW_ENSHEAB.getValue());
        }
    }

    @SuppressLint("SimpleDateFormat")
    Bitmap createImageDescription(String description) {
        int small = 100;
        if (Build.VERSION.SDK_INT == 29)
            small = 85;
        Bitmap src = BitmapFactory.decodeResource(getResources(), R.drawable.description);
        Bitmap dest = Bitmap.createBitmap(src.getWidth(), src.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas cs = new Canvas(dest);
        cs.drawBitmap(src, 0f, 0f, null);

        Paint tPaint = new Paint();
        tPaint.setTypeface(Typeface.createFromAsset(context.getAssets(), MyApplication.fontName));
        tPaint.setStyle(Paint.Style.FILL);
        tPaint.setColor(Color.BLACK);
        tPaint.setTextAlign(Paint.Align.RIGHT);
        tPaint.setTextSize(small);

        float yCoordinate;
        float xCoordinate = (float) src.getWidth() * 33 / 36;

        for (int i = 0; i <= description.length() / 100; i++) {
            yCoordinate = (float) src.getHeight() * (14 + i * 5) / 288;
            if (description.length() < 100) {
                cs.drawText(description, xCoordinate, yCoordinate, tPaint);
            } else if (description.length() < (i + 1) * 100) {
                cs.drawText(description.substring(i * 100), xCoordinate, yCoordinate, tPaint);
            } else {
                cs.drawText(description.substring(i * 100, (i + 1) * 100),
                        xCoordinate, yCoordinate, tPaint);
            }
        }

        return dest;
    }

    @SuppressLint("SimpleDateFormat")
    Bitmap createImage() {
        int small = 35;
        if (Build.VERSION.SDK_INT == 29)
            small = 25;
        Bitmap src = BitmapFactory.decodeResource(getResources(), R.drawable.export);
        Bitmap dest = Bitmap.createBitmap(src.getWidth(), src.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas cs = new Canvas(dest);
        cs.drawBitmap(src, 0f, 0f, null);

        Paint tPaint = new Paint();
        tPaint.setTypeface(Typeface.createFromAsset(context.getAssets(), MyApplication.fontName));
        tPaint.setStyle(Paint.Style.FILL);
        tPaint.setColor(Color.BLACK);
        tPaint.setTextSize(small);

        float yCoordinate = (float) src.getHeight() * 7 / 144;
        float xCoordinate = (float) src.getWidth() * 28 / 36;
        cs.drawText(examinerDuties.getTrackNumber(), xCoordinate, yCoordinate, tPaint);

        xCoordinate = (float) src.getWidth() * 85 / 144;
        if (examinerDuties.getBillId() == null || examinerDuties.getBillId().length() < 1)
            cs.drawText(examinerDuties.getNeighbourBillId(), xCoordinate, yCoordinate, tPaint);
        else
            cs.drawText(examinerDuties.getBillId(), xCoordinate, yCoordinate, tPaint);

        xCoordinate = (float) src.getWidth() * 16 / 36;
        cs.drawText(examinerDuties.getZoneTitle(), xCoordinate, yCoordinate, tPaint);
        xCoordinate = (float) src.getWidth() * 40 / 144;
        cs.drawText(examinerDuties.getEshterak(), xCoordinate, yCoordinate, tPaint);
        xCoordinate = (float) src.getWidth() * 8 / 72;
        cs.drawText(examinerDuties.getRadif(), xCoordinate, yCoordinate, tPaint);

        yCoordinate = (float) src.getHeight() * 23 / 288;
        xCoordinate = (float) src.getWidth() * 25 / 36;
        cs.drawText(examinerDuties.getNationalId(), xCoordinate, yCoordinate, tPaint);
        xCoordinate = (float) src.getWidth() * 17 / 36;
        cs.drawText(examinerDuties.getShenasname(), xCoordinate, yCoordinate, tPaint);
        xCoordinate = (float) src.getWidth() * 12 / 36;
        cs.drawText(examinerDuties.getFirstName(), xCoordinate, yCoordinate, tPaint);
        xCoordinate = (float) src.getWidth() * 4 / 36;
        cs.drawText(examinerDuties.getSureName(), xCoordinate, yCoordinate, tPaint);

        yCoordinate = (float) src.getHeight() * 28 / 288;
        xCoordinate = (float) src.getWidth() * 27 / 36;
        cs.drawText(examinerDuties.getFatherName(), xCoordinate, yCoordinate, tPaint);
        xCoordinate = (float) src.getWidth() * 21 / 36;
        cs.drawText(examinerDuties.getPhoneNumber(), xCoordinate, yCoordinate, tPaint);
        xCoordinate = (float) src.getWidth() * 12 / 36;
        cs.drawText(examinerDuties.getNotificationMobile(), xCoordinate, yCoordinate, tPaint);
        xCoordinate = (float) src.getWidth() * 7 / 72;
        cs.drawText(examinerDuties.getPostalCode(), xCoordinate, yCoordinate, tPaint);

        yCoordinate = (float) src.getHeight() * 17 / 144;
        xCoordinate = (float) src.getWidth() * 5 / 36;
        cs.drawText(examinerDuties.getAddress(), xCoordinate, yCoordinate, tPaint);

        yCoordinate = (float) src.getHeight() * 24 / 144;
        xCoordinate = (float) src.getWidth() * 28 / 36;
        cs.drawText(examinerDuties.getServiceGroup(), xCoordinate, yCoordinate, tPaint);

        yCoordinate = (float) src.getHeight() * 22 / 144;
        xCoordinate = (float) src.getWidth() * 4 / 36;
        String request = "";
        ArrayList<RequestDictionary> requestDictionaries = calculationUserInput.setSelectedServices(calculationUserInput);
        for (RequestDictionary requestDictionary : requestDictionaries) {
            if (requestDictionary.isSelected())
                request = request.concat(requestDictionary.getTitle()).concat("، ");
        }
        request = String.valueOf(request.subSequence(0, request.length() - 2));
        if (request.length() > 70) {
            String temp = request.substring(70);
            cs.drawText(request.substring(0, 70), xCoordinate, yCoordinate, tPaint);
            yCoordinate = (float) src.getHeight() * 24 / 144;
            cs.drawText(temp, xCoordinate, yCoordinate, tPaint);
        } else
            cs.drawText(request, xCoordinate, yCoordinate, tPaint);

        yCoordinate = (float) src.getHeight() * 29 / 144;
        xCoordinate = (float) src.getWidth() * 20 / 36;
        cs.drawText(karbari, xCoordinate, yCoordinate, tPaint);
        xCoordinate = (float) src.getWidth() * 4 / 36;
        cs.drawText(noeVagozari, xCoordinate, yCoordinate, tPaint);

        yCoordinate = (float) src.getHeight() * 63 / 288;
        xCoordinate = (float) src.getWidth() * 26 / 36;
        cs.drawText(qotrEnsheab, xCoordinate, yCoordinate, tPaint);
        xCoordinate = (float) src.getWidth() * 20 / 36;
        cs.drawText(String.valueOf(examinerDuties.getArse()), xCoordinate, yCoordinate, tPaint);
        xCoordinate = (float) src.getWidth() * 16 / 36;
        cs.drawText(String.valueOf(examinerDuties.getSifoon100()), xCoordinate, yCoordinate, tPaint);
        xCoordinate = (float) src.getWidth() * 12 / 36;
        cs.drawText(String.valueOf(examinerDuties.getSifoon125()), xCoordinate, yCoordinate, tPaint);
        xCoordinate = (float) src.getWidth() * 8 / 36;
        cs.drawText(String.valueOf(examinerDuties.getSifoon150()), xCoordinate, yCoordinate, tPaint);
        xCoordinate = (float) src.getWidth() * 4 / 36;
        cs.drawText(String.valueOf(examinerDuties.getSifoon200()), xCoordinate, yCoordinate, tPaint);

        yCoordinate = (float) src.getHeight() * 70 / 288;
        xCoordinate = (float) src.getWidth() * 25 / 36;
        cs.drawText(String.valueOf(examinerDuties.getAianMaskooni()), xCoordinate, yCoordinate, tPaint);
        xCoordinate = (float) src.getWidth() * 15 / 36;
        cs.drawText(String.valueOf(examinerDuties.getTedadTejari()), xCoordinate, yCoordinate, tPaint);
        xCoordinate = (float) src.getWidth() * 4 / 36;
        cs.drawText(String.valueOf(examinerDuties.getZarfiatQarardadi()), xCoordinate, yCoordinate, tPaint);

        yCoordinate = (float) src.getHeight() * 77 / 288;
        xCoordinate = (float) src.getWidth() * 25 / 36;
        cs.drawText(String.valueOf(examinerDuties.getAianNonMaskooni()), xCoordinate, yCoordinate, tPaint);
        xCoordinate = (float) src.getWidth() * 15 / 36;
        cs.drawText(String.valueOf(examinerDuties.getTedadMaskooni()), xCoordinate, yCoordinate, tPaint);
        xCoordinate = (float) src.getWidth() * 4 / 36;
        cs.drawText(String.valueOf(examinerDuties.getArzeshMelk()), xCoordinate, yCoordinate, tPaint);

        yCoordinate = (float) src.getHeight() * 83 / 288;
        xCoordinate = (float) src.getWidth() * 25 / 36;
        cs.drawText(String.valueOf(examinerDuties.getAianKol()), xCoordinate, yCoordinate, tPaint);
        xCoordinate = (float) src.getWidth() * 15 / 36;
        cs.drawText(String.valueOf(examinerDuties.getTedadSaier()), xCoordinate, yCoordinate, tPaint);

        if (examinerDuties.getTedadTejari() > 0 || examinerDuties.getTedadSaier() > 0)
            for (int i = 0; i < others.size(); i++) {
                Tejariha tejariha = others.get(i);
                yCoordinate = (float) src.getHeight() * (49 + i * 3) / 144;
                xCoordinate = (float) src.getWidth() * 104 / 144;
                cs.drawText(tejariha.karbari, xCoordinate, yCoordinate, tPaint);
                xCoordinate = (float) src.getWidth() * 84 / 144;
                cs.drawText(tejariha.noeShoql, xCoordinate, yCoordinate, tPaint);
                xCoordinate = (float) src.getWidth() * 18 / 36;
                cs.drawText(String.valueOf(tejariha.tedadVahed), xCoordinate, yCoordinate, tPaint);
                xCoordinate = (float) src.getWidth() * 20 / 72;
                cs.drawText(String.valueOf(tejariha.vahedMohasebe), xCoordinate, yCoordinate, tPaint);
                xCoordinate = (float) src.getWidth() * 4 / 36;
                cs.drawText(String.valueOf(tejariha.a), xCoordinate, yCoordinate, tPaint);
            }

        yCoordinate = (float) src.getHeight() * 147 / 288;
        xCoordinate = (float) src.getWidth() * 23 / 36;
        cs.drawText(String.valueOf(secondForm.getKhakiAb()), xCoordinate, yCoordinate, tPaint);
        xCoordinate = (float) src.getWidth() * 17 / 36;
        cs.drawText(String.valueOf(secondForm.getAsphalutAb()), xCoordinate, yCoordinate, tPaint);
        xCoordinate = (float) src.getWidth() * 10 / 36;
        cs.drawText(String.valueOf(secondForm.getSangFarshAb()), xCoordinate, yCoordinate, tPaint);
        xCoordinate = (float) src.getWidth() * 4 / 36;
        cs.drawText(String.valueOf(secondForm.getOtherAb()), xCoordinate, yCoordinate, tPaint);

        yCoordinate = (float) src.getHeight() * 152 / 288;
        xCoordinate = (float) src.getWidth() * 23 / 36;
        cs.drawText(String.valueOf(secondForm.getKhakiFazelab()), xCoordinate, yCoordinate, tPaint);
        xCoordinate = (float) src.getWidth() * 17 / 36;
        cs.drawText(String.valueOf(secondForm.getAsphalutFazelab()), xCoordinate, yCoordinate, tPaint);
        xCoordinate = (float) src.getWidth() * 10 / 36;
        cs.drawText(String.valueOf(secondForm.getSangFarshFazelab()), xCoordinate, yCoordinate, tPaint);
        xCoordinate = (float) src.getWidth() * 4 / 36;
        cs.drawText(String.valueOf(secondForm.getOtherFazelab()), xCoordinate, yCoordinate, tPaint);

        yCoordinate = (float) src.getHeight() * 158 / 288;
        xCoordinate = (float) src.getWidth() * 55 / 72;
        cs.drawText(String.valueOf(secondForm.getQotreLoole()), xCoordinate, yCoordinate, tPaint);
        xCoordinate = (float) src.getWidth() * 21 / 36;
        cs.drawText(secondForm.getJenseLoole(), xCoordinate, yCoordinate, tPaint);
        xCoordinate = (float) src.getWidth() * 11 / 36;
        if (secondForm.isVaziatNasbePomp() == 0) {
            cs.drawText(getString(R.string.monaseb), xCoordinate, yCoordinate, tPaint);
        } else if (secondForm.isVaziatNasbePomp() == 1)
            cs.drawText(getString(R.string.namonaseb), xCoordinate, yCoordinate, tPaint);
        else if (secondForm.isVaziatNasbePomp() == 2)
            cs.drawText(getString(R.string.have_n), xCoordinate, yCoordinate, tPaint);
        xCoordinate = (float) src.getWidth() * 4 / 36;
        cs.drawText(String.valueOf(secondForm.getOmqFazelab()), xCoordinate, yCoordinate, tPaint);

        yCoordinate = (float) src.getHeight() * 164 / 288;
        xCoordinate = (float) src.getWidth() * 28 / 36;
        cs.drawText(String.valueOf(secondForm.getOmqeZirzamin()), xCoordinate, yCoordinate, tPaint);
        xCoordinate = (float) src.getWidth() * 19 / 36;
        cs.drawText(getString(R.string.have), xCoordinate, yCoordinate, tPaint);
        if (!secondForm.isEtesalZirzamin())
            cs.drawText(getString(R.string.have_n), xCoordinate, yCoordinate, tPaint);
        xCoordinate = (float) src.getWidth() * 8 / 36;
        cs.drawText(secondForm.getNoeMasrafString(), xCoordinate, yCoordinate, tPaint);

        yCoordinate = (float) src.getHeight() * 169 / 288;
        xCoordinate = (float) src.getWidth() * 24 / 36;
        if (secondForm.isEzhaNazarF())
            cs.drawText(getString(R.string.have), xCoordinate, yCoordinate, tPaint);
        else cs.drawText(getString(R.string.have_n), xCoordinate, yCoordinate, tPaint);
        xCoordinate = (float) src.getWidth() * 31 / 72;
        if (secondForm.isEzhaNazarA())
            cs.drawText(getString(R.string.have), xCoordinate, yCoordinate, tPaint);
        else cs.drawText(getString(R.string.have_n), xCoordinate, yCoordinate, tPaint);

        xCoordinate = (float) src.getWidth() * 10 / 36;
        if (secondForm.isLooleA())
            cs.drawText(getString(R.string.have), xCoordinate, yCoordinate, tPaint);
        else cs.drawText(getString(R.string.have_n), xCoordinate, yCoordinate, tPaint);
        xCoordinate = (float) src.getWidth() * 4 / 36;
        if (secondForm.isLooleF())
            cs.drawText(getString(R.string.have), xCoordinate, yCoordinate, tPaint);
        else cs.drawText(getString(R.string.have_n), xCoordinate, yCoordinate, tPaint);

        yCoordinate = (float) src.getHeight() * 175 / 288;
        xCoordinate = (float) src.getWidth() * 27 / 36;
        if (secondForm.isChahAbBaran())
            cs.drawText(getString(R.string.have), xCoordinate, yCoordinate, tPaint);
        else cs.drawText(getString(R.string.have_n), xCoordinate, yCoordinate, tPaint);
        xCoordinate = (float) src.getWidth() * 19 / 36;
        if (examinerDuties.isEstelamShahrdari())
            cs.drawText(getString(R.string.have), xCoordinate, yCoordinate, tPaint);
        else cs.drawText(getString(R.string.have_n), xCoordinate, yCoordinate, tPaint);
        xCoordinate = (float) src.getWidth() * 12 / 36;
        if (examinerDuties.isParvane())
            cs.drawText(getString(R.string.have), xCoordinate, yCoordinate, tPaint);
        else cs.drawText(getString(R.string.have_n), xCoordinate, yCoordinate, tPaint);
        xCoordinate = (float) src.getWidth() * 4 / 36;
        if (examinerDuties.isMotaqazi())
            cs.drawText(getString(R.string.have), xCoordinate, yCoordinate, tPaint);
        else cs.drawText(getString(R.string.have_n), xCoordinate, yCoordinate, tPaint);

        String description = secondForm.getMasrafDescription().concat(" **** ").concat(
                secondForm.getChahDescription()).concat(" **** ").concat(examinerDuties.getDescription());
        xCoordinate = (float) src.getWidth() * 32 / 36;
        tPaint.setTextAlign(Paint.Align.RIGHT);

        for (int i = 0; i <= description.length() / 85; i++) {
            yCoordinate = (float) src.getHeight() * (188 + i * 5) / 288;
            if (description.length() < 85) {
                cs.drawText(description, xCoordinate, yCoordinate, tPaint);
            } else if (description.length() < (i + 1) * 85) {
                cs.drawText(description.substring(i * 85), xCoordinate, yCoordinate, tPaint);
            } else {
                cs.drawText(description.substring(i * 85, (i + 1) * 85),
                        xCoordinate, yCoordinate, tPaint);
            }
        }
//        yCoordinate = (float) src.getHeight() * 188 / 288;
//        xCoordinate = (float) src.getWidth() * 4 / 36;
//        if (secondForm.getMasrafDescription().length() > 80) {
//            cs.drawText(secondForm.getMasrafDescription().substring(0, 80), xCoordinate, yCoordinate, tPaint);
//            yCoordinate = (float) src.getHeight() * 193 / 288;
//            cs.drawText(secondForm.getMasrafDescription().substring(80), xCoordinate, yCoordinate, tPaint);
//        } else
//            cs.drawText(secondForm.getMasrafDescription(), xCoordinate, yCoordinate, tPaint);
//
//        yCoordinate = (float) src.getHeight() * 202 / 288;
//
//        if (secondForm.getChahDescription().length() > 80) {
//            cs.drawText(secondForm.getChahDescription().substring(0, 80), xCoordinate, yCoordinate, tPaint);
//            yCoordinate = (float) src.getHeight() * 207 / 288;
//            cs.drawText(secondForm.getChahDescription().substring(80), xCoordinate, yCoordinate, tPaint);
//        } else
//            cs.drawText(secondForm.getChahDescription(), xCoordinate, yCoordinate, tPaint);
//
//        yCoordinate = (float) src.getHeight() * 216 / 288;
//        if (examinerDuties.getDescription().length() > 80) {
//            cs.drawText(examinerDuties.getDescription().substring(0, 80), xCoordinate, yCoordinate, tPaint);
//            yCoordinate = (float) src.getHeight() * 221 / 288;
//            cs.drawText(examinerDuties.getDescription().substring(80), xCoordinate, yCoordinate, tPaint);
//        } else
//            cs.drawText(examinerDuties.getDescription(), xCoordinate, yCoordinate, tPaint);


//        if (secondForm.getMasrafDescription().length() > 80) {
//            cs.drawText(secondForm.getMasrafDescription().substring(0, 80), xCoordinate, yCoordinate, tPaint);
//            yCoordinate = (float) src.getHeight() * 193 / 288;
//            cs.drawText(secondForm.getMasrafDescription().substring(80), xCoordinate, yCoordinate, tPaint);
//        } else
//            cs.drawText(secondForm.getMasrafDescription(), xCoordinate, yCoordinate, tPaint);
//
//        yCoordinate = (float) src.getHeight() * 202 / 288;

//        if (secondForm.getChahDescription().length() > 80) {
//            cs.drawText(secondForm.getChahDescription().substring(0, 80), xCoordinate, yCoordinate, tPaint);
//            yCoordinate = (float) src.getHeight() * 207 / 288;
//            cs.drawText(secondForm.getChahDescription().substring(80), xCoordinate, yCoordinate, tPaint);
//        } else
//            cs.drawText(secondForm.getChahDescription(), xCoordinate, yCoordinate, tPaint);
//
//        yCoordinate = (float) src.getHeight() * 216 / 288;
//        if (examinerDuties.getDescription().length() > 80) {
//            cs.drawText(examinerDuties.getDescription().substring(0, 80), xCoordinate, yCoordinate, tPaint);
//            yCoordinate = (float) src.getHeight() * 221 / 288;
//            cs.drawText(examinerDuties.getDescription().substring(80), xCoordinate, yCoordinate, tPaint);
//        } else
//            cs.drawText(examinerDuties.getDescription(), xCoordinate, yCoordinate, tPaint);

        tPaint.setTextAlign(Paint.Align.LEFT);
        yCoordinate = (float) src.getHeight() * 241 / 288;
        xCoordinate = (float) src.getWidth() * 20 / 36;
        PersianCalendar persianCalendar = new PersianCalendar();
        String dateWaterMark = " - ".concat(persianCalendar.getPersianLongDate());
        String timeWaterMark = (new SimpleDateFormat("HH:mm:ss")).format(new Date());
        cs.drawText(timeWaterMark.concat(dateWaterMark), xCoordinate, yCoordinate, tPaint);
        yCoordinate = (float) src.getHeight() * 245 / 288;
        cs.drawText(examinerDuties.getExaminerName(), xCoordinate, yCoordinate, tPaint);
        return dest;
    }


    @SuppressLint("StaticFieldLeak")
    class CreateImage extends AsyncTask<Integer, Integer, Integer> {
        CustomProgressBar progressDialog;
        Context context;

        public CreateImage(Context context) {
            super();
            this.context = context;
            progressDialog = new CustomProgressBar();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show(context, context.getString(R.string.waiting));
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            progressDialog.getDialog().dismiss();
        }

        @Override
        protected Integer doInBackground(Integer... integers) {
            bitmap = createImage();
            runOnUiThread(() -> binding.imageViewExport.setImageBitmap(bitmap));
            return null;
        }
    }

    class SendCalculation implements ICallback<SimpleMessage> {
        @Override
        public void execute(SimpleMessage simpleMessage) {
            uploadImage();
            if (bitmapDescription != null)
                uploadDescriptionImage();
            DaoCalculationUserInput daoCalculationUserInput = dataBase.daoCalculationUserInput();
            daoCalculationUserInput.updateCalculationUserInput(true, trackNumber);
        }
    }

    class SendCalculationIncomplete implements ICallbackIncomplete<SimpleMessage> {
        @Override
        public void executeIncomplete(Response<SimpleMessage> response) {
            uploadImage();
            if (bitmapDescription != null)
                uploadDescriptionImage();
            CustomErrorHandlingNew customErrorHandlingNew = new CustomErrorHandlingNew(context);
            String error = customErrorHandlingNew.getErrorMessageDefault(response);
            new CustomDialog(DialogType.Yellow, CreateImageActivity.this, error,
                    CreateImageActivity.this.getString(R.string.dear_user),
                    CreateImageActivity.this.getString(R.string.login),
                    CreateImageActivity.this.getString(R.string.accepted));
        }
    }

    class UploadImageDoc implements ICallback<UploadImage> {
        @Override
        public void execute(UploadImage responseBody) {
            if (responseBody.isSuccess()) {
                Toast.makeText(CreateImageActivity.this,
                        CreateImageActivity.this.getString(R.string.upload_success), Toast.LENGTH_LONG).show();
                finish();
            } else {
                CustomFile.saveTempBitmap(Constants.bitmapSelectedImage, context, dataBase, billId, trackNumber,
                        String.valueOf(docId), "ارزیابی", isNew);
                new CustomDialog(DialogType.Yellow, CreateImageActivity.this,
                        CreateImageActivity.this.getString(R.string.error_upload).concat("\n")
                                .concat(responseBody.getError()),
                        CreateImageActivity.this.getString(R.string.dear_user),
                        CreateImageActivity.this.getString(R.string.upload_image),
                        CreateImageActivity.this.getString(R.string.accepted));
            }
        }
    }

    class UploadImageDocIncomplete implements ICallbackIncomplete<UploadImage> {

        @Override
        public void executeIncomplete(Response<UploadImage> response) {
            CustomFile.saveTempBitmap(Constants.bitmapSelectedImage, context, dataBase, billId, trackNumber,
                    String.valueOf(docId), "ارزیابی", isNew);
            CustomErrorHandlingNew customErrorHandlingNew = new CustomErrorHandlingNew(context);
            String error = customErrorHandlingNew.getErrorMessageDefault(response);
            new CustomDialog(DialogType.Yellow, CreateImageActivity.this, error,
                    CreateImageActivity.this.getString(R.string.dear_user),
                    CreateImageActivity.this.getString(R.string.upload_image),
                    CreateImageActivity.this.getString(R.string.accepted));
        }
    }

    class UploadDescriptionImageDoc implements ICallback<UploadImage> {
        @Override
        public void execute(UploadImage responseBody) {
            if (responseBody.isSuccess()) {
                Toast.makeText(CreateImageActivity.this,
                        CreateImageActivity.this.getString(R.string.upload_success), Toast.LENGTH_LONG).show();
                finish();
            } else {
                CustomFile.saveTempBitmap(bitmapDescription, context, dataBase, billId, trackNumber,
                        String.valueOf(docIdDescription), "ارزیابی", isNew);
                new CustomDialog(DialogType.Yellow, CreateImageActivity.this,
                        CreateImageActivity.this.getString(R.string.error_upload).concat("\n")
                                .concat(responseBody.getError()),
                        CreateImageActivity.this.getString(R.string.dear_user),
                        CreateImageActivity.this.getString(R.string.upload_image),
                        CreateImageActivity.this.getString(R.string.accepted));
            }
        }
    }

    class UploadDescriptionImageDocIncomplete implements ICallbackIncomplete<UploadImage> {

        @Override
        public void executeIncomplete(Response<UploadImage> response) {
            CustomFile.saveTempBitmap(bitmapDescription, context, dataBase, billId, trackNumber,
                    String.valueOf(docIdDescription), "ارزیابی", isNew);
            CustomErrorHandlingNew customErrorHandlingNew = new CustomErrorHandlingNew(context);
            String error = customErrorHandlingNew.getErrorMessageDefault(response);
            new CustomDialog(DialogType.Yellow, CreateImageActivity.this, error,
                    CreateImageActivity.this.getString(R.string.dear_user),
                    CreateImageActivity.this.getString(R.string.upload_image),
                    CreateImageActivity.this.getString(R.string.accepted));
        }
    }

    class GetError implements ICallbackError {
        @Override
        public void executeError(Throwable t) {
            CustomFile.saveTempBitmap(Constants.bitmapSelectedImage, context, dataBase, billId, trackNumber,
                    String.valueOf(docId), "ارزیابی", isNew);
            if (bitmapDescription != null) {
                CustomFile.saveTempBitmap(bitmapDescription, context, dataBase, billId, trackNumber,
                        String.valueOf(docIdDescription), "ارزیابی", isNew);
            }
            CustomErrorHandlingNew customErrorHandlingNew = new CustomErrorHandlingNew(context);
            String error = customErrorHandlingNew.getErrorMessageTotal(t);
            Toast.makeText(CreateImageActivity.this, error, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        Runtime.getRuntime().totalMemory();
        Runtime.getRuntime().freeMemory();
        Runtime.getRuntime().maxMemory();
        Debug.getNativeHeapAllocatedSize();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding.imageViewExport.setImageDrawable(null);
        Runtime.getRuntime().totalMemory();
        Runtime.getRuntime().freeMemory();
        Runtime.getRuntime().maxMemory();
        Debug.getNativeHeapAllocatedSize();
    }
}