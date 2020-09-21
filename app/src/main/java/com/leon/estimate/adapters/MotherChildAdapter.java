package com.leon.estimate.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.leon.estimate.R;
import com.leon.estimate.Tables.MotherChild;
import com.leon.estimate.activities.FormActivity;

public class MotherChildAdapter extends RecyclerView.Adapter<MotherChildAdapter.ViewHolder> {
    private Context context;

    public MotherChildAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public MotherChildAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.item_mother_child, parent, false);
        MotherChildAdapter.ViewHolder holder = new MotherChildAdapter.ViewHolder(view);
        holder.imageViewMinus.setOnClickListener(v -> {
            FormActivity.motherChildren.remove(viewType);
            notifyDataSetChanged();
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MotherChildAdapter.ViewHolder holder, int position) {
        MotherChild motherChild = FormActivity.motherChildren.get(position);
        holder.textViewKarbari.setText(motherChild.karbari);
        holder.textViewA2.setText(String.valueOf(motherChild.a));
        holder.textViewNoeShoql.setText(String.valueOf(motherChild.noeShoql));
        holder.textViewTedadVahed.setText(String.valueOf(motherChild.tedadVahed));
        holder.textViewVahedMohasebe.setText(String.valueOf(motherChild.vahedMohasebe));
    }

    @Override
    public int getItemCount() {
        return FormActivity.motherChildren.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewKarbari;
        TextView textViewTedadVahed;
        TextView textViewVahedMohasebe;
        TextView textViewNoeShoql;
        TextView textViewA2;
        ImageView imageViewMinus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewKarbari = itemView.findViewById(R.id.textViewKarbari);
            textViewNoeShoql = itemView.findViewById(R.id.textViewNoeShoql);
            textViewTedadVahed = itemView.findViewById(R.id.textViewTedadVahed);
            textViewVahedMohasebe = itemView.findViewById(R.id.textViewVahedMohasebe);
            textViewA2 = itemView.findViewById(R.id.textViewA2);
            imageViewMinus = itemView.findViewById(R.id.imageViewMinus);
        }
    }
}
