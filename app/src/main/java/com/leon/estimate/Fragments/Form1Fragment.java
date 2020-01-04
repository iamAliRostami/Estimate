package com.leon.estimate.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.fragment.app.Fragment;

import com.leon.estimate.R;
import com.leon.estimate.Utils.FontManager;

import org.jetbrains.annotations.NotNull;

import butterknife.BindView;


public class Form1Fragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;
    @BindView(R.id.frameLayout)
    FrameLayout frameLayout;
    private View findViewById;
    private Context context;
    public Form1Fragment() {

    }

    public static Form1Fragment newInstance(String param1, String param2) {
        Form1Fragment fragment = new Form1Fragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        findViewById = inflater.inflate(R.layout.form1_fragment, container, false);
        initialize();
        return findViewById;
    }

    private void initialize() {
        FontManager fontManager = new FontManager(context);
        fontManager.setFont(frameLayout);
    }

    @Override
    public void onAttach(@NotNull Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
