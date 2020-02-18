package com.leon.estimate.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.room.Room;
import androidx.viewpager.widget.ViewPager;

import com.leon.estimate.R;
import com.leon.estimate.Tables.DaoMapScreen;
import com.leon.estimate.Tables.MyDatabase;
import com.leon.estimate.Utils.FontManager;
import com.leon.estimate.Utils.MyPagerAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FormActivity extends AppCompatActivity {
    FragmentPagerAdapter adapterViewPager;
    @BindView(R.id.view_pager)
    ViewPager viewPager;
    @BindView(R.id.relativeLayout)
    RelativeLayout relativeLayout;
    @BindView(R.id.button_next)
    Button buttonNext;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        setContentView(R.layout.form_activity);
        ButterKnife.bind(this);
        context = this;
        initialize();

        MyDatabase dataBase = Room.databaseBuilder(context, MyDatabase.class, "MyDatabase")
                .allowMainThreadQueries().build();
        DaoMapScreen daoMapScreen = dataBase.daoMapScreen();
        Log.e("Size", String.valueOf(daoMapScreen.getMapScreen().size()));
    }

    void initialize() {
        adapterViewPager = new MyPagerAdapter(getSupportFragmentManager(), context);
        viewPager.setAdapter(adapterViewPager);
        FontManager fontManager = new FontManager(getApplicationContext());
        fontManager.setFont(relativeLayout);
        buttonNext.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), DocumentActivity.class);
            startActivity(intent);
        });
    }


}
