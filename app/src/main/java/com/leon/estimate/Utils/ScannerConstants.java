package com.leon.estimate.Utils;

import android.graphics.Bitmap;

public class ScannerConstants {
    public static Bitmap bitmapSelectedImage;
    public static String fileName;
    public static String cropText = "برش", backText = "بازگشت",
            imageError = "هیچ تصویری انتخاب نشده است ، لطفاً دوباره امتحان کنید.",
            cropError = "شما یک فیلد معتبر انتخاب نکرده اید. لطفاً تا زمانی که خطوط به رنگ آبی نباشد ، تصحیح کنید.";
    public static String cropColor = "#6666ff", backColor = "#ff0000", progressColor = "#331199";
    public static boolean saveStorage = false;
}
