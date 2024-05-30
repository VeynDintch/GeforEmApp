package com.example.geforemapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    // Déclaration des widgets
    private EditText emailEditText, passwordEditText;
    private Button submitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        // Initialisation des widgets
        emailEditText = findViewById(R.id.mail);
        passwordEditText = findViewById(R.id.pwdUser);
        submitButton = findViewById(R.id.butSum);

        // Définir le listener sur le bouton de soumission
        submitButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        // Vérification des champs de texte
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        if (email.isEmpty()) {
            Toast.makeText(getApplicationContext(), "L'email ne peut pas être vide", Toast.LENGTH_SHORT).show();
        } else if (password.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Le mot de passe ne peut pas être vide", Toast.LENGTH_SHORT).show();
        } else {
            // Si les champs ne sont pas vides, démarrer l'activité Home
            Intent intent = new Intent(MainActivity.this, Home.class);
            startActivity(intent);
            Toast.makeText(getApplicationContext(), "Méthode de connexion en cours", Toast.LENGTH_SHORT).show();
        }
    }
}