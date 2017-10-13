package com.example.logistik.logistikgobkg;

import android.content.Intent;
import android.support.transition.Transition;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.Timer;
import java.util.TimerTask;

public class InicioActivity extends AppCompatActivity {

    private static final long SPLASH_SCREEN_DELAY = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio);
        TimerTask task = new TimerTask() {
            @Override
            public void run() {

                // Start the next activity
                Intent mainIntent = new Intent().setClass(
                        InicioActivity.this, LoginActivity.class);
                startActivity(mainIntent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                // Close the activity so the user won't able to go back this
                // activity pressing Back button
                finish();
            }
        };


        // Simulate a long loading process on application startup.
        Timer timer = new Timer();
        timer.schedule(task, SPLASH_SCREEN_DELAY);
    }

}
