package com.leon.estimate.Activities;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.leon.estimate.R;
import com.leon.estimate.Tables.DaoExaminerDuties;
import com.leon.estimate.Tables.ExaminerDuties;
import com.leon.estimate.Tables.MyDatabase;
import com.leon.estimate.Utils.CustomAdapter1;
import com.leon.estimate.Utils.FontManager;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ListActivity extends AppCompatActivity {
    Context context;
    @BindView(R.id.constraintLayout1)
    ConstraintLayout constraintLayout;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.textViewEmpty)
    TextView textViewEmpty;
    List<ExaminerDuties> examinerDuties;
    CustomAdapter1 customAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        setContentView(R.layout.list_activity);
        ButterKnife.bind(this);
        initialize();
    }

    void initialize() {
        context = this;
        FontManager fontManager = new FontManager(getApplicationContext());
        fontManager.setFont(constraintLayout);
        initializeRecyclerView();
    }

    void initializeRecyclerView() {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;

        MyDatabase dataBase = Room.databaseBuilder(context, MyDatabase.class, "MyDatabase")
                .allowMainThreadQueries().build();
        DaoExaminerDuties daoExaminerDuties = dataBase.daoExaminerDuties();
//        examinerDuties = daoExaminerDuties.unreadExaminerDuties();
        examinerDuties = daoExaminerDuties.ExaminerDuties();
        if (this.examinerDuties.isEmpty()) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            recyclerView.setVisibility(View.GONE);
            textViewEmpty.setVisibility(View.VISIBLE);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            textViewEmpty.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            customAdapter = new CustomAdapter1(context, this.examinerDuties, width);
            customAdapter.notifyDataSetChanged();
            recyclerView.setAdapter(customAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(this) {
                @Override
                public boolean requestChildRectangleOnScreen(@NonNull RecyclerView parent,
                                                             @NonNull View child, @NonNull Rect rect, boolean immediate) {
                    return false;
                }
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        initializeRecyclerView();
    }
}
