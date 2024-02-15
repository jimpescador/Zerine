package com.example.zerine;
import static android.app.ProgressDialog.show;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;
import java.util.Map;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class Register extends AppCompatActivity {
    EditText editTextUser, editTextPass, editTextEmail, editTextFname, editTextLname;
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
        editTextLname = findViewById(R.id.reg_lname);

        Button button1 = findViewById(R.id.btnRegisterAccount);

        progressBar = findViewById(R.id.progressBar2); // Make sure to initialize progressBar

        firestore = FirebaseFirestore.getInstance();
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                RegisterUser();
            }
        });
    }

    private void RegisterUser() {
        String username = editTextUser.getText().toString().trim();
        String password = editTextPass.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String firstName = editTextFname.getText().toString().trim();
        String lastName = editTextLname.getText().toString().trim();


        //Test Validation of Empty Field START
        String strVar1, strVar2, strVar3, strVar4, strVar5;



        if (username.isEmpty()) {
            strVar1 = "Username";
            progressBar.setVisibility(View.GONE);
        } else {
            strVar1 = "";
            progressBar.setVisibility(View.GONE);
        }

        if (password.isEmpty()) {
            strVar2 = "Password";
            progressBar.setVisibility(View.GONE);
        } else {
            strVar2 = "";
            progressBar.setVisibility(View.GONE);
        }

        if (email.isEmpty()) {
            strVar3 = "Email";
            progressBar.setVisibility(View.GONE);
        } else
        {
            strVar3 = "";
            progressBar.setVisibility(View.GONE);
        }

        if (firstName.isEmpty()) {
            strVar4 = "First Name";
            progressBar.setVisibility(View.GONE);
        } else {
            strVar4 = "";
            progressBar.setVisibility(View.GONE);
        }

        if (lastName.isEmpty()) {
            strVar5 = "Last Name";
            progressBar.setVisibility(View.GONE);
        } else {
            strVar5 = "";
            progressBar.setVisibility(View.GONE);
        }

        if (username.isEmpty() || password.isEmpty() || email.isEmpty() || firstName.isEmpty() || lastName.isEmpty()) {
            String message = strVar1 + strVar2 + strVar3 + strVar4 + strVar5 + " is empty";
            // Display the toast message here using Android Toast API
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
        }
        else
        {
            Map<String, Object> account = new HashMap<>();
            account.put("Username", username);
            account.put("Password", password);
            account.put("Email", email);

            firestore.collection("accounts").add(account)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            String accountId = documentReference.getId();
                            Map<String, Object> info = new HashMap<>();
                            info.put("FirstName", firstName);
                            info.put("LastName", lastName);

                            firestore.collection("info").document(accountId).set(info)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            progressBar.setVisibility(View.GONE);
                                            Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_LONG).show();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            progressBar.setVisibility(View.GONE);
                                            Toast.makeText(getApplicationContext(), "Failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                        }
                                    });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(getApplicationContext(), "Failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
        }

        //Test Validation of Empty Field END


    }
}
