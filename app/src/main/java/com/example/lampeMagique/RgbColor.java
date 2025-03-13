package com.example.lampeMagique;

import android.graphics.Color;
import android.os.Parcel;
import android.os.Parcelable;


import androidx.annotation.NonNull;

import java.security.InvalidParameterException;

import kotlin.NotImplementedError;

public class RgbColor implements Parcelable {
    private int red;
    private int green;
    private int blue;
    
    private enum COMPS {
        RED, GREEN, BLUE;
        public static final int MAX = 255;
        public static final int MIN = 0;
    }

    public RgbColor() {
        red = COMPS.MAX;
        green = COMPS.MAX;
        blue = COMPS.MAX;
    }
    public RgbColor(int red, int blue, int green) {
        setColor(Color.rgb(red, green, blue));
    }
    public RgbColor(int color) {
        setColor(color);
    }
    public RgbColor(String colorString) {
        setColor(Color.parseColor(colorString));
    }
    public RgbColor(float hue, float sat, float val) {
        setColor(Color.HSVToColor( new float[]{hue, sat, val} ));
    }

    protected RgbColor(Parcel in) {
        red = in.readInt();
        green = in.readInt();
        blue = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(red);
        dest.writeInt(green);
        dest.writeInt(blue);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<RgbColor> CREATOR = new Creator<RgbColor>() {
        @Override
        public RgbColor createFromParcel(Parcel in) {
            return new RgbColor(in);
        }

        @Override
        public RgbColor[] newArray(int size) {
            return new RgbColor[size];
        }
    };

    public void setColor(int color) {
        setRed(Color.red(color));
        setGreen(Color.green(color));
        setBlue(Color.blue(color));
    }

    private int getComponent(@NonNull COMPS c) {
        switch (c) {
            case RED: return this.red;
            case GREEN: return this.green;
            case BLUE: return this.blue;
            default: throw new NotImplementedError("le composant "+c.name()+" n'a pas été implémenté");
        }
    }

    private void setComponent(COMPS c, int value) {
        if(value>COMPS.MAX || value<COMPS.MIN)
            throw new InvalidParameterException("int "+c.name()+" must be between "+COMPS.MIN+" and "+COMPS.MAX);
        switch (c) {
            case RED: this.red = value; break;
            case GREEN: this.green = value; break;
            case BLUE: this.blue = value; break;
            default: throw new NotImplementedError("le composant "+c.name()+" n'a pas été implémenté");
        }
    }

    public int red() { return red; }
    public int green() { return green; }
    public int blue() { return blue; }

    public void setRed(int red) { setComponent(COMPS.RED, red); }
    public void setGreen(int green) { setComponent(COMPS.GREEN, green); }
    public void setBlue(int blue) { setComponent(COMPS.BLUE, blue); }

    private void addComponent(COMPS c, int value) {
        if(value+getComponent(c) > COMPS.MAX)
            value = COMPS.MAX;
        else if(value+getComponent(c) < COMPS.MIN)
            value = COMPS.MIN;
        else
            value = getComponent(c)+value;

        setComponent(c, value);
    }

    public void addRed(int red) { addComponent(COMPS.RED, red); }
    public void addGreen(int green) { addComponent(COMPS.GREEN, green); }
    public void addBlue(int blue) { addComponent(COMPS.BLUE, blue); }
    public void rmvRed(int red) { addComponent(COMPS.RED, -red); }
    public void rmvGreen(int green) { addComponent(COMPS.GREEN, -green); }
    public void rmvBlue(int blue) { addComponent(COMPS.BLUE, -blue); }


    public int toIntColor() {
        return Color.rgb(red, green, blue);
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
        return "R:"+red+" G:"+green+" B:"+blue;
    }
}
