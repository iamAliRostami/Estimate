package com.leon.estimate.Utils;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.util.Log;
import android.widget.Toast;

import com.leon.estimate.Enums.DialogType;
import com.leon.estimate.Enums.ErrorHandlerType;
import com.leon.estimate.Enums.ProgressType;
import com.leon.estimate.Infrastructure.ICallback;
import com.leon.estimate.R;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Leon on 12/12/2017.
 */

final public class HttpClientWrapper {

    private HttpClientWrapper() {
    }

    public static <T> void callHttpAsync(Call<T> call, final ICallback callback, final Context context, int dialogType) {
        callHttpAsync(call, callback, context, dialogType, ErrorHandlerType.ordinary);
    }

    public static <T> void callHttpAsync(Call<T> call, final ICallback callback, final Context context, int dialogType, final ErrorHandlerType errorHandlerType) {
        final ProgressDialog dialog = new ProgressDialog(context);
        if (dialogType == ProgressType.SHOW.getValue() || dialogType == ProgressType.SHOW_CANCELABLE.getValue()) {
            dialog.setMessage(context.getString(R.string.loading_getting_info));
            dialog.setTitle(context.getString(R.string.loading_connecting));
            dialog.show();
            if (ProgressType.SHOW_CANCELABLE.getValue() == dialogType)
                dialog.setCancelable(true);
            else dialog.setCancelable(false);
        }
        if (isOnline(context)) {
            final String[] error = new String[1];
            call.enqueue(new Callback<T>() {
                @Override
                public void onResponse(Call<T> call, Response<T> response) {
                    try {
                        if (response.isSuccessful()) {
                            if (response.code() == 204) {
                                new CustomDialog(DialogType.Green, context, "ارزیابی جدید برای شما وجود ندارد.",
                                        context.getString(R.string.dear_user), context.getString(R.string.receive), context.getString(R.string.accepted));
                                dialog.cancel();
                            } else {
                                T responseT = response.body();
                                callback.execute(responseT);
                                dialog.dismiss();
                            }
                        } else {
                            try {
                                JSONObject jsonObject = new JSONObject(response.errorBody().string());
                                error[0] = jsonObject.getString(context.getString(R.string.message));
                            } catch (Exception e) {
                                CustomErrorHandling customErrorHandling = new CustomErrorHandling(context);
                                error[0] = customErrorHandling.getErrorMessage(response.code(), errorHandlerType);
                            }
                            new CustomDialog(DialogType.Yellow, context, error[0], context.getString(R.string.dear_user),
                                    context.getString(R.string.error), context.getString(R.string.accepted));
                            dialog.dismiss();
                        }
                    } catch (Exception e) {

                        try {
                            JSONObject jsonObject = new JSONObject(response.errorBody().string());
                            error[0] = jsonObject.getString(context.getString(R.string.message));
                        } catch (Exception e1) {
                            CustomErrorHandling customErrorHandling = new CustomErrorHandling(context);
                            error[0] = customErrorHandling.getErrorMessage(response.code(), errorHandlerType);
                        }
                        new CustomDialog(DialogType.Yellow, context, error[0], context.getString(R.string.dear_user),
                                context.getString(R.string.error), context.getString(R.string.accepted));
                        dialog.dismiss();
                    }
                }

                @Override
                public void onFailure(Call<T> call, Throwable t) {
                    Activity activity = (Activity) context;
                    Log.e("error", t.getMessage());
                    Log.e("error", t.toString());
                    if (!activity.isFinishing()) {
                        CustomErrorHandling customErrorHandling = new CustomErrorHandling(context);
                        error[0] = customErrorHandling.getErrorMessageTotal(t);
                        new CustomDialog(DialogType.Red, context, error[0], context.getString(R.string.dear_user),
                                context.getString(R.string.error), context.getString(R.string.accepted));
                    }
                    dialog.dismiss();
                }
            });
        } else {
            dialog.dismiss();
            Toast.makeText(context, "اینترنت وصل نیست، اینترنت وایفای یا همراه خود را روشن کنید.", Toast.LENGTH_LONG).show();
        }
    }

    public static <T> void callHttpAsync(Call<T> call, final ICallback callback, final ErrorHandlerType errorHandlerType) {
        final String[] error = new String[1];
//        Context context = MyApplication.getContext();
        call.enqueue(new Callback<T>() {
            @Override
            public void onResponse(Call<T> call, Response<T> response) {
                try {
                    if (response.isSuccessful()) {
                        T responseT = response.body();
                        callback.execute(responseT);
                    } else {
                        try {
                            JSONObject jsonObject = new JSONObject(response.errorBody().string());
//                            error[0] = jsonObject.getString(context.getString(R.string.message));
                        } catch (Exception e) {
//                            CustomErrorHandling customErrorHandling = new CustomErrorHandling(context);
//                            error[0] = customErrorHandling.getErrorMessage(response.code(), errorHandlerType);
                        }
//                        Toast.makeText(context, error[0], Toast.LENGTH_SHORT).show();
//                        Log.e("Error", error[0]);
                    }
                } catch (Exception e) {

                    try {
//                        JSONObject jsonObject = new JSONObject(response.errorBody().string());
//                        error[0] = jsonObject.getString(context.getString(R.string.message));
                    } catch (Exception e1) {
//                        CustomErrorHandling customErrorHandling = new CustomErrorHandling(context);
//                        error[0] = customErrorHandling.getErrorMessage(response.code(), errorHandlerType);
                    }
//                    Toast.makeText(context, error[0], Toast.LENGTH_SHORT).show();
//                    Log.e("Error", error[0]);
                }
            }

            @Override
            public void onFailure(Call<T> call, Throwable t) {
//                CustomErrorHandling customErrorHandling = new CustomErrorHandling(context);
//                error[0] = customErrorHandling.getErrorMessageTotal(t);
//                Log.e("Error", error[0]);
//                Toast.makeText(context, error[0], Toast.LENGTH_SHORT).show();
            }
        });
    }

    private static boolean isOnline(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }
}
