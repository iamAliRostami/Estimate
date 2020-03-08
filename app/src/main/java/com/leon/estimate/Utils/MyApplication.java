package com.leon.estimate.Utils;

import android.app.Application;
import android.content.Context;

import com.leon.estimate.R;
import com.mapbox.mapboxsdk.Mapbox;

public class MyApplication extends Application {
    private static Context context;

    public static Context getContext() {
        return context;
    }

    public static void setContext(Context context) {
        MyApplication.context = context;
    }

    @Override
    public void onCreate() {
        context = getApplicationContext();
        super.onCreate();
        Mapbox.getInstance(getApplicationContext(), getString(R.string.mapbox_access_token));
    }
}
