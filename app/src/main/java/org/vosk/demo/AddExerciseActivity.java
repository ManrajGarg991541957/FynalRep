package org.vosk.demo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddExerciseActivity extends AppCompatActivity {


    FirebaseDatabase rootNode;
    DatabaseReference reference;
    EditText exerciseName, exerciseRep, exerciseSet, exerciseWeight;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ativity_workout_exercise);


        exerciseName = findViewById(R.id.exercise_name);
        exerciseRep = findViewById(R.id.exercise_rep);
        exerciseSet = findViewById(R.id.exercise_set);
        exerciseWeight = findViewById(R.id.exercise_weight);


        Button addExercise;
        final String workoutName;
        final org.vosk.demo.Exercise exercise = new org.vosk.demo.Exercise();

        // grabbing string from previous activity
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                workoutName = null;
            } else {
                workoutName = extras.getString("workoutId");
            }
        } else {
            workoutName = (String) savedInstanceState.getSerializable("workoutId");
        }

        final Workout workoutObject = new Workout(workoutName);

        addExercise = findViewById(R.id.add_button_exercise);

        addExercise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String ex1Name =  exerciseName.getText().toString();
                String ex1Rep = exerciseRep.getText().toString();
                String ex1Set = exerciseSet.getText().toString();
                String ex1Weight = exerciseWeight.getText().toString();

                exercise.setExerciseName(ex1Name);
                exercise.setRepCount(ex1Rep);
                exercise.setSetCount(ex1Set);
                exercise.setWeight(ex1Weight);

                workoutObject.setExercise(exercise);

                rootNode = FirebaseDatabase.getInstance();
                reference = rootNode.getReference("Workout");

                Intent intent = getIntent();
                String workoutId = intent.getStringExtra("workoutId");
                reference.child(workoutId).child("Exercises").push().setValue(exercise);
                intent = new Intent(AddExerciseActivity.this, org.vosk.demo.CreateNewWorkoutActivity.class);
                intent.putExtra("workoutName", workoutId);
                startActivity(intent);
            }
        });




    }
    public void callIntent(View view) {

    }
}
