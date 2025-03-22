package com.example.lampeMagique.task;

import android.annotation.SuppressLint;
import android.util.Log;

import com.example.lampeMagique.model.Couleur;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;


/**
 * idée abandonnée de garder un thread en arrière plan tout le long de la vie de MainActivity
 * pour éviter d'en créer un à chaque fois que je veux envoyer un message au serveur
 */
public class ServerDaemon extends Thread {
    private static final String SERVER_NAME = "chadok.info";
    private static final int SERVER_PORT = 9998;
    private static final int NO_LAMP = 5;
    private final Couleur couleurLampe;

    public ServerDaemon(Couleur couleurLampe) {
        this.couleurLampe = couleurLampe;
    }

    @Override
    public synchronized void run() {
        try {
            Socket serveur = new Socket(SERVER_NAME, SERVER_PORT);
            PrintWriter writer = new PrintWriter(serveur.getOutputStream(), true);
            BufferedReader reader = new BufferedReader(new InputStreamReader(serveur.getInputStream()));
            while (!isInterrupted()) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                @SuppressLint("DefaultLocale")
                String message = String.format("%02d%02x%02x%02x", NO_LAMP, couleurLampe.red(), couleurLampe.green(), couleurLampe.blue());

                writer.println(message);
                Log.i("[server response]", "sent:[ " + message + " ], response:[ " + reader.readLine() + " ]");
            }
            serveur.close();
            writer.close();
            reader.close();
        } catch (UnknownHostException e) {
            Log.e("serveur de la lampe", "exception UnknownHostException", e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
