package org.vosk.demo;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    private TextInputLayout loginEmail, loginPassword;
    private String email, password;
    private ProgressBar progressBar;
    FirebaseDatabase rootNode;
    DatabaseReference reff, userReff;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginEmail = findViewById(R.id.login_email_address);
        loginPassword = findViewById(R.id.login_password);

        progressBar = findViewById(R.id.progressBar2);

        FirebaseApp.initializeApp(this);
        rootNode = FirebaseDatabase.getInstance();
        reff = rootNode.getReference().child("User");

        mAuth = FirebaseAuth.getInstance();

        findViewById(R.id.button_login).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                email = loginEmail.getEditText().getText().toString().trim();
                password = loginPassword.getEditText().getText().toString().trim();

                if(email.isEmpty()){
                    loginEmail.getEditText().setError("Email is required!");
                    loginEmail.requestFocus();
                    return;
                }

                if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    loginEmail.getEditText().setError("Please provide a valid email address.");
                    loginEmail.requestFocus();
                    return;
                }

                if(password.isEmpty()){
                    loginPassword.getEditText().setError("Password is required!");
                    loginPassword.requestFocus();
                    return;
                }

                if(password.length() < 6){
                    loginPassword.getEditText().setError("Min password length should be 6 characters.");
                    loginPassword.requestFocus();
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);

                mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(task.isSuccessful()){
                            homePage(view);
                        }else{
                            Toast.makeText(LoginActivity.this, "Failed to login! Please verify your credentials", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });


    }

    public void homePage(View view){
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }




}