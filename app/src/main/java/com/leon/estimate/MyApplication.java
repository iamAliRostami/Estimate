package com.leon.estimate;

import android.app.Application;
import android.content.Context;


public class MyApplication extends Application {

    private static Context sContext;

    public static Context getContext() {
        return sContext;
    }

    @Override
    public void onCreate() {
        sContext = getApplicationContext();
        super.onCreate();
    }
}
