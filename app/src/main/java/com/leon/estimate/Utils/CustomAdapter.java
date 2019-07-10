package com.leon.estimate.Utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.leon.estimate.R;
import com.leon.estimate.Tables.Calculation;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {
    private Context context;
    private ArrayList<Calculation> calculations;
    private int width;

    private int size = 0;

    public CustomAdapter(Context context, ArrayList<Calculation> calculations, int width) {
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
            view = layoutInflater.inflate(R.layout.item_address_1, null);
        return new ViewHolder(view);
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
        viewHolder.textViewAddress.setText(calculation.getAddress());
        viewHolder.textViewRadif.setText(calculation.getRadif());
        viewHolder.textViewTrackNumber.setText(calculation.getTrackNumber());
        viewHolder.textViewNotificationMobile.setText(calculation.getNotificationMobile());
        viewHolder.textViewMoshtarakMobile.setText(calculation.getMoshtarakMobile());
        viewHolder.textViewNeighbourBillId.setText(calculation.getNeighbourBillId());

        Typeface typeface = Typeface.createFromAsset(context.getAssets(), "fonts/BYekan_3.ttf");
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

//        viewHolder.textViewNote.setWidth((width - viewHolder.imageViewInfo.getWidth()) / 4);
//        viewHolder.textViewCost.setWidth((width - viewHolder.imageViewInfo.getWidth()) / 4);
//        viewHolder.textViewDate.setWidth((width - viewHolder.imageViewInfo.getWidth()) / 3);
//        viewHolder.textViewUse.setWidth((width - viewHolder.imageViewInfo.getWidth()) / 6);

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
        @BindView(R.id.textViewName)
        TextView textViewName;
        @BindView(R.id.textViewPeymayesh)
        TextView textViewPeymayesh;
        @BindView(R.id.textViewExaminationDay)
        TextView textViewExaminationDay;
        @BindView(R.id.textViewServiceGroup)
        TextView textViewServiceGroup;
        @BindView(R.id.textViewAddress)
        TextView textViewAddress;
        @BindView(R.id.textViewTrackNumber)
        TextView textViewTrackNumber;
        @BindView(R.id.textViewNotificationMobile)
        TextView textViewNotificationMobile;
        @BindView(R.id.textViewMoshtarakMobile)
        TextView textViewMoshtarakMobile;
        @BindView(R.id.textViewNeighbourBillId)
        TextView textViewNeighbourBillId;
        @BindView(R.id.textViewRadif)
        TextView textViewRadif;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(itemView);
        }
    }
}