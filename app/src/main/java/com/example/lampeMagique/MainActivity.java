package com.example.lampeMagique;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener {

    public static String K_SAVED_COLOR = "K_SAVED_COLOR";

    private static final int DFT_COULEUR_LAMPE = Color.BLACK;
    private RgbColor couleurLampe;
    private RgbColor couleurLampeInitiale;
    private RgbColor couleurLampeAvantAnimation;
    private RgbColor couleurLampeApresAnimation;
    private final Handler handler = new Handler();
    private String texteLampe() { return "R:"+couleurLampe.red()+" G:"+couleurLampe.green()+" B:"+couleurLampe.blue(); }
    private Thread threadAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Dbg.logMethod();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActivityUtil.initWindowMode(this);

        if(getIntent() != null)
            couleurLampe = (RgbColor) getIntent().getParcelableExtra(K_SAVED_COLOR);
        if(savedInstanceState != null)
            couleurLampe = (RgbColor) savedInstanceState.getParcelable(K_SAVED_COLOR);

        if(couleurLampe == null)
            couleurLampe = new RgbColor(DFT_COULEUR_LAMPE);

        couleurLampeInitiale = couleurLampe;

        try {
            colorierLampe(couleurLampe.toIntColor());
            Button btnLampe = findViewById(R.id.btnLampe);
            btnLampe.setOnClickListener(this);
            btnLampe.setOnLongClickListener(this);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        Button btn;
        for(int id : new int[]{R.id.btnMoreRed, R.id.btnLessRed, R.id.btnMoreGreen, R.id.btnLessGreen, R.id.btnMoreBlue, R.id.btnLessBlue}) {
            btn = findViewById(id);
            btn.setOnClickListener(this);
            int color = Color.WHITE;
            if(id == R.id.btnMoreRed || id == R.id.btnLessRed){
                color = Color.RED;
            } else if(id == R.id.btnMoreGreen || id == R.id.btnLessGreen) {
                color = Color.GREEN;
            } else if(id == R.id.btnMoreBlue || id == R.id.btnLessBlue) {
                color = Color.BLUE;
            }
            btn.setTextColor(RgbColor.textColorToContrast(color));
        }
    }

    @Override
    public void onClick(View v) {
        Dbg.logMethod();
        if(threadAnimation == null) {
            if (v.getId() == R.id.btnLampe) {
                couleurLampeAvantAnimation = new RgbColor(couleurLampe.toIntColor());
                couleurLampeApresAnimation = couleurLampeAvantAnimation;
                threadAnimation = new ThreadAnimation(couleurLampe);
                threadAnimation.start();
                return;
            }

            int color;
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

            colorierLampe(couleurLampe.toIntColor());
            return;
        }
        else {
            if (v.getId() == R.id.btnLampe) {
                Dbg.logInMethod("[clic court]: désactivation de l'animation");
                couleurLampeApresAnimation = couleurLampe;
                arreterThreadAnimation();
                return;
            }
        }
    }

    @Override
    public boolean onLongClick(View v) {
        Dbg.logMethod();
        if(v.getId() == R.id.btnLampe) {
            if(threadAnimation == null) {
                Dbg.logInMethod("[clic long]: retour à la couleur initiale");
                couleurLampe = couleurLampeInitiale;
                colorierLampe(couleurLampeInitiale.toIntColor());
                return true;
            } else {
                Dbg.logInMethod("[clic long]: désactivation de l'animation");
                couleurLampeApresAnimation = couleurLampeAvantAnimation;
                arreterThreadAnimation();
                return true;
            }
        }

        return false;
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

    private void colorierLampe(int nouvCouleur) {
        colorierBouton(findViewById(R.id.btnLampe), nouvCouleur);
    }

    private void colorierBouton(Button btn, int nouvCouleur) {
        btn.setBackgroundColor(nouvCouleur);
        btn.setText(texteLampe());
        btn.setTextColor(RgbColor.textColorToContrast(nouvCouleur));
    }

    public void arreterThreadAnimation() {
        threadAnimation.interrupt();
        try {
            threadAnimation.join();
            Dbg.logInMethod("threadAnimation has been joined");
        } catch (InterruptedException e) {
            Dbg.logInMethod("threadAnimation has thrown InterruptedException: "+e.getMessage());
            throw new RuntimeException(e);
        }
        threadAnimation = null;
    }


    private class ThreadAnimation extends Thread {
        private RgbColor couleurInitiale;
        private final Handler handler = new Handler();

        public ThreadAnimation(RgbColor couleurLampe) {
            this.couleurInitiale = couleurLampe;
        }

        @Override
        public void run () {
            Dbg.logMethod();
            float[] hsv = new float[3];
            Color.colorToHSV(couleurInitiale.toIntColor(), hsv);
            // l'animation revient automatiquement à la couleur initiale si elle se termine correctement
            for(int i=0; i<=360; i+=3) {
                if(isInterrupted())
                    break;
                int couleurTemp = Color.HSVToColor(new float[]{(hsv[0]+i)%360, hsv[1], hsv[2]});
                handleChangeCouleurLampe(couleurTemp);
                try {
                    Thread.sleep(60);
                } catch (InterruptedException e) {
                    interrupt();
                    Dbg.logInMethod("thread d'animation interrompu durant Thread.sleep()");
                }
            }
            handler.post(() -> {
                Dbg.logInMethod("changement de la couleur à couleurLampeApresAnimation : "+couleurLampe);
                couleurLampe = couleurLampeApresAnimation;
                colorierLampe(couleurLampe.toIntColor());
                arreterThreadAnimation();
            });
        }

        private void handleChangeCouleurLampe(int nouvCouleur) {
            handler.post(() -> {
                couleurLampe = new RgbColor(nouvCouleur);
                colorierLampe(nouvCouleur);
            });
        }
    }

}