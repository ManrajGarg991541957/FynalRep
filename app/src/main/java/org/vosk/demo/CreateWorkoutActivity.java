package org.vosk.demo;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class CreateWorkoutActivity extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;
    private FirebaseUser user;
    private DatabaseReference reference, newref;

    private String userID;

    private TextView customWorkout, workoutTV;
    private Button button_delete_workout, button_add_workout, button_add_exercise, button_save_workout;

    AlertDialog.Builder builder;

    String workoutNameTV;

    private ListView listView;
    private ArrayAdapter<String> arrayAdapter;
    private ArrayList<String> arrayList = new ArrayList<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_workout_info);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Fynal Rep");
        toolbar.setTitleTextColor(Color.WHITE);

        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);

        mDrawerLayout = findViewById(R.id.drawer_layout);
        button_save_workout = findViewById(R.id.button_confirmation);
        button_delete_workout = findViewById(R.id.button_delete_workout);
        button_add_exercise = findViewById(R.id.button_add_exercise);
        workoutTV = findViewById(R.id.workoutTV);
        builder = new AlertDialog.Builder(this);

        NavigationView navView = findViewById(R.id.nav_view);
        View headerView = navView.getHeaderView(0);
        final TextView fullNameTextView = headerView.findViewById(R.id.nav_user_full_name);
        final TextView emailTextView = headerView.findViewById(R.id.nav_user_email);

        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("User");
        userID = user.getUid();

        reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User userProfile = snapshot.getValue(User.class);

                if(userProfile != null){
                    String fullName = userProfile.getFullName();
                    String email = userProfile.getEmail();

                    fullNameTextView.setText("Welcome, " + fullName);
                    emailTextView.setText(email);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        navView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                        int selectedItemId = menuItem.getItemId();
                        //set item as selected to persist highlight
                        menuItem.setChecked(true);
                        //close drawer when item is tapped
                        mDrawerLayout.closeDrawers();
                        Toast.makeText(CreateWorkoutActivity.this.getApplicationContext(), menuItem.getTitle(),
                                Toast.LENGTH_LONG).show();
                        switch (selectedItemId) {
                            case R.id.log_out:
                                FirebaseAuth.getInstance().signOut();
                                startActivity(new Intent(CreateWorkoutActivity.this, LandingPageActivity.class));
                                Toast.makeText(CreateWorkoutActivity.this, "You have successfully  logged out", Toast.LENGTH_LONG).show();

                                break;
                        }

                        return true;
                    }
                });

        mDrawerLayout.addDrawerListener(
                new DrawerLayout.DrawerListener() {
                    @Override
                    public void onDrawerSlide(View drawerView, float slideOffset) {

                    }

                    @Override
                    public void onDrawerOpened(View drawerView) {
                        // Respond when the drawer is opened
                    }

                    @Override
                    public void onDrawerClosed(View drawerView) {
                        // Respond when the drawer is closed
                    }

                    @Override
                    public void onDrawerStateChanged(int newState) {
                        // Respond when the drawer motion state changes
                    }
                }
        );

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

        //edit workout
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                customWorkout = findViewById(R.id.workoutTV);
                String workoutName = customWorkout.getText().toString();
                String exerciseName = (String) (listView.getItemAtPosition(position));


                Intent intent = null;
                intent = new Intent(CreateWorkoutActivity.this, EditExerciseActivity.class);
                intent.putExtra("workoutId", workoutName);
                intent.putExtra("exerciseId", exerciseName);

                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
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

        //save and confirm button
        button_save_workout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view)
            {
                builder.setMessage("Do you want to save the exercises to this workout?") .setTitle("Confirmation");

                //Setting message manually and performing action on button click
                builder.setMessage("Do you want to save the exercises to this workout?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                finish();
                                Toast.makeText(getApplicationContext(),"exercise(s) saved",
                                        Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(CreateWorkoutActivity.this, EditWorkoutActivity.class);
                                startActivity(intent);
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //  Action for 'NO' Button
                                dialog.cancel();
                                Toast.makeText(getApplicationContext(),"Continue editing workout",
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
                //Creating dialog box
                AlertDialog alert = builder.create();
                //Setting the title manually
                alert.setTitle("Confirmation");
                alert.show();
            }
        });


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
        }
        return super.onOptionsItemSelected(item);
    }
}