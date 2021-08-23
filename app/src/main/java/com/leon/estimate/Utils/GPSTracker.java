package com.leon.estimate.Utils;

import static com.leon.estimate.Utils.Constants.MIN_DISTANCE_CHANGE_FOR_UPDATES;
import static com.leon.estimate.Utils.Constants.MIN_TIME_BW_UPDATES;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.leon.estimate.R;

import org.osmdroid.config.Configuration;

public class GPSTracker extends Service {
    final Activity activity;
    boolean canGetLocation = false;
    double latitude;
    double longitude;
    boolean checkGPS = false;
    boolean checkNetwork = false;
    Location location;
    LocationManager locationManager;
    android.location.LocationListener locationListener = new android.location.LocationListener() {
        public void onLocationChanged(Location location) {
            if (locationManager != null)
                locationManager.removeUpdates(locationListener);
            latitude = location.getLatitude();
            longitude = location.getLongitude();
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        public void onProviderEnabled(String provider) {
        }

        public void onProviderDisabled(String provider) {
        }
    };

    public GPSTracker(Activity activity) {
        this.activity = activity;
        Configuration.getInstance().load(activity,
                PreferenceManager.getDefaultSharedPreferences(activity));
        getLocation();
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public boolean canGetLocation() {
        return this.canGetLocation;
    }

    @SuppressLint({"MissingPermission", "ShowToast"})
    public void getLocation() {
        try {
            locationManager = (LocationManager) activity
                    .getSystemService(LOCATION_SERVICE);
            // get GPS status
            checkGPS = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            // get network provider status
            checkNetwork = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            if (!checkGPS && !checkNetwork) {
                Toast.makeText(activity, getString(R.string.services_is_not_available), Toast.LENGTH_LONG).show();
            } else {
                this.canGetLocation = true;
                if (checkNetwork) {
                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, locationListener);//TODO
                    if (locationManager != null) {
                        location = locationManager
                                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        }
                    }
                }
                if (checkGPS && location == null) {
                    if (locationManager != null) {
                        locationManager.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, locationListener);//TODO
                    }
                    if (locationManager != null) {
                        location = locationManager
                                .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("error", e.toString());
        }
        new Handler().postDelayed(this::getLocation, MIN_TIME_BW_UPDATES);
    }

    public void stopListener() {
        if (locationManager != null)
            locationManager.removeUpdates(locationListener);
    }

    public double getAccuracy() {
        getLocation();
        return location.getAccuracy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        stopListener();
        return null;
    }
}