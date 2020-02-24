package com.leon.estimate.Activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.room.Room;
import androidx.viewpager.widget.ViewPager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.leon.estimate.Enums.BundleEnum;
import com.leon.estimate.R;
import com.leon.estimate.Tables.DaoExaminerDuties;
import com.leon.estimate.Tables.ExaminerDuties;
import com.leon.estimate.Tables.MyDatabase;
import com.leon.estimate.Tables.RequestDictionary;
import com.leon.estimate.Utils.FontManager;
import com.leon.estimate.Utils.MyPagerAdapter;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FormActivity extends AppCompatActivity {
    FragmentPagerAdapter adapterViewPager;
    @BindView(R.id.view_pager)
    ViewPager viewPager;
    @BindView(R.id.relativeLayout)
    RelativeLayout relativeLayout;
    Context context;
    String trackNumber, json;
    List<RequestDictionary> requestDictionaries;
    ExaminerDuties examinerDuties;
    ExaminerDuties examinerDutiesTemp;
    MyDatabase dataBase;
    DaoExaminerDuties daoExaminerDuties;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        setContentView(R.layout.form_activity);
        ButterKnife.bind(this);
        context = this;
        if (getIntent().getExtras() != null) {
            trackNumber = getIntent().getExtras().getString(BundleEnum.TRACK_NUMBER.getValue());
            json = getIntent().getExtras().getString(BundleEnum.SERVICES.getValue());
            Gson gson = new GsonBuilder().create();
            requestDictionaries = Arrays.asList(gson.fromJson(json, RequestDictionary[].class));
        }
        initialize();
    }

    @SuppressLint("ClickableViewAccessibility")
    void initialize() {
        dataBase = Room.databaseBuilder(context, MyDatabase.class, "MyDatabase")
                .allowMainThreadQueries().build();
        daoExaminerDuties = dataBase.daoExaminerDuties();
        examinerDuties = daoExaminerDuties.unreadExaminerDutiesByTrackNumber(trackNumber);
        adapterViewPager = new MyPagerAdapter(getSupportFragmentManager(), context, examinerDuties);
        viewPager.setAdapter(adapterViewPager);
        viewPager.setOnTouchListener((v, event) -> true);
        FontManager fontManager = new FontManager(getApplicationContext());
        fontManager.setFont(relativeLayout);
    }

    public void nextPage(Bitmap bitmap) {
        if (viewPager.getCurrentItem() == 0)
            viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
        else {
            Intent intent = new Intent(getApplicationContext(), DocumentActivity.class);
//            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//            bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
//            byte[] bytes = byteArrayOutputStream.toByteArray();
            intent.putExtra(BundleEnum.IMAGE_BITMAP.getValue(), convertBitmapToByte(bitmap));
            context.startActivity(intent);
        }
    }

    private byte[] convertBitmapToByte(Bitmap bitmap) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
        return bos.toByteArray();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
