package com.example.zerine;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.zerine.R;
import android.database.sqlite.SQLiteDatabase;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
public class Login extends AppCompatActivity {
    EditText loginUser, loginPass;
    Button btnLogin, btnRegister;
    FirebaseFirestore firestore;
    ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_screen);

        loginUser = findViewById(R.id.EditText_Username);
        loginPass = findViewById(R.id.EditText_Password);
        btnLogin = findViewById(R.id.LS_btnLogin);
        btnRegister= findViewById(R.id.LS_btnRegister);
        firestore = FirebaseFirestore.getInstance();
        progressBar = findViewById(R.id.progressBar);



        btnLogin.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                loginUser();
                progressBar.setVisibility(View.VISIBLE);
            }


        });
    }


    private void loginUser() {
        String username = loginUser.getText().toString().trim();
        String password = loginPass.getText().toString().trim();

        // Check the user credentials against the Firestore collection
        firestore.collection("accounts")
                .whereEqualTo("Username", username)
                .whereEqualTo("Password", password)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        progressBar.setVisibility(View.GONE);
                        // Login successful
                        Toast.makeText(getApplicationContext(), "Login successful", Toast.LENGTH_LONG).show();
                        // You can redirect the user to another activity here
                        // For example, start a new activity:
                        // startActivity(new Intent(LoginActivity.this, YourNextActivity.class));
                        Intent intent = new Intent(Login.this, MainNavigation.class);
                        startActivity(intent);
                        finish();

                    } else {
                        // Login failed
                        Toast.makeText(getApplicationContext(), "Invalid credentials", Toast.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle failure
                    Toast.makeText(getApplicationContext(), "Failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }
}
















