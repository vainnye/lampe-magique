package com.example.lampeMagique;

import android.graphics.Color;

import java.security.InvalidParameterException;

import kotlin.NotImplementedError;

public class RgbColor {
    private int red;
    private int green;
    private int blue;

    private enum COMPS {
        RED,
        GREEN,
        BLUE
    }

    public RgbColor() {
        red = 255;
        green = 255;
        blue = 255;
    }

    public RgbColor(int red, int blue, int green) {
        setRed(red);
        setGreen(green);
        setBlue(blue);
    }

    private int getComponent(COMPS c) {
        switch (c) {
            case RED: return this.red;
            case GREEN: return this.green;
            case BLUE: return this.blue;
            default: throw new NotImplementedError("le composant "+c.name()+" n'a pas été implémenté");
        }
    }

    private void setComponent(COMPS c, int value) {
        if(value>255 || value<0)
            throw new InvalidParameterException("int red must be between 0 and 255");
        switch (c) {
            case RED: this.red = value; break;
            case GREEN: this.green = value; break;
            case BLUE: this.blue = value; break;
            default: throw new NotImplementedError("le composant "+c.name()+" n'a pas été implémenté");
        }
    }

    public void setRed(int red) { setComponent(COMPS.RED, red); }
    public void setGreen(int green) { setComponent(COMPS.GREEN, green); }
    public void setBlue(int blue) { setComponent(COMPS.BLUE, blue); }

    private Boolean addComponent(COMPS c, int value) {
        try {
            setComponent(c, getComponent(c)+value);
            return true;
        }
        catch (Exception e) {
            return false;
        }
    }

    public Boolean addRed(int red) { return addComponent(COMPS.RED, red); }
    public Boolean addGreen(int green) { return addComponent(COMPS.GREEN, green); }
    public Boolean addBlue(int blue) { return addComponent(COMPS.BLUE, blue); }
    public Boolean rmvRed(int red) { return addComponent(COMPS.RED, -red); }
    public Boolean rmvGreen(int green) { return addComponent(COMPS.GREEN, -green); }
    public Boolean rmvBlue(int blue) { return addComponent(COMPS.BLUE, -blue); }


    public int toColor() {
        return Color.rgb(red, blue, green);
    }
}
