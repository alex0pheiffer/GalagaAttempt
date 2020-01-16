package com.example.galagaattempt;

public class bullets {

    private int perc;
    private int region; //which box are we in? from 0 to rows-1
    private int yCoor;

    public bullets(int perc, int region) {
        this.perc = Math.abs(perc);
        this.region = region;
        this.yCoor = -1;
    }

    public int getPerc() {
        return perc;
    }

    public void setyCoor(int yCoor) {
        this.yCoor = yCoor;
    }

    public int getRegion() {
        return region;
    }

    public int getyCoor() {
        return yCoor;
    }
}
