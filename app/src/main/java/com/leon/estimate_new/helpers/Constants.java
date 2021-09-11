package com.leon.estimate_new.helpers;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;

import java.util.ArrayList;

public class Constants {
    public static final String fontName = "font/font_1.ttf";
    public static final int TOAST_TEXT_SIZE = 20;
    public static final int GPS_CODE = 1231;
    public static final int REQUEST_NETWORK_CODE = 1232;
    public static final int REQUEST_WIFI_CODE = 1233;
    public static final int CARRIER_PRIVILEGE_STATUS = 901;
    public static final int CAMERA = 1446;
    public static final int REPORT = 1445;
    public static final int NAVIGATION = 1903;
    public static final int COUNTER_LOCATION = 1914;
    public static final int DESCRIPTION = 1909;
    public static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10;
    public static final long MIN_TIME_BW_UPDATES = 10000;
    public static final long FASTEST_INTERVAL = 10000;
    public static final int REQUEST_LOCATION_CODE = 1236;
    public final static int GALLERY_IMAGE_LOADED = 1001;

    public static String karbari, noeVagozari, qotrEnsheab;
    public static ArrayList<Integer> valueInteger;
    public static Bitmap selectedImageBitmap;

    public static final int IMAGE_CROP_REQUEST = 1234;
    public static final int IMAGE_BRIGHTNESS_CONTRAST_REQUEST = 1324;
    public static final int MAX_IMAGE_SIZE = 200000;
    public static final int CAMERA_REQUEST = 1888;
    public static final int GALLERY_REQUEST = 1889;
    public static final String DATABASE_NAME = "MyDatabase";

    public static Uri PHOTO_URI;
    public static Bitmap BITMAP_SELECTED;
    public static String IMAGE_FILE_NAME;

    public static Context appContext;
}
