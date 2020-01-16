package com.example.galagaattempt;

public class bullets {

    private int perc;
    private int yCoor;

    public bullets(int perc) {
        this.perc = perc;
        this.yCoor = -1;
    }

    public int getPerc() {
        return perc;
    }

    public void setyCoor(int yCoor) {
        this.yCoor = yCoor;
    }

    public int getyCoor() {
        return yCoor;
    }
}
