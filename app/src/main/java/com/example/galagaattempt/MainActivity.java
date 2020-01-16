package com.example.galagaattempt;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActionBar;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    //yeah you need to change this in mapDraw too, sorry
    private final double YCOORSTOTAL = 12;

    private final String ScoreText = "Score: ";
    private int lines = 5;
    private final int rows = 11;

    private TextView timerTextView;
    private long startTime = 0;
    private int count = 0;
    private int needsMoveCount = 0;
    private boolean canShoot;
    private int shipPerc;
    private int blockedRows;
    private boolean started;
    private boolean paused;
    private int score;
    private boolean lose = false;

    private shipDraw ship;
    private mapDraw map;
    private Button pauseBtn;

    private int[][] givenMap;
    private ArrayList<bullets> bulletsList;

    //runs without a timer by reposting this handler at the end of the runnable
    Handler timerHandler = new Handler();
    Runnable timerRunnable = new Runnable() {

        @Override
        public void run() {
            if (!paused && !lose) {
                //Log.d("DEBUG","shipPerc : "+shipPerc);
                long millis = System.currentTimeMillis() - startTime;
                //int seconds = (int) (millis / 1000);
                //int minutes = seconds / 60;
                //seconds = seconds % 60;
                count++;
                needsMoveCount++;
                if (count == 5) {
                    canShoot = true;
                    count = 0;
                }
                if (needsMoveCount == 45) {
                    lines++;
                    if (lines > YCOORSTOTAL) {
                        lose = true;
                        hasLost();
                    }
                    shiftLines(lines-1);
                    needsMoveCount=0;
                }
                moveBullet();
                shipPerc = 5 + shipPerc;
                if (shipPerc >= 100) {
                    shipPerc = -100;
                }
                moveShip();
                //Log.d("DEBUG","runnable: "+givenMap);
                map.updateLists(givenMap);

                timerHandler.postDelayed(this, 200);
            }
            else if(lose) {
                hasLost();
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        createMap();
        //Log.d("DEBUG","createMap: "+givenMap);
        started = false; paused = true;
        ship = findViewById(R.id.bottomCanvas);

        map = findViewById(R.id.topCanvas);
        map.updateLists(givenMap);
        pauseBtn = findViewById(R.id.button);
        pauseBtn.setText("-");
        score = 0;

         map.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                //game is paused
                Log.d("PRESS","mapView is pressed");
                if (paused) {
                    //game isn't started
                    if (!started) {
                        if (score != 0) {
                            start_game(true);
                        }
                        else start_game(false);
                    }
                }
                //game is running
                else {
                    //shooot!
                    if (canShoot) {
                        //we can shoot!
                        //create a new bullet
                        bullets bull = new bullets(shipPerc, rows);
                        bulletsList.add(bull);
                        map.addBullet(bull);
                        Log.d("DEBUG","created bulletsList: "+bulletsList.size());
                        canShoot = false;
                    }
                }
            }
        });

        pauseBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                //unpause the game
                Log.d("PRESS","pause btn is pressed");
                if (started) {
                    Button b = (Button) view;
                    Log.d("PRESS","is started");
                    if (paused) {
                        Log.d("PRESS","unpausing");
                        b.setText("O");
                        timerHandler.postDelayed(timerRunnable, 0);
                        paused = false;
                    }
                    //pause the game;
                    else {
                        b.setText("-");
                        Log.d("PRESS","pausing");
                        startTime = System.currentTimeMillis();
                        timerHandler.removeCallbacks(timerRunnable);
                        paused = true;
                    }
                }
            }
        });
        /*

        //timer ex code

        timerTextView = (TextView) findViewById(R.id.timerTextView);

        Button b = (Button) findViewById(R.id.button);
        b.setText("start");
        b.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Button b = (Button) v;
                if (b.getText().equals("stop")) {
                    timerHandler.removeCallbacks(timerRunnable);
                    b.setText("start");
                } else {
                    startTime = System.currentTimeMillis();
                    timerHandler.postDelayed(timerRunnable, 0);
                    b.setText("stop");
                }
            }
        });

        */
    }

    public void start_game(boolean restart) {
        //start the timer
        if (restart) {
            lines = 5;
            createMap();
            map.updateLists(givenMap);
        }

        map.giveRadius(ship.getRadius());

        if (!restart) {
            //block the rows if they're blocked by the radius. compare box width to radius. box width > .5*radius to be eligible
            double boxWidth = (double) map.getWidth() / rows;
            double radius = ship.getRadius();
            int blockedRows = 0;
            while (radius / boxWidth > .5) {
                blockedRows++;
                radius = radius - boxWidth;
            }
        }
        androidx.appcompat.app.ActionBar ab = getSupportActionBar();
        score = 0;
        shipPerc = 0;
        canShoot = true;
        bulletsList = new ArrayList<bullets>();
        ab.setTitle(ScoreText+score);
        Log.d("DEBUG","start_game");
        startTime = System.currentTimeMillis();
        pauseBtn.setText("O");
        timerHandler.postDelayed(timerRunnable, 0);
        //begin to move the ship

        //game is now started
        started = true;
        paused = false;
        lose = false;
    }


    public void moveBullet() {
        //Log.d("DEBUG","moveBullet:collision: "+map.getCollision());
        if (map.getCollision()) {
            System.out.println("collision...");
            if (map.getCollisionLine() != -1) {
                checkLine(map.getCollisionLine());
                score++;
                androidx.appcompat.app.ActionBar ab = getSupportActionBar();
                ab.setTitle(ScoreText+score);
            }
            map.setCollision(false);
            bulletsList.remove(map.getBulletIndex());
        }
    }

    public void moveShip() {
        //Log.d("DEBUG","moveShip");
        ship.updatePerc(shipPerc);
        ship.refreshDrawableState();
    }

    //lines is the number of lines you would like to create for the map. min is 1
    public void createMap() {
        if (lines > 1) {
            givenMap = new int[lines][rows];
            for (int i=0; i < lines; i++) {
                fillRow(i);
            }
        }
    }

    public void fillRow(int line) {
        //int[][] newMap = new int[lines][rows];
        for (int j=0; j < rows; j++) {
            if (j <= blockedRows || j >= rows-blockedRows-1) {
                givenMap[line][j]=0;
            }
            else if (Math.random()*10 > 4) {
                givenMap[line][j] = 1;
            }
            else givenMap[line][j] = 0;
        }
    }

    public boolean checkLine(int line) {
        //System.out.println("checking line: "+line);
        for (int i=0; i < rows; i++) {
            if (givenMap[line][i] > 0) {
                return false;
            }
        }
        Log.d("CHECKLINE","LINE: "+line+" / "+lines);
        //they were all 0
        if (line != lines-1) {
            //shift down
            shiftLines(line);
            needsMoveCount=0;
        }
        else lines--;
        map.updateLists(givenMap);
        return true;
    }

    //give the line we start at to shift (the one that is empty)
    public void shiftLines(int line) {
        //move the blocks down
        //System.out.println("all are zero");
        int[][] newMap = new int[lines][rows];

        for (int i = line; i > 0; i--) {
            for (int j = 0; j < rows; j++) {
                newMap[i][j] = givenMap[i-1][j];
            }
        }
        for (int i = line+1; i < lines; i++) {
            for (int j = 0; j < rows; j++) {
                newMap[i][j] = givenMap[i][j];
            }
        }
        System.out.println("shifted rows");
        givenMap = newMap;
        map.updateLists(givenMap);
        //fill the last row
        fillRow(0);
    }

    public void hasLost() {
        Log.d("FINISH","is finished");
        timerHandler.removeCallbacks(timerRunnable);
        startTime = System.currentTimeMillis();
        Toast.makeText(getApplicationContext(),"You Lose : "+score, Toast.LENGTH_LONG);
        paused = true;
        started = false;
    }

    @Override
    public void onPause() {
        super.onPause();
        timerHandler.removeCallbacks(timerRunnable);
        startTime = System.currentTimeMillis();
        pauseBtn.setText("-");
    }

}
