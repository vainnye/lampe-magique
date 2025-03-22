package com.github.vainnye.lampe_magique.task;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.github.vainnye.lampe_magique.model.Couleur;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Random;
import java.util.function.Consumer;

public class ServerTask implements Runnable {
    private static final String SERVER_NAME = "chadok.info";
    private static final int SERVER_PORT = 9998;
    private static final int LAMP = 5;
    private static long lastTimeShown = System.currentTimeMillis();
    int red, green, blue;
    private Consumer<String> onServerResponse;
    private Cible cible;

    public enum Cible {
        DFT_LAMP,
        ALL_LAMPS
    }


    public ServerTask(Couleur couleur, Cible cible, Consumer<String> onServerResponse) {
        this.red = couleur.red();
        this.green = couleur.green();
        this.blue = couleur.blue();
        this.onServerResponse = onServerResponse;
        this.cible = cible;
    }

    @SuppressLint("DefaultLocale")
    @Override
    public synchronized void run() {
        try {
            Socket serveur = new Socket(SERVER_NAME, SERVER_PORT);
            PrintWriter writer = new PrintWriter(serveur.getOutputStream(), true);
            BufferedReader reader = new BufferedReader(new InputStreamReader(serveur.getInputStream()));

            int selectedLamp;
            String message, response;
            Random rand = new Random();

            if(cible == Cible.ALL_LAMPS) {
                selectedLamp = rand.nextInt(9)+1;
            }
            else {
                selectedLamp = LAMP;
            }

            message = String.format("%02d%02x%02x%02x", selectedLamp, red, green, blue);
            writer.println(message);
            response = reader.readLine();

            Log.i("[server response]", "sent:[ " + message + " ], response:[ " + response + " ]");
            new Handler(Looper.getMainLooper()).post(() -> {
                onServerResponse.accept(response);
            });


            serveur.close();
            writer.close();
            reader.close();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
