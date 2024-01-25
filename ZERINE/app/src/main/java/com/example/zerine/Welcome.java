package com.example.zerine;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import com.example.zerine.R;

public class Welcome extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome_screen);

        Button reg_button = findViewById(R.id.LS_btnRegister);
        Button log_button = findViewById(R.id.LS_btnLogin);

        reg_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                regact();
            }

        });

        log_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginact();
            }
        });
    }
    private void regact() {
        Intent intent = new Intent(Welcome.this, Register.class);
        startActivity(intent);


    }

    private void loginact(){
        Intent intent = new Intent(Welcome.this, Login.class);
        startActivity(intent);
    }


}

