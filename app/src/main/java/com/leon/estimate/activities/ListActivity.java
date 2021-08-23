package com.leon.estimate.activities;

import static com.leon.estimate.Utils.Constants.customAdapter;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Debug;
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

import com.leon.estimate.Enums.SharedReferenceKeys;
import com.leon.estimate.Enums.SharedReferenceNames;
import com.leon.estimate.MyApplication;
import com.leon.estimate.R;
import com.leon.estimate.Tables.DaoExaminerDuties;
import com.leon.estimate.Tables.ExaminerDuties;
import com.leon.estimate.Tables.MyDatabase;
import com.leon.estimate.Utils.SharedPreferenceManager;
import com.leon.estimate.adapters.CustomListAdapter;
import com.leon.estimate.databinding.ListActivityBinding;
import com.leon.estimate.fragments.SearchFragment;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ListActivity extends AppCompatActivity {
    Context context;
    List<ExaminerDuties> examinerDuties;
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
        examinerDuties = daoExaminerDuties.ExaminerDuties();
        if (this.examinerDuties.isEmpty()) {
            binding.recyclerView.setVisibility(View.GONE);
            binding.textViewEmpty.setVisibility(View.VISIBLE);
        } else {
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
        SharedPreferenceManager sharedPreferenceManager = new SharedPreferenceManager(context, SharedReferenceNames.ACCOUNT.getValue());
        if (sharedPreferenceManager.getStringData(SharedReferenceKeys.TRACK_NUMBER.getValue()) == null ||
                sharedPreferenceManager.getStringData(SharedReferenceKeys.TRACK_NUMBER.getValue()).length() < 1) {
            menu.getItem(1).setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NotNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_search) {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            SearchFragment searchFragment = SearchFragment.newInstance();
            searchFragment.show(fragmentTransaction, "");
        } else if (id == R.id.menu_clear) {
            customAdapter.filter("", "", "", "", "", "", "");
        } else if (id == R.id.menu_last) {
            SharedPreferenceManager sharedPreferenceManager = new SharedPreferenceManager(context,
                    SharedReferenceNames.ACCOUNT.getValue());
            customAdapter.filter("", sharedPreferenceManager.getStringData(
                    SharedReferenceKeys.TRACK_NUMBER.getValue()), "", "", "", "", "");
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initializeRecyclerView();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Runtime.getRuntime().totalMemory();
        Runtime.getRuntime().freeMemory();
        Runtime.getRuntime().maxMemory();
        Debug.getNativeHeapAllocatedSize();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Runtime.getRuntime().totalMemory();
        Runtime.getRuntime().freeMemory();
        Runtime.getRuntime().maxMemory();
        Debug.getNativeHeapAllocatedSize();
    }
}