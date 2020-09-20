package com.leon.estimate.activities;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.leon.estimate.MyApplication;
import com.leon.estimate.R;
import com.leon.estimate.Tables.DaoExaminerDuties;
import com.leon.estimate.Tables.ExaminerDuties;
import com.leon.estimate.Tables.MyDatabase;
import com.leon.estimate.adapters.CustomListAdapter;
import com.leon.estimate.databinding.ListActivityBinding;
import com.leon.estimate.fragments.SearchFragment;

import java.util.List;

public class ListActivity extends AppCompatActivity {
    Context context;
    List<ExaminerDuties> examinerDuties;
    CustomListAdapter customAdapter;
    ProgressDialog dialog;
    ListActivityBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        binding = ListActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initialize();
    }

    void initialize() {
        context = this;
        dialog = new ProgressDialog(context);
        dialog.setMessage(context.getString(R.string.loading_getting_info));
        dialog.setTitle(context.getString(R.string.loading_connecting));
        dialog.setCancelable(false);
        initializeRecyclerView();
    }

    @SuppressLint("SourceLockedOrientationActivity")
    void initializeRecyclerView() {
        dialog.show();
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        MyDatabase dataBase = Room.databaseBuilder(context, MyDatabase.class, MyApplication.getDBNAME())
                .allowMainThreadQueries().build();
        DaoExaminerDuties daoExaminerDuties = dataBase.daoExaminerDuties();
//        examinerDuties = daoExaminerDuties.unreadExaminerDuties();
        examinerDuties = daoExaminerDuties.ExaminerDuties();
        if (this.examinerDuties.isEmpty()) {
//            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            binding.recyclerView.setVisibility(View.GONE);
            binding.textViewEmpty.setVisibility(View.VISIBLE);
        } else {
//            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            binding.textViewEmpty.setVisibility(View.GONE);
            binding.recyclerView.setVisibility(View.VISIBLE);
            customAdapter = new CustomListAdapter(context, this.examinerDuties);
            customAdapter.notifyDataSetChanged();
            binding.recyclerView.setAdapter(customAdapter);
            binding.recyclerView.setLayoutManager(new LinearLayoutManager(this) {
                @Override
                public boolean requestChildRectangleOnScreen(@NonNull RecyclerView parent,
                                                             @NonNull View child,
                                                             @NonNull Rect rect, boolean immediate) {
                    return false;
                }
            });
        }
        dialog.dismiss();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_search) {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            SearchFragment signFragment = SearchFragment.newInstance("trackNumber");
            signFragment.show(fragmentTransaction, "");
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initializeRecyclerView();
    }
}