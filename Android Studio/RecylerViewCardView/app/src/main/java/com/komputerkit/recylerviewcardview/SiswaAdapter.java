package com.komputerkit.recylerviewcardview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class SiswaAdapter extends  RecyclerView.Adapter<SiswaAdapter.ViewHolder> {

    private Context context;
    private List<Siswa> SiswaList;

    public SiswaAdapter(Context context, List<Siswa> SiswaList) {
        this.context = context;
        this.SiswaList = SiswaList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_siswa, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int p) {
        final Siswa Siswa = SiswaList.get(p);
        holder.tvnama.setText(Siswa.getNama());
        holder.tvalamat.setText(Siswa.getAlamat());

        holder.tvmenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(context, holder.tvmenu);
                popupMenu.inflate(R.menu.menu_option);

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        int id = item.getItemId();
                        if (id == R.id.menuSimpan) {
                            Toast.makeText(context, "Simpan " + Siswa.getNama(), Toast.LENGTH_SHORT).show();
                            return true;
                        }else {
                            SiswaList.remove(p);
                            notifyDataSetChanged();
                            Toast.makeText(context, "Hapus " + Siswa.getNama(), Toast.LENGTH_SHORT).show();

                        }
                        return false;
                    }
                });
                popupMenu.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return SiswaList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvnama;
        TextView tvalamat;
        TextView tvmenu;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvnama = itemView.findViewById(R.id.tvnama);
            tvalamat = itemView.findViewById(R.id.tvalamat);
            tvmenu = itemView.findViewById(R.id.tvmenu);
        }
    }
}
