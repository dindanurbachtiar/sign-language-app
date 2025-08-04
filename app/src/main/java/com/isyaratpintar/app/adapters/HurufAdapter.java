package com.isyaratpintar.app.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.isyaratpintar.app.R;
import com.isyaratpintar.app.models.Huruf;

import java.util.List;

public class HurufAdapter extends RecyclerView.Adapter<HurufAdapter.ViewHolder> {

    private Context context;
    private List<Huruf> hurufList;
    private OnHurufClickListener listener;

    public interface OnHurufClickListener {
        void onHurufClick(Huruf huruf);
    }

    public HurufAdapter(Context context, List<Huruf> hurufList, OnHurufClickListener listener) {
        this.context = context;
        this.hurufList = hurufList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_huruf, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Huruf huruf = hurufList.get(position);
        holder.tvNamaHuruf.setText(huruf.getNama());
        // Jika Anda memiliki gambar drawable untuk setiap huruf, set di sini
        // holder.ivHuruf.setImageResource(huruf.getGambarResId());
        // Untuk saat ini, asumsikan gambarResId belum digunakan di item_huruf.xml atau tidak penting
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onHurufClick(huruf);
            }
        });
    }

    @Override
    public int getItemCount() {
        return hurufList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNamaHuruf;
        ImageView ivHuruf; // Jika ingin menampilkan gambar di item_huruf

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNamaHuruf = itemView.findViewById(R.id.tvNamaHuruf);
            ivHuruf = itemView.findViewById(R.id.ivHuruf); // Pastikan ID ini ada di item_huruf.xml
        }
    }
}