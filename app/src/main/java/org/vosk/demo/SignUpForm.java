package org.vosk.demo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpForm extends AppCompatActivity {

    //Variables
    private String mTag = "activity_sign_up_form";
    private TextInputLayout regFullName, regEmailAddress, regPassword;
    private Button regCreateAcc;
    private FirebaseAuth mAuth;
    private ProgressBar progressBar;
    FirebaseDatabase rootNode;
    DatabaseReference reff;
    org.vosk.demo.User user;
    final Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(mTag);

        //Hooks to all xml elements in sign_in_form.xml
        regFullName = findViewById(R.id.reg_full_name);
        regEmailAddress = findViewById(R.id.reg_email_address);
        regPassword = findViewById(R.id.reg_password);
        regCreateAcc = findViewById(R.id.reg_create_account_btn);

        progressBar = findViewById(R.id.progressBar);

        mAuth = FirebaseAuth.getInstance();

        FirebaseApp.initializeApp(this);
        rootNode = FirebaseDatabase.getInstance();
        reff = rootNode.getReference().child("User");

        regCreateAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String name = regFullName.getEditText().getText().toString().trim();
                String email = regEmailAddress.getEditText().getText().toString().trim();
                String password = regPassword.getEditText().getText().toString().trim();

                if(name.isEmpty()){
                    regFullName.getEditText().setError("Full name is required!");
                    regFullName.requestFocus();
                    return;
                }

                if(email.isEmpty()){
                    regEmailAddress.getEditText().setError("Email is required!");
                    regEmailAddress.requestFocus();
                    return;
                }

                if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    regEmailAddress.getEditText().setError("Please provide a valid email address.");
                    regEmailAddress.requestFocus();
                    return;
                }

                if(password.isEmpty()){
                    regPassword.getEditText().setError("Password is required!");
                    regPassword.requestFocus();
                    return;
                }

                if(password.length() < 6){
                    regPassword.getEditText().setError("Min password length should be 6 characters.");
                    regPassword.requestFocus();
                    return;
                }

                progressBar.setVisibility(view.VISIBLE);
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                if(task.isSuccessful()){
                                    User user = new User(name, email);

                                    reff.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                            .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if(task.isSuccessful()){
                                                Toast.makeText(SignUpForm.this, "User has been registered successfully!", Toast.LENGTH_SHORT).show();
                                                progressBar.setVisibility(View.GONE);
                                                Intent intent = new Intent(SignUpForm.this, LoginActivity.class);
                                                context.startActivity(intent);
                                                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                                            }else{
                                                Toast.makeText(SignUpForm.this, "Failed to register, try again.", Toast.LENGTH_SHORT).show();
                                                progressBar.setVisibility(View.GONE);
                                            }
                                        }
                                    });
                                }else{
                                    Toast.makeText(SignUpForm.this, "Failed to register, try again.", Toast.LENGTH_SHORT).show();
                                    progressBar.setVisibility(View.GONE);
                                }

                            }
                        });
            }
        });
    }

    private void setContentView(String tag) {
        int id = getResources().getIdentifier(tag, "layout", getPackageName());
        setContentView(id);
    }

}
