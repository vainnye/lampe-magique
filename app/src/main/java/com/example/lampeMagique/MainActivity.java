package com.example.lampeMagique;

import static com.example.lampeMagique.Util.returnNotNull;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener {

    public static final String K_SAVED_COULEUR = "K_SAVED_COULEUR";
    public static final String K_SAVED_COULEUR_CHOIX_INITIAL = "K_SAVED_COULEUR_CHOIX_INITIAL";
    public static final String K_SAVED_COULEUR_SELECTIONNEE = "K_SAVED_COULEUR_SELECTIONNEE";
    public static final String K_SAVED_COULEUR_AVANT_ANIMATION = "K_SAVED_COULEUR_AVANT_ANIMATION";
    public static final String K_SAVED_ANIMATION_STATE = "K_SAVED_ANIMATION_STATE";

    private static final int DFT_COULEUR_LAMPE = Color.BLACK;

    private final Handler handler = new Handler(Looper.getMainLooper());

    private RgbColor couleurLampe;
    private RgbColor couleurLampeSelectionneeAvecBoutons;
    private RgbColor couleurLampeChoixInitial;
    private RgbColor lampColorBeforeAnimation;
    private RgbColor lampColorOnExitAnimation;
    private volatile int animationStateAtTheEnd;
    private String texteLampe() { return "R:"+couleurLampe.red()+" G:"+couleurLampe.green()+" B:"+couleurLampe.blue(); }
    private ThreadAnimation threadAnimation;
    private OneTimeServerThread serverThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Dbg.logMethod();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActivityUtil.initWindowMode(this);

        if(getIntent() != null) {
            couleurLampe = (RgbColor) getIntent().getParcelableExtra(K_SAVED_COULEUR);
            couleurLampeChoixInitial = (RgbColor) getIntent().getParcelableExtra(K_SAVED_COULEUR_CHOIX_INITIAL);
        }
        if(savedInstanceState != null) {
            couleurLampe = (RgbColor) returnNotNull(couleurLampe, savedInstanceState.getParcelable(K_SAVED_COULEUR));
            couleurLampeSelectionneeAvecBoutons = (RgbColor) returnNotNull(couleurLampeSelectionneeAvecBoutons, savedInstanceState.getParcelable(K_SAVED_COULEUR_SELECTIONNEE));
            couleurLampeChoixInitial = (RgbColor) returnNotNull(couleurLampeChoixInitial, savedInstanceState.getParcelable(K_SAVED_COULEUR_CHOIX_INITIAL));
            lampColorBeforeAnimation = (RgbColor) returnNotNull(lampColorBeforeAnimation, savedInstanceState.getParcelable(K_SAVED_COULEUR_AVANT_ANIMATION));
            animationStateAtTheEnd = savedInstanceState.getInt(K_SAVED_ANIMATION_STATE);
            if(animationStateAtTheEnd > 0) {
                reprendreAnimation(animationStateAtTheEnd);
            }
        }

        if(couleurLampe == null)
            couleurLampe = new RgbColor(DFT_COULEUR_LAMPE);
        if(couleurLampeChoixInitial == null)
            couleurLampeChoixInitial = couleurLampe.copy();

        couleurLampeSelectionneeAvecBoutons = couleurLampe.copy();
        lampColorOnExitAnimation = couleurLampe.copy();

        colorierLampe(couleurLampe.toIntColor());
        Button btnLampe = findViewById(R.id.btnLampe);
        btnLampe.setOnClickListener(this);
        btnLampe.setOnLongClickListener(this);

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
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        Dbg.logMethod();
        super.onSaveInstanceState(outState);
        if(serverThread != null){
            serverThread.interrupt();
            serverThread = null;
        }
        if(threadAnimation != null) {
            interrompreAnimation();
            outState.putParcelable(K_SAVED_COULEUR_AVANT_ANIMATION, lampColorBeforeAnimation);
            outState.putInt(K_SAVED_ANIMATION_STATE, animationStateAtTheEnd);
        }

        outState.putParcelable(K_SAVED_COULEUR, couleurLampe);
        outState.putParcelable(K_SAVED_COULEUR_CHOIX_INITIAL, couleurLampeChoixInitial);
        outState.putParcelable(K_SAVED_COULEUR_SELECTIONNEE, couleurLampeSelectionneeAvecBoutons);
    }

    @Override
    public void onClick(View v) {
        Dbg.logMethod();
        if(threadAnimation == null) {
            if (v.getId() == R.id.btnLampe) {
                demarrerAnimation();
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

            couleurLampeSelectionneeAvecBoutons.setTo(couleurLampe);
            colorierLampe(couleurLampe.toIntColor());
            return;
        }
        else {
            if (v.getId() == R.id.btnLampe) {
                Dbg.logInMethod("[clic court]: désactivation de l'animation");
                arreterAnimation(couleurLampe);
                animationStateAtTheEnd = 0;
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
                if(!couleurLampe.equals(couleurLampeSelectionneeAvecBoutons))
                    couleurLampe.setTo(couleurLampeSelectionneeAvecBoutons);
                else
                    couleurLampe.setTo(couleurLampeChoixInitial);
                colorierLampe(couleurLampe.toIntColor());
                return true;
            } else {
                Dbg.logInMethod("[clic long]: désactivation de l'animation");
                arreterAnimation(lampColorBeforeAnimation);
                animationStateAtTheEnd = 0;
                return true;
            }
        }

        return false;
    }

    private void colorierLampe(int nouvCouleur) {
        Button lampe = findViewById(R.id.btnLampe);
        lampe.setBackgroundColor(nouvCouleur);
        lampe.setText(texteLampe());
        lampe.setTextColor(RgbColor.textColorToContrast(nouvCouleur));
        if(serverThread != null) serverThread.interrupt();
        serverThread = new OneTimeServerThread(couleurLampe.red(), couleurLampe.green(), couleurLampe.blue());
        serverThread.start();
    }

    @Override
    protected void onResume() { Dbg.logMethod(); super.onResume(); }
    @Override
    protected void onPause() { Dbg.logMethod(); super.onPause(); }
    @Override
    protected void onStart() { Dbg.logMethod(); super.onStart(); }
    @Override
    protected void onStop() { Dbg.logMethod(); super.onStop(); }
    @Override
    protected void onRestart() { Dbg.logMethod(); super.onRestart(); }
    @Override
    protected void onDestroy() { Dbg.logMethod(); super.onDestroy(); }

    public void demarrerAnimation() {
        lampColorBeforeAnimation = couleurLampe.copy();
        threadAnimation = new ThreadAnimation(couleurLampe);
        threadAnimation.start();
    }
    public void reprendreAnimation(int animationState) {
        lampColorBeforeAnimation = couleurLampe.copy();
        threadAnimation = new ThreadAnimation(couleurLampe, animationState);
        threadAnimation.start();
    }

    public void arreterAnimation(RgbColor colorOnExit) {
        lampColorOnExitAnimation.setTo(colorOnExit);
        interrompreAnimation();
        animationStateAtTheEnd = 0;
    }
    public void interrompreAnimation() {
        threadAnimation.interrupt();
        try {
            threadAnimation.join();
            Dbg.logInMethod("threadAnimation has been joined");
        } catch (InterruptedException e) {
            Dbg.logInMethod("threadAnimation has thrown InterruptedException: "+e.getMessage());
            throw new RuntimeException(e);
        }
    }


    private class ThreadAnimation extends Thread {
        private RgbColor couleurInitiale;
        private int animationState;
        private static final int MAX_DEG_HUE = 720;
        private static final int MILLIS_DELAY = 35;

        public ThreadAnimation(RgbColor couleurLampe) {
            this.couleurInitiale = couleurLampe;
            this.animationState = 0;
        }
        public ThreadAnimation(RgbColor couleurLampe, int animationState) {
            this.couleurInitiale = couleurLampe;
            this.animationState = animationState;
        }

        @Override
        public void run() {
            Dbg.logMethod();
            float[] hsv = new float[3];
            Color.colorToHSV(couleurInitiale.toIntColor(), hsv);
            // l'animation revient automatiquement à la couleur initiale si elle se termine correctement
            for(int i=animationState; i<=MAX_DEG_HUE; i+=3) {
                if(isInterrupted()){
                    animationState = i;
                    break;
                }
                int couleurTemp = Color.HSVToColor(new float[]{(hsv[0]+i)%360, hsv[1], hsv[2]});
                handleChangeCouleurLampe(couleurTemp);
                try {
                    Thread.sleep(MILLIS_DELAY);
                } catch (InterruptedException e) {
                    interrupt();
                    Dbg.logInMethod("thread d'animation interrompu durant Thread.sleep()");
                }
            }
            animationStateAtTheEnd = animationState;
            Dbg.logInMethod("changement de la couleur à couleurLampeApresAnimation : "+couleurLampe);
            couleurLampe.setTo(lampColorOnExitAnimation);
            colorierLampe(couleurLampe.toIntColor());
            threadAnimation = null;
        }

        private void handleChangeCouleurLampe(int nouvCouleur) {
            handler.post(() -> {
                couleurLampe = new RgbColor(nouvCouleur);
                colorierLampe(nouvCouleur);
            });
        }
    }

}