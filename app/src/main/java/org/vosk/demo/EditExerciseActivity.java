package org.vosk.demo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EditExerciseActivity extends AppCompatActivity {

    private FirebaseUser user;
    private DatabaseReference reference, newref, newref2;

    private String userID;
    private String exerciseKey;

    private FirebaseDatabase rootNode;
    // private DatabaseReference reference;
    private EditText exerciseName, exerciseRep, exerciseSet, exerciseWeight;

    String workoutNameTV;
    String exerciseNameTV;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ativity_edit_workout_exercise);


        exerciseName = findViewById(R.id.exercise_name);
        exerciseRep = findViewById(R.id.exercise_rep);
        exerciseSet = findViewById(R.id.exercise_set);
        exerciseWeight = findViewById(R.id.exercise_weight);


        Button editExercise;
        final String workoutName;
        final String exerciseId;
        final org.vosk.demo.Exercise exercise = new org.vosk.demo.Exercise();

        // grabbing string from previous activity
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                workoutName = null;
                exerciseId = null;
            } else {
                workoutName = extras.getString("workoutId");
                exerciseId = extras.getString("exerciseId");
            }
        } else {
            workoutName = (String) savedInstanceState.getSerializable("workoutId");
            exerciseId = (String) savedInstanceState.getSerializable("exerciseId");
        }

        final Workout workoutObject = new Workout(workoutName);

        editExercise = findViewById(R.id.edit_button_exercise);

        Intent intent = getIntent();
        workoutNameTV = intent.getStringExtra("workoutId");
        exerciseNameTV = intent.getStringExtra("exerciseId");

        user = FirebaseAuth.getInstance().getCurrentUser();
        userID = user.getUid();

        reference = FirebaseDatabase.getInstance().getReference("User").child(userID).child("Workout");

        newref = reference.child(workoutNameTV).child("Exercises");

        newref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot datas: dataSnapshot.getChildren()){
                    //gets data
                    String valueName = (datas.child("exerciseName").getValue().toString());

                    if(valueName.equals(exerciseNameTV))
                    {
                        exerciseName.setText(datas.child("exerciseName").getValue().toString());
                        exerciseRep.setText(datas.child("repCount").getValue().toString());
                        exerciseSet.setText(datas.child("setCount").getValue().toString());
                        exerciseWeight.setText(datas.child("weight").getValue().toString());

                        exerciseKey = datas.getKey();
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });


        editExercise.setOnClickListener(new View.OnClickListener() {
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

                user = FirebaseAuth.getInstance().getCurrentUser();
                reference = FirebaseDatabase.getInstance().getReference("User");
                userID = user.getUid();

                Intent intent = getIntent();
                String workoutId = intent.getStringExtra("workoutId");
                reference.child(userID).child("Workout").child(workoutId).child("Exercises").child(exerciseKey).setValue(exercise);
                intent = new Intent(EditExerciseActivity.this, org.vosk.demo.CreateNewWorkoutActivity.class);
                intent.putExtra("workoutName", workoutId);
                startActivity(intent);
            }
        });

    }
    public void callIntent(View view) {

    }
}
