package com.komputerkit.konversisuhu;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
    Spinner spinner;
    EditText etnilai;
    TextView tvhasil;

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
//        isiSpinner();
    }

    public void load () {
        spinner = findViewById(R.id.spinner);
        etnilai = findViewById(R.id.etnilai);
        tvhasil = findViewById(R.id.tvhasil);
    }

//    public void isiSpinner () {
//        String[] isi = {"Celcius to Reamur", "Celcius to Fahrenheit", "Celcius to Kelvin"};
//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,isi
//        );
//        spinner.setAdapter(adapter);
//    }


    public void btnkonversi(View view) {
        String pilihan = spinner.getSelectedItem().toString();

        if (etnilai.getText().toString().equals("")) {
            Toast.makeText(this, "Nilai Kosong", Toast.LENGTH_SHORT).show();
        }else {

            if (pilihan.equals("Celsius to Reamur")){
                ctor();
            }
            if (pilihan.equals("Celsius to Fahrenheit")){
                ctof();
            }
            if (pilihan.equals("Celsius to Kelvin")){
                ctok();
            }
            if (pilihan.equals("Reamur to Celcius")){
                rtoc();
            }
            if (pilihan.equals("Reamur to Fahrenheit")){
                rtof();
            }
            if (pilihan.equals("Reamur to Kelvin")){
                rtok();
            }
            if (pilihan.equals("Fahrenheit to Celcius")){
                ftoc();
            }
            if (pilihan.equals("Fahrenheit to Reamur")){
                ftor();
            }
            if (pilihan.equals("Kelvin to Celcius")){
                ktoc();
            }
            if (pilihan.equals("Kelvin to Reamur")){
                ktor();
            }
        }
    }

    public void ctor () {
        double suhu = Double.parseDouble(etnilai.getText().toString());
        double hasil = (4.0/5.0) * suhu;

        tvhasil.setText(hasil + "");
    }

    public void ctof () {
        double suhu = Double.parseDouble(etnilai.getText().toString());
        double hasil = ((9.0/5.0) * suhu) + 32;

        tvhasil.setText(hasil + "");
    }

    public void ctok () {
        double suhu = Double.parseDouble(etnilai.getText().toString());
        double hasil = suhu + 273;

        tvhasil.setText(hasil + "");
    }

    public void rtoc () {
        double suhu = Double.parseDouble(etnilai.getText().toString());
        double hasil = (5.0/4.0) * suhu;

        tvhasil.setText(hasil + "");
    }

    public void rtof () {
        double suhu = Double.parseDouble(etnilai.getText().toString());
        double hasil = (9.0/4.0) * suhu;

        tvhasil.setText(hasil + "");
    }

    public void rtok () {
        double suhu = Double.parseDouble(etnilai.getText().toString());
        double hasil = ((5.0/4.0) * suhu) + 273;

        tvhasil.setText(hasil + "");
    }
    public void ftoc () {
        double suhu = Double.parseDouble(etnilai.getText().toString());
        double hasil = (5.0/9.0) * (suhu - 32);

        tvhasil.setText(hasil + "");
    }
    public void ftor () {
        double suhu = Double.parseDouble(etnilai.getText().toString());
        double hasil = (4.0/9.0) * (suhu - 32);

        tvhasil.setText(hasil + "");
    }

    public void ktoc () {
        double suhu = Double.parseDouble(etnilai.getText().toString());
        double hasil = suhu - 273;

        tvhasil.setText(hasil + "");
    }

    public void ktor () {
        double suhu = Double.parseDouble(etnilai.getText().toString());
        double hasil = (4.0/5.0) * (suhu - 273);

        tvhasil.setText(hasil + "");
    }


}