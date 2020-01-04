package com.leon.estimate.Utils;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.leon.estimate.Fragments.Form1Fragment;
import com.leon.estimate.Fragments.MapFragment;
import com.leon.estimate.R;

import org.jetbrains.annotations.NotNull;

public class MyPagerAdapter extends FragmentPagerAdapter {
    private static int NUM_ITEMS = 2;
    Context context;

    public MyPagerAdapter(FragmentManager fragmentManager, Context context) {
        super(fragmentManager);
        this.context = context;
    }

    @Override
    public int getCount() {
        return NUM_ITEMS;
    }

    // Returns the fragment to display for that page
    @NotNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0: // Fragment # 0 - This will show FirstFragment
                return Form1Fragment.newInstance("0", "Page # 1");
            case 1: // Fragment # 0 - This will show FirstFragment different title
                return MapFragment.newInstance("1", "Page # 2");
            case 2: // Fragment # 1 - This will show SecondFragment
//                return Form2Fragment.newInstance("2", "Page # 3");
            case 3: // Fragment # 1 - This will show SecondFragment
//                return Form4Fragment.newInstance("4", "Page # 4");
            default:
                return null;
        }
    }

    // Returns the page title for the top indicator
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return context.getString(R.string.base_information);
            case 1:
                return context.getString(R.string.map_information);
            case 2:
//                return context.getString(R.string.final_information);
            case 3:
//                return context.getString(R.string.information);
            default:
                return null;
        }
//        return "Page " + position;
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return super.getItemPosition(object);
    }
}