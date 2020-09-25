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
import android.os.Bundle;
import android.os.Debug;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
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
import com.leon.estimate.Tables.DaoImages;
import com.leon.estimate.Tables.DaoResultDictionary;
import com.leon.estimate.Tables.Images;
import com.leon.estimate.Tables.MotherChild;
import com.leon.estimate.Tables.MyDatabase;
import com.leon.estimate.Tables.RequestDictionary;
import com.leon.estimate.Tables.ResultDictionary;
import com.leon.estimate.Tables.UploadImage;
import com.leon.estimate.Utils.CustomDialog;
import com.leon.estimate.Utils.CustomErrorHandlingNew;
import com.leon.estimate.Utils.CustomProgressBar;
import com.leon.estimate.Utils.HttpClientWrapper;
import com.leon.estimate.Utils.NetworkHelper;
import com.leon.estimate.Utils.ScannerConstants;
import com.leon.estimate.Utils.SharedPreferenceManager;
import com.leon.estimate.Utils.SimpleMessage;
import com.leon.estimate.databinding.CreateImageActivityBinding;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

import static com.leon.estimate.activities.FormActivity.calculationUserInput;
import static com.leon.estimate.activities.FormActivity.examinerDuties;
import static com.leon.estimate.activities.FormActivity.karbari;
import static com.leon.estimate.activities.FormActivity.noeVagozari;
import static com.leon.estimate.activities.FormActivity.secondForm;
import static com.leon.estimate.activities.FormActivity.shenasname;

public class CreateImageActivity extends AppCompatActivity {
    Context context;
    CreateImageActivityBinding binding;
    Bitmap bitmap;
    List<ResultDictionary> resultDictionaries;
    MyDatabase dataBase;
    SharedPreferenceManager sharedPreferenceManager;
    String trackNumber, billId, imageFileName;
    boolean isNew;
    int docId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

        float yCoordinate = (float) bitmap.getHeight() * 117 / 144;
        float xCoordinate = (float) bitmap.getWidth() * 22 / 36;

