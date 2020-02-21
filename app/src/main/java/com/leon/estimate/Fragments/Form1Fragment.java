package com.leon.estimate.Fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import androidx.fragment.app.Fragment;

import com.leon.estimate.Activities.FormActivity;
import com.leon.estimate.R;
import com.leon.estimate.Utils.FontManager;

import org.jetbrains.annotations.NotNull;

import butterknife.BindView;
import butterknife.ButterKnife;


public class Form1Fragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;
    @BindView(R.id.relativeLayout)
    RelativeLayout relativeLayout;
    @BindView(R.id.button_next)
    Button buttonNext;
    @BindView(R.id.editText19)
    EditText editText19;
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
        context = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        findViewById = inflater.inflate(R.layout.form1_fragment, container, false);
        ButterKnife.bind(this, findViewById);
        initialize();
        return findViewById;
    }

    private void initialize() {
        FontManager fontManager = new FontManager(context);
        fontManager.setFont(relativeLayout);
        buttonNext.setOnClickListener(v -> ((FormActivity) getActivity()).nextPage(null));
    }

    private void sendData() {
        //INTENT OBJ
        Intent i = new Intent(getActivity().getBaseContext(), FormActivity.class);
        i.putExtra("SENDER_KEY", "name");
        i.putExtra("NAME_KEY", "slm");
        getActivity().startActivity(i);
    }

    public interface test {
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
