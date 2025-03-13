package fr.universite.paris.saclay.chantal.keller.ackermann;

import android.os.Bundle;
import android.os.Handler;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Stack;

public class AckermannActivity extends AppCompatActivity implements View.OnClickListener {

    // Saisie des deux paramètres par l'utilisateurice
    private EditText l, r;

    // Affichage des résultats
    private TextView resDevice, resServer;

    // Thread pour le calcul sur la tablette
    private AckermannThread ackermannThread;

    // Thread pour appeler le serveur pour faire le calcul
    private ServerThread serverThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ackermann);
        TextView wp = findViewById(R.id.wp);
        wp.setMovementMethod(LinkMovementMethod.getInstance());
        Button add = findViewById(R.id.ack);
        l = findViewById(R.id.l);
        r = findViewById(R.id.r);
        resDevice = findViewById(R.id.resDevice);
        resServer = findViewById(R.id.resServer);
        add.setOnClickListener(this);
        Button stopDevice = findViewById(R.id.stopDevice);
        Button stopServer = findViewById(R.id.stopServer);
        stopDevice.setOnClickListener(this);
        stopServer.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.ack) {
            // Lancement du calcul de la fonction Ackermann
            // Récupération des valeurs saisies par l'utilisateurice
            int g0 = getInt(l);
            int d0 = getInt(r);
            // Lancement directement dans le thread graphique
//            int r0 = ackermann(g0, d0);
//            resDevice.setText(String.valueOf(r0));
            // Lancement dans un worker thread
            ackermannThread = new AckermannThread(g0, d0);
            ackermannThread.start();
            // Communication avec le serveur dans un worker thread
            serverThread = new ServerThread(g0, d0);
            serverThread.start();
        } else if (view.getId() == R.id.stopDevice) {
            // Demande d'interruption du calcul sur la tablette
            ackermannThread.interrupt();
        } else if (view.getId() == R.id.stopServer) {
            // Demande d'interruption de la connection avec le serveur
            serverThread.interrupt();
        }
    }

    // Méthode pour lire un champ de texte éditable sous forme d'entier
    private int getInt(EditText e) {
        String s = String.valueOf(e.getText());
        return Integer.parseInt(s);
    }

    // Worker thread pour calculer Ackermann sur la tablette
    private class AckermannThread extends Thread {
        private final int arg1, arg2;
        private final Handler handler = new Handler();
        public AckermannThread(int a1, int a2) {
            arg1 = a1;
            arg2 = a2;
        }

        @Override
        public void run() {
            // Affichage au démarrage du calcul
            handler.post(new Runnable() {
                @Override
                public void run() {
                    resDevice.setText("Calcul lancé...");
                }
            });
            // Calcul
            int r0 = ackermann(arg1, arg2);
            // Affichage du résultat
            String r = (r0 == -3)?"Calcul interrompu":String.valueOf(r0);
            handler.post(new Runnable() {
                @Override
                public void run() {
                    resDevice.setText(r);
                }
            });
        }
    }

    // Worker thread pour communiquer avec le serveur
    private class ServerThread extends Thread {
        private final int arg1, arg2;
        private final Handler handler = new Handler();
        public ServerThread(int a1, int a2) {
            arg1 = a1;
            arg2 = a2;
        }

        public void run() {
            // Affichage au démarrage du calcul
            handler.post(new Runnable() {
                @Override
                public void run() {
                    resServer.setText("Calcul lancé...");
                }
            });
            try {
                // Tentative de connexion au server
                Socket socket = new Socket("chadok.info", 9874);
                // Flux sortant et entrant
                PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                // Envoi des deux paramètres au serveur
                writer.println(arg1);
                writer.println(arg2);
                try {
                    // Gestion de l'interruption potentielle tant que le serveur n'a pas répondu
                    while (!reader.ready()) {
                        Thread.sleep(500);
                    }
                    // Lecture du résultat
                    int r0 = Integer.parseInt(reader.readLine());
                    // Affichage du résultat
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            resServer.setText(String.valueOf(r0));
                        }
                    });
                } catch (InterruptedException e) {
                    // Cas où l'utilisateurice a demandé l'interruption
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            resServer.setText("Calcul interrompu");
                        }
                    });
                }
                socket.close();
            } catch (IOException e) {
                // Cas où la connexion au serveur a échoué
                throw new RuntimeException(e);
            }
        }
    }


    // Fonction d'Ackermann, calculée de manière itérative (ne fait pas partie du cours)
    public int ackermann(int m0, int n0) {
        Stack<Integer> k = new Stack<>();
        int m = m0, n = n0;
        boolean isAck = true;
        while (!(ackermannThread.isInterrupted())) {
            if (isAck) {
                if (m < 0) return -1;
                if (n < 0) return -2;
                if (m == 0) {
                    m = n+1;
                    isAck = false;
                } else if (n == 0) {
                    m = m-1;
                    n = 1;
                } else {
                    k.push(m-1);
                    n = n-1;
                }
            } else {
                if (k.isEmpty()) return m;
                n = m;
                m = k.pop();
                isAck = true;
            }
        }
        return -3;
    }
}