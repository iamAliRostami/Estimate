package com.leon.estimate.Activities;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.leon.estimate.R;
import com.leon.estimate.Utils.MyPagerAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FormActivity extends AppCompatActivity {

    FragmentPagerAdapter adapterViewPager;
    @BindView(R.id.view_pager)
    ViewPager viewPager;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        setContentView(R.layout.form_activity);
        ButterKnife.bind(this);
        context = this;
        initialize();
    }

    void initialize() {
        adapterViewPager = new MyPagerAdapter(getSupportFragmentManager(), context);
        viewPager.setAdapter(adapterViewPager);
    }
}
