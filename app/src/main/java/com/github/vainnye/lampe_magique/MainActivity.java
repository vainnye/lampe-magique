package com.github.vainnye.lampe_magique;

import static com.github.vainnye.lampe_magique.util.Util.returnNotNull;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.github.vainnye.lampe_magique.model.Couleur;
import com.github.vainnye.lampe_magique.task.ServerTask;
import com.github.vainnye.lampe_magique.util.ActivityUtil;
import com.github.vainnye.lampe_magique.util.Dbg;

import org.jetbrains.annotations.NotNull;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener {

    public static final String K_SAVED_COULEUR = "K_SAVED_COULEUR";
    public static final String K_SAVED_SWAPPABLE_COULEUR = "K_SWAPPABLE_COULEUR";
    public static final String K_SAVED_COULEUR_CHOIX_INITIAL = "K_SAVED_COULEUR_CHOIX_INITIAL";
    public static final String K_SAVED_COULEUR_SELECTIONNEE = "K_SAVED_COULEUR_SELECTIONNEE";
    public static final String K_SAVED_COULEUR_AVANT_ANIMATION = "K_SAVED_COULEUR_AVANT_ANIMATION";
    public static final String K_SAVED_ANIMATION_STATE = "K_SAVED_ANIMATION_STATE";
    public static final int MAX_COLOR_GAP = 16;

    private static final int DFT_COULEUR_LAMPE = Color.BLACK;
    private static final int DFT_COULEUR_SWAPPABLE = R.color.darkGreen;

    private Couleur couleurLampe, couleurSwappable;
    private Couleur couleurLampeSelectionneeAvecBoutons;
    private Couleur couleurLampeChoixInitial;
    private Couleur lampColorBeforeAnimation;
    private Couleur lampColorOnExitAnimation;
    private volatile int animationStateAtTheEnd;
    private String texteLampe() {
        return  getString(R.string.lamp_color_rgb, couleurLampe.red(), couleurLampe.green(), couleurLampe.blue());
    }
    private Thread threadAnimation;
    private Thread serverThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Dbg.logMethod();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActivityUtil.initWindowMode(this);

        if(getIntent() != null) {
            couleurLampe = (Couleur) getIntent().getParcelableExtra(K_SAVED_COULEUR);
            couleurSwappable = (Couleur) getIntent().getParcelableExtra(K_SAVED_SWAPPABLE_COULEUR);
            couleurLampeChoixInitial = (Couleur) getIntent().getParcelableExtra(K_SAVED_COULEUR_CHOIX_INITIAL);
        }
        if(savedInstanceState != null) {
            couleurLampe = (Couleur) returnNotNull(couleurLampe, savedInstanceState.getParcelable(K_SAVED_COULEUR));
            couleurLampeSelectionneeAvecBoutons = (Couleur) returnNotNull(couleurLampeSelectionneeAvecBoutons, savedInstanceState.getParcelable(K_SAVED_COULEUR_SELECTIONNEE));
            couleurLampeChoixInitial = (Couleur) returnNotNull(couleurLampeChoixInitial, savedInstanceState.getParcelable(K_SAVED_COULEUR_CHOIX_INITIAL));
            lampColorBeforeAnimation = (Couleur) returnNotNull(lampColorBeforeAnimation, savedInstanceState.getParcelable(K_SAVED_COULEUR_AVANT_ANIMATION));
            animationStateAtTheEnd = savedInstanceState.getInt(K_SAVED_ANIMATION_STATE);
            if(animationStateAtTheEnd > 0) {
                reprendreAnimation(animationStateAtTheEnd);
            }
        }

        if(couleurLampe == null)
            couleurLampe = new Couleur(DFT_COULEUR_LAMPE);
        if(couleurSwappable == null)
            couleurSwappable = new Couleur(DFT_COULEUR_SWAPPABLE);
        if(couleurLampeChoixInitial == null)
            couleurLampeChoixInitial = couleurLampe.copy();

        couleurLampeSelectionneeAvecBoutons = couleurLampe.copy();
        lampColorOnExitAnimation = couleurLampe.copy();

        paintLamps(couleurLampe, ServerTask.Cible.DFT_LAMP);
        Button btnLampe = findViewById(R.id.btnLampe);
        btnLampe.setOnClickListener(this);
        btnLampe.setOnLongClickListener(this);

        Button btnSwap = findViewById(R.id.btnExchange);
        paintButton(btnSwap, couleurSwappable, null);
        btnSwap.setOnClickListener(this);

        Button btn;
        Couleur coulTemp = new Couleur();
        for(int id : new int[]{R.id.btnMoreRed, R.id.btnLessRed, R.id.btnMoreGreen, R.id.btnLessGreen, R.id.btnMoreBlue, R.id.btnLessBlue}) {
            btn = findViewById(id);
            btn.setOnClickListener(this);
            coulTemp.setTo(Color.WHITE);
            if(id == R.id.btnMoreRed || id == R.id.btnLessRed){
                coulTemp.setTo(Color.RED);
            } else if(id == R.id.btnMoreGreen || id == R.id.btnLessGreen) {
                coulTemp.setTo(Color.GREEN);
            } else if(id == R.id.btnMoreBlue || id == R.id.btnLessBlue) {
                coulTemp.setTo(Color.BLUE);
            }
            paintButton(btn, coulTemp, null);
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

        outState.putParcelable(K_SAVED_SWAPPABLE_COULEUR, couleurSwappable);
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
            if (v.getId() == R.id.btnExchange) {
                couleurLampeSelectionneeAvecBoutons = couleurSwappable;
                couleurSwappable = couleurLampe;
                couleurLampe = couleurLampeSelectionneeAvecBoutons;

                paintButton(findViewById(R.id.btnExchange), couleurSwappable, null);
                paintLamps(couleurLampe, ServerTask.Cible.DFT_LAMP);
            }

            if (v.getId() == R.id.btnMoreRed)
                couleurLampe.addRed(MAX_COLOR_GAP);
            else if (v.getId() == R.id.btnLessRed)
                couleurLampe.rmvRed(MAX_COLOR_GAP);
            else if (v.getId() == R.id.btnMoreGreen)
                couleurLampe.addGreen(MAX_COLOR_GAP);
            else if (v.getId() == R.id.btnLessGreen)
                couleurLampe.rmvGreen(MAX_COLOR_GAP);
            else if (v.getId() == R.id.btnMoreBlue)
                couleurLampe.addBlue(MAX_COLOR_GAP);
            else if (v.getId() == R.id.btnLessBlue)
                couleurLampe.rmvBlue(MAX_COLOR_GAP);

            couleurLampeSelectionneeAvecBoutons.setTo(couleurLampe);
            paintLamps(couleurLampe, ServerTask.Cible.DFT_LAMP);
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
                if(!couleurLampe.equals(couleurLampeSelectionneeAvecBoutons)) {
                    couleurLampe.setTo(couleurLampeSelectionneeAvecBoutons);
                    Toast.makeText(this /* MyActivity */, getString(R.string.back_to_selected_color), Toast.LENGTH_SHORT).show();
                }
                else {
                    couleurLampe.setTo(couleurLampeChoixInitial);
                    Toast.makeText(this /* MyActivity */, getString(R.string.back_to_initial_color), Toast.LENGTH_SHORT).show();
                }
                paintLamps(couleurLampe, ServerTask.Cible.DFT_LAMP);
                return true;
            } else {
                Dbg.logInMethod("[clic long]: désactivation de l'animation");
                arreterAnimation(lampColorBeforeAnimation);
                animationStateAtTheEnd = 0;
                Toast.makeText(this /* MyActivity */, getString(R.string.back_to_color_before_animation), Toast.LENGTH_SHORT).show();
                return true;
            }
        }

        return false;
    }

    private void paintButton(Button btn, Couleur nouvCouleur, String text) {
        btn.setBackgroundColor(nouvCouleur.toIntColor());
        if(text != null)
            btn.setText(text);
        btn.setTextColor(Couleur.textColorToContrast(nouvCouleur.toIntColor()));
    }

    private void paintOnlyInAppLamp(Couleur nouvCouleur) {
        paintButton(findViewById(R.id.btnLampe), nouvCouleur, texteLampe());
    }

    private void paintLamps(Couleur nouvCouleur, ServerTask.Cible cible) {
        paintOnlyInAppLamp(nouvCouleur);
        if(serverThread != null) serverThread.interrupt();
        serverThread = new Thread( new ServerTask(couleurLampe, cible, (response) -> {
            ((TextView) findViewById(R.id.tvServerResponse)).setText(
                    getString(R.string.server_response,response)
            );
        }));
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
        lampColorOnExitAnimation = couleurLampe.copy();
        threadAnimation = new ThreadAnimation(couleurLampe);
        threadAnimation.start();
    }
    public void reprendreAnimation(int animationState) {
        lampColorBeforeAnimation = couleurLampe.copy();
        threadAnimation = new ThreadAnimation(couleurLampe, animationState);
        threadAnimation.start();
    }

    public void arreterAnimation(Couleur colorOnExit) {
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
        private final Handler handler = new Handler(Looper.getMainLooper());
        private Couleur couleurInitiale;
        private int animationState;
        private static final int MAX_DEG_HUE = 720;
        private static final int MILLIS_DELAY = 35;

        public ThreadAnimation(Couleur couleurLampe) {
            this.couleurInitiale = couleurLampe;
            this.animationState = 0;
        }
        public ThreadAnimation(Couleur couleurLampe, int animationState) {
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
            paintLamps(couleurLampe, ServerTask.Cible.DFT_LAMP);
            threadAnimation = null;
        }

        private void handleChangeCouleurLampe(int nouvCouleur) {
            handler.post(() -> {
                couleurLampe = new Couleur(nouvCouleur);
                paintLamps(couleurLampe, ServerTask.Cible.ALL_LAMPS);
            });
        }
    }
}