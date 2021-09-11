package com.leon.estimate_new.helpers;

import android.app.Application;
import android.content.Context;
import android.os.Bundle;

import com.google.firebase.analytics.FirebaseAnalytics;

public class MyApplication extends Application {

    public static String getDBNAME() {
        return Constants.DATABASE_NAME;
    }

    public static Context getContext() {
        return Constants.appContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Constants.appContext = getApplicationContext();
        FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "id");
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "name");
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "image");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
    }
}
