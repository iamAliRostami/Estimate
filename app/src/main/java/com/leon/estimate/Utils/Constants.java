package com.leon.estimate.Utils;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;

import com.leon.estimate.Tables.Arzeshdaraei;
import com.leon.estimate.Tables.CalculationUserInput;
import com.leon.estimate.Tables.ExaminerDuties;
import com.leon.estimate.Tables.RequestDictionary;
import com.leon.estimate.Tables.SecondForm;
import com.leon.estimate.Tables.Tejariha;
import com.leon.estimate.adapters.CustomListAdapter;

import java.util.ArrayList;
import java.util.List;

public class Constants {
    public static Bitmap bitmapSelectedImage;
    public static Bitmap bitmapMapImage;
    public static String fileName;
    public static String imageError = "هیچ تصویری انتخاب نشده است ، لطفاً دوباره امتحان کنید.";
    public static String cropError = "شما یک فیلد معتبر انتخاب نکرده اید. لطفاً تا زمانی که خطوط به رنگ آبی نباشد ، تصحیح کنید.";
    public static String progressColor = "#331199";

    public static String karbari, noeVagozari;
    public static List<RequestDictionary> requestDictionaries;
    public static ExaminerDuties examinerDuties;
    public static CalculationUserInput calculationUserInput, calculationUserInputTemp;
    public static SecondForm secondForm;
    public static Arzeshdaraei arzeshdaraei;
    public static int value;
    public static ArrayList<Tejariha> tejarihas;
    public static ArrayList<Integer> valueInteger;

    @SuppressLint("StaticFieldLeak")
    public static CustomListAdapter customAdapter;
}
