package com.mezun.app;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {

    EditText et_email, et_password;
    Button btn_login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        et_email = findViewById(R.id.et_email);
        et_password = findViewById(R.id.et_password);
        btn_login = findViewById(R.id.btn_login);

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = et_email.getText().toString().trim();
                String password = et_password.getText().toString().trim();
                if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    //error message
                    et_email.setError(getString(R.string.invaild_email));
                    et_email.setFocusable(true);
                }else {
                    /*if(password.length() < 6) {
                        //error message
                        et_password.setError("Şifre 6 haneden kısa olamaz");
                        et_password.setFocusable(true);
                    }*/

                    login(email,password); //login user
                }

            }
        });
    }

    private void login(String email, String password) {
        //Login User
    }
}