package com.leon.estimate.Activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.room.Room;
import androidx.viewpager.widget.ViewPager;

import com.leon.estimate.Enums.BundleEnum;
import com.leon.estimate.Fragments.Form1Fragment;
import com.leon.estimate.R;
import com.leon.estimate.Tables.DaoMapScreen;
import com.leon.estimate.Tables.MyDatabase;
import com.leon.estimate.Utils.FontManager;
import com.leon.estimate.Utils.MyPagerAdapter;

import java.io.ByteArrayOutputStream;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FormActivity extends AppCompatActivity implements Form1Fragment.test {
    FragmentPagerAdapter adapterViewPager;
    @BindView(R.id.view_pager)
    ViewPager viewPager;
    @BindView(R.id.relativeLayout)
    RelativeLayout relativeLayout;
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

    @SuppressLint("ClickableViewAccessibility")
    void initialize() {
        adapterViewPager = new MyPagerAdapter(getSupportFragmentManager(), context);
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
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
            byte[] bytes = byteArrayOutputStream.toByteArray();
            intent.putExtra(BundleEnum.IMAGE_BITMAP.getValue(), bytes);
            context.startActivity(intent);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        final String sender = this.getIntent().getExtras().getString("SENDER_KEY");
        if (sender != null) {
            this.receiveData();
            Toast.makeText(this, "Received", Toast.LENGTH_SHORT).show();
        }
    }

    private void receiveData() {
        Intent i = getIntent();
        String name = i.getStringExtra("NAME_KEY");
        Log.e("log", name);
    }
}
