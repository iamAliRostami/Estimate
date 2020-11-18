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

import static com.leon.estimate.Utils.Constants.others;

public class OthersAdapter extends RecyclerView.Adapter<OthersAdapter.ViewHolder> {
    private Context context;
    private final MyDatabase dataBase;

    public OthersAdapter(Context context) {
        this.context = context;
        dataBase = Room.databaseBuilder(context, MyDatabase.class, MyApplication.getDBNAME())
                .allowMainThreadQueries().build();
    }

    @NonNull
    @Override
    public OthersAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.item_tejari, parent, false);
        OthersAdapter.ViewHolder holder = new OthersAdapter.ViewHolder(view);
        holder.imageViewMinus.setOnClickListener(v -> {
            Tejariha tejariha = others.get(viewType);
            DaoTejariha daoTejariha = dataBase.daoTejariha();
            daoTejariha.delete(tejariha.id);
            others.remove(viewType);
            notifyDataSetChanged();

        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull OthersAdapter.ViewHolder holder, int position) {
        Tejariha tejariha = others.get(position);
        holder.textViewKarbari.setText(tejariha.karbari);
        holder.textViewA2.setText(String.valueOf(tejariha.a));
        holder.textViewNoeShoql.setText(String.valueOf(tejariha.noeShoql));
        holder.textViewTedadVahed.setText(String.valueOf(tejariha.tedadVahed));
        holder.textViewVahedMohasebe.setText(String.valueOf(tejariha.vahedMohasebe));
    }

    @Override
    public int getItemCount() {
        return others.size();
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
