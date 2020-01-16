package com.example.galagaattempt;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;

public class mapDraw extends View {

    private final double YCOORSTOTAL = 12;
    // % of height per 1/3 second
    private final double bulletVelocity = .004;

    private final Paint paint1;
    private final Paint paint2;
    private final Paint paint3;
    private int[][] map;
    private ArrayList<bullets> bulletList;
    private int shipRadius;
    private boolean collision;
    private int collisionLine;
    private int bulletIndex;

    public mapDraw (Context context, AttributeSet attrs) {
        super(context, attrs);

        paint1= new Paint();
        //paint1.setStyle(Paint.Style.STROKE);
        paint1.setARGB(255, 100, 220, 190);
        paint2= new Paint();
        ///paint2.setStyle(Paint.Style.STROKE);
        paint2.setARGB(255, 100, 220, 220);
        paint3= new Paint();
        paint3.setStyle(Paint.Style.STROKE);
        paint3.setARGB(255, 255, 255, 255);

        bulletList = new ArrayList<bullets>();
        map = new int[0][0];

    }

    public void addBullet(bullets bulletAdded) {
        bulletList.add(bulletAdded);
    }

    public void updateLists(int[][] mapDraw) {
        //Log.d("DEBUG","map: "+mapDraw);
        map = mapDraw;
    }

    //"box"
    //draws a box at the position in the map
    private void box(int x, int y, Paint paint, Canvas canvas) {
        int width = this.getWidth();
        int height = (int)(this.getHeight());
        //Log.d("DRAW","map[0].length="+map[0].length+" map.length="+map.length);
        float xEdge = (float)width/map[0].length;
        float yEdge = (float)(height/YCOORSTOTAL);
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
    /*
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
    */

    public int drawBullet(bullets bullet, Canvas canvas) {
        int width = this.getWidth();
        int height = this.getHeight();
        int xCoor = (int)((Math.abs(bullet.getPerc())/100.0)*(width-(2*shipRadius))+shipRadius);
        //determine what region this bullet will hit
        int region = 0;
        //the ship percent looks at radius --> width-radius
        //what % of the first and last region is not able to be hit by the bullet? (radius/boxwidth)
        //float unablePerc = shipRadius/( (float)width/map[0].length );
        while (((double)(region+1)/(bullet.getRows()))*width < xCoor) {
            if (((double)(region+1)/(bullet.getRows()))*width+10 > xCoor) {
                //the bullet is basically on the border
                if (bullet.getPerc() > 0) {
                    //going to the right>>>
                    xCoor -= 10;
                    region--;
                }
                else if (bullet.getPerc() < 0) {
                    //bullet is going to the left <<<
                    xCoor += 10;
                }
            }
            region++;
        }
        System.out.println(" reg: "+region);
        bullet.setRegion(region);
        //update yCoor
        if (bullet.getyCoor() < 0) {
            //must set bullet yCoor
            bullet.setyCoor(height-(int)(bulletVelocity*height));
        }
        else {
            //bullet is already moving
            canvas.drawCircle(xCoor, bullet.getyCoor(), 3, paint2);
            bullet.setyCoor(bullet.getyCoor() - (int)(bulletVelocity*height));
        }
        //is it in the collision region? ==should always be
        if (bullet.getyCoor() < (float)height*map.length/YCOORSTOTAL) {
            if (bullet.getyCoor() < 0) {
                //reached end of screen
                return map.length;
            }
            int yRegion = 0;
            while (bullet.getyCoor() > ((float)height*(yRegion+1)/YCOORSTOTAL)) {
                yRegion++;
            }
            //yRegion = map.length - yRegion;
            int curRegion = map[yRegion][bullet.getRegion()];
            if (curRegion > 0) {
                //there is a box, remove the box
                map[yRegion][bullet.getRegion()] = 0;
                //System.out.println("yRegion: "+yRegion);
                return yRegion;
            }
        }
        //if you didnt hit a box
        canvas.drawCircle(xCoor, bullet.getyCoor(), 12, paint3);
        return -1;
    }

    @Override
    public void onDraw(Canvas canvas) {
        // Get the drawable's bounds
        super.onDraw(canvas);
        postInvalidate();
        for (int n=0; n < bulletList.size(); n++) {
            System.out.print("bullet: "+n);
            int tempLine = drawBullet(bulletList.get(n), canvas);
            if ( tempLine >= 0) {
                if (tempLine == map.length) {
                    //collided with the wall
                    collisionLine = -1;
                }
                else {
                    //a box is destoryed
                    collisionLine = tempLine;
                }
                collision = true;
                bulletIndex = n;
                bulletList.remove(n);
                n--;
            }
        }
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[0].length; j++) {
                if (map[i][j] == 1) {
                    if (i % 2 == 0) {
                        //is even
                        box(j, i, paint1, canvas);
                    }
                    else {
                        box(j,i, paint2, canvas);
                    }
                }
            }
        }
    }

    public int getCollisionLine() {
        return collisionLine;
    }

    public boolean getCollision() {
        return collision;
    }

    public void giveRadius(int rad) {
        shipRadius = rad;
    }

    public int getBulletIndex() {
        return bulletIndex;
    }

    public void setCollision(boolean bool) {
        collision = bool;
    }
}

