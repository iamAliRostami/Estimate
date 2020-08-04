package com.leon.estimate.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.leon.estimate.R;
import com.leon.estimate.Tables.ImageData;

import java.util.List;

public class ImageViewAdapter extends BaseAdapter {
    public List<ImageData> imageData;
    public List<Image> images;
    LayoutInflater inflater;
    private Context context;

    public ImageViewAdapter(Context c, List<ImageData> imageData) {
        this.imageData = imageData;
        context = c;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        LayoutInflater inflater = (LayoutInflater.from(context));
    }

    public int getCount() {
        return imageData.size();
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
        View view = convertView;
        if (view == null) {
            view = inflater.inflate(R.layout.item_check_box, null);
        }
        holder = new ImageViewHolder(view);
//        holder.imageView.setImageURI();
        return view;
    }

    public static class ImageViewHolder {
        public ImageView imageView;
        public TextView textView;

        public ImageViewHolder(View view) {
            imageView = (ImageView) view.findViewById(R.id.imageView);
            textView = (TextView) view.findViewById(R.id.textView);
        }
    }
}
