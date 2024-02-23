package com.example.zerine;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.zerine.R;

public class Welcome extends AppCompatActivity {
    private static final int REQUEST_CODE_PERMISSION = 100;
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

        // Check for permissions and request if not granted
        checkAndRequestPermissions();
    }
    private void regact() {
        Intent intent = new Intent(Welcome.this, Register.class);
        startActivity(intent);


    }

    private void loginact(){
        Intent intent = new Intent(Welcome.this, Login.class);
        startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CODE_PERMISSION) {
            // Check if all permissions are granted
            boolean allPermissionsGranted = true;
            for (int grantResult : grantResults) {
                if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    allPermissionsGranted = false;
                    break;
                }
            }

            if (allPermissionsGranted) {
                //Toast.makeText(this, "All permissions are granted.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "All permissions are required for this app.", Toast.LENGTH_SHORT).show();
                this.finish();
                System.exit(0);
            }
        }
    }

    private void checkAndRequestPermissions() {
        String[] requiredPermissions = new String[]{android.Manifest.permission.BLUETOOTH, android.Manifest.permission.BLUETOOTH_ADMIN
                , android.Manifest.permission.BLUETOOTH_SCAN, android.Manifest.permission.BLUETOOTH_ADVERTISE
                , android.Manifest.permission.BLUETOOTH_CONNECT, android.Manifest.permission.INTERNET
                , android.Manifest.permission.SEND_SMS, android.Manifest.permission.READ_PHONE_STATE
                , android.Manifest.permission.ACCESS_FINE_LOCATION};
        boolean allPermissionsGranted = true;

        for (String permission : requiredPermissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                allPermissionsGranted = false;
                break;
            }
        }

        if (!allPermissionsGranted) {
            ActivityCompat.requestPermissions(this, requiredPermissions, REQUEST_CODE_PERMISSION);
        }
    }


}

