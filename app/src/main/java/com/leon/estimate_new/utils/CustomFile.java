package com.leon.estimate_new.utils;

import static com.leon.estimate_new.helpers.Constants.IMAGE_FILE_NAME;
import static com.leon.estimate_new.helpers.Constants.MAX_IMAGE_SIZE;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;

import com.leon.estimate_new.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class CustomFile {
    @SuppressLint("SimpleDateFormat")
    public static MultipartBody.Part bitmapToFile(Bitmap bitmap, Context context) {
        String timeStamp = (new SimpleDateFormat(context.getString(R.string.save_format_name))).format(new Date());
        String fileNameToSave = "JPEG_" + new Random().nextInt() + "_" + timeStamp + ".jpg";
        File f = new File(context.getCacheDir(), fileNameToSave);
        try {
            f.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        byte[] bitmapData = compressBitmap(bitmap);
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(f);
            fos.write(bitmapData);
            fos.flush();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        RequestBody requestBody = RequestBody.create(MediaType.parse("image/jpeg"), f);
        return MultipartBody.Part.createFormData("imageFile", f.getName(), requestBody);
    }

    static byte[] compressBitmap(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        if (stream.toByteArray().length > MAX_IMAGE_SIZE) {
            int qualityPercent = Math.max((int) ((double)
                    stream.toByteArray().length / MAX_IMAGE_SIZE), 20);
            bitmap = Bitmap.createScaledBitmap(bitmap
                    , (int) ((double) bitmap.getWidth() * qualityPercent / 100)
                    , (int) ((double) bitmap.getHeight() * qualityPercent / 100), false);
            stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        }
        return stream.toByteArray();
    }

    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    @SuppressLint({"SimpleDateFormat"})
    public static File createImageFile(Context context) throws IOException {
        String timeStamp = (new SimpleDateFormat(context.getString(R.string.save_format_name))).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        StringBuilder stringBuilder = (new StringBuilder()).append("file:");
        IMAGE_FILE_NAME = stringBuilder.append(image.getAbsolutePath()).toString();
        return image;
    }
}
