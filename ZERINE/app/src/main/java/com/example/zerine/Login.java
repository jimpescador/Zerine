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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
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


        mAuth.createUserWithEmailAndPassword(username, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(EmailPasswordActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
}

















