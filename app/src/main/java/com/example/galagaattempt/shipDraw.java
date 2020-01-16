package com.example.galagaattempt;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.Log;

public class shipDraw extends Drawable {

    private final Paint topPaint;
    private final Paint backPaint;
    private int perc;
    private Canvas canvas;

    public shipDraw (int perc) {

        topPaint= new Paint();
        topPaint.setStyle(Paint.Style.STROKE);
        topPaint.setARGB(255, 255, 255, 255);
        backPaint= new Paint();
        backPaint.setStyle(Paint.Style.STROKE);
        backPaint.setARGB(255, 0, 0, 0);
    }

    public void updatePerc(int perc) {
        this.perc = perc;

    }

    public void drawShip() {
        // Get the drawable's bounds
        int width = getBounds().width();
        int height = getBounds().height();
        int radius = height/2;
        canvas.drawRect(0,0,width,height,backPaint);
        int xDistance = perc*(width-(2*radius));
        canvas.drawCircle(xDistance,height/2,radius,topPaint);
    }

    @Override
    public void draw(Canvas canvas) {
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