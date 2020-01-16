package com.example.galagaattempt;

public class bullets {

    private int perc;
    private int rows; //number of rows there are when this bullet was created
    private int region;
    private int yCoor;

    public bullets(int perc, int rows) {
        this.perc = perc;
        this.rows = rows;
        this.yCoor = -1;
        this.region = -1;
    }

    public int getPerc() {
        return perc;
    }

    public void setyCoor(int yCoor) {
        this.yCoor = yCoor;
    }

    public void setRegion(int region) {
        this.region = region;
    }

    public int getRegion() {
        return region;
    }

    public int getRows() {
        return rows;
    }

    public int getyCoor() {
        return yCoor;
    }
}
