package com.leon.estimate.Utils.GIS;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import androidx.room.Room;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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
import com.leon.estimate.Utils.Constants;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class CustomFile {

    @SuppressLint("SimpleDateFormat")
    public static MultipartBody.Part bitmapToFile(Bitmap bitmap, Context context) {
        String timeStamp = (new SimpleDateFormat(context.getString(R.string.save_format_name))).format(new Date());
        String fileNameToSave = "JPEG_" + timeStamp + "_";
        File f = new File(context.getCacheDir(), fileNameToSave);
        try {
            f.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //Convert bitmap to byte array
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 40 /*ignored for PNG*/, bos);
        byte[] bitmapData = bos.toByteArray();
        //write the bytes in file
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(f);
            fos.write(bitmapData);
            fos.flush();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        RequestBody reqFile = RequestBody.create(MediaType.parse("image/jpeg"), f);
        return MultipartBody.Part.createFormData("imageFile", f.getName(), reqFile);
    }

    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    public static void saveTempBitmap(Bitmap bitmap, Context context) {
        if (isExternalStorageWritable()) {
            saveImage(bitmap, context);
        } else {
            Log.e("error", "ExternalStorage is not Writable");
            Toast.makeText(context, context.getString(R.string.error_external_storage_is_not_writable), Toast.LENGTH_LONG).show();
        }
    }

    @SuppressLint("SimpleDateFormat")
    static void saveImage(Bitmap bitmapImage, Context context) {
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES) + context.getString(R.string.camera_folder));
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return;
            }
        }
        String timeStamp = (new SimpleDateFormat(context.getString(R.string.save_format_name))).format(new Date());
        String fileNameToSave = "JPEG_" + timeStamp + "_";
        File file = new File(mediaStorageDir, fileNameToSave);
        if (file.exists()) file.delete();
        try {
            FileOutputStream out = new FileOutputStream(file);
            bitmapImage.compress(Bitmap.CompressFormat.JPEG, 40, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("error", Objects.requireNonNull(e.getMessage()));
        }
        MediaScannerConnection.scanFile(context, new String[]{file.getPath()}, new String[]{"image/jpeg"}, null);
    }

    @SuppressLint({"SimpleDateFormat"})
    public static File createImageFile(Context context) throws IOException {
        String timeStamp = (new SimpleDateFormat(context.getString(R.string.save_format_name))).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        storageDir.mkdirs();
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        StringBuilder stringBuilder = (new StringBuilder()).append("file:");
        Objects.requireNonNull(image);
        Constants.fileName = stringBuilder.append(image.getAbsolutePath()).toString();
        return image;
    }

    static File findFile(File dir, String name) {
        File[] children = dir.listFiles();
        if (children != null) {
            for (File child : children) {
                if (child.isDirectory()) {
                    File found = findFile(child, name);
                    if (found != null) return found;
                } else {
                    if (name.equals(child.getName())) return child;
                }
            }
        }
        return null;
    }

    public static void readData(Context context) {
        File sdcard = Environment.getExternalStorageDirectory();
        File file = new File(sdcard, "json.txt");
        StringBuilder text = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }
            br.close();
        } catch (IOException ignored) {
            Log.e("Error", ignored.toString());
        }
        String json = text.toString();
        Log.e("json", json);
        Gson gson = new GsonBuilder().create();
        Input input = gson.fromJson(json, Input.class);
        List<ExaminerDuties> examinerDutiesList = input.getExaminerDuties();
        for (int i = 0; i < examinerDutiesList.size(); i++) {
            Gson gson1 = new Gson();
            examinerDutiesList.get(i).setRequestDictionaryString(
                    gson1.toJson(examinerDutiesList.get(i).getRequestDictionary()));
        }
        MyDatabase dataBase = Room.databaseBuilder(context, MyDatabase.class, MyApplication.getDBNAME())
                .allowMainThreadQueries().build();
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
                if (examinerDuties.getTrackNumber().equals(examinerDutiesTemp.getTrackNumber())) {
                    examinerDutiesList.remove(i);
                    j = examinerDutiesListTemp.size();
                    i--;
                }
            }
        }
        daoExaminerDuties.insertAll(examinerDutiesList);
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
