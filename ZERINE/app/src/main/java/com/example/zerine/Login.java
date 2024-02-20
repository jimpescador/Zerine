package com.example.zerine;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class Login extends AppCompatActivity {
    EditText loginUser, loginPass;
    Button btnLogin, btnRegister;
    FirebaseAuth firebaseAuth;
    ProgressBar progressBar;
    TextView forgotpass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_screen);

        loginUser = findViewById(R.id.EditText_Username);
        loginPass = findViewById(R.id.EditText_Password);
        btnLogin = findViewById(R.id.LS_btnLogin);
        btnRegister = findViewById(R.id.LS_btnRegister);
        progressBar = findViewById(R.id.progressBar);
        forgotpass = findViewById(R.id.forgot);

        firebaseAuth = FirebaseAuth.getInstance();


        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateFields()) {
                    loginUser();
                    progressBar.setVisibility(View.VISIBLE);
                }
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the registration activity
                Intent intent = new Intent(Login.this, Register.class);
                startActivity(intent);
            }
        });
    }

    private void loginUser() {
        String username = loginUser.getText().toString().trim();
        String password = loginPass.getText().toString().trim();

        // Use Firebase Authentication signInWithEmailAndPassword method
        firebaseAuth.signInWithEmailAndPassword(username, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            progressBar.setVisibility(View.GONE);

                            Toast.makeText(getApplicationContext(), "Login successful", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(Login.this, MainNavigation.class);
                            startActivity(intent);
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(getApplicationContext(), "Authentication failed.", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });
    }

    private boolean validateFields() {
        String username = loginUser.getText().toString().trim();
        String password = loginPass.getText().toString().trim();

        if (username.isEmpty() && password.isEmpty()) {
            loginUser.setError("Username and password are empty");
            return false;
        } else if (username.isEmpty()) {
            loginUser.setError("Username is empty");
            return false;
        }

        return true;
    }
}