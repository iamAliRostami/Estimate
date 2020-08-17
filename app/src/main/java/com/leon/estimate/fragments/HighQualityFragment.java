package com.leon.estimate.fragments;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.fragment.app.DialogFragment;

import com.leon.estimate.Enums.BundleEnum;
import com.leon.estimate.databinding.HighQualityFragmentBinding;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.util.Objects;

public class HighQualityFragment extends DialogFragment {
    HighQualityFragmentBinding binding;
    private Bitmap bitmap;
    private String title;

    public HighQualityFragment() {
    }

    public static HighQualityFragment newInstance(Bitmap param, String title) {
        HighQualityFragment fragment = new HighQualityFragment();
        Bundle args = new Bundle();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        param.compress(Bitmap.CompressFormat.JPEG, 100, bos);
        args.putByteArray(BundleEnum.IMAGE_BITMAP.getValue(), bos.toByteArray());
        args.putString(BundleEnum.TITLE.getValue(), title);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            byte[] bytes = getArguments().getByteArray((BundleEnum.IMAGE_BITMAP.getValue()));
            if (bytes != null) {
                bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            }
            title = getArguments().getString(BundleEnum.TITLE.getValue());
            Log.e("title", title);
        }
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = HighQualityFragmentBinding.inflate(inflater, container, false);
        initialize();
        return binding.getRoot();
    }

    void initialize() {
        binding.imageViewHighQuality.setImageBitmap(bitmap);
    }

    @Override
    public void onResume() {
        WindowManager.LayoutParams params = Objects.requireNonNull(
                Objects.requireNonNull(getDialog()).getWindow()).getAttributes();
        params.width = ViewGroup.LayoutParams.WRAP_CONTENT;
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        Objects.requireNonNull(getDialog().getWindow()).setAttributes(params);
        super.onResume();
    }
}