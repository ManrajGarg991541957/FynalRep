package org.vosk.demo;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;
import org.vosk.LibVosk;
import org.vosk.LogLevel;
import org.vosk.Model;
import org.vosk.Recognizer;
import org.vosk.android.RecognitionListener;
import org.vosk.android.SpeechService;
import org.vosk.android.SpeechStreamService;
import org.vosk.android.StorageService;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Locale;

public class WorkoutListen extends AppCompatActivity implements RecognitionListener{

    private static final long START_TIME_IN_MILLIS = 10000;
    private TextView mTextViewCountDown;
    private CountDownTimer mCountDownTimer;
    private long mTimeLeftInMillis = START_TIME_IN_MILLIS;

    static private final int STATE_START = 0;
    static private final int STATE_READY = 1;
    static private final int STATE_DONE = 2;
    static private final int STATE_MIC  = 3;

    // Strings for detecting keywords
    private final String keywordPauseWorkout = "pause";
    private final String keywordSetCompleted = "complete";
    private final String keywordResumeWorkout = "resume";
    private String userInput;

    // Kaldi speech model and speechService Objects needed for Vosk to work
    private Model model;
    private SpeechService speechService;
    private SpeechStreamService speechStreamService;

    private FirebaseUser user;
    private String userID;

    private DatabaseReference reference, newref;

    /* Used to handle permission request */
    private static final int PERMISSIONS_REQUEST_RECORD_AUDIO = 1;

    ArrayAdapter<String> arrayAdapterExercises;

    ArrayList<String> arrayListExercises = new ArrayList<>();
    Intent intent = null;

    // Declaring UI components
    TextView workoutResult, workoutResult2, text1, text2;
    Button beginWorkout;
    int amountExercises = 0;

