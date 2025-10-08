package com.komputerkit.sqlitedatabase;

import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    Database db;
    EditText etBarang, etStok, etHarga;
    TextView tvPilihan;

    List<Barang> databarang = new ArrayList<>();
    BarangAdapter adapter;
    RecyclerView rcvBarang;
    String idbarang;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.etStok), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        load();
        selectData();
    }

    public void load() {
        db = new Database(this);

        etBarang = findViewById(R.id.etBarang);
        etStok = findViewById(R.id.etStok);
        etHarga = findViewById(R.id.etHarga);
        tvPilihan = findViewById(R.id.tvPilihan);
        rcvBarang = findViewById(R.id.rcvBarang);

        tvPilihan.setText("insert");

        rcvBarang.setLayoutManager(new LinearLayoutManager(this));
        rcvBarang.setHasFixedSize(true);
    }

    public void btnSimpan(View view) {
        String barang = etBarang.getText().toString();
        String stok = etStok.getText().toString();
        String harga = etHarga.getText().toString();
        String pilihan = tvPilihan.getText().toString();

        if (barang.isEmpty() || stok.isEmpty() || harga.isEmpty()) {
            pesan("Data Kosong");
        } else {
            if (pilihan.equals("insert")) {
                String sql = "INSERT INTO tblbarang (barang, stok, harga) VALUES ('" + barang + "', " + stok + ", " + harga + ")";
                if (db.runSQL(sql)) {
                    pesan("Berhasil");
                    selectData();
                } else {
                    pesan("Gagal");
                }
            } else {
                String sql = "UPDATE tblbarang\n" +
                        " SET barang = \'"+barang+"', stok = "+stok+", harga = "+harga+" \n" +
                        " WHERE idbarang = "+idbarang+";";

                if (db.runSQL(sql)) {
                    pesan("Data sudhah diubah");
                    selectData();
                }else {
                    pesan("Data tidak diubah");
                }
            }
        }

        etBarang.setText("");
        etHarga.setText("");
        etStok.setText("");
        tvPilihan.setText("insert");
    }

    public void pesan(String isi) {
        Toast.makeText(this, isi, Toast.LENGTH_SHORT).show();
    }

    public void selectData() {
        String sql = "SELECT * FROM tblbarang ORDER BY barang ASC";
        Cursor cursor = db.select(sql);
        databarang.clear();

        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                String idbarang = cursor.getString(cursor.getColumnIndexOrThrow("idbarang"));
                String barang = cursor.getString(cursor.getColumnIndexOrThrow("barang"));
                String stok = cursor.getString(cursor.getColumnIndexOrThrow("stok"));
                String harga = cursor.getString(cursor.getColumnIndexOrThrow("harga"));

                databarang.add(new Barang(idbarang, barang, stok, harga));
            }

            adapter = new BarangAdapter(this, databarang);
            rcvBarang.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        } else {
            pesan("Data Kosong");
        }
    }

    public void deleteData (String id) {
        idbarang = id;
        // Alert
        AlertDialog.Builder al = new AlertDialog.Builder(this);
        al.setTitle("PERINGATAN !");
        al.setMessage("Yakin anda ingin menghapusnya ?");
        al.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String sql = "DELETE FROM tblbarang WHERE idbarang = "+idbarang+";";

                if (db.runSQL(sql)) {
                    pesan("Data telah dihapus");
                    selectData();
                }else {
                    pesan("Data tidak terhapus");
                }
            }
        });
        al.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        al.show();

    }

    public void selectUpdate (String id) {
        idbarang = id;
        String sql = "SELECT * FROM tblbarang WHERE idbarang = "+id+";";
        Cursor cursor = db.select(sql);
        cursor.moveToNext();

        etBarang.setText(cursor.getString(cursor.getColumnIndexOrThrow("barang")));
        etStok.setText(cursor.getString(cursor.getColumnIndexOrThrow("stok")));
        etHarga.setText(cursor.getString(cursor.getColumnIndexOrThrow("harga")));

        tvPilihan.setText("update");
    }
}