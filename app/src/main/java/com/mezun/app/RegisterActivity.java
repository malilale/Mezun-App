package com.mezun.app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {

    private EditText et_name, et_lastname,et_starty, et_endy, et_email, et_password;
    private Button btn_register;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        et_name = findViewById(R.id.et_name);
        et_lastname = findViewById(R.id.et_lastname);
        et_starty = findViewById(R.id.et_starty);
        et_endy = findViewById(R.id.et_endy);
        et_email = findViewById(R.id.et_email);
        et_password = findViewById(R.id.et_password);
        btn_register = findViewById(R.id.btn_register);
        mAuth = FirebaseAuth.getInstance();



        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = et_name.getText().toString().trim();
                String lastname = et_lastname.getText().toString().trim();
                String starty = et_starty.getText().toString().trim();
                String endy = et_endy.getText().toString().trim();
                String email = et_email.getText().toString().trim();
                String password = et_password.getText().toString().trim();

                if(!name.isEmpty() || !lastname.isEmpty() ||!starty.isEmpty() ||!endy.isEmpty() ||!email.isEmpty() ||!password.isEmpty()) {
                    if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                        //error message
                        et_email.setError(getString(R.string.invaild_email));
                        et_email.setFocusable(true);
                    }else {
                        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(task -> {
                            if(task.isSuccessful()) {
                                sendToMain();
                            }else {
                                String error = Objects.requireNonNull(task.getException()).getMessage();
                                Toast.makeText(RegisterActivity.this,"Hata"+error, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }else {
                    Toast.makeText(RegisterActivity.this,"Lütfen tüm alanları doldurun", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void sendToMain() {
        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}