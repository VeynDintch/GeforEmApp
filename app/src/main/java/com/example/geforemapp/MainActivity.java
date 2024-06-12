package com.example.geforemapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText editTextEmail;
    private EditText editTextPassword;
    private Button buttonLogin;
    private Button buttonForgotPassword;
    private LoginManager loginManager;

    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        editTextEmail = findViewById(R.id.mail);
        editTextPassword = findViewById(R.id.pwdUser);
        buttonLogin = findViewById(R.id.butSum);
        buttonForgotPassword = findViewById(R.id.button);

        loginManager = new LoginManager(this);

        buttonLogin.setOnClickListener(this);
        buttonForgotPassword.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == buttonLogin) {
            String email = editTextEmail.getText().toString().trim();
            String password = editTextPassword.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show();
            } else {
                if (email.matches(emailPattern)) {
                    loginManager.validateLogin(email, password, new LoginManager.LoginCallback() {
                        @Override
                        public void onSuccess(String token) {
                            Toast.makeText(MainActivity.this, "Login successful!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(MainActivity.this, Home.class);
                            startActivity(intent);
                        }

                        @Override
                        public void onError(String errorMessage) {
                            Toast.makeText(MainActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(getApplicationContext(), "Invalid email address", Toast.LENGTH_SHORT).show();
                }
            }
        } else if (v == buttonForgotPassword) {
            Intent intent = new Intent(this, PasswordRecoveryManager.class);
            startActivity(intent);
        }
    }
}
