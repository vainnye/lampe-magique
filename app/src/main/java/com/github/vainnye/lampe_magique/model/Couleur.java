package com.github.vainnye.lampe_magique.model;

import android.graphics.Color;
import android.os.Parcel;
import android.os.Parcelable;


import androidx.annotation.NonNull;

import java.security.InvalidParameterException;
import java.util.Objects;

import kotlin.NotImplementedError;

public class Couleur implements Parcelable {

    private int couleur;

    private final static Couleur NULL_OBJECT = null;

    private enum COMPS {
        RED, GREEN, BLUE;
        public static final int MAX = 255;
        public static final int MIN = 0;
    }

    public Couleur() {
        setTo(Color.rgb(COMPS.MAX, COMPS.MAX, COMPS.MAX));
    }
    public Couleur(int red, int blue, int green) {
        setTo(Color.rgb(red, green, blue));
    }
    public Couleur(int color) {
        setTo(color);
    }
    public Couleur(@NonNull Couleur color) {
        setTo(color);
    }
    public Couleur(String colorString) {
        setTo(Color.parseColor(colorString));
    }
    public Couleur(float hue, float sat, float val) {
        setTo(Color.HSVToColor( new float[]{hue, sat, val} ));
    }

    public void setTo(@NonNull Couleur color) {
        setTo(color.toIntColor());
    }

    public void setTo(int color) {
        if(!isColor(color))
            throw new InvalidParameterException("the int color provided is not valid");
        else
            couleur = color;
    }

    public Couleur copy() {
        return new Couleur(this);
    }

    private static Boolean isColor(int color) {
        int[] comps = {Color.red(color), Color.green(color), Color.blue(color)};
        for(int comp : comps) {
            if(comp < COMPS.MIN || comp > COMPS.MAX) return false;
        }
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o instanceof Integer) return isColor((Integer) o);
        if (o == null || getClass() != o.getClass()) return false;
        Couleur coul = (Couleur) o;
        return toIntColor() == coul.toIntColor();
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(toIntColor());
    }

    private int getComponent(@NonNull COMPS c) {
        switch (c) {
            case RED: return red();
            case GREEN: return green();
            case BLUE: return blue();
            default: throw new NotImplementedError("le composant "+c.name()+" n'a pas été implémenté");
        }
    }

    private void setComponent(COMPS c, int value) {
        if(value>COMPS.MAX || value<COMPS.MIN)
            throw new InvalidParameterException("int "+c.name()+" must be between "+COMPS.MIN+" and "+COMPS.MAX);
        switch (c) {
            case RED:
                couleur = Color.rgb(value, green(), blue());
                break;
            case GREEN:
                couleur = Color.rgb(red(), value, blue());
                break;
            case BLUE:
                couleur = Color.rgb(red(), green(), value);
                break;
            default:
                throw new NotImplementedError("le composant "+c.name()+" n'a pas été implémenté");
        }
    }
    public void setRed(int red) { setComponent(COMPS.RED, red); }
    public void setGreen(int green) { setComponent(COMPS.GREEN, green); }
    public void setBlue(int blue) { setComponent(COMPS.BLUE, blue); }

    public int red() { return Color.red(toIntColor()); }
    public int green() { return Color.green(toIntColor()); }
    public int blue() { return Color.blue(toIntColor()); }

    private void addComponent(COMPS c, int value) {
        if(value+getComponent(c) > COMPS.MAX)
            value = COMPS.MAX;
        else
            value = Math.max(value + getComponent(c), COMPS.MIN);

        setComponent(c, value);
    }
    public void addRed(int red) { addComponent(COMPS.RED, red); }
    public void addGreen(int green) { addComponent(COMPS.GREEN, green); }
    public void addBlue(int blue) { addComponent(COMPS.BLUE, blue); }
    public void rmvRed(int red) { addComponent(COMPS.RED, -red); }
    public void rmvGreen(int green) { addComponent(COMPS.GREEN, -green); }
    public void rmvBlue(int blue) { addComponent(COMPS.BLUE, -blue); }


    public int toIntColor() {
        return couleur;
    }

    public double getLuminance() {
        return Color.luminance(toIntColor());
    }

    public static int textColorToContrast(int color) {
        if(Color.luminance(color) <= 0.5)
            return Color.WHITE;
        else
            return Color.BLACK;
    }

    @NonNull
    public String toString() {
        return "R:"+red()+" G:"+green()+" B:"+blue();
    }




    // -------------------------
    // Parcelable implementation
    // -------------------------
    protected Couleur(Parcel in) {
        setTo(in.readInt());
    }
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(toIntColor());
    }
    @Override
    public int describeContents() {
        return 0;
    }
    public static final Creator<Couleur> CREATOR = new Creator<Couleur>() {
        @Override
        public Couleur createFromParcel(Parcel in) {
            return new Couleur(in);
        }

        @Override
        public Couleur[] newArray(int size) {
            return new Couleur[size];
        }
    };
}
