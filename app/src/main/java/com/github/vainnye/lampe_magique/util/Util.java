package com.github.vainnye.lampe_magique.util;

public class Util {
    // si la deuxième valeur est null, cette fonction renvoie la première valeur
    public static <T> T returnNotNull(T fallback, T nullable) {
        return nullable != null ? nullable : fallback;
    }
}
