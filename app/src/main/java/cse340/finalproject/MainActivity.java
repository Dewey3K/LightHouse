package cse340.finalproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    /** Speed of a single morse code unit (in seconds) */
    protected double SPEED;

    /** Text for the orientation */
    protected String BOX_TEXT;

    /** Map storing the character to morse code translations */
    protected Map<Character, int[]> morseDictionary = new HashMap<>();

    /** Camera manager for the flash function */
    protected CameraManager camManager;

    /** Camera ID (0 for back camera, 1 for front) */
    protected String cameraID;

    /** Sensor Manager to use Accelerometer and Magnetic Field */
    private SensorManager mSensorManager;

    /** Stores the azimuth the phone's y-axis is pointing in */
    private float mOrientation;

    /** The key for speed value for the intent */
    private final String SPEED_KEY = "speed_setting";

    /** The key for preset button text for the intent */
    private final String PRESETS_KEY = "preset_text";


    /**
     * Callback that is called when the activity is first created.
     * @param savedInstanceState contains the activity's previously saved state.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensorManager.getSensorList(Sensor.TYPE_ALL);
        Sensor accelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        Sensor magnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        mSensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_NORMAL);

        Button orientationButton = findViewById(R.id.checkOrientation);
        TextView orientationBox = findViewById(R.id.displayOrientation);

        orientationButton.setOnClickListener(v -> updateOrientationBox(orientationBox));

        Button presetButton = findViewById(R.id.goPreset);
        Button infoButton = findViewById(R.id.goInfo);

        SPEED = getIntent().getDoubleExtra(SPEED_KEY, 0.5);
        BOX_TEXT = getIntent().getStringExtra(PRESETS_KEY);
        if (BOX_TEXT == null) {
            BOX_TEXT = "";
        }

        camManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);

        try {
            cameraID = camManager.getCameraIdList()[0];
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }

        setMorseDictionary(morseDictionary);

        presetButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, PresetActivity.class);
            startActivity(intent);
        });

        infoButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, InfoActivity.class);
            intent.putExtra(SPEED_KEY, SPEED);
            startActivity(intent);
        });


        Button sendMorse = findViewById(R.id.sendMorse);

        EditText textBox = findViewById(R.id.inputBox);
        textBox.setText(BOX_TEXT);

        // when the transmit button has been clicked, either transmits the text inside the box or
        // says that the user needs to input data first
        sendMorse.setOnClickListener(v -> {
            String inputText = textBox.getText().toString();

            if (inputText.isEmpty()) {
                Toast.makeText(getApplicationContext(), "Please Enter the Data", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "Transmitting Morse Code",
                        Toast.LENGTH_SHORT).show();

                try {
                    transmitMorse(inputText);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (CameraAccessException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * This method processes the azimuth calculated
     * into tangible directions that is displayed to the user through
     * the passed in view
     * @param morseDictionary The empty map where the characters will be mapped onto
     *                        their corresponding morse code units (1 for dot, 3 for dash)
     */
    private void setMorseDictionary(Map<Character, int[]> morseDictionary) {
        morseDictionary.put('a', new int[]{1, 3});
        morseDictionary.put('b', new int[]{3, 1, 1, 1});
        morseDictionary.put('c', new int[]{3, 1, 3, 1});
        morseDictionary.put('d', new int[]{3, 1, 1});
        morseDictionary.put('e', new int[]{1});
        morseDictionary.put('f', new int[]{1, 1, 3, 1});
        morseDictionary.put('g', new int[]{3, 3, 1});
        morseDictionary.put('h', new int[]{1, 1, 1, 1});
        morseDictionary.put('i', new int[]{1, 1});
        morseDictionary.put('j', new int[]{1, 3, 3, 3});
        morseDictionary.put('k', new int[]{1, 3, 1, 1});
        morseDictionary.put('l', new int[]{1, 3, 1, 1});
        morseDictionary.put('m', new int[]{3, 3});
        morseDictionary.put('n', new int[]{3, 1});
        morseDictionary.put('o', new int[]{3, 3, 3});
        morseDictionary.put('p', new int[]{1, 3, 3, 1});
        morseDictionary.put('q', new int[]{3, 3, 1, 3});
        morseDictionary.put('r', new int[]{1, 3, 1});
        morseDictionary.put('s', new int[]{1, 1, 1});
        morseDictionary.put('t', new int[]{3});
        morseDictionary.put('u', new int[]{1, 1, 3});
        morseDictionary.put('v', new int[]{1, 1, 1, 3});
        morseDictionary.put('w', new int[]{1, 3, 3});
        morseDictionary.put('x', new int[]{3, 1, 1, 3});
        morseDictionary.put('y', new int[]{3, 1, 3, 3});
        morseDictionary.put('z', new int[]{3, 3, 1, 1});

        morseDictionary.put(' ', new int[]{3, 3, 3});
    }


    /**
     * This method takes the user's input in the EditText box
     * and processes it to allow the phone's camera flash to transmit it
     * in morse code
     * @param inputText The text in the EditText the user inputs
     */
    protected void transmitMorse(String inputText) throws InterruptedException,
            CameraAccessException {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {

            for (char c : inputText.toCharArray()) {
                c = Character.toLowerCase(c);
                for (int d : morseDictionary.get(c)) {
                    camManager.setTorchMode(cameraID, true);
                    TimeUnit.MILLISECONDS.sleep((long) (d * SPEED * 1000));
                    camManager.setTorchMode(cameraID, false);
                    TimeUnit.MILLISECONDS.sleep((long) (1 * SPEED * 1000));
                }
                TimeUnit.MILLISECONDS.sleep((long) (3 * SPEED * 1000));
            }
            Toast.makeText(getApplicationContext(), "Finished Transmitting Morse",
                    Toast.LENGTH_SHORT).show();
        } else {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA},
                    0);
        }

    }

    /**
     * This method processes the azimuth calculated
     * into tangible directions that is displayed to the user through
     * the passed in view
     * @param orientationBox The TextView that the direction will be displayed in.
     */
    private void updateOrientationBox(TextView orientationBox) {
        if (mOrientation < (Math.PI / 4)  && mOrientation > -(Math.PI / 4)) {
            orientationBox.setText("North");
        } else if (mOrientation > (Math.PI/4) && mOrientation < 3 * ((Math.PI / 4))) {
            orientationBox.setText("East");
        } else if (mOrientation < -(Math.PI / 4) && mOrientation >  3 * -((Math.PI / 4))) {
            orientationBox.setText("West");
        } else if (mOrientation > 3 * ((Math.PI / 4)) || mOrientation < 3 * -((Math.PI / 4))) {
                orientationBox.setText("South");
        } else {
            orientationBox.setText("Sensor Error");
        }

    }

    // Azimuth Code from Android Documentation
    // Used to find angle relative to Magnetic North
    // Url: https://developer.android.com/guide/topics/sensors/sensors_position#:~:text=If%20the
    // %20top%20edge%20of%20the%20device%20faces,Pitch%20%28degrees%20of%20rotation%20about%20the%20x%20axis%29.
    private final float[] accelerometerReading = new float[3];
    private final float[] magnetometerReading = new float[3];

    private final float[] rotationMatrix = new float[9];
    private final float[] orientationAngles = new float[3];

    /**
     * Called when there is a new sensor event.
     * Processes sensor data to find phone's angle relative to Magnetic North
     * FROM ANDROID DOCUMENTATION
     * URL: https://developer.android.com/guide/topics/sensors/sensors_position#:~:text=If%20the
     *     // %20top%20edge%20of%20the%20device%20faces,
     *     Pitch%20%28degrees%20of%20rotation%20about%20the%20x%20axis%29.
     * @param event The sensor event that happened when the sensor was read
     */
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            System.arraycopy(event.values, 0, accelerometerReading,
                    0, accelerometerReading.length);
        } else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            System.arraycopy(event.values, 0, magnetometerReading,
                    0, magnetometerReading.length);
        }
        SensorManager.getRotationMatrix(rotationMatrix, null,
                accelerometerReading, magnetometerReading);

        // "rotationMatrix" now has up-to-date information.

        SensorManager.getOrientation(rotationMatrix, orientationAngles);
        mOrientation = orientationAngles[0];
    }


    // Method required when making class a SensorEventListener
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this,
                mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this,
                mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
                SensorManager.SENSOR_DELAY_NORMAL);


    }


    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }




}