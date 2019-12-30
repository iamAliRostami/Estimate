package com.leon.estimate.Utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.leon.estimate.Activities.Form1Activity;
import com.leon.estimate.Enums.BundleEnum;
import com.leon.estimate.R;
import com.leon.estimate.Tables.Calculation;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {
    private Context context;
    private List<Calculation> calculations;
    private int width;
    private int size = 0;

    public CustomAdapter(Context context, List<Calculation> calculations, int width) {
        this.context = context;
        this.calculations = calculations;
        this.width = width;
    }

    @SuppressLint("InflateParams")
    @NotNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        context = viewGroup.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view;
        if (size % 2 == 0)
            view = layoutInflater.inflate(R.layout.item_address_1, null);
        else
            view = layoutInflater.inflate(R.layout.item_address_2, null);
        ViewHolder holder = new ViewHolder(view);
        holder.itemView.setOnClickListener(view1 -> {
            Intent intent = new Intent(context, Form1Activity.class);
            intent.putExtra(BundleEnum.TRACK_NUMBER.getValue(), calculations.get(i).getTrackNumber());
            context.startActivity(intent);
        });
        return holder;
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        Calculation calculation = calculations.get(i);

        viewHolder.textViewName.setText(calculation.getNameAndFamily());
        if (calculation.isPeymayesh())
            viewHolder.textViewPeymayesh.setText("پیمایش شده");
        else
            viewHolder.textViewPeymayesh.setText("پیمایش نشده");
        viewHolder.textViewExaminationDay.setText(calculation.getExaminationDay());
        viewHolder.textViewServiceGroup.setText(calculation.getServiceGroup());
        viewHolder.textViewAddress.setText(calculation.getAddress().trim());
        viewHolder.textViewRadif.setText(calculation.getRadif());
        viewHolder.textViewTrackNumber.setText(calculation.getTrackNumber());
        viewHolder.textViewNotificationMobile.setText(calculation.getNotificationMobile());
        viewHolder.textViewMoshtarakMobile.setText(calculation.getMoshtarakMobile());
        viewHolder.textViewNeighbourBillId.setText(calculation.getNeighbourBillId());

        Typeface typeface = Typeface.createFromAsset(context.getAssets(), "font/BYekan_3.ttf");
        viewHolder.textViewName.setTypeface(typeface);
        viewHolder.textViewPeymayesh.setTypeface(typeface);
        viewHolder.textViewExaminationDay.setTypeface(typeface);
        viewHolder.textViewServiceGroup.setTypeface(typeface);
        viewHolder.textViewAddress.setTypeface(typeface);
        viewHolder.textViewRadif.setTypeface(typeface);
        viewHolder.textViewTrackNumber.setTypeface(typeface);
        viewHolder.textViewNotificationMobile.setTypeface(typeface);
        viewHolder.textViewMoshtarakMobile.setTypeface(typeface);
        viewHolder.textViewNeighbourBillId.setTypeface(typeface);

        viewHolder.textViewName.setWidth(width / 6);
        viewHolder.textViewExaminationDay.setWidth(width / 6);
        viewHolder.textViewAddress.setWidth(width / 3);
        viewHolder.textViewServiceGroup.setWidth(width / 6);
        viewHolder.textViewPeymayesh.setWidth(width / 6);

        viewHolder.textViewRadif.setWidth(width / 4);
        viewHolder.textViewTrackNumber.setWidth(width / 4);
        viewHolder.textViewNotificationMobile.setWidth(width / 4);
        viewHolder.textViewMoshtarakMobile.setWidth(width / 4);
        viewHolder.textViewNeighbourBillId.setWidth(width / 4);

        viewHolder.textViewName.setGravity(1);
        viewHolder.textViewPeymayesh.setGravity(1);
        viewHolder.textViewExaminationDay.setGravity(1);
        viewHolder.textViewServiceGroup.setGravity(1);
        viewHolder.textViewAddress.setGravity(1);
        viewHolder.textViewRadif.setGravity(1);
        viewHolder.textViewTrackNumber.setGravity(1);
        viewHolder.textViewNotificationMobile.setGravity(1);
        viewHolder.textViewMoshtarakMobile.setGravity(1);
        viewHolder.textViewNeighbourBillId.setGravity(1);

        size++;
    }


    @Override
    public int getItemCount() {
        return calculations.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewName;
        TextView textViewPeymayesh;
        TextView textViewExaminationDay;
        TextView textViewServiceGroup;
        TextView textViewAddress;
        TextView textViewTrackNumber;
        TextView textViewNotificationMobile;
        TextView textViewMoshtarakMobile;
        TextView textViewNeighbourBillId;
        TextView textViewRadif;

        @SuppressLint("NewApi")
        ViewHolder(View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.textViewName);
            textViewPeymayesh = itemView.findViewById(R.id.textViewPeymayesh);
            textViewExaminationDay = itemView.findViewById(R.id.textViewExaminationDay);
            textViewServiceGroup = itemView.findViewById(R.id.textViewServiceGroup);
            textViewAddress = itemView.findViewById(R.id.textViewAddress);
            textViewTrackNumber = itemView.findViewById(R.id.textViewTrackNumber);
            textViewNotificationMobile = itemView.findViewById(R.id.textViewNotificationMobile);
            textViewMoshtarakMobile = itemView.findViewById(R.id.textViewMoshtarakMobile);
            textViewNeighbourBillId = itemView.findViewById(R.id.textViewNeighbourBillId);
            textViewRadif = itemView.findViewById(R.id.textViewRadif);
        }
    }
}