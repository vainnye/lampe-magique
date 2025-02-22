package com.example.lampeMagique;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public static String K_SAVED_COLOR = "K_SAVED_COLOR";

    private static final int DFT_COULEUR_LAMPE = Color.BLACK;
    private RgbColor couleurLampe;

    private String texteLampe() { return "R:"+couleurLampe.red()+" G:"+couleurLampe.green()+" B:"+couleurLampe.blue(); }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Dbg.logMethod();
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        // code déjà donné
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Button lamp = findViewById(R.id.btnLampe);


        couleurLampe = (RgbColor) getIntent().getParcelableExtra(K_SAVED_COLOR);
        if(savedInstanceState != null)
            couleurLampe = (RgbColor) savedInstanceState.getParcelable(K_SAVED_COLOR);

        if(couleurLampe == null)
            couleurLampe = new RgbColor(DFT_COULEUR_LAMPE);

        lamp.setBackgroundColor(couleurLampe.toIntColor());
        lamp.setText(this.texteLampe());
        adaptTextColor();
        lamp.setOnClickListener(this);

        findViewById(R.id.btnMoreRed).setOnClickListener(this);
        findViewById(R.id.btnLessRed).setOnClickListener(this);
        findViewById(R.id.btnMoreGreen).setOnClickListener(this);
        findViewById(R.id.btnLessGreen).setOnClickListener(this);
        findViewById(R.id.btnMoreBlue).setOnClickListener(this);
        findViewById(R.id.btnLessBlue).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Dbg.logMethod();

        int color;
        Button btnLampe = findViewById(R.id.btnLampe);

        int colorGap = 16;

        if (v.getId() == R.id.btnMoreRed)
            couleurLampe.addRed(colorGap);
        else if (v.getId() == R.id.btnLessRed)
            couleurLampe.rmvRed(colorGap);
        else if (v.getId() == R.id.btnMoreGreen)
            couleurLampe.addGreen(colorGap);
        else if (v.getId() == R.id.btnLessGreen)
            couleurLampe.rmvGreen(colorGap);
        else if (v.getId() == R.id.btnMoreBlue)
            couleurLampe.addBlue(colorGap);
        else if (v.getId() == R.id.btnLessBlue)
            couleurLampe.rmvBlue(colorGap);

        btnLampe.setBackgroundColor(couleurLampe.toIntColor());
        btnLampe.setText(this.texteLampe());
        adaptTextColor();
    }


    @Override
    protected void onStart() {
        Dbg.logMethod();
        super.onStart();
    }

    @Override
    protected void onResume() {
        Dbg.logMethod();
        super.onResume();
    }

    @Override
    protected void onPause() {
        Dbg.logMethod();
        super.onPause();
    }

    @Override
    protected void onStop() {
        Dbg.logMethod();
        super.onStop();
    }

    @Override
    protected void onRestart() {
        Dbg.logMethod();
        super.onRestart();
    }

    @Override
    protected void onDestroy() {
        Dbg.logMethod();
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        Dbg.logMethod();
        super.onSaveInstanceState(outState);
        outState.putParcelable(K_SAVED_COLOR, couleurLampe);
    }

    private void adaptTextColor() {
        Button btnLampe = findViewById(R.id.btnLampe);
        if(couleurLampe.getLuminance() <= 0.5)
            btnLampe.setTextColor(Color.WHITE);
        else
            btnLampe.setTextColor(Color.BLACK);
    }
}