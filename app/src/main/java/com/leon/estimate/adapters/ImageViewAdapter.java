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
import com.leon.estimate.Tables.ImageDataTitle;

import java.util.ArrayList;

public class ImageViewAdapter extends BaseAdapter {
    public ArrayList<ImageDataTitle> imageDataTitleList;
    LayoutInflater inflater;

    public ImageViewAdapter(Context c, ArrayList<ImageDataTitle> imageDataTitleList) {
        this.imageDataTitleList = imageDataTitleList;
        inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getCount() {
        return imageDataTitleList.size();
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
        ImageDataTitle imageDataTitle = imageDataTitleList.get(position);
        View view = convertView;
        if (view == null) {
            view = inflater.inflate(R.layout.item_image, null);
        }
        holder = new ImageViewHolder(view);
        holder.textView.setText(imageDataTitle.getTitle());
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
