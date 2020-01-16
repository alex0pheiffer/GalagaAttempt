package com.example.galagaattempt;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private final String ScoreText = "Score: ";
    private final int lines = 5;
    private final int rows = 12;

    private TextView timerTextView;
    private long startTime = 0;
    private int count = 0;
    private boolean canShoot;
    private int shipPerc;
    private boolean started;
    private boolean paused;

    private View shipView;
    private shipDraw ship;
    private View mapView;
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
                long millis = System.currentTimeMillis() - startTime;
                //int seconds = (int) (millis / 1000);
                //int minutes = seconds / 60;
                //seconds = seconds % 60;
                count++;
                if (count == 3) {
                    canShoot = true;
                }
                moveBullet();
                shipPerc =+ 2;
                if (shipPerc >= 100) {
                    shipPerc = -100;
                }
                moveShip();

                timerHandler.postDelayed(this, 250);
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        int actionBarTitle = Resources.getSystem().getIdentifier("action_bar_title", "id", "android");
        TextView actionBarTitleView = (TextView) getWindow().findViewById(actionBarTitle);
        actionBarTitleView.setText(ScoreText+0);
        createMap();
        started = false; paused = true;
        shipView = findViewById(R.id.bottomCanvas);
        mapView = findViewById(R.id.topCanvas);
        pauseBtn = findViewById(R.id.button);
        pauseBtn.setText("-");
        ship = new shipDraw(shipPerc);
        shipPerc = 0;
        shipView.setBackground(ship);
        map = new mapDraw(givenMap);

        mapView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                //game is paused
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

                    }
                }
            }
        });

        pauseBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                //unpause the game
                if (started) {
                    Button b = (Button) view;
                    if (paused) {
                        b.setText("O");
                        timerHandler.postDelayed(timerRunnable, 0);
                    }
                    //pause the game;
                    else {
                        b.setText("-");
                        startTime = System.currentTimeMillis();
                        timerHandler.removeCallbacks(timerRunnable);
                    }
                }
            }
        });

        canShoot = true;
        bulletsList = new ArrayList<bullets>();
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
        startTime = System.currentTimeMillis();
        pauseBtn.setText("O");
        timerHandler.postDelayed(timerRunnable, 0);
        //begin to move the ship

        //game is now started
        started = true;
        paused = false;
    }

    public void moveBullet() {
        for (bullets b : bulletsList) {

        }
    }

    public void moveShip() {
        ship.updatePerc(shipPerc);
        ship.drawShip();
    }

    //lines is the number of lines you would like to create for the map. min is 1
    public void createMap() {
        if (lines < 1) {
            givenMap = new int[lines][rows];
            for (int i=0; i < lines; i++) {
                fillRow(i);
            }
        }
    }

    public void fillRow(int line) {
        for (int j=0; j < rows; j++) {
            if (Math.random()*10 > 4) {
                givenMap[line][j] = 1;
            }
            else givenMap[line][j] = 0;
        }
    }

    public boolean checkLine(int line) {
        for (int i=0; i < rows; i++) {
            if (givenMap[line][i] > 0) {
                return false;
            }
        }
        //they were all 0
        //move the blocks down
        for (int i = line; i < lines-1; i++) {
            for (int j = 0; j < rows; j++) {
                givenMap[i][j] = givenMap[i+1][j];
            }
        }
        //fill the last row
        fillRow(rows-1);
        map.shiftLines(line, givenMap);
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
