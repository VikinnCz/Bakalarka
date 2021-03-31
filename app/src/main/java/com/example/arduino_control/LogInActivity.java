package com.example.arduino_control;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LogInActivity extends AppCompatActivity {

    private static final String TAG = LogInActivity.class.getName();

    private EditText editTextEmail;
    private EditText editTextPassword;
    private Button buttonLogIn;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        editTextEmail = findViewById(R.id.editTextTextEmailAddress);
        editTextPassword = findViewById(R.id.editTextTextPassword);
        buttonLogIn = findViewById(R.id.buttonLogIn);

        mAuth = FirebaseAuth.getInstance();

        logIn();
    }

    private void logIn() {
        buttonLogIn.setOnClickListener(v -> {
            if (editTextEmail.getText().toString().equals("")) {
                Toast.makeText(this, "Can not Log-in without email", Toast.LENGTH_SHORT).show();
            } else if (editTextPassword.getText().toString().equals("")) {
                Toast.makeText(this, "Can not Log-in without password.", Toast.LENGTH_SHORT).show();
            } else {
                mAuth.signInWithEmailAndPassword(editTextEmail.getText().toString(), editTextPassword.getText().toString()).addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "logIn: success");
                        FirebaseUser currentUser = mAuth.getCurrentUser();
                        startMainActivity(currentUser);
                    } else {
                        Log.d(TAG, "logIn: failure", task.getException());
                        Toast.makeText(this, "Log-in failed.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    public void signUp(View v) {
        if (editTextEmail.getText().toString().equals("")) {
            Toast.makeText(this, "Can not Sign-up without email", Toast.LENGTH_SHORT).show();
        } else if (editTextPassword.getText().toString().equals("")) {
            Toast.makeText(this, "Can not Sign-up without password.", Toast.LENGTH_SHORT).show();
        } else {
            mAuth.createUserWithEmailAndPassword(editTextEmail.getText().toString(), editTextPassword.getText().toString()).addOnCompleteListener(this, task -> {
                if (task.isSuccessful()) {
                    Log.d(TAG, "signUp: success");
                    FirebaseUser currentUser = mAuth.getCurrentUser();
                    startMainActivity(currentUser);
                } else {
                    Log.d(TAG, "signUp: failure", task.getException());
                    Toast.makeText(this, "Sig-in failed", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public void changePassword(View v) {
        if(!editTextEmail.getText().toString().equals("")){
            mAuth.sendPasswordResetEmail(editTextEmail.getText().toString()).addOnCompleteListener(this, task -> {
                if (task.isSuccessful()) {
                    Log.d(TAG, "changePassword: Email sent");
                    Toast.makeText(this, "Link for reset password send to email", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, "Need email to change password.", Toast.LENGTH_SHORT).show();
        }
    }

    private void startMainActivity(FirebaseUser currentUser) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("user", currentUser);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mAuth.getCurrentUser() != null) {
            FirebaseUser currentUser = mAuth.getCurrentUser();
            startMainActivity(currentUser);
        }
    }
}
