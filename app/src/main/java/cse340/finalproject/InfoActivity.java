package cse340.finalproject;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class InfoActivity extends AppCompatActivity {

    /** Speed of a single morse code unit (in seconds) */
    protected double SPEED;

    /** The key for speed value for the intent */
    private final String SPEED_KEY = "speed_setting";

    /**
     * Callback that is called when the activity is first created.
     * @param savedInstanceState contains the activity's previously saved state.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.info_activity);

        // gets the speed from the intent passed over from MainActivity
        SPEED = getIntent().getDoubleExtra(SPEED_KEY, 0.5);

        // scales the speed to be from 0 to 100 instead of 0 to 1 for the SeekBar logic
        int scaleForSpeedBar = (int) (SPEED * 100);

        SeekBar speedBar = findViewById(R.id.speedSlider);
        speedBar.setProgress(scaleForSpeedBar);
        TextView speedValue = findViewById(R.id.speedValue);
        speedValue.setText(getResources().getString(R.string.speed_display,
                Double.toString(SPEED).substring(0, 3)));

        // When the seekbar has changed, set the speed to whatever value it is on, scaling
        // it down by 100
        // sets the seekbar display text to the scaled speed value when the bar stops
        // getting interacted with
        speedBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                speedValue.setText(getResources().getString(R.string.speed_display,
                        Double.toString(SPEED).substring(0, 3)));
                //speedValue.setText(Double.toString(SPEED).substring(0, 3) + " Seconds");
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                SPEED = (float) (progress / 100.0);
            }
        });

        Button homeButton = findViewById(R.id.goMain);

        homeButton.setOnClickListener(v -> {
            Intent intent = new Intent(InfoActivity.this, MainActivity.class);
            intent.putExtra(SPEED_KEY, SPEED);
            startActivity(intent);
        });








    }

}
