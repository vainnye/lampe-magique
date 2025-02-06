package com.example.lampeMagique;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private int red = 255;
    private int green = 000;
    private int blue = 000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        // code déjà donné
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        //      Button lamp = findViewById(R.id.btnLampe);
//        lamp.setBackgroundColor(Color.BLACK);
//        lamp.setOnClickListener(this);
//        findViewById(R.id.btnMoreRed).setOnClickListener(this);
//        findViewById(R.id.btnLessRed).setOnClickListener(this);
//        findViewById(R.id.btnMoreGreen).setOnClickListener(this);
//        findViewById(R.id.btnLessGreen).setOnClickListener(this);
//        findViewById(R.id.btnMoreBlue).setOnClickListener(this);
//        findViewById(R.id.btnLessBlue).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Color.rgb(red, blue, green);
        findViewById(R.id.btnLessBlue).setBackgroundColor(Color.rgb(10, 10,10));
//        int color;
//        Button btnLampe = findViewById(R.id.btnLampe);
//        Drawable background = btnLampe.getBackground();
//        if (background instanceof ColorDrawable) {
//            color = ((ColorDrawable) background).getColor();
//            btnLampe.setBackgroundColor(color);
//        }
//        else throw new Error("pas de background");
//
//        switch (v.getId()) {
//            case R.id.btnLampe:
//        }
    }


    private static
}