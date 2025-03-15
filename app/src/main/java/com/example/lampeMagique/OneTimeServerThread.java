package com.example.lampeMagique;

import android.annotation.SuppressLint;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.sql.Time;
import java.util.Random;

public class OneTimeServerThread extends Thread {
    private static final String SERVER_NAME = "chadok.info";
    private static final int SERVER_PORT = 9998;
    private static final int LAMP = 5;
    private static long lastTimeChanged = System.currentTimeMillis();;
    int red, green, blue;

    public OneTimeServerThread(int red, int green, int blue) {
        this.red = red;
        this.green = green;
        this.blue = blue;
    }

    @Override
    public synchronized void run() {
        try {
            Socket serveur = new Socket(SERVER_NAME, SERVER_PORT);
            PrintWriter writer = new PrintWriter(serveur.getOutputStream(), true);
            BufferedReader reader = new BufferedReader(new InputStreamReader(serveur.getInputStream()));

            Random rand = new Random();
            int selectedLamp;

            if(System.currentTimeMillis() - lastTimeChanged < 40) {
                selectedLamp = rand.nextInt(9)+1;
            }
            else {
                selectedLamp = LAMP;
            }

            @SuppressLint("DefaultLocale")
            String message = String.format("%02d%02x%02x%02x", selectedLamp, red, green, blue);
            writer.println(message);
            Log.i("[server response]", "sent:[ " + message + " ], response:[ " + reader.readLine() + " ]");
            lastTimeChanged = System.currentTimeMillis();

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
