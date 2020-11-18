package com.leon.estimate.adapters;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.leon.estimate.R;
import com.leon.estimate.Tables.ExaminerDuties;
import com.leon.estimate.fragments.FormFragment;
import com.leon.estimate.fragments.MapFragment;
import com.leon.estimate.fragments.PersonalFragment;
import com.leon.estimate.fragments.ServicesFragment;

import org.jetbrains.annotations.NotNull;

public class MyPagerFormAdapter extends FragmentPagerAdapter {
    private static final int NUM_ITEMS = 2;
    ExaminerDuties examinerDuties;
    Context context;

    public MyPagerFormAdapter(FragmentManager fragmentManager, Context context, ExaminerDuties examinerDuties) {
        super(fragmentManager);
        this.context = context;
        this.examinerDuties = examinerDuties;
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
                return ServicesFragment.newInstance(examinerDuties, "Page # 1");
            case 1: // Fragment # 0 - This will show FirstFragment different title
                return FormFragment.newInstance(examinerDuties);
            case 2: // Fragment # 1 - This will show SecondFragment
                return PersonalFragment.newInstance(examinerDuties, "Page # 3");
            case 3: // Fragment # 1 - This will show SecondFragment
                return MapFragment.newInstance(examinerDuties, "Page # 4");
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