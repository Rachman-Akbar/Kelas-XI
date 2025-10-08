package com.komputerkit.imageinternet;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity {

    private ImageView onlineImage1;
    private ImageView onlineImage2;

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

        onlineImage1 = findViewById(R.id.onlineImage1);
        onlineImage2 = findViewById(R.id.onlineImage2);

        // URL gambar online yang bebas hak cipta dari Unsplash
        String imageUrl1 = "https://images.unsplash.com/photo-1506905925346-21bda4d32df4?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=1000&q=80"; // Mountain landscape
        String imageUrl2 = "https://images.unsplash.com/photo-1441974231531-c6227db76b6e?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=1000&q=80"; // Forest path

        // Memuat gambar pertama dengan Picasso dan placeholder
        Picasso.get()
                .load(imageUrl1)
                .placeholder(R.drawable.placeholder_image) // Gambar placeholder di drawable
                .into(onlineImage1);

        // Memuat gambar kedua dengan Picasso dan placeholder
        Picasso.get()
                .load(imageUrl2)
                .placeholder(R.drawable.placeholder_image)
                .into(onlineImage2);
    }
}