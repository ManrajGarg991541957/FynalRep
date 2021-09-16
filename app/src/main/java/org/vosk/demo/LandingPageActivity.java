package org.vosk.demo;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class LandingPageActivity extends AppCompatActivity {

    private Button signUpButton;
    private Button logInButton;
    private static final int PERMISSIONS_REQUEST_RECORD_AUDIO = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing_page);

        // Check if user has given permission to record audio
        int permissionCheck = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.RECORD_AUDIO);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, PERMISSIONS_REQUEST_RECORD_AUDIO);
            return;
        }

        signUpButton = findViewById(R.id.signUpButton);
        logInButton = findViewById(R.id.logInButton);

        signUpButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                openSignUpPage();
            }
        });

        logInButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                openLoginPage();
            }
        });

    }

    public void openSignUpPage() {
        Intent intent = new Intent(this, SignUpForm.class);
        startActivity(intent);
    }

    public void  openLoginPage() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);


    }
}










