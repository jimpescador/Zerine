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

public class Login extends AppCompatActivity {
    EditText loginUser, loginPass;
    Button btnLogin, btnRegister;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_screen);

        loginUser = findViewById(R.id.EditText_Username);
        loginPass = findViewById(R.id.EditText_Password);
        btnLogin = findViewById(R.id.LS_btnLogin);
        btnRegister= findViewById(R.id.LS_btnRegister);


        btnLogin.setOnClickListener(v -> LoginAct());

        btnRegister.setOnClickListener(v -> {
            Intent intent = new Intent(Login.this,Register.class);
            startActivity(intent);
            finish();

        });
    }
    private void LoginAct() {
        String username = loginUser.getText().toString().trim();
        String password = loginPass.getText().toString().trim();


        if (validateInputs(username, password)) {
            Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(Login.this, MainNavigation.class);
            startActivity(intent);
            loginUser.getText().clear();
            loginPass.getText().clear();

        } else if (username.isEmpty() && password.isEmpty()) {
            Toast.makeText(this, "Please fill in both username and password.", Toast.LENGTH_SHORT).show();
        } else if (username.isEmpty()) {
            Toast.makeText(this, "Please fill in the username.", Toast.LENGTH_SHORT).show();
        } else if (password.isEmpty()) {
            Toast.makeText(this,"Please fill in the password", Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(this, "Invalid input. Please check your username and password.", Toast.LENGTH_SHORT).show();
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

















