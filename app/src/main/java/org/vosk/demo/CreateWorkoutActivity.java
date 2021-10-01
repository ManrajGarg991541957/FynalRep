package org.vosk.demo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class CreateWorkoutActivity extends AppCompatActivity {

    private FirebaseUser user;
    private DatabaseReference reference;

    private String userID;

    private TextView customWorkout;
    private Button button_delete_workout;
    private Button button_add_exercise;

    private DatabaseReference newref;

    private TextView workoutTV;

    private ListView listView;
    private ArrayAdapter<String> arrayAdapter;
    private ArrayList<String> arrayList = new ArrayList<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_workout_info);


        button_delete_workout = findViewById(R.id.button_delete_workout);
        button_add_exercise = findViewById(R.id.button_add_exercise);
        workoutTV = findViewById(R.id.workoutTV);

        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("User");
        userID = user.getUid();

        String workoutNameTV;
        // grabbing string from previous activity
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                workoutNameTV = null;
            } else {
                workoutNameTV = extras.getString("workoutName");
            }
        } else {
            workoutNameTV = (String) savedInstanceState.getSerializable("workoutName");
        }

        Intent intent = getIntent();
        workoutNameTV = intent.getStringExtra("workoutName");
        workoutTV.setText(workoutNameTV);

        newref = reference.child(userID).child("Workout").child(workoutNameTV).child("Exercises");

        listView = (ListView) findViewById(R.id.listView_workout_exercises);
        arrayAdapter = new ArrayAdapter<String>(this, R.layout.custom_textview, arrayList);
        listView.setAdapter(arrayAdapter);

        newref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot datas: dataSnapshot.getChildren()){
                    //gets data
                    String value = (datas.child("exerciseName").getValue().toString());
                    arrayList.add(value);
                    arrayAdapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        // Delete entire workout
        button_delete_workout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customWorkout = findViewById(R.id.workoutTV);
                String workoutName = customWorkout.getText().toString();
                reference.child(userID).child("Workout").child(workoutName).removeValue();
                Intent intent = null;
                intent = new Intent(CreateWorkoutActivity.this, EditWorkoutActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });


        button_add_exercise.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view)
            {
                customWorkout = findViewById(R.id.workoutTV);
                String workoutName = customWorkout.getText().toString();
                Intent intent = null;
                intent = new Intent(CreateWorkoutActivity.this, AddExerciseActivity.class);
                intent.putExtra("workoutId", workoutName);

                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });


    }
}