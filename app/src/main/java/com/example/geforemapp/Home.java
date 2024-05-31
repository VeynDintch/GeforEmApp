package com.example.geforemapp;

import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Home extends AppCompatActivity {

    private TextView userMail;
    private TextView userFirst;
    private TextView userLast;
    private TextView userSection;
    private TextView userOption;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);

        // Initialisation des vues
        userMail = findViewById(R.id.userMail);
        userFirst = findViewById(R.id.userFirst);
        userLast = findViewById(R.id.userLast);
        userSection = findViewById(R.id.userSection);
        userOption = findViewById(R.id.userOption);

        // Exécution de la tâche en arrière-plan pour charger les données
        new LoadUserDataTask().execute();
    }

    private class LoadUserDataTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            return homepage();
        }

        @Override
        protected void onPostExecute(String result) {
            infomation(result);
        }
    }

    private String homepage() {
        InputStream inputStream = null;
        BufferedReader bufferedReader = null;
        try {
            inputStream = getAssets().open("user.json");
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }
            return stringBuilder.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void infomation(String response) {
        if (response != null) {
            try {
                JSONObject jsonObject = new JSONObject(response);
                String email = jsonObject.getString("email");
                String nom = jsonObject.getString("nom");
                String prenom = jsonObject.getString("prenom");
                String section = jsonObject.getString("section");
                String option = jsonObject.getString("option");

                userMail.setText("email: " + email);
                userFirst.setText("nom: " + nom);
                userLast.setText("prenom: " + prenom);
                userSection.setText("section: " + section);
                userOption.setText("option: " + option);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}