package com.example.zerine;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.common.internal.AccountType;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity {
    EditText editTextUser, editTextPass, editTextEmail, editTextFname, editTextCNum;
    String AccountTypeChoice;

    RadioButton RadioUser,RadioParent;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firestore;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_screen);

        editTextUser = findViewById(R.id.reg_user);
        editTextPass = findViewById(R.id.reg_pass);
        editTextEmail = findViewById(R.id.reg_email);
        editTextFname = findViewById(R.id.reg_fname);
        editTextCNum = findViewById(R.id.reg_cnum);
        RadioUser = findViewById(R.id.radUser);
        RadioParent = findViewById(R.id.radParent);

        Button button1 = findViewById(R.id.btnRegisterAccount);

        progressBar = findViewById(R.id.progressBar2);
        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                registerUser();
            }
        });
    }

    private void registerUser() {



        String AccLevel = AccountTypeChoice;


        String username = editTextUser.getText().toString().trim();
        String password = editTextPass.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String firstName = editTextFname.getText().toString().trim();
        String contact = editTextCNum.getText().toString().trim();




        if (username.isEmpty() || password.isEmpty() || email.isEmpty() || firstName.isEmpty() || contact.isEmpty()) {
            Toast.makeText(getApplicationContext(), "All fields must be filled", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
            return;
        }


        if (RadioUser.isChecked()) {
            AccountTypeChoice = RadioUser.getText().toString().trim();
        }
        else if (RadioParent.isChecked()){
            AccountTypeChoice = RadioParent.getText().toString().trim();
        }
        else {
            Toast.makeText(getApplicationContext(), "Select Account Type User or Parent", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
            return;

        }

        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Registration success, now add user data to Firestore
                        String accountId = firebaseAuth.getCurrentUser().getUid();
                        Map<String, Object> info = new HashMap<>();
                        info.put("Name", firstName);
                        info.put("Mobile", contact);
                        info.put("AccountType", AccountTypeChoice);

                        firestore.collection("info").document(accountId).set(info)
                                .addOnSuccessListener(aVoid -> {
                                    progressBar.setVisibility(View.GONE);
                                    Toast.makeText(getApplicationContext(), "Registration successful", Toast.LENGTH_LONG).show();
                                    Intent intent = new Intent(Register.this, Login.class);
                                    startActivity(intent);
                                })
                                .addOnFailureListener(e -> {
                                    progressBar.setVisibility(View.GONE);
                                    Toast.makeText(getApplicationContext(), "Failed to save user info: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                });
                    } else {
                        // If registration fails, display a message to the user.
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(getApplicationContext(), "Registration failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }
}