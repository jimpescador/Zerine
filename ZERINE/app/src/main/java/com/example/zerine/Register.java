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
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import com.example.zerine.MyDBHelper;

public class Register extends AppCompatActivity {
    EditText editTextUser, editTextPass, editTextEmail, editTextcode;
    private MyDBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_screen);

        editTextUser = findViewById(R.id.reg_user);
        editTextPass = findViewById(R.id.reg_pass);
        editTextEmail = findViewById(R.id.reg_email);
        editTextcode = findViewById(R.id.code);
        Button button1 = findViewById(R.id.btnRegisterAccount);

        MyDBHelper dbHelper = new MyDBHelper(this);

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


        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("Username", username);
        values.put("Password", password);
        values.put("Email", email);

        long newRowId = db.insert("AccountTBL", null, values);

        if (newRowId != -1) {
            // Registration successful
            Toast.makeText(Register.this, "Registration successful", Toast.LENGTH_SHORT).show();
            // Add code to navigate to the login activity or perform other actions
        } else {
            // Registration failed
            Toast.makeText(Register.this, "Registration failed", Toast.LENGTH_SHORT).show();
        }

        db.close();
    }
}










