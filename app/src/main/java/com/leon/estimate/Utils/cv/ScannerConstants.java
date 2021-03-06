package com.leon.reading_counter.utils.cv;

import android.graphics.Bitmap;

public class ScannerConstants {
    public static Bitmap selectedImageBitmap;
    public static String cropText = "KIRP", backText = "KAPAT",
            imageError = "Görsel seçilmedi, lütfen tekrar deneyin.",
            cropError = "Geçerli bir alan seçmediniz. Lütfen çizgiler mavi olana dek düzeltme yapın.";
    public static String cropColor = "#6666ff", backColor = "#ff0000", progressColor = "#331199";
    public static boolean saveStorage = false;
}