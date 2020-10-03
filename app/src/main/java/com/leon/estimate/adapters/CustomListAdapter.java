package com.leon.estimate.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.leon.estimate.Enums.BundleEnum;
import com.leon.estimate.Enums.SharedReferenceKeys;
import com.leon.estimate.Enums.SharedReferenceNames;
import com.leon.estimate.R;
import com.leon.estimate.Tables.ExaminerDuties;
import com.leon.estimate.Utils.SharedPreferenceManager;
import com.leon.estimate.activities.FormActivity;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class CustomListAdapter extends RecyclerView.Adapter<CustomListAdapter.ViewHolder> {
    private Context context;
    private List<ExaminerDuties> examinerDuties;
    private ArrayList<ExaminerDuties> tempExaminerDuties;
    private int size = 0;

    public CustomListAdapter(Context context, List<ExaminerDuties> examinerDuties) {
        this.context = context;
        Log.e("size", String.valueOf(examinerDuties.size()));
//        for (int i = 0; i < examinerDuties.size(); i++) {
//            if (examinerDuties.get(i).getZoneId() == null ||
//                    examinerDuties.get(i).getZoneId().equals("0")) {
//                examinerDuties.remove(i);
//                i--;
//            }
//        }
        Collections.sort(examinerDuties, (o1, o2) -> o2.getExaminationDay().compareTo(
                o1.getExaminationDay()));
        this.examinerDuties = examinerDuties;
        this.tempExaminerDuties = new ArrayList<>();
        this.tempExaminerDuties.addAll(examinerDuties);
    }

    @SuppressLint("InflateParams")
    @NotNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        context = viewGroup.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view;
        if (size % 2 == 0)
            view = layoutInflater.inflate(R.layout.item_address_1, viewGroup, false);
        else
            view = layoutInflater.inflate(R.layout.item_address_2, viewGroup, false);
        ViewHolder holder = new ViewHolder(view);

        holder.itemView.setOnClickListener(view1 -> {
            if (tempExaminerDuties.get(i).isPeymayesh()) {
                Toast.makeText(context, context.getText(R.string.is_peymayesh), Toast.LENGTH_LONG).show();
            } else {
                Intent intent = new Intent(context, FormActivity.class);
                intent.putExtra(BundleEnum.TRACK_NUMBER.getValue(), tempExaminerDuties.get(i).getTrackNumber());
                intent.putExtra(BundleEnum.SERVICES.getValue(), tempExaminerDuties.get(i).getRequestDictionaryString());
                SharedPreferenceManager sharedPreferenceManager = new SharedPreferenceManager(context, SharedReferenceNames.ACCOUNT.getValue());
                sharedPreferenceManager.putData(SharedReferenceKeys.TRACK_NUMBER.getValue(), tempExaminerDuties.get(i).getTrackNumber());
                context.startActivity(intent);
            }
        });
        return holder;
    }


    @SuppressLint({"NewApi", "UseCompatLoadingForDrawables"})
    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        ExaminerDuties examinerDuties = getItem(i);

        viewHolder.textViewName.setText(examinerDuties.getNameAndFamily());
        if (examinerDuties.isPeymayesh()) {
            viewHolder.textViewPeymayesh.setText("پیمایش شده");
            viewHolder.textViewPeymayesh.setBackground(context.getDrawable(R.drawable.border_green_2));

        } else {
            viewHolder.textViewPeymayesh.setText("پیمایش نشده");
            viewHolder.textViewPeymayesh.setBackground(context.getDrawable(R.drawable.border_red_2));
        }
        viewHolder.textViewExaminationDay.setText(examinerDuties.getExaminationDay());
        viewHolder.textViewServiceGroup.setText(examinerDuties.getServiceGroup());
        viewHolder.textViewAddress.setText(examinerDuties.getAddress().trim());
