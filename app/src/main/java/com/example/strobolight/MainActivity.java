package com.example.strobolight;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.util.concurrent.Executor;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class MainActivity extends AppCompatActivity {

    SeekBar seekBarFrequency;

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    Switch aSwitch;
    TextView textViewMillisec;
    Handler handler = new Handler();
    private boolean isRunning = false;
    private final int initialTimeDelay = 5;
    private int timeDelay = initialTimeDelay;
    FlashlightProvider flp = new FlashlightProvider(MainActivity.this);
    Thread thread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        aSwitch = findViewById(R.id.switch1);
        textViewMillisec = findViewById(R.id.textViewMillisec);

        seekBarFrequency = findViewById(R.id.seekBarFrequency);
        seekBarFrequency.setOnSeekBarChangeListener(seekBarFrequencyChangeListener);


    }

    SeekBar.OnSeekBarChangeListener seekBarFrequencyChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            // updated continuously as the user slides the thumb
             // tvProgressLabel.setText("Progress: " + progress);

            timeDelay = initialTimeDelay + progress * 2;
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            // called when the user first touches the SeekBar
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            // called after the user finishes moving the SeekBar
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    class Strobe implements Runnable {

        @Override
        public void run() {

            while (isRunning) {
                flp.turnFlashlightOn();
                long start = System.currentTimeMillis();

                try {
                    int timeRunning = 5;
                    Thread.sleep(timeRunning);
                } catch (InterruptedException e) {
                    // e.printStackTrace();
                }
                flp.turnFlashlightOff();
                try {
                    Thread.sleep(timeDelay);
                } catch (InterruptedException e) {
                    // e.printStackTrace();
                }

                long elapsedTimeMillis = System.currentTimeMillis() - start;

                handler.post(() -> textViewMillisec.setText(String.valueOf(elapsedTimeMillis)));
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void turnOnOff(View view) {
        if (!flp.hashFlash()) {
            Toast.makeText(MainActivity.this, "No flashlight detected", Toast.LENGTH_SHORT).show();
        } else {
            if (!isRunning) {
                isRunning = true;
                Strobe strobe = new Strobe();
                thread = new Thread(strobe);
                thread.start();
            } else {
                isRunning = false;
                thread.interrupt();
            }
        }
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        if (thread != null && thread.isAlive()) {
            isRunning =false;
            thread.interrupt();
        }
        if (aSwitch.isChecked()) {
            aSwitch.setChecked(false);
        }
    }
    
    public void closeApp(View view) {
        this.finishAffinity();
    }
}

