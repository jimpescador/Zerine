package com.example.zerine;

import android.content.AsyncQueryHandler;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.os.AsyncTask;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import androidx.appcompat.app.AppCompatActivity;

import com.example.zerine.R;

public class Login extends AppCompatActivity {
    EditText loginUser, loginPass;
    Button btnLogin, btnRegister;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_screen);

        loginUser = (EditText)findViewById(R.id.EditText_Username);
        loginPass = (EditText) findViewById(R.id.EditText_Password);
        btnLogin = (Button)findViewById(R.id.LS_btnLogin);
        btnRegister= (Button)findViewById(R.id.LS_btnRegister);


        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



            }
        });


        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this,Register.class);
                startActivity(intent);
                finish();

            }
        });
    }




}