    int counter = 1;
    int index = 1;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.workout_in_progress);

        // Setup layout
        workoutResult = findViewById(R.id.textView_ExProgress);
        workoutResult2 = findViewById(R.id.textView_ExProgress2);
        beginWorkout = findViewById(R.id.button_begin_workout);
        text1 = findViewById(R.id.text_test_1);
        text2 = findViewById(R.id.text_test_2);
        mTextViewCountDown = findViewById(R.id.text_view_countdown);

        user = FirebaseAuth.getInstance().getCurrentUser();
        userID = user.getUid();

        String workoutNameTV;
        // grabbing string from previous activity
        if (state == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                workoutNameTV = null;
            } else {
                workoutNameTV = extras.getString("workoutName");
            }
        } else {
            workoutNameTV = (String) state.getSerializable("workoutName");
        }

        Intent intent = getIntent();
        workoutNameTV = intent.getStringExtra("workoutName");

        reference = FirebaseDatabase.getInstance().getReference("User").child(userID).child("Workout");

        newref = reference.child(workoutNameTV).child("Exercises");


        arrayAdapterExercises = new ArrayAdapter<String>(this, R.layout.custom_textview, arrayListExercises);



        newref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot datas: dataSnapshot.getChildren()){
                    //gets data
                    String valueName = (datas.child("exerciseName").getValue().toString());
                    arrayListExercises.add(valueName);
                    String valueSetCount = (datas.child("setCount").getValue().toString());
                    arrayListExercises.add(valueSetCount);
                    amountExercises++;
                    arrayAdapterExercises.notifyDataSetChanged();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        printProgress();

        setUiState(STATE_START);
        findViewById(R.id.button_begin_workout).setOnClickListener(view -> recognizeMicrophone());

        // Check if user has given permission to record audio, init the model after permission is granted
        int permissionCheck = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.RECORD_AUDIO);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, PERMISSIONS_REQUEST_RECORD_AUDIO);
        } else {
            initModel();
        }
    }
    private void initModel() {
        StorageService.unpack(this, "model-en-us", "model",
                (model) -> {
                    this.model = model;
                    setUiState(STATE_READY);
                },
                (exception) -> setErrorState("Failed to unpack the model" + exception.getMessage()));
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSIONS_REQUEST_RECORD_AUDIO) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Recognizer initialization is a time-consuming and it involves IO,
                // so we execute it in async task
                initModel();
            } else {
                finish();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (speechService != null) {
            speechService.stop();
            speechService.shutdown();
        }

        if (speechStreamService != null) {
            speechStreamService.stop();
        }
    }

    private void setUiState(int state) {
        switch (state) {
            case STATE_START:
                workoutResult.setText(R.string.preparing);
                workoutResult.setMovementMethod(new ScrollingMovementMethod());
                text1.setMovementMethod(new ScrollingMovementMethod());
                text2.setMovementMethod(new ScrollingMovementMethod());
                ((Button) findViewById(R.id.button_begin_workout)).setText(R.string.WaitForVoiceAssistant);
                break;
            case STATE_READY:
                ((Button) findViewById(R.id.button_begin_workout)).setText(R.string.begin_workout);
                break;
            case STATE_DONE:
                ((Button) findViewById(R.id.button_begin_workout)).setText(R.string.begin_workout);
                break;
            case STATE_MIC:
                ((Button) findViewById(R.id.button_begin_workout)).setText(R.string.stop_workout);
                ((Button) findViewById(R.id.button_begin_workout)).setText(R.string.stop_workout);
                findViewById(R.id.button_begin_workout).setEnabled(true);
                break;
        }
    }

    private void setErrorState(String message) {
        workoutResult.setText(message);
        ((Button) findViewById(R.id.button_begin_workout)).setText(R.string.recognize_microphone);
        findViewById(R.id.button_begin_workout).setEnabled(false);
    }

    public void recognizeMicrophone() {
        if (speechService != null) {
            setUiState(STATE_DONE);
            speechService.cancel();
            speechService = null;
            resetWorkout();
        } else {
            setUiState(STATE_MIC);
            try {
                Recognizer rec = new Recognizer(model, 16000.0f);
                speechService = new SpeechService(rec, 16000.0f);
                speechService.startListening(this);
            } catch (IOException e) {
                setErrorState(e.getMessage());
            }
        }
    }

    @Override
    public void onPartialResult(String hypothesis) {
        //workoutResult.append(hypothesis + "\n");
    }

    //Todo: Fix accuracy issue in keyword detection
    @Override
    public void onResult(String hypothesis) {
        //text1.append(hypothesis + "\n");
        try {
        userInput = new JSONObject(hypothesis).getString("text");

        //text2.append("Trying to print keyword: ");
        //text2.append(userInput + "\n");

        keyWordFound(userInput, keywordPauseWorkout, keywordSetCompleted);
        }
        catch (JSONException e) {
        System.out.println(e.getMessage());
        }
    }

    @Override
    public void onFinalResult(String s) {

    }

    //Todo: Make function to increment count on reps
    public void keyWordFound(String userInput, String keywordPauseWorkout, String keywordSetCompleted) {
        if (userInput.equals(keywordPauseWorkout)) {
            stopTimer();
        }
        else if (userInput.equals(keywordResumeWorkout)){
            startTimer();
        }
            else if (userInput.equals(keywordSetCompleted)){
            countProgress();
            mTimeLeftInMillis = (30000);
            startTimer();
        }
    }

    @Override
    public void onError(Exception e) {

    }

    @Override
    public void onTimeout() {
        speechService.cancel();
        speechService = null;
        setUiState(STATE_READY);

    }

    private void startTimer() {
        final MediaPlayer sound = MediaPlayer.create(this, R.raw.horn);
        mCountDownTimer = new CountDownTimer(mTimeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mTimeLeftInMillis = millisUntilFinished;
                updateCountDownText();
            }

            @Override
            public void onFinish() {
                sound.start();
            }
        }.start();
    }

    private void updateCountDownText(){
        int minutes = (int) (mTimeLeftInMillis / 1000) / 60;
        int seconds = (int) (mTimeLeftInMillis / 1000) % 60;

        String timeLeftFormatted = String.format(Locale.getDefault(),"%02d:%02d", minutes, seconds);

        mTextViewCountDown.setText(timeLeftFormatted);
    }

    private void stopTimer(){
        mCountDownTimer.cancel();
    }

    private void resetWorkout() {
        workoutResult2.setText("");
        mTextViewCountDown.setText("");
        stopTimer();
    }

    private void countProgress() {
        final MediaPlayer soundSetsComplete = MediaPlayer.create(this, R.raw.allsetscomplete);

        final MediaPlayer soundSet1Complete = MediaPlayer.create(this, R.raw.set1complete);
        final MediaPlayer soundSet2Complete = MediaPlayer.create(this, R.raw.set2complete);
        final MediaPlayer soundSet3Complete = MediaPlayer.create(this, R.raw.set3complete);
        final MediaPlayer soundSet4Complete = MediaPlayer.create(this, R.raw.set4complete);
        final MediaPlayer soundSet5Complete = MediaPlayer.create(this, R.raw.set5complete);

        int sets = Integer.parseInt(arrayListExercises.get(index));
        if (counter == sets) {
            workoutResult2.setText("Current Set: " + counter + "/" + sets);
            soundSetsComplete.start();
            index += 2;
            counter = 1;
        } else {
            workoutResult2.setText("Current Set: " + counter + "/" + sets);
            switch(counter) {
                case 1:
                    soundSet1Complete.start();
                    break;
                case 2:
                    soundSet2Complete.start();
                    break;
                case 3:
                    soundSet3Complete.start();
                    break;
                case 4:
                    soundSet4Complete.start();
                    break;
                case 5:
                    soundSet5Complete.start();
                    break;
            }
            counter++;
        }
    }

    private void printProgress() {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Do something after 5s = 5000ms
                String exName, setCount;
                int loop = amountExercises * 2;
                workoutResult.setText("Workout Info\n\n");
                for (int i = 0; i < loop; i+=2){
                    exName = arrayListExercises.get(i).toString();
                    setCount = arrayListExercises.get(i+1).toString();
                    workoutResult.append("Exercise: " + exName + "\n");
                    workoutResult.append("Set Count: " + setCount + "\n\n");
                }
            }
        }, 3000);


    }
}
