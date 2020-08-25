package com.leon.estimate.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import com.leon.estimate.Enums.DialogType;
import com.leon.estimate.Enums.ProgressType;
import com.leon.estimate.Enums.SharedReferenceKeys;
import com.leon.estimate.Enums.SharedReferenceNames;
import com.leon.estimate.Infrastructure.IAbfaService;
import com.leon.estimate.Infrastructure.ICallback;
import com.leon.estimate.Infrastructure.ICallbackError;
import com.leon.estimate.Infrastructure.ICallbackIncomplete;
import com.leon.estimate.R;
import com.leon.estimate.Tables.Images;
import com.leon.estimate.Utils.CustomDialog;
import com.leon.estimate.Utils.CustomErrorHandlingNew;
import com.leon.estimate.Utils.HttpClientWrapper;
import com.leon.estimate.Utils.NetworkHelper;
import com.leon.estimate.Utils.SharedPreferenceManager;
import com.leon.estimate.fragments.HighQualityFragment;

import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ImageViewAdapter extends BaseAdapter {
    public ArrayList<Images> images;
    LayoutInflater inflater;
    Context context;
    ImageViewHolder holder;

    public ImageViewAdapter(Context c, ArrayList<Images> images) {
        this.images = images;
        context = c;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getCount() {
        return images.size();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return 0;
    }

    @SuppressLint({"ViewHolder", "InflateParams"})
    public View getView(int position, View convertView, ViewGroup parent) {
//        ImageViewHolder holder;
        Images imageDataTitle = images.get(position);
        View view = convertView;
        if (view == null) {
            view = inflater.inflate(R.layout.item_image, null);
        }
        holder = new ImageViewHolder(view);
        holder.textView.setText(imageDataTitle.getDocTitle());
        holder.imageView.setImageBitmap(imageDataTitle.getBitmap());
//        holder.imageView.setImageBitmap(getResizedBitmap(imageDataTitle.getBitmap(), 500));
        holder.imageView.setOnClickListener(view1 -> {
            if (imageDataTitle.getUri() == null) {
                FragmentTransaction fragmentTransaction = ((FragmentActivity) context).
                        getSupportFragmentManager().beginTransaction();
                HighQualityFragment highQualityFragment =
                        HighQualityFragment.newInstance(imageDataTitle.getBitmap(), imageDataTitle.getDocTitle());
                highQualityFragment.show(fragmentTransaction, "Image # 2");
            } else {
                Log.e("uri", imageDataTitle.getUri().replace("thumbnail", "main"));
                getImageMain(imageDataTitle.getUri().replace("thumbnail", "main"));
            }
        });
        return view;
    }

    void getImageMain(String uri) {
        Retrofit retrofit = NetworkHelper.getInstance(true, "");
//        Retrofit retrofit = NetworkHelper.getInstanceWithCache(context);
        final IAbfaService getImage = retrofit.create(IAbfaService.class);

        SharedPreferenceManager sharedPreferenceManager = new SharedPreferenceManager(context,
                SharedReferenceNames.ACCOUNT.getValue());
        Call<ResponseBody> call = getImage.getDoc(sharedPreferenceManager.getStringData(
                SharedReferenceKeys.TOKEN_FOR_FILE.getValue()), new com.leon.estimate.Tables.Uri(uri));
        GetImageDoc imageDoc = new GetImageDoc();
        GetError error = new GetError();
        GetImageDocErrorIncomplete incomplete = new GetImageDocErrorIncomplete();
        HttpClientWrapper.callHttpAsync(call, ProgressType.SHOW.getValue(), context,
                imageDoc, incomplete, error);
    }

    Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }

    public static class ImageViewHolder {

        public ImageView imageView;
        public TextView textView;

        public ImageViewHolder(View view) {
            imageView = view.findViewById(R.id.imageView);
            textView = view.findViewById(R.id.textViewTitle);
        }

    }

    class GetImageDoc implements ICallback<ResponseBody> {
        @Override
        public void execute(ResponseBody responseBody) {
            Bitmap bmp = BitmapFactory.decodeStream(responseBody.byteStream());
//            holder.imageView.setImageBitmap(bmp);
            if (bmp != null) {
                FragmentTransaction fragmentTransaction = ((FragmentActivity) context).
                        getSupportFragmentManager().beginTransaction();
                HighQualityFragment highQualityFragment =
                        HighQualityFragment.newInstance(bmp, "Image # 1");
                highQualityFragment.show(fragmentTransaction, "Image # 2");
            }
        }
    }

    class GetImageDocErrorIncomplete implements ICallbackIncomplete<ResponseBody> {

        @Override
        public void executeIncomplete(Response<ResponseBody> response) {
            CustomErrorHandlingNew customErrorHandlingNew = new CustomErrorHandlingNew(context);
            String error = customErrorHandlingNew.getErrorMessageDefault(response);
            new CustomDialog(DialogType.Yellow, context, error,
                    context.getString(R.string.dear_user),
                    context.getString(R.string.download_document),
                    context.getString(R.string.accepted));
        }
    }

    class GetError implements ICallbackError {
        @Override
        public void executeError(Throwable t) {
            CustomErrorHandlingNew customErrorHandlingNew = new CustomErrorHandlingNew(context);
            String error = customErrorHandlingNew.getErrorMessageTotal(t);
            Toast.makeText(context, error, Toast.LENGTH_LONG).show();
        }
    }
}
