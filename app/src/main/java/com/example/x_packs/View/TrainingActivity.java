package com.example.x_packs.View;


import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.view.View;
import android.widget.Toast;


import com.example.x_packs.R;
import com.example.x_packs.Utils.SharedData;
import com.example.x_packs.ViewModel.PlanViewModel;
import com.example.x_packs.databinding.ActivityTrainingBinding;

import java.util.List;
import java.util.Locale;

import static android.speech.tts.TextToSpeech.Engine.CHECK_VOICE_DATA_PASS;

public class TrainingActivity extends AppCompatActivity implements Dialog.DialogListener{
    private final int CHECK_TTS = 1;
    private TextToSpeech tts;
    private SensorManager oSM;
    private Sensor sensor;
    private ActivityTrainingBinding binding;
    private View view;
    private CountDownTimer countDownTimer;
    private static final long DECREASE_TIMER_IN_MILLIS = 1000;
    private long timeLeft;
    private boolean restState;
    private int count;
    private int index;
    private String[] Session;
    private Dialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTrainingBinding.inflate(getLayoutInflater());
        view = binding.getRoot();
        setContentView(view);

        Intent intent = new Intent(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(intent, CHECK_TTS);
        tts = new TextToSpeech(TrainingActivity.this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    tts.setLanguage(Locale.ENGLISH);
                    tts.setPitch((float) 1);
                    tts.setSpeechRate((float)0.8);
                }
            }
        });

        index = 0;
        String data = SharedData.currentPlan.getData();
        Session = data.split(" - ");
        binding.dataPlaceHolder.setText("TRAINING SESSION\n"+data);
        binding.counts.setText(Session[index]);
        count = Integer.parseInt(Session[index]);
        dialog = new Dialog();
        checkSensor();
        startTimer(6000);

        binding.abortBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exitPressed();
            }
        });

    }

    private void speakText(String s) {
        tts.speak(s, TextToSpeech.QUEUE_ADD, null, null);
    }

    private void checkSensor() {
        oSM = (SensorManager) getApplicationContext().getSystemService(Context.SENSOR_SERVICE);
        List<Sensor> sensorList = oSM.getSensorList(Sensor.TYPE_PROXIMITY);
        if(!sensorList.isEmpty()){
            sensor = oSM.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        }else{
            //error
            Toast.makeText(this, "You should have the proximity sensor to use this app", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    SensorEventListener sensorListener = new SensorEventListener() {
        public void onSensorChanged(SensorEvent event) {
            float x =event.values[0];
            if(event.sensor.getType() == Sensor.TYPE_PROXIMITY){
                if(x<sensor.getMaximumRange()){
                    addCount();
                }
            }
        }
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };
    public void startTimer(long l){
        binding.labelCount.setText("PREPARE IN");
        restState = true;
        countDownTimer = new CountDownTimer(l,DECREASE_TIMER_IN_MILLIS) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeft = millisUntilFinished;
                updateTimeUI(timeLeft);
            }
            @Override
            public void onFinish() {
                binding.labelCount.setText("PUSH UPS");
                binding.counts.setText(""+count);
                speakText(Session[index]+" push ups to go.");
                oSM.registerListener(sensorListener,  sensor, SensorManager.SENSOR_DELAY_UI);
                restState = false;
            }
        }.start();
    }

    private void updateTimeUI(long l) {
        int minutes = (int) (l / 1000) / 60;
        int seconds = (int) (l / 1000) % 60;
        String timeLeft = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
        binding.counts.setText(timeLeft);
    }

    public void addCount(){
        oSM.unregisterListener(sensorListener);
        count--;
        binding.counts.setText(""+count);
        if (count == 0) {
            index++;
            if(index < Session.length){
                count = Integer.parseInt(Session[index]);
                speakText("Rest now.");
                startTimer(90000);
            }else{
                tts.speak("congratulations! you have completed training session", TextToSpeech.QUEUE_ADD, null, "id");
                tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                    @Override
                    public void onStart(String utteranceId) {

                    }

                    @Override
                    public void onDone(String utteranceId) {
                        terminate();
                    }

                    @Override
                    public void onError(String utteranceId) {

                    }
                });
            }
        }else{
            oSM.registerListener(sensorListener,  sensor, SensorManager.SENSOR_DELAY_UI);
        }
    }

    private void terminate() {
        SharedData.currentPlan.setDone(1);
        PlanViewModel planViewModel =  ViewModelProviders.of(this).get(PlanViewModel.class);
        planViewModel.update(SharedData.currentPlan);
        finish();
    }


    @Override
    protected void onStop() {
        super.onStop();
        oSM.unregisterListener(sensorListener);
        if (countDownTimer != null){
            countDownTimer.cancel();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        oSM.unregisterListener(sensorListener);
        if (countDownTimer != null){
            countDownTimer.cancel();
        }
    }

    @Override
    protected void onRestart() {
        onNoClicked();
        super.onRestart();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CHECK_TTS) {
            if (resultCode != CHECK_VOICE_DATA_PASS) {
                String action = TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA;
                Intent install = new Intent();
                install.setAction(action);
                startActivity(install);
            }
        }
    }

    @Override
    public void onYesClicked() {
        finish();
    }

    @Override
    public void onNoClicked() {
        if (restState){
            startTimer(timeLeft);
        }else{
            oSM.registerListener(sensorListener,  sensor, SensorManager.SENSOR_DELAY_UI);
        }
    }

    private void exitPressed(){
        oSM.unregisterListener(sensorListener);
        if (countDownTimer != null){
            countDownTimer.cancel();
        }
        dialog.show(getSupportFragmentManager(),"Alter Dialog");
        dialog.setCancelable(false);
    }

    @Override
    public void onBackPressed() {
        exitPressed();
    }

}