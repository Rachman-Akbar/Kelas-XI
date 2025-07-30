package com.komputerkit.recylerviewcardview;

import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    SiswaAdapter adapter;
    List<Siswa> SiswaList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        load();
        isiData();
    }

    public void load() {
        recyclerView = findViewById(R.id.revsiswa);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    public void isiData() {
        SiswaList = new ArrayList<Siswa>();
        SiswaList.add(new Siswa("Joni", "Sidoarjo"));
        SiswaList.add(new Siswa("Jony", "Surabaya"));
        SiswaList.add(new Siswa("Jony", "Surabaya"));
        SiswaList.add(new Siswa("Jony", "Surabaya"));
        SiswaList.add(new Siswa("Jony", "Surabaya"));
        SiswaList.add(new Siswa("Jony", "Surabaya"));
        SiswaList.add(new Siswa("Jony", "Surabaya"));
        SiswaList.add(new Siswa("Jony", "Surabaya"));
        SiswaList.add(new Siswa("Jony", "Surabaya"));
        SiswaList.add(new Siswa("Jony", "Surabaya"));
        SiswaList.add(new Siswa("Jony", "Surabaya"));
        SiswaList.add(new Siswa("Jony", "Surabaya"));
        SiswaList.add(new Siswa("Jony", "Surabaya"));
        SiswaList.add(new Siswa("Jony", "Surabaya"));
        SiswaList.add(new Siswa("Jony", "Surabaya"));
        SiswaList.add(new Siswa("Jony", "Surabaya"));
        SiswaList.add(new Siswa("Jony", "Surabaya"));
        SiswaList.add(new Siswa("Jony", "Surabaya"));
        SiswaList.add(new Siswa("Jony", "Surabaya"));
        SiswaList.add(new Siswa("Jony", "Surabaya"));

        adapter = new SiswaAdapter(this, SiswaList);
        recyclerView.setAdapter(adapter);
    }

    public void btnTambah(View view) {
        SiswaList.add(new Siswa("Jonyyyyyyyy", "Surabaya"));
        adapter.notifyDataSetChanged();
    }
}