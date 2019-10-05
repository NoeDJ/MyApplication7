package com.example.myapplication;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.media.projection.MediaProjectionManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.io.IOException;
import java.util.Objects;




public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    boolean isHeadphoneConnected;
    private static final String LOG_TAG = "AudioRecordTest";
    private MediaRecorder recorders = null;
    private static String fileName = "/dev/null";

    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    public static final String CAPTURE_AUDIO_OUTPUT = null;

    // Requesting permission to RECORD_AUDIO
    private boolean permissionToRecordAccepted = false;
    private String [] permissions = {Manifest.permission.RECORD_AUDIO};

//    private soundmter SoundMeter;

    private Recorder recorder;
    private AudioCalculator audioCalculator;
//    private Callback callback;
    private Handler handler;

    private TextView textAmplitude;
    private TextView textFrequency;



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case REQUEST_RECORD_AUDIO_PERMISSION:
                permissionToRecordAccepted  = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                break;
        }
        if (!permissionToRecordAccepted ) finish();

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recorder = new Recorder(callback);
        audioCalculator = new AudioCalculator();
        handler = new Handler(Looper.getMainLooper());

        textAmplitude = findViewById(R.id.amplitudeTextView);
        textFrequency = findViewById(R.id.textFrequency);



        ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION);
//        ActivityCompat.requestPermissions(this, permissions, Integer.parseInt(CAPTURE_AUDIO_OUTPUT));

        MusicIntentReceiver myReceiver2 = new MusicIntentReceiver();
        IntentFilter filter = new IntentFilter(Intent.ACTION_HEADSET_PLUG);
        registerReceiver(myReceiver2, filter);

        ToggleButton toggle = findViewById(R.id.button1);
        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
//                    recorder.start();
                    Button recButt1 = findViewById(R.id.button1);
                    recButt1.setText("Pause");

                    // The toggle is enabled
                } else {
//                    recorder.stop();
                    Button recButt = findViewById(R.id.button1);
                    recButt.setText("Play");

                    // The toggle is disabled
                }
            }
        });

    }

    private Callback callback = new Callback() {
        @Override
        public void onBufferAvailable(byte[] buffer) {
            audioCalculator.setBytes(buffer);
            int amplitude = audioCalculator.getAmplitude();
            double decibel = audioCalculator.getDecibel();
            double frequency = audioCalculator.getFrequency();

            final String amp = amplitude + " Amp";
            final String db = decibel + " db";
            final String hz = frequency + " Hz";

            handler.post(new Runnable() {
                @Override
                public void run() {
                    textAmplitude.setText(amp);
//                    textDecibel.setText(db);
                    textFrequency.setText(hz);
                }
            });
        }
    };




    public class MusicIntentReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Objects.equals(intent.getAction(), Intent.ACTION_HEADSET_PLUG)) {
                int state = intent.getIntExtra("state", -1);
                switch (state) {
                    case 0:
                        isHeadphoneConnected = false;
//                        if (isHeadphoneConnected = false)
//                        Toast.makeText(MainActivity.this, "", Toast.LENGTH_LONG).show();
                        TextView tv = findViewById(R.id.hssc);
                        tv.setText("Please Plug The Cable");
//                        stopRecording();
                        recorder.stop();
                        break;
                    case 1:
                        isHeadphoneConnected = true;
//                        if (isHeadphoneConnected = true)
//                        Toast.makeText(MainActivity.this, "", Toast.LENGTH_LONG).show();
                        TextView tv1 = findViewById(R.id.hssc);
                        tv1.setText("Welcome to android");
//                        startRecording();
                        recorder.start();
                        break;
                }
            }
        }

    }

}








