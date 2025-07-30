package com.komputerkit.kalkulator;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
    TextView tvhasil;
    EditText etBil;
    EditText etBil_2;

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
    }

    public void load () {
        tvhasil = findViewById(R.id.tvhasil);
        etBil = findViewById(R.id.etBil);
        etBil_2 = findViewById(R.id.etBil_2);
    }


    public void btnJumlah(View view) {
        if (etBil.getText().toString().equals("") || etBil_2.getText().toString().equals("")) {
            Toast.makeText(this, "ada bilangan yang kosong", Toast.LENGTH_SHORT).show();
        } else {
            double bil = Double.parseDouble(etBil.getText().toString());
            double bil2 = Double.parseDouble(etBil_2.getText().toString());

            double hasil = bil + bil2;
            tvhasil.setText(hasil + "");
        }
    }

    public void btnkurang(View view) {
        if (etBil.getText().toString().equals("") || etBil_2.getText().toString().equals("")) {
            Toast.makeText(this, "ada bilangan yang kosong", Toast.LENGTH_SHORT).show();
        } else {
            double bil = Double.parseDouble(etBil.getText().toString());
            double bil2 = Double.parseDouble(etBil_2.getText().toString());

            double hasil = bil - bil2;
            tvhasil.setText(hasil + "");
        }
    }

    public void btnkali(View view) {
        if (etBil.getText().toString().equals("") || etBil_2.getText().toString().equals("")) {
            Toast.makeText(this, "ada bilangan yang kosong", Toast.LENGTH_SHORT).show();
        } else {
            double bil = Double.parseDouble(etBil.getText().toString());
            double bil2 = Double.parseDouble(etBil_2.getText().toString());

            double hasil = bil * bil2;
            tvhasil.setText(hasil + "");
        }
    }

    public void btnbagi(View view) {
        if (etBil.getText().toString().equals("") || etBil_2.getText().toString().equals("")) {
            Toast.makeText(this, "ada bilangan yang kosong", Toast.LENGTH_SHORT).show();
        } else {
            double bil = Double.parseDouble(etBil.getText().toString());
            double bil2 = Double.parseDouble(etBil_2.getText().toString());

            double hasil = bil / bil2;
            tvhasil.setText(hasil + "");
        }
    }
}