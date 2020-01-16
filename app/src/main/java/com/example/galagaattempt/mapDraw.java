package com.example.galagaattempt;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;

public class mapDraw extends Drawable {
    
    private final Paint paint1;
    private final Paint paint2;
    private int[][] map;
    private Canvas canvas;

    public mapDraw (int[][] mapDraw) {

        paint1= new Paint();
        paint1.setStyle(Paint.Style.STROKE);
        paint1.setARGB(255, 255, 255, 255);
        paint2= new Paint();
        paint2.setStyle(Paint.Style.STROKE);
        paint2.setARGB(255, 0, 0, 0);
    }

    public void addBox(int x, int y) {
        box(x,y,paint1);
    }

    public void removeBox(int x, int y) {
        box(x,y, paint2);
    }

    //"box"
    //draws a box at the position in the map
    private void box(int x, int y, Paint paint) {
        int width = getBounds().width();
        int height = getBounds().height()/5*2;
        float xEdge = (float)width/map[0].length;
        float yEdge = (float)height/map.length;
        //x is 0 to rows-1
        //will give the left edge of the box
        float xCoor = (xEdge)*x;
        //y is 0 to lines-1
        //will give the bottom edge of the box
        float yCoor = (yEdge)*y;
        canvas.drawRect(xCoor,yCoor+yEdge,xCoor+xEdge,yCoor,paint);
    }

    //"shift_line"
    //moves the lines from given line to the top line (rows)
    public void shiftLines(int line, int[][] map) {
        //first update the map
        this.map = map;
        //update the UI
        for (int i = line; i < map.length; i++) {
            for (int j = 0; j < map[0].length; j++) {
                if (map[i][j] < 1) {
                    //the item is 0
                    removeBox(i,j);
                }
                else addBox(i,j);
            }
        }
    }

    public void drawBullet(int perc) {
        int width = getBounds().width();
        int height = getBounds().height()/5*2;
    }

    @Override
    public void draw(Canvas canvas) {
        // Get the drawable's bounds
        this.canvas = canvas;
    }

    @Override
    public void setAlpha(int alpha) {
        // This method is required
    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {
        // This method is required
    }

    @Override
    public int getOpacity() {
        // Must be PixelFormat.UNKNOWN, TRANSLUCENT, TRANSPARENT, or OPAQUE
        return PixelFormat.OPAQUE;
    }
}

