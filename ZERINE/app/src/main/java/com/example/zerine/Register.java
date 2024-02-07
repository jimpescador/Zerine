package com.example.zerine;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.zerine.R;
import android.database.sqlite.SQLiteDatabase;
import android.content.ContentValues;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;


public class Register extends AppCompatActivity {
    EditText editTextUser, editTextPass, editTextEmail, editTextcode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_screen);

        editTextUser = findViewById(R.id.reg_user);
        editTextPass = findViewById(R.id.reg_pass);
        editTextEmail = findViewById(R.id.reg_email);
        editTextcode = findViewById(R.id.code);
        Button button1 = findViewById(R.id.btnRegisterAccount);

        ConnectionHelper connectionHelper = new ConnectionHelper();

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

        if (validateInputs(username, password, email)) {
            ConnectionHelper connectionHelper = new ConnectionHelper();
            Connection con = connectionHelper.connectionclass();

            if (con != null) {
                try {
                    String query = "INSERT INTO Users (Username, Password, Email) VALUES (?, ?, ?)";
                    PreparedStatement stmt = con.prepareStatement(query);
                    stmt.setString(1, username);
                    stmt.setString(2, password);
                    stmt.setString(3, email);
                    int affectedRows = stmt.executeUpdate();

                    if (affectedRows > 0) {
                        Toast.makeText(Register.this, "Registration successful", Toast.LENGTH_SHORT).show();
                        // Optionally, you can navigate to another activity or perform additional actions here
                    } else {
                        Toast.makeText(Register.this, "Registration failed", Toast.LENGTH_SHORT).show();
                    }
                } catch (SQLException e) {
                    Toast.makeText(Register.this, "Registration failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(Register.this, "Connection failed", Toast.LENGTH_SHORT).show();
            }
        }
    }



        private boolean validateInputs (String username, String password, String email){
            if (username.isEmpty() && password.isEmpty() && email.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return false;
            } else if (username.isEmpty() && password.isEmpty()) {
                // Display a Toast message for either empty username or password
                Toast.makeText(this, "Please fill in username and password", Toast.LENGTH_SHORT).show();
                return false;
            } else if (username.isEmpty() && email.isEmpty()) {
                Toast.makeText(this, "Please fill in username and email", Toast.LENGTH_SHORT).show();
                return false;
            } else if (password.isEmpty() && email.isEmpty()) {
                Toast.makeText(this, "Please fill in password and email", Toast.LENGTH_SHORT).show();
                return false;
            } else if (username.isEmpty()) {
                // Display a Toast message for empty username
                Toast.makeText(this, "Please fill in username", Toast.LENGTH_SHORT).show();
                return false;
            } else if (password.isEmpty()) {
                // Display a Toast message for empty password
                Toast.makeText(this, "Please enter a password", Toast.LENGTH_SHORT).show();
                return false;
            } else if (email.isEmpty()) {
                // Display a Toast message for empty email
                Toast.makeText(this, "Please enter an email address", Toast.LENGTH_SHORT).show();
                return false;
            }


            // Validate username (at least 4 characters)
            if (username.length() < 4) {
                Toast.makeText(this, "Username must be at least 4 characters", Toast.LENGTH_SHORT).show();
                return false;
            }

            // Validate password (at least 6 characters)
            if (password.length() < 6) {
                Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
                return false;
            }


            return true;
        }
    }





