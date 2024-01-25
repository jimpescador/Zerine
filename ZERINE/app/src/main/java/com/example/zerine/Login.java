package com.example.zerine;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.zerine.R;

public class Login extends AppCompatActivity {
    EditText loginUser, loginPass;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_screen);
        Button button1 = findViewById(R.id.LS_btnLogin);
        loginUser = findViewById(R.id.EditText_Username);
        loginPass = findViewById(R.id.EditText_Password);

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginAct();
            }
        });
        }
    private void LoginAct(){
        String username = loginUser.getText().toString().trim();
        String password = loginPass.getText().toString().trim();


        if (validateInputs(username, password)) {
            Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean validateInputs(String username, String password) {
        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
    }
