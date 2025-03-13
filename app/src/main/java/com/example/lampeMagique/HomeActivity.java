package com.example.lampeMagique;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Dbg.logMethod();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        ActivityUtil.initWindowMode(this);

        Button btn;
        for(int id : new int[]{R.id.btnRed, R.id.btnGreen, R.id.btnBlue}) {
            btn = findViewById(id);
            btn.setOnClickListener(this);
            int color =Color.WHITE;
            if(id == R.id.btnRed){
                color = Color.RED;
            } else if(id == R.id.btnGreen) {
                color = Color.GREEN;
            } else if(id == R.id.btnBlue) {
                color = Color.BLUE;
            }
            btn.setTextColor(RgbColor.textColorToContrast(color));
        }
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