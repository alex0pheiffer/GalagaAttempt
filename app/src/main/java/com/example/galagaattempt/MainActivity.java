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

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private final String ScoreText = "Score: ";
    private int lines = 5;
    private final int rows = 12;

    private TextView timerTextView;
    private long startTime = 0;
    private int count = 0;
    private boolean canShoot;
    private int shipPerc;
    private boolean started;
    private boolean paused;
    private int score;

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
            if (!paused) {
                //Log.d("DEBUG","shipPerc : "+shipPerc);
                long millis = System.currentTimeMillis() - startTime;
                //int seconds = (int) (millis / 1000);
                //int minutes = seconds / 60;
                //seconds = seconds % 60;
                count++;
                if (count == 3) {
                    canShoot = true;
                    count = 0;
                }
                moveBullet();
                shipPerc = 5 + shipPerc;
                if (shipPerc >= 100) {
                    shipPerc = -100;
                }
                moveShip();
                //Log.d("DEBUG","runnable: "+givenMap);
                map.updateLists(givenMap);

                timerHandler.postDelayed(this, 250);
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
        shipPerc = 0;
        canShoot = true;
        bulletsList = new ArrayList<bullets>();

         map.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                //game is paused
                Log.d("PRESS","mapView is pressed");
                if (paused) {
                    //game isn't started
                    if (!started) {
                        start_game();
                    }
                }
                //game is running
                else {
                    //shooot!
                    if (canShoot) {
                        //we can shoot!
                        //determine what region this bullet will hit
                        int region = 0;
                        while (((double)(region+1)/rows)*100 < Math.abs(shipPerc)) {
                            region++;
                        }
                        //create a new bullet
                        bullets bull = new bullets(shipPerc, region);
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
                    if (paused) {
                        b.setText("O");
                        timerHandler.postDelayed(timerRunnable, 0);
                        paused = false;
                    }
                    //pause the game;
                    else {
                        b.setText("-");
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


    public void start_game() {
        //start the timer
        androidx.appcompat.app.ActionBar ab = getSupportActionBar();
        score = 0;
        ab.setTitle(ScoreText+score);
        Log.d("DEBUG","start_game");
        startTime = System.currentTimeMillis();
        pauseBtn.setText("O");
        timerHandler.postDelayed(timerRunnable, 0);
        //begin to move the ship

        //game is now started
        started = true;
        paused = false;
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
            if (Math.random()*10 > 4) {
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
        //they were all 0
        //move the blocks down
        //System.out.println("all are zero");
        for (int i = lines-1; i > 0; i--) {
            for (int j = 0; j < rows; j++) {
                givenMap[i][j] = givenMap[i-1][j];
            }
        }
        System.out.println("shifted rows");
        //fill the last row
        fillRow(0);
        //map.shiftLines(line, givenMap);
        //Log.d("DEBUG","checkLine: "+givenMap);
        map.updateLists(givenMap);
        return true;
    }

    @Override
    public void onPause() {
        super.onPause();
        timerHandler.removeCallbacks(timerRunnable);
        startTime = System.currentTimeMillis();
        pauseBtn.setText("-");
    }

}
