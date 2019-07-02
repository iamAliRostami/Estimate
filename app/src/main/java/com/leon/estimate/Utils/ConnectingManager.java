package com.leon.estimate.Utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.lang.reflect.Method;

import static android.content.Context.WIFI_SERVICE;

/**
 * Created by Leon on 12/18/2017.
 */

public class ConnectingManager {
    Context context;

    public ConnectingManager(Context context) {
        this.context = context;
    }

    public boolean isWifiOn() {
        WifiManager wifi = (WifiManager) context.getSystemService(WIFI_SERVICE);
        return wifi.isWifiEnabled();
    }

    public boolean checkMobileDataIsOn() {
        boolean mobileDataEnabled = false; // Assume disabled
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        try {
            Class cmClass = Class.forName(cm.getClass().getName());
            Method method = cmClass.getDeclaredMethod("getMobileDataEnabled");
            method.setAccessible(true); // Make the method callable
            // get the setting for "mobile data"
            mobileDataEnabled = (Boolean) method.invoke(cm);
            Log.e("data is ", mobileDataEnabled + "");
            return mobileDataEnabled;
        } catch (Exception e) {
            // Some problem accessible private API
            // TODO do whatever error handling you want here
            Log.e("data is ", " false catched error");
            return false;
        }
    }

}

