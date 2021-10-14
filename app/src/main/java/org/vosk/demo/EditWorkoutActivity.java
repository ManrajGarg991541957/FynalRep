package org.vosk.demo;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class EditWorkoutActivity extends AppCompatActivity {

    private DatabaseReference dbReff, reference;
    private FirebaseUser user;

    private String userID;

    private ListView listView;
    private ArrayAdapter<String> arrayAdapter;
    private ArrayList<String> arrayList = new ArrayList<>();
    private Intent intent = null;
    private DrawerLayout mDrawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_workout_selection);

        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("User");
        userID = user.getUid();


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Fynal Rep");
        toolbar.setTitleTextColor(Color.WHITE);

        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);

        mDrawerLayout = findViewById(R.id.drawer_layout);

        NavigationView navView = findViewById(R.id.nav_view);
        View headerView = navView.getHeaderView(0);

        final TextView fullNameTextView = headerView.findViewById(R.id.nav_user_full_name);
        final TextView emailTextView = headerView.findViewById(R.id.nav_user_email);

        String workoutName;
        dbReff = FirebaseDatabase.getInstance().getReference("Workout");
        listView = (ListView) findViewById(R.id.listView_Workout_edit);
        arrayAdapter = new ArrayAdapter<String>(this,R.layout.custom_textview, arrayList);
        listView.setAdapter(arrayAdapter);

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
                        Toast.makeText(EditWorkoutActivity.this.getApplicationContext(), menuItem.getTitle(),
                                Toast.LENGTH_LONG).show();
                        switch (selectedItemId) {
                            case R.id.nav_home:
                                startActivity(new Intent(EditWorkoutActivity.this, HomeActivity.class));
                                Toast.makeText(EditWorkoutActivity.this, "Home", Toast.LENGTH_LONG).show();

                                break;
                            case R.id.log_out:
                                FirebaseAuth.getInstance().signOut();
                                startActivity(new Intent(EditWorkoutActivity.this, LandingPageActivity.class));
                                Toast.makeText(EditWorkoutActivity.this, "You have successfully  logged out", Toast.LENGTH_LONG).show();

                                break;

                            case R.id.about_us:
                                startActivity(new Intent(EditWorkoutActivity.this, AboutUsActivity.class));
                                Toast.makeText(EditWorkoutActivity.this, "About Us", Toast.LENGTH_LONG).show();

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


        reference.child(userID).child("Workout").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String value = snapshot.getKey();
                arrayList.add(value);
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                String workoutName = (String) (listView.getItemAtPosition(position));

                intent = new Intent(EditWorkoutActivity.this, CreateWorkoutActivity.class);
                intent.putExtra("workoutName", workoutName);

                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
    }


    public void goToCreateWorkout(View view) {
        Intent intent = null;
        switch (view.getId()) {
            case R.id.add_button:
                createWorkoutName();
                break;
            default:
                break;
        }
    }

    public void createWorkoutName() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("New Workout Name");

        // Set up the input
        final EditText input = new EditText(this);
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String workoutName;
                workoutName = input.getText().toString();

                reference.child(userID).child("Workout").child(workoutName);
                intent = new Intent(EditWorkoutActivity.this, CreateNewWorkoutActivity.class);
                intent.putExtra("workoutName", workoutName);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
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