//        viewHolder.textViewRadif.setText(examinerDuties.getZoneId());
        viewHolder.textViewRadif.setText(examinerDuties.getRadif());
        viewHolder.textViewTrackNumber.setText(examinerDuties.getTrackNumber());
        viewHolder.textViewNotificationMobile.setText(examinerDuties.getNotificationMobile());
        viewHolder.textViewMoshtarakMobile.setText(examinerDuties.getMoshtarakMobile());
        if (examinerDuties.getBillId() != null)
            viewHolder.textViewBillId.setText(examinerDuties.getBillId());
        else {
            viewHolder.textViewBillId.setText(examinerDuties.getNeighbourBillId());
            viewHolder.textViewBillIdTitle.setText(context.getString(R.string.neighbour_bill_id));
        }
        viewHolder.textViewName.setGravity(1);
        viewHolder.textViewPeymayesh.setGravity(1);
        viewHolder.textViewExaminationDay.setGravity(1);
        viewHolder.textViewServiceGroup.setGravity(1);
        viewHolder.textViewAddress.setGravity(1);
        viewHolder.textViewRadif.setGravity(1);
        viewHolder.textViewTrackNumber.setGravity(1);
        viewHolder.textViewNotificationMobile.setGravity(1);
        viewHolder.textViewMoshtarakMobile.setGravity(1);
        viewHolder.textViewBillId.setGravity(1);

        size++;
    }

    public void filter(String billId, String trackingNumber, String name, String family,
                       String nationId, String mobile, String date) {
        billId = billId.toLowerCase(Locale.getDefault());
        trackingNumber = trackingNumber.toLowerCase(Locale.getDefault());
        name = name.toLowerCase(Locale.getDefault());
        family = family.toLowerCase(Locale.getDefault());
        nationId = nationId.toLowerCase(Locale.getDefault());
        mobile = mobile.toLowerCase(Locale.getDefault());
        tempExaminerDuties.clear();
        ArrayList<ExaminerDuties> list = new ArrayList<>(examinerDuties);
        if (billId.length() == 0)
            tempExaminerDuties.addAll(list);
        else {
            for (ExaminerDuties examinerDuty : list) {
                if (((examinerDuty.getBillId() != null
                        && examinerDuty.getBillId().toLowerCase(Locale.getDefault()).contains(billId))
                        || (examinerDuty.getNeighbourBillId() != null
                        && examinerDuty.getNeighbourBillId().toLowerCase(Locale.getDefault()).contains(billId)))
                ) {
                    tempExaminerDuties.remove(examinerDuty);
                    tempExaminerDuties.add(examinerDuty);
                }
            }
            list.clear();
            list.addAll(tempExaminerDuties);
        }
        if (trackingNumber.length() > 0) {
            tempExaminerDuties.clear();
            for (ExaminerDuties examinerDuty : list) {
                if (examinerDuty.getTrackNumber() != null &&
                        examinerDuty.getTrackNumber().toLowerCase(Locale.getDefault()).contains(trackingNumber)) {
                    tempExaminerDuties.remove(examinerDuty);
                    tempExaminerDuties.add(examinerDuty);
                }
            }
            list.clear();
            list.addAll(tempExaminerDuties);
        }
        if (name.length() > 0) {
            tempExaminerDuties.clear();
            for (ExaminerDuties examinerDuty : list) {
                if (examinerDuty.getNameAndFamily() != null &&
                        examinerDuty.getNameAndFamily().toLowerCase(Locale.getDefault()).contains(name)) {
                    tempExaminerDuties.add(examinerDuty);
                }
            }
            list.clear();
            list.addAll(tempExaminerDuties);
        }

        if (family.length() > 0) {
            tempExaminerDuties.clear();
            for (ExaminerDuties examinerDuty : list) {
                if (examinerDuty.getNameAndFamily() != null &&
                        examinerDuty.getNameAndFamily().toLowerCase(Locale.getDefault()).contains(family)) {
                    tempExaminerDuties.add(examinerDuty);
                }
            }
            list.clear();
            list.addAll(tempExaminerDuties);
        }
        if (nationId.length() > 0) {
            tempExaminerDuties.clear();
            for (ExaminerDuties examinerDuty : list) {
                if (examinerDuty.getNationalId() != null &&
                        examinerDuty.getNationalId().toLowerCase(Locale.getDefault()).contains(nationId)) {
                    tempExaminerDuties.add(examinerDuty);
                }
            }
            list.clear();
            list.addAll(tempExaminerDuties);
        }
        if (mobile.length() > 0) {
            tempExaminerDuties.clear();
            for (ExaminerDuties examinerDuty : list) {
                if ((examinerDuty.getMobile() != null &&
                        examinerDuty.getMobile().toLowerCase(Locale.getDefault()).contains(mobile))
                        || (examinerDuty.getMoshtarakMobile() != null &&
                        examinerDuty.getMoshtarakMobile().toLowerCase(Locale.getDefault()).contains(mobile))
                        || (examinerDuty.getNotificationMobile() != null &&
                        examinerDuty.getNotificationMobile().toLowerCase(Locale.getDefault()).contains(mobile))) {
                    tempExaminerDuties.add(examinerDuty);
                }
            }
            list.clear();
            list.addAll(tempExaminerDuties);
        }
        if (date.length() > 0) {
            tempExaminerDuties.clear();
            date = date.substring(2);
            for (ExaminerDuties examinerDuty : list) {
                if ((examinerDuty.getExaminationDay() != null &&
                        examinerDuty.getExaminationDay().toLowerCase(Locale.getDefault()).contains(date))) {
                    tempExaminerDuties.add(examinerDuty);
                }
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return tempExaminerDuties.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public ExaminerDuties getItem(int position) {
        return tempExaminerDuties.get(position);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewName;
        TextView textViewPeymayesh;
        TextView textViewExaminationDay;
        TextView textViewServiceGroup;
        TextView textViewAddress;
        TextView textViewTrackNumber;
        TextView textViewNotificationMobile;
        TextView textViewMoshtarakMobile;
        TextView textViewBillId;
        TextView textViewBillIdTitle;
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
            textViewBillId = itemView.findViewById(R.id.textViewBillId);
            textViewBillIdTitle = itemView.findViewById(R.id.textViewBillIdTilte);
            textViewRadif = itemView.findViewById(R.id.textViewRadif);
        }
    }
}