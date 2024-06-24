package com.example.zerine;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.zerine.databinding.ActivityMainNavigationBinding;
import com.example.zerine.ui.ForgotPasswordActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class Login extends AppCompatActivity {
    EditText loginEmail, loginPass;
    Button btnLogin, btnRegister;
    FirebaseAuth firebaseAuth;
    ProgressBar progressBar;
    TextView forgotpass;
    CheckBox checkBoxRememberMe;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_screen);

        loginEmail = findViewById(R.id.EditText_Username);
        loginPass = findViewById(R.id.EditText_Password);
        btnLogin = findViewById(R.id.LS_btnLogin);
        btnRegister = findViewById(R.id.LS_btnRegister);
        progressBar = findViewById(R.id.progressBar);
        forgotpass = findViewById(R.id.forgot);
        checkBoxRememberMe = findViewById(R.id.checkBoxRememberMe);


        firebaseAuth = FirebaseAuth.getInstance();


        loadSavedCredentials();

        forgotpass.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Login.this, ForgotPasswordActivity.class));
            }

        });

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
        String email = loginEmail.getText().toString().trim();
        String password = loginPass.getText().toString().trim();
        if (checkBoxRememberMe.isChecked()) {
            // Save credentials if "Remember Me" is checked
            saveCredentials(email, password);
        }

        // Use Firebase Authentication signInWithEmailAndPassword method
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(getApplicationContext(), "Login successful", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(Login.this, MainNavigation.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
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
        String email = loginEmail.getText().toString().trim();
        String password = loginPass.getText().toString().trim();

        if (email.isEmpty()) {
            loginEmail.setError("Email is empty");
            return false;
        }

        if (password.isEmpty()) {
            loginPass.setError("Password is empty");
            return false;
        }

        return true;
    }

    private void resetPassword(String email) {
        firebaseAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(Login.this, "Password reset email sent", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(Login.this, "Failed to send password reset email", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void saveCredentials(String email, String password) {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("savedEmail", email);
        editor.putString("savedPassword", password);
        editor.putBoolean("rememberMe", true);
        editor.apply();
    }

    private void loadSavedCredentials() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        boolean rememberMe = sharedPreferences.getBoolean("rememberMe", false);

        if (rememberMe) {
            // Load and set saved email and password
            String savedEmail = sharedPreferences.getString("savedEmail", "");
            String savedPassword = sharedPreferences.getString("savedPassword", "");

            loginEmail.setText(savedEmail);
            loginPass.setText(savedPassword);
            checkBoxRememberMe.setChecked(true);
        }
    }
}