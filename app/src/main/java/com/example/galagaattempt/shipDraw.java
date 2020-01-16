package com.example.galagaattempt;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

public class shipDraw extends View {

    private Paint topPaint;
    private Paint backPaint;
    private int perc;
    private Canvas canvas;

    public shipDraw(Context context, AttributeSet attrs) {
        super(context, attrs);
        topPaint = new Paint();
        //topPaint.setStyle(Paint.Style.STROKE);
        topPaint.setARGB(255, 100, 220, 190);
        backPaint = new Paint();
        backPaint.setStyle(Paint.Style.STROKE);
        backPaint.setARGB(255, 0, 0, 0);
        //Bitmap b = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);
        //canvas = new Canvas(b);
    }

    public void updatePerc(int perc) {
        this.perc = perc;

    }

    public void drawShip(Canvas canvas) {
        // Get the drawable's bounds
        //Log.d("DEBUG","drawShip");
        int width = this.getWidth();
        int height = this.getHeight();
        int radius = height/2;
        //Log.d("DEBUG","drawRect");
        //canvas.drawRect(0,0,width,height,backPaint);
        int xDistance = (int)((Math.abs(perc)/100.0)*(width-(2*radius))+radius);
        //Log.d("DEBUG","drawCircle");
        canvas.drawCircle(xDistance,height/2,radius,topPaint);
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //this.canvas = canvas;
        postInvalidate();
        drawShip(canvas);
    }

}