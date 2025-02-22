package com.example.lampeMagique;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.Console;
import java.io.Serializable;

import kotlin.jvm.internal.Reflection;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Dbg.logMethod();
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.home), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        findViewById(R.id.btnRed).setOnClickListener(this);
        findViewById(R.id.btnGreen).setOnClickListener(this);
        findViewById(R.id.btnBlue).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Dbg.logMethod();

        int color;
        Intent intent = new Intent(this, MainActivity.class);

        if (v.getId() == R.id.btnRed)       color = Color.RED;
        else if(v.getId() == R.id.btnGreen) color = Color.GREEN;
        else if(v.getId() == R.id.btnBlue)  color = Color.BLUE;
        else return;

        intent.putExtra(MainActivity.K_SAVED_COLOR, new RgbColor(color));
        startActivity(intent);
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
    }
}