package com.leon.estimate.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;

import com.leon.estimate.R;
import com.leon.estimate.Tables.RequestDictionary;

import java.util.List;

public class CheckBoxAdapter extends BaseAdapter {
    public List<RequestDictionary> requestDictionaries;
    LayoutInflater inflater;
    private Context context;

    public CheckBoxAdapter(Context c, List<RequestDictionary> requestDictionaries) {
        this.requestDictionaries = requestDictionaries;
        context = c;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        LayoutInflater inflater = (LayoutInflater.from(context));
    }

    public int getCount() {
        return requestDictionaries.size();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return 0;
    }

    @SuppressLint({"ViewHolder", "InflateParams"})
    public View getView(int position, View convertView, ViewGroup parent) {
        CheckBoxViewHolder holder;
        View view = convertView;
        if (view == null) {
            view = inflater.inflate(R.layout.item_check_box, null);
        }
        holder = new CheckBoxViewHolder(view);
        holder.checkBox.setText(requestDictionaries.get(position).getTitle());
        holder.checkBox.setOnCheckedChangeListener((compoundButton, b) -> {
            requestDictionaries.get(position).setSelected(b);
//            if (b) counter = counter + 1;
//            else counter = counter - 1;
//            Log.e("select".concat(String.valueOf(position)), String.valueOf(b));
//            Log.e("number selected", String.valueOf(counter));
        });
        holder.checkBox.setChecked(requestDictionaries.get(position).isSelected());
        return view;
    }

    public static class CheckBoxViewHolder {
        public CheckBox checkBox;

        public CheckBoxViewHolder(View view) {
            this.checkBox = (CheckBox) view.findViewById(R.id.checkbox);
        }
    }
}
