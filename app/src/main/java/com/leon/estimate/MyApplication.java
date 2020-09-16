package com.leon.estimate;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;


public class MyApplication extends Application {

    public static final String fontName = "font/my_font1.ttf";
    @SuppressLint("StaticFieldLeak")
    private static Context sContext;

    public static String getDBNAME() {
        return "MyDatabase";
    }

    public static Context getContext() {
        return sContext;
    }

    @Override
    public void onCreate() {
        sContext = getApplicationContext();
        super.onCreate();
    }
}
