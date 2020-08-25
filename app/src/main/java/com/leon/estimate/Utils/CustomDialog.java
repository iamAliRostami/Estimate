package com.leon.estimate.Utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.leon.estimate.Enums.DialogType;
import com.leon.estimate.R;
import com.leon.estimate.activities.ListActivity;


/**
 * Created by Leon on 12/11/2017.
 */

public class CustomDialog {
    @SuppressLint("StaticFieldLeak")
    static LovelyStandardDialog lovelyStandardDialog;
    Context context;
    String Top, Title, Message, ButtonText;


    public CustomDialog(DialogType choose, Context context, String message, String title, String top, String buttonText) {
        this.context = context;
        Message = message;
        Title = title;
        Top = top;
        ButtonText = buttonText;
        lovelyStandardDialog = new LovelyStandardDialog(context)
                .setTitle(Title)
                .setMessage(Message)
                .setTopTitle(Top);
        if (choose == DialogType.Green)
            CustomGreenDialog(this.context, ButtonText);
        else if (choose == DialogType.Yellow)
            CustomYellowDialog(this.context, ButtonText);
        else if (choose == DialogType.Red)
            CustomRedDialog(this.context, ButtonText);
        else if (choose == DialogType.GreenRedirect)
            CustomGreenDialogRedirect(this.context, ButtonText);
        else if (choose == DialogType.YellowRedirect)
            CustomYellowDialogRedirect(this.context, ButtonText);
        else if (choose == DialogType.RedRedirect)
            CustomRedDialogRedirect(this.context, ButtonText);
    }

    public static void CustomGreenDialogRedirect(final Context context, String ButtonText) {
        lovelyStandardDialog
                .setTopColorRes(R.color.green2)
                .setTopTitleColor(context.getResources().getColor(R.color.white))
                .setButtonsBackground(R.drawable.border_green_2)
                .setPositiveButton(ButtonText, v -> {
                    Intent intent = new Intent(context, ListActivity.class);
                    context.startActivity(intent);
                });
        lovelyStandardDialog.show();
    }

    public static void CustomYellowDialogRedirect(final Context context, String buttonText) {
        lovelyStandardDialog
                .setTopTitleColor(context.getResources().getColor(R.color.white))
                .setButtonsBackground(R.drawable.border_yellow_2)
                .setTopColorRes(R.color.yellow1)
                .setPositiveButton(buttonText, v -> {
                })
                .show();
    }

    public static void CustomRedDialogRedirect(final Context context, String buttonText) {
        lovelyStandardDialog
                .setTopColorRes(R.color.red1)
                .setTopTitleColor(context.getResources().getColor(R.color.white))
                .setButtonsBackground(R.drawable.border_red_2)
                .setPositiveButton(buttonText, v -> lovelyStandardDialog.dismiss())
                .show();
    }

    public static void CustomGreenDialog(final Context context, String ButtonText) {
        lovelyStandardDialog
                .setTopColorRes(R.color.green2)
                .setTopTitleColor(context.getResources().getColor(R.color.white))
                .setButtonsBackground(R.drawable.border_green_2)
                .setPositiveButton(ButtonText, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        lovelyStandardDialog.dismiss();
                    }
                })
                .show();
    }

    public static void CustomYellowDialog(final Context context, String buttonText) {
        lovelyStandardDialog
                .setTopTitleColor(context.getResources().getColor(R.color.white))
                .setTopColorRes(R.color.yellow1)
                .setButtonsBackground(R.drawable.border_yellow_2)
                .setPositiveButton(buttonText, v -> {
                })
                .show();
    }

    public static void CustomRedDialog(final Context context, String buttonText) {
        lovelyStandardDialog
                .setTopColorRes(R.color.red1)
                .setTopTitleColor(context.getResources().getColor(R.color.white))
                .setButtonsBackground(R.drawable.border_red_2)
                .setPositiveButton(buttonText, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        lovelyStandardDialog.dismiss();
                    }
                })
                .show();
    }

    public interface Inline {
        void inline(String negative, int negativeColor);
    }

}
