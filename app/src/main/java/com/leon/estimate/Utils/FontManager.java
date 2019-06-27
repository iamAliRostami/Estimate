package com.leon.estimate.Utils;

import android.content.Context;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Stack;

/**
 * Created by Leon on 2/18/2018.
 */

public class FontManager {
    Context context;
    private Typeface typeface;
    private String fontName = "font/BYekan_3.ttf";

    public FontManager(Context context) {
        this.context = context;
        initializeTypeface();
    }

    private void initializeTypeface() {
        typeface = Typeface.createFromAsset(context.getAssets(), fontName);
    }

    public void setFont(ViewGroup viewGroup) {
        Stack<ViewGroup> stackOfViewGroup = new Stack<ViewGroup>();
        stackOfViewGroup.push(viewGroup);
        while (!stackOfViewGroup.isEmpty()) {
            ViewGroup tree = stackOfViewGroup.pop();
            for (int i = 0; i < tree.getChildCount(); i++) {
                View child = tree.getChildAt(i);
                if (child instanceof ViewGroup) {
                    stackOfViewGroup.push((ViewGroup) child);
                } else if (child instanceof Button) {
                    ((Button) child).setTypeface(typeface);
                } else if (child instanceof EditText) {
                    ((EditText) child).setTypeface(typeface);
                } else if (child instanceof TextView) {
                    ((TextView) child).setTypeface(typeface);
                } else if (child instanceof ListView) {
                    TextView textView = (TextView) ((ListView) child).getChildAt(0);
                    textView.setTypeface(typeface);
                    textView = (TextView) ((ListView) child).getChildAt(2);
                    textView.setTypeface(typeface);
                }
            }
        }
    }

    public void setFont(SpannableString spannableString) {
        spannableString.setSpan(typeface, 0, spannableString.length(), 0);
    }

    public void setFont(View view) {
        ((TextView) view).setTypeface(typeface);
    }
}
