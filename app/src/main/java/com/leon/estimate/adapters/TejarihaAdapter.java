package com.leon.estimate.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.leon.estimate.MyApplication;
import com.leon.estimate.R;
import com.leon.estimate.Tables.DaoTejariha;
import com.leon.estimate.Tables.MyDatabase;
import com.leon.estimate.Tables.Tejariha;

import static com.leon.estimate.Utils.Constants.tejarihas;

public class TejarihaAdapter extends RecyclerView.Adapter<TejarihaAdapter.ViewHolder> {
    private Context context;
    private MyDatabase dataBase;

    public TejarihaAdapter(Context context) {
        this.context = context;
        dataBase = Room.databaseBuilder(context, MyDatabase.class, MyApplication.getDBNAME())
                .allowMainThreadQueries().build();
    }

    @NonNull
    @Override
    public TejarihaAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.item_mother_child, parent, false);
        TejarihaAdapter.ViewHolder holder = new TejarihaAdapter.ViewHolder(view);
        holder.imageViewMinus.setOnClickListener(v -> {
            Tejariha tejariha = tejarihas.get(viewType);
            DaoTejariha daoTejariha = dataBase.daoTejariha();
            daoTejariha.delete(tejariha.id);
            tejarihas.remove(viewType);
            notifyDataSetChanged();

        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull TejarihaAdapter.ViewHolder holder, int position) {
        Tejariha tejariha = tejarihas.get(position);
        holder.textViewKarbari.setText(tejariha.karbari);
        holder.textViewA2.setText(String.valueOf(tejariha.a));
        holder.textViewNoeShoql.setText(String.valueOf(tejariha.noeShoql));
        holder.textViewTedadVahed.setText(String.valueOf(tejariha.tedadVahed));
        holder.textViewVahedMohasebe.setText(String.valueOf(tejariha.vahedMohasebe));
    }

    @Override
    public int getItemCount() {
        return tejarihas.size();
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
