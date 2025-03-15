package com.example.lampeMagique;

import android.annotation.SuppressLint;
import android.graphics.ColorSpace;
import android.os.Looper;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

public class ServerThread extends Thread {
    private static final String SERVER_NAME = "chadok.info";
    private static final int SERVER_PORT = 9998;
    private static final int NO_LAMP = 5;
    private final RgbColor couleurLampe;

    public ServerThread(RgbColor couleurLampe) {
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
            e.printStackTrace();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
