package com.leon.estimate.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.leon.estimate.R;
import com.leon.estimate.Tables.Images;

import java.util.ArrayList;

public class ImageViewAdapter extends BaseAdapter {
    public ArrayList<Images> images;
    LayoutInflater inflater;

    public ImageViewAdapter(Context c, ArrayList<Images> images) {
        this.images = images;
        inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getCount() {
        return images.size();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return 0;
    }

    @SuppressLint({"ViewHolder", "InflateParams"})
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageViewHolder holder;
        Images imageDataTitle = images.get(position);
        View view = convertView;
        if (view == null) {
            view = inflater.inflate(R.layout.item_image, null);
        }
        holder = new ImageViewHolder(view);
        holder.textView.setText(imageDataTitle.getDocTitle());
        holder.imageView.setImageBitmap(imageDataTitle.getBitmap());
        return view;
    }

    public static class ImageViewHolder {
        public ImageView imageView;
        public TextView textView;

        public ImageViewHolder(View view) {
            imageView = view.findViewById(R.id.imageView);
            textView = view.findViewById(R.id.textView);
        }
    }
}
