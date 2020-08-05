package com.leon.estimate.Utils;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.leon.estimate.Enums.ProgressType;
import com.leon.estimate.Infrastructure.ICallback;
import com.leon.estimate.Infrastructure.ICallbackError;
import com.leon.estimate.Infrastructure.ICallbackIncomplete;
import com.leon.estimate.R;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HttpClientWrapper {
    public static <T> void callHttpAsync(Call<T> call, int dialogType,
                                         final Context context,
                                         final ICallback<T> callback,
                                         final ICallbackIncomplete<T> callbackIncomplete,
                                         final ICallbackError callbackError) {
        CustomProgressBar progressBar = new CustomProgressBar();
        if (dialogType == ProgressType.SHOW.getValue() ||
                dialogType == ProgressType.SHOW_CANCELABLE.getValue()) {
            progressBar.show(context, context.getString(R.string.waiting), true);
        }
        if (isOnline(context)) {
            call.enqueue(new Callback<T>() {
                @Override
                public void onResponse(@NonNull Call<T> call, @NonNull Response<T> response) {
                    if (response.isSuccessful()) {
                        callback.execute(response.body());
                    } else {
                        ((Activity) context).runOnUiThread(() -> callbackIncomplete.executeIncomplete(response));
                    }
                    if (progressBar.getDialog() != null)
                        progressBar.getDialog().dismiss();
                }

                @Override
                public void onFailure(@NonNull Call<T> call, @NonNull Throwable t) {
                    ((Activity) context).runOnUiThread(() -> callbackError.executeError(t));
                    if (progressBar.getDialog() != null)
                        progressBar.getDialog().dismiss();
                }
            });
        } else {
            if (progressBar.getDialog() != null)
                progressBar.getDialog().dismiss();
            Toast.makeText(context, R.string.turn_internet_on, Toast.LENGTH_SHORT).show();
        }

    }

    private static boolean isOnline(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return Objects.requireNonNull(cm).getActiveNetworkInfo() != null &&
                Objects.requireNonNull(cm.getActiveNetworkInfo()).isConnectedOrConnecting();
    }
}
