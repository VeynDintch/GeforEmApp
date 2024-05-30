package com.example.ge;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.geforemapp.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

import EmergenceActivity1.EmergenceActivity;

public class MainActivity extends AppCompatActivity {

    // Déclaration des vues
    private EditText editTextLogin;
    private EditText editTextPassword;
    private TextView textViewMessage;
    private Button buttonResetPassword;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialisation des vues
        editTextLogin = findViewById(R.id.editTextLogin);
        editTextPassword = findViewById(R.id.editTextPassword);
        textViewMessage = findViewById(R.id.textViewMessage);
        buttonResetPassword = findViewById(R.id.buttonResetPassword);
        Button buttonLogin = findViewById(R.id.buttonLogin);

        // Lecture fichier JSON vérification identifiants sur le bouton connexion
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                String login = editTextLogin.getText().toString();
                String password = editTextPassword.getText().toString();

                // Lecture fichier JSON
                String jsonStr = loadJSONFromAsset();

                if (jsonStr != null) {
                    try {
                        // Conversion du fichier JSON en objet JSON
                        JSONObject jsonObj = new JSONObject(jsonStr);
                        // Tableau d'utilisateurs
                        JSONArray users = jsonObj.getJSONArray("users");

                        // Vérification des identifiants de l'utilisateur
                        for (int i = 0; i < users.length(); i++) {
                            JSONObject user = users.getJSONObject(i);
                            String email = user.getString("email");
                            String userPassword = user.getString("password");

                            if (login.equals(email) && password.equals(userPassword)) {
                                // Rediriger vers la page d'émergence si les identifiants sont corrects
                                Intent intent = new Intent(MainActivity.this, EmergenceActivity.class);
                                startActivity(intent);
                                return;
                            }
                        }
                        // Afficher message d'erreur si les identifiants sont incorrects
                        textViewMessage.setText("Invalid login or password");
                        textViewMessage.setVisibility(View.VISIBLE);
                        textViewMessage.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                        buttonResetPassword.setVisibility(View.VISIBLE);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        // Réinitialiser le mot de passe sur le bouton de réinitialisation
        buttonResetPassword.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                String email = editTextLogin.getText().toString();
                if (!email.isEmpty()) {
                    showResetPasswordDialog(email);
                } else {
                    textViewMessage.setText("Please enter your email");
                    textViewMessage.setVisibility(View.VISIBLE);
                    textViewMessage.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                }
                editTextPassword.setText("");
                buttonResetPassword.setVisibility(View.GONE);
            }
        });
    }

    // Méthode pour afficher une boîte de dialogue pour entrer le nouveau mot de passe
    private void showResetPasswordDialog(final String email) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Reset Password");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        builder.setView(input);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newPassword = input.getText().toString();
                if (!newPassword.isEmpty()) {
                    updatePasswordInJson(email, newPassword);
                    textViewMessage.setText("Password reset successfully");
                    textViewMessage.setVisibility(View.VISIBLE);
                    textViewMessage.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
                } else {
                    textViewMessage.setText("Password cannot be empty");
                    textViewMessage.setVisibility(View.VISIBLE);
                    textViewMessage.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                }
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    // Méthode pour mettre à jour le mot de passe dans le fichier JSON
    private void updatePasswordInJson(String email, String newPassword) {
        String jsonStr = loadJSONFromAsset();
        if (jsonStr != null) {
            try {
                JSONObject jsonObj = new JSONObject(jsonStr);
                JSONArray users = jsonObj.getJSONArray("users");

                for (int i = 0; i < users.length(); i++) {
                    JSONObject user = users.getJSONObject(i);
                    if (user.getString("email").equals(email)) {
                        // Mettre à jour le mot de passe de l'utilisateur
                        user.put("password", newPassword);
                        break;
                    }
                }

                // Écrire le JSON modifié dans le fichier (cette étape peut nécessiter des permissions d'écriture)
                writeJSONToFile(jsonObj.toString());

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    // Méthode pour écrire le contenu JSON modifié dans un fichier
    private void writeJSONToFile(String jsonStr) {
        try {
            OutputStream os = openFileOutput("redirection.json", MODE_PRIVATE);
            os.write(jsonStr.getBytes(StandardCharsets.UTF_8));
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Méthode pour lire le contenu du fichier JSON
    private String loadJSONFromAsset() {
        String json;
        try {
            InputStream is = getAssets().open("redirection.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, StandardCharsets.UTF_8);
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }
}
