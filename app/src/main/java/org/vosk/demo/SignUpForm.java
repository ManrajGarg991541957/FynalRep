package org.vosk.demo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SignUpForm extends AppCompatActivity {

    //Variables
    private String mTag = "activity_sign_in_form";
    TextInputLayout regUserName, regFullName, regEmailAddress, regPassword;
    Button regCreateAcc, nextPage;
    FirebaseDatabase rootNode;
    DatabaseReference reff;
    org.vosk.demo.User user;
    long maxId = 0;
    final Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(mTag);

        //Hooks to all xml elements in sign_in_form.xml
        regUserName = findViewById(R.id.reg_username);
        regFullName = findViewById(R.id.reg_full_name);
        regEmailAddress = findViewById(R.id.reg_email_address);
        regPassword = findViewById(R.id.reg_password);
        regCreateAcc = findViewById(R.id.reg_create_account_btn);

        FirebaseApp.initializeApp(this);
        rootNode = FirebaseDatabase.getInstance();
        reff = rootNode.getReference().child("User");

        reff.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                    maxId=snapshot.getChildrenCount();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        regCreateAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                user = new org.vosk.demo.User();



                String username = regUserName.getEditText().getText().toString();
                String name = regFullName.getEditText().getText().toString();
                String email = regEmailAddress.getEditText().getText().toString();
                String password = regPassword.getEditText().getText().toString();
                user.setUserName(username);
                user.setFirstName(name);
                user.setEmail(email);
                user.setPassword(password);

                reff.child(username).setValue(user);
                Toast.makeText(SignUpForm.this, "User Account Created",  Toast.LENGTH_LONG).show();
                Intent intent = new Intent(SignUpForm.this, LoginActivity.class);
                context.startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
    }

    private void setContentView(String tag) {
        int id = getResources().getIdentifier(tag, "layout", getPackageName());
        setContentView(id);
    }

}
