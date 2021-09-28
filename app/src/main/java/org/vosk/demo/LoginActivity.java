package org.vosk.demo;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    TextInputLayout loginUsername, loginPassword;
    String username, password;
    FirebaseDatabase rootNode;
    DatabaseReference reff, userReff;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginUsername = findViewById(R.id.login_username);
        loginPassword = findViewById(R.id.login_password);


        FirebaseApp.initializeApp(this);
        rootNode = FirebaseDatabase.getInstance();
        reff = rootNode.getReference().child("User");

        findViewById(R.id.button_login).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                username = loginUsername.getEditText().getText().toString();
                password = loginPassword.getEditText().getText().toString();
                userReff = reff.child(username);
                if(userReff.child("password").getKey().equals(password)){
                    homePage(view);
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);

// 2. Chain together various setter methods to set the dialog characteristics
                    builder.setMessage("Some of your information isn't correct. Please try again.")
                            .setTitle("Invalid Credentials")
                            .setNeutralButton(android.R.string.ok, null);

// 3. Get the <code><a href="/reference/android/app/AlertDialog.html">AlertDialog</a></code> from <code><a href="/reference/android/app/AlertDialog.Builder.html#create()">create()</a></code>
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }
        });


    }

    public void homePage(View view){
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

}