package com.example.zerine;
import static android.app.ProgressDialog.show;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.example.zerine.R;
import android.database.sqlite.SQLiteDatabase;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import com.example.zerine.MyDBHelper;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class Register extends AppCompatActivity {
    EditText editTextUser, editTextPass, editTextEmail, editTextcode;
    FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_screen);

        editTextUser = findViewById(R.id.reg_user);
        editTextPass = findViewById(R.id.reg_pass);
        editTextEmail = findViewById(R.id.reg_email);
        editTextcode = findViewById(R.id.code);
        Button button1 = findViewById(R.id.btnRegisterAccount);

        // DBconnection dBconnection = new DBconnection(this);
        firestore = FirebaseFirestore.getInstance();
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                RegisterUser();
            }
        });
    }

    private void RegisterUser() {
        String username = editTextUser.getText().toString().trim();
        String password = editTextPass.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String code = editTextcode.getText().toString().trim();

        Map<String, Object> account = new HashMap<>();
        account.put("Username", username);
        account.put("Password", password);
        account.put("Email", email);

        firestore.collection("accounts").add(account).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Toast.makeText(getApplicationContext(), "Success",Toast.LENGTH_LONG).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Failed",Toast.LENGTH_LONG).show();

            }
        });
    }





    }
}










