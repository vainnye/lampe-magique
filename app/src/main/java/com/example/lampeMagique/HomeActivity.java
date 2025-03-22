package com.example.lampeMagique;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.os.LocaleListCompat;

import com.example.lampeMagique.model.Couleur;
import com.example.lampeMagique.util.ActivityUtil;
import com.example.lampeMagique.util.Dbg;

import java.util.Locale;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    private static Boolean localeHasChanged = false;

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
            btn.setTextColor(Couleur.textColorToContrast(color));
        }


        Spinner spinner = (Spinner) findViewById(R.id.locale_selector);
        // Create an ArrayAdapter using the string array and a default spinner layout.
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.locale_array,
                android.R.layout.simple_spinner_item
        );
        // Specify the layout to use when the list of choices appears.
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner.
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
        spinner.setSelection(adapter.getPosition(getString(R.string.current_language_name)));
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

        intent.putExtra(MainActivity.K_SAVED_COULEUR, new Couleur(color));
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

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
        String localeCode = "en";
        if(adapterView.getItemAtPosition(pos).toString().equals(getString(R.string.lang_fr))) {
            localeCode = "fr";
        }

        // la méthode onItemSelected() a l'air d'être appelée une autre fois après avoir changé la locale
        // et HomeActivity est détruite lorsque je change la locale, donc localeHasChnaged est un attribut statique
        // TODO: un bug bizarre, onItemSelected() est appelée 2 fois après onResume() en choisissant le français, au lieu d'1 fois en choisissant l'anglais
        if(localeHasChanged) {
            Toast.makeText(this, getString(R.string.language_was_set_to, getString(R.string.current_language_name)), Toast.LENGTH_SHORT).show();
            localeHasChanged = false;
        }

        Locale newLocale = new Locale(localeCode); // Change to desired locale
        if(!AppCompatDelegate.getApplicationLocales().get(0).toLanguageTag().equals(newLocale.toLanguageTag())) {
            AppCompatDelegate.setApplicationLocales(LocaleListCompat.create(newLocale));
            localeHasChanged = true;
        }

        Dbg.logInMethod("localeCode = "+localeCode+", currLang = "+getString(R.string.current_language_name));
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
    }
}