        cs.drawBitmap(bitmap, 0f, 0f, null);
        cs.drawBitmap(bitmap2, xCoordinate, yCoordinate, tPaint);
        xCoordinate = (float) bitmap.getWidth() * 6 / 36;
        cs.drawBitmap(bitmap1, xCoordinate, yCoordinate, tPaint);
        bitmap = dest;
        binding.imageViewExport.setImageBitmap(bitmap);
        binding.imageViewExport.setVisibility(View.VISIBLE);
        binding.signatureView1.setVisibility(View.GONE);
        binding.signatureView2.setVisibility(View.GONE);
    }

    void uploadImage() {
        Retrofit retrofit = NetworkHelper.getInstance(true, "");
        final IAbfaService getImage = retrofit.create(IAbfaService.class);
        MultipartBody.Part body = bitmapToFile(bitmap);
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

    void send() {
        String token = sharedPreferenceManager.getStringData(SharedReferenceKeys.TOKEN.getValue());
        Retrofit retrofit = NetworkHelper.getInstance(true, token);
        final IAbfaService abfaService = retrofit.create(IAbfaService.class);

        DaoExaminerDuties daoExaminerDuties = dataBase.daoExaminerDuties();
        daoExaminerDuties.updateExamination(true, trackNumber);

        SendCalculation sendCalculation = new SendCalculation();
        SendCalculationIncomplete incomplete = new SendCalculationIncomplete();
        GetError error = new GetError();

        ArrayList<CalculationUserInputSend> calculationUserInputSends = new ArrayList<>();
        calculationUserInput.resultId = resultDictionaries.get(binding.spinner1.getSelectedItemPosition()).getId();
        calculationUserInputSends.add(new CalculationUserInputSend(calculationUserInput));
        Call<SimpleMessage> call = abfaService.setExaminationInfo(calculationUserInputSends);
        HttpClientWrapper.callHttpAsync(call, ProgressType.SHOW.getValue(), context,
                sendCalculation, incomplete, error);
    }

    void getExtra() {
        if (getIntent().getExtras() != null) {
            billId = Objects.requireNonNull(
                    getIntent().getExtras()).getString(BundleEnum.BILL_ID.getValue());
            trackNumber = getIntent().getExtras().getString(BundleEnum.TRACK_NUMBER.getValue());
            docId = getIntent().getExtras().getInt(BundleEnum.TITLE.getValue());
        }
    }

    Bitmap createImage() {
        int small = 40;
        Bitmap src = BitmapFactory.decodeResource(getResources(), R.drawable.export);
        Bitmap dest = Bitmap.createBitmap(src.getWidth(), src.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas cs = new Canvas(dest);
        cs.drawBitmap(src, 0f, 0f, null);

        Paint tPaint = new Paint();
        tPaint.setTypeface(Typeface.createFromAsset(context.getAssets(), MyApplication.fontName));
        tPaint.setStyle(Paint.Style.FILL);
        tPaint.setColor(Color.BLACK);
        tPaint.setTextSize(small);

        float yCoordinate = (float) src.getHeight() * 8 / 144;

        float xCoordinate = (float) src.getWidth() * 29 / 36;
        cs.drawText(examinerDuties.getTrackNumber(), xCoordinate, yCoordinate, tPaint);

        xCoordinate = (float) src.getWidth() * 21 / 36;
        if (examinerDuties.getBillId() == null || examinerDuties.getBillId().length() < 1)
            cs.drawText(examinerDuties.getNeighbourBillId(), xCoordinate, yCoordinate, tPaint);
        else
            cs.drawText(examinerDuties.getBillId(), xCoordinate, yCoordinate, tPaint);

        xCoordinate = (float) src.getWidth() * 16 / 36;
        cs.drawText(examinerDuties.getZoneTitle(), xCoordinate, yCoordinate, tPaint);
        xCoordinate = (float) src.getWidth() * 9 / 36;
        cs.drawText(examinerDuties.getEshterak(), xCoordinate, yCoordinate, tPaint);
        xCoordinate = (float) src.getWidth() * 3 / 36;
        cs.drawText(examinerDuties.getRadif(), xCoordinate, yCoordinate, tPaint);

        yCoordinate = (float) src.getHeight() * 13 / 144;
        xCoordinate = (float) src.getWidth() * 26 / 36;
        cs.drawText(examinerDuties.getNationalId(), xCoordinate, yCoordinate, tPaint);
        xCoordinate = (float) src.getWidth() * 17 / 36;
        cs.drawText(shenasname, xCoordinate, yCoordinate, tPaint);
        xCoordinate = (float) src.getWidth() * 11 / 36;
        cs.drawText(examinerDuties.getFirstName(), xCoordinate, yCoordinate, tPaint);
        xCoordinate = (float) src.getWidth() * 2 / 36;
        cs.drawText(examinerDuties.getSureName(), xCoordinate, yCoordinate, tPaint);

        yCoordinate = (float) src.getHeight() * 17 / 144;
        xCoordinate = (float) src.getWidth() * 28 / 36;
        cs.drawText(examinerDuties.getFatherName(), xCoordinate, yCoordinate, tPaint);
        xCoordinate = (float) src.getWidth() * 21 / 36;
        cs.drawText(examinerDuties.getPhoneNumber(), xCoordinate, yCoordinate, tPaint);
        xCoordinate = (float) src.getWidth() * 11 / 36;
        cs.drawText(examinerDuties.getNotificationMobile(), xCoordinate, yCoordinate, tPaint);
        xCoordinate = (float) src.getWidth() * 2 / 36;
        cs.drawText(examinerDuties.getPostalCode(), xCoordinate, yCoordinate, tPaint);

        yCoordinate = (float) src.getHeight() * 22 / 144;
        xCoordinate = (float) src.getWidth() * 8 / 36;
        cs.drawText(examinerDuties.getAddress(), xCoordinate, yCoordinate, tPaint);

        yCoordinate = (float) src.getHeight() * 31 / 144;
        xCoordinate = (float) src.getWidth() * 29 / 36;
        cs.drawText(examinerDuties.getServiceGroup(), xCoordinate, yCoordinate, tPaint);

        yCoordinate = (float) src.getHeight() * 28 / 144;
        xCoordinate = (float) src.getWidth() * 2 / 36;
        String request = "";
        ArrayList<RequestDictionary> requestDictionaries = calculationUserInput.setSelectedServices(calculationUserInput);
        for (RequestDictionary requestDictionary : requestDictionaries) {
            if (requestDictionary.isSelected())
                request = request.concat(requestDictionary.getTitle()).concat("، ");
        }
        request = String.valueOf(request.subSequence(0, request.length() - 2));
        cs.drawText(request, xCoordinate, yCoordinate, tPaint);

        yCoordinate = (float) src.getHeight() * 37 / 144;
        xCoordinate = (float) src.getWidth() * 23 / 36;
        cs.drawText(karbari, xCoordinate, yCoordinate, tPaint);//TODO
        xCoordinate = (float) src.getWidth() * 3 / 36;
        cs.drawText(noeVagozari, xCoordinate, yCoordinate, tPaint);//TODO

        yCoordinate = (float) src.getHeight() * 41 / 144;
        xCoordinate = (float) src.getWidth() * 29 / 36;
        cs.drawText(String.valueOf(examinerDuties.getQotrEnsheabId()), xCoordinate, yCoordinate, tPaint);//TODO
        xCoordinate = (float) src.getWidth() * 21 / 36;
        cs.drawText(String.valueOf(examinerDuties.getArse()), xCoordinate, yCoordinate, tPaint);
        xCoordinate = (float) src.getWidth() * 16 / 36;
        cs.drawText(String.valueOf(examinerDuties.getSifoon100()), xCoordinate, yCoordinate, tPaint);
        xCoordinate = (float) src.getWidth() * 12 / 36;
        cs.drawText(String.valueOf(examinerDuties.getSifoon125()), xCoordinate, yCoordinate, tPaint);
        xCoordinate = (float) src.getWidth() * 8 / 36;
        cs.drawText(String.valueOf(examinerDuties.getSifoon150()), xCoordinate, yCoordinate, tPaint);
        xCoordinate = (float) src.getWidth() * 3 / 36;
        cs.drawText(String.valueOf(examinerDuties.getSifoon200()), xCoordinate, yCoordinate, tPaint);

        yCoordinate = (float) src.getHeight() * 44 / 144;
        xCoordinate = (float) src.getWidth() * 25 / 36;
        cs.drawText(String.valueOf(examinerDuties.getAianMaskooni()), xCoordinate, yCoordinate, tPaint);
        xCoordinate = (float) src.getWidth() * 15 / 36;
        cs.drawText(String.valueOf(examinerDuties.getAianNonMaskooni()), xCoordinate, yCoordinate, tPaint);
        xCoordinate = (float) src.getWidth() * 4 / 36;
        cs.drawText(String.valueOf(examinerDuties.getAianKol()), xCoordinate, yCoordinate, tPaint);

        yCoordinate = (float) src.getHeight() * 48 / 144;
        xCoordinate = (float) src.getWidth() * 25 / 36;
        cs.drawText(String.valueOf(examinerDuties.getTedadTejari()), xCoordinate, yCoordinate, tPaint);
        xCoordinate = (float) src.getWidth() * 15 / 36;
        cs.drawText(String.valueOf(examinerDuties.getTedadMaskooni()), xCoordinate, yCoordinate, tPaint);
        xCoordinate = (float) src.getWidth() * 4 / 36;
        cs.drawText(String.valueOf(examinerDuties.getTedadSaier()), xCoordinate, yCoordinate, tPaint);

        yCoordinate = (float) src.getHeight() * 52 / 144;
        xCoordinate = (float) src.getWidth() * 23 / 36;
        cs.drawText(String.valueOf(examinerDuties.getZarfiatQarardadi()), xCoordinate, yCoordinate, tPaint);
        xCoordinate = (float) src.getWidth() * 7 / 36;
        cs.drawText(String.valueOf(examinerDuties.getArzeshMelk()), xCoordinate, yCoordinate, tPaint);

        //TODO
        for (int i = 0; i < FormActivity.motherChildren.size(); i++) {
            MotherChild motherChild = FormActivity.motherChildren.get(i);
            yCoordinate = (float) src.getHeight() * (61 + i * 3) / 144;
            xCoordinate = (float) src.getWidth() * 25 / 36;
            cs.drawText(motherChild.karbari, xCoordinate, yCoordinate, tPaint);
            xCoordinate = (float) src.getWidth() * 19 / 36;
            cs.drawText(motherChild.noeShoql, xCoordinate, yCoordinate, tPaint);
            xCoordinate = (float) src.getWidth() * 16 / 36;
            cs.drawText(String.valueOf(motherChild.tedadVahed), xCoordinate, yCoordinate, tPaint);
            xCoordinate = (float) src.getWidth() * 7 / 36;
            cs.drawText(String.valueOf(motherChild.vahedMohasebe), xCoordinate, yCoordinate, tPaint);
            xCoordinate = (float) src.getWidth() * 2 / 36;
            cs.drawText(String.valueOf(motherChild.a), xCoordinate, yCoordinate, tPaint);
        }


        yCoordinate = (float) src.getHeight() * 92 / 144;
        xCoordinate = (float) src.getWidth() * 23 / 36;
        cs.drawText(String.valueOf(secondForm.getKhakiAb()), xCoordinate, yCoordinate, tPaint);
        xCoordinate = (float) src.getWidth() * 17 / 36;
        cs.drawText(String.valueOf(secondForm.getAsphalutAb()), xCoordinate, yCoordinate, tPaint);
        xCoordinate = (float) src.getWidth() * 10 / 36;
        cs.drawText(String.valueOf(secondForm.getSangFarshAb()), xCoordinate, yCoordinate, tPaint);
        xCoordinate = (float) src.getWidth() * 4 / 36;
        cs.drawText(String.valueOf(secondForm.getOtherAb()), xCoordinate, yCoordinate, tPaint);

        yCoordinate = (float) src.getHeight() * 96 / 144;
        xCoordinate = (float) src.getWidth() * 23 / 36;
        cs.drawText(String.valueOf(secondForm.getKhakiFazelab()), xCoordinate, yCoordinate, tPaint);
        xCoordinate = (float) src.getWidth() * 17 / 36;
        cs.drawText(String.valueOf(secondForm.getAsphalutFazelab()), xCoordinate, yCoordinate, tPaint);
        xCoordinate = (float) src.getWidth() * 10 / 36;
        cs.drawText(String.valueOf(secondForm.getSangFarshFazekab()), xCoordinate, yCoordinate, tPaint);
        xCoordinate = (float) src.getWidth() * 4 / 36;
        cs.drawText(String.valueOf(secondForm.getOtherFazelab()), xCoordinate, yCoordinate, tPaint);

        yCoordinate = (float) src.getHeight() * 99 / 144;
        xCoordinate = (float) src.getWidth() * 29 / 36;
        cs.drawText(String.valueOf(secondForm.getQotreLoole()), xCoordinate, yCoordinate, tPaint);
        xCoordinate = (float) src.getWidth() * 21 / 36;
        cs.drawText(secondForm.getJenseLoole(), xCoordinate, yCoordinate, tPaint);
        xCoordinate = (float) src.getWidth() * 10 / 36;
        cs.drawText(getString(R.string.monaseb), xCoordinate, yCoordinate, tPaint);
        if (!secondForm.isVaziatNasbePomp())
            cs.drawText(getString(R.string.namonaseb), xCoordinate, yCoordinate, tPaint);
        xCoordinate = (float) src.getWidth() * 3 / 36;
        cs.drawText(String.valueOf(secondForm.getOmqFazelab()), xCoordinate, yCoordinate, tPaint);

        yCoordinate = (float) src.getHeight() * 103 / 144;
        xCoordinate = (float) src.getWidth() * 29 / 36;
        cs.drawText(String.valueOf(secondForm.getOmqeZirzamin()), xCoordinate, yCoordinate, tPaint);
        xCoordinate = (float) src.getWidth() * 19 / 36;
        cs.drawText(getString(R.string.have), xCoordinate, yCoordinate, tPaint);
        if (!secondForm.isEtesalZirzamin())
            cs.drawText(getString(R.string.have_n), xCoordinate, yCoordinate, tPaint);
        xCoordinate = (float) src.getWidth() * 8 / 36;
        cs.drawText(secondForm.getNoeMasrafString(), xCoordinate, yCoordinate, tPaint);


        yCoordinate = (float) src.getHeight() * 107 / 144;
        xCoordinate = (float) src.getWidth() * 25 / 36;
        if (FormActivity.estelamShahrdari)
            cs.drawText(getString(R.string.have), xCoordinate, yCoordinate, tPaint);
        else cs.drawText(getString(R.string.have_n), xCoordinate, yCoordinate, tPaint);
        xCoordinate = (float) src.getWidth() * 19 / 36;
        cs.drawText(getString(R.string.have), xCoordinate, yCoordinate, tPaint);
        if (FormActivity.parvane)
            cs.drawText(getString(R.string.have), xCoordinate, yCoordinate, tPaint);
        else cs.drawText(getString(R.string.have_n), xCoordinate, yCoordinate, tPaint);
        xCoordinate = (float) src.getWidth() * 4 / 36;
        if (FormActivity.motaqazi)
            cs.drawText(getString(R.string.have), xCoordinate, yCoordinate, tPaint);
        else cs.drawText(getString(R.string.have_n), xCoordinate, yCoordinate, tPaint);


        return dest;
    }

    @SuppressLint("SimpleDateFormat")
    MultipartBody.Part bitmapToFile(Bitmap bitmap) {
        String fileNameToSave;
        String timeStamp = (new SimpleDateFormat("yyyyMMdd_HHmmss")).format(new Date());
        fileNameToSave = "JPEG_" + timeStamp + "_";
        File f = new File(context.getCacheDir(), fileNameToSave);
        try {
            f.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //Convert bitmap to byte array
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 0 /*ignored for PNG*/, bos);
        byte[] bitmapData = bos.toByteArray();
        //write the bytes in file
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(f);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            if (fos != null) {
                fos.write(bitmapData);
                fos.flush();
                fos.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        RequestBody reqFile = RequestBody.create(MediaType.parse("image/jpeg"), f);
        return MultipartBody.Part.createFormData("imageFile", f.getName(), reqFile);
    }

    public void saveTempBitmap(Bitmap bitmap) {
        if (isExternalStorageWritable()) {
            saveImage(bitmap);
        } else {
            Log.e("error", "isExternalStorageWritable");
        }
    }

    @SuppressLint("SimpleDateFormat")
    void saveImage(Bitmap bitmapImage) {
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES) + "/AbfaCamera/");
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return;
            }
        }
        String timeStamp = (new SimpleDateFormat("yyyyMMdd_HHmmss")).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + ".jpg";
        File file = new File(mediaStorageDir, imageFileName);
        if (file.exists()) file.delete();
        try {
            FileOutputStream out = new FileOutputStream(file);
            bitmapImage.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();

            DaoImages daoImages = dataBase.daoImages();
            Images image = new Images(imageFileName, billId, trackNumber,
                    String.valueOf(docId), "ارزیابی",
                    bitmapImage, true);
            if (isNew)
                image.setBillId("");
            else image.setTrackingNumber("");
            daoImages.insertImage(image);
        } catch (Exception e) {
            Log.e("error", Objects.requireNonNull(e.getMessage()));
            e.printStackTrace();
        }
    }

    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    @SuppressLint("StaticFieldLeak")
    class CreateImage extends AsyncTask<Integer, Integer, Integer> {
        CustomProgressBar progressDialog;
        Context context;

        public CreateImage(Context context) {
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
            DaoCalculationUserInput daoCalculationUserInput = dataBase.daoCalculationUserInput();
            daoCalculationUserInput.updateCalculationUserInput(true, trackNumber);
            uploadImage();

        }
    }

    class SendCalculationIncomplete implements ICallbackIncomplete<SimpleMessage> {
        @Override
        public void executeIncomplete(Response<SimpleMessage> response) {
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
                saveTempBitmap(ScannerConstants.bitmapSelectedImage);
                Toast.makeText(CreateImageActivity.this,
                        CreateImageActivity.this.getString(R.string.error_upload), Toast.LENGTH_LONG).show();
            }
        }
    }

    class UploadImageDocIncomplete implements ICallbackIncomplete<UploadImage> {

        @Override
        public void executeIncomplete(Response<UploadImage> response) {
            CustomErrorHandlingNew customErrorHandlingNew = new CustomErrorHandlingNew(context);
            String error = customErrorHandlingNew.getErrorMessageDefault(response);
            new CustomDialog(DialogType.Yellow, CreateImageActivity.this, error,
                    CreateImageActivity.this.getString(R.string.dear_user),
                    CreateImageActivity.this.getString(R.string.login),
                    CreateImageActivity.this.getString(R.string.accepted));
            saveTempBitmap(ScannerConstants.bitmapSelectedImage);
        }
    }

    class GetError implements ICallbackError {
        @Override
        public void executeError(Throwable t) {
            CustomErrorHandlingNew customErrorHandlingNew = new CustomErrorHandlingNew(context);
            String error = customErrorHandlingNew.getErrorMessageTotal(t);
            Toast.makeText(CreateImageActivity.this, error, Toast.LENGTH_LONG).show();
//            finish();
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
        Runtime.getRuntime().totalMemory();
        Runtime.getRuntime().freeMemory();
        Runtime.getRuntime().maxMemory();
        Debug.getNativeHeapAllocatedSize();
    }
}