package com.example.geforemapp;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;

public class LoginManager {
    private Context context;

    public LoginManager(Context context) {
        this.context = context;
    }

    public void validateLogin(String email, String password, LoginCallback callback) {
        new LoginTask(email, password, callback).execute();
    }

    private static class LoginTask extends AsyncTask<Void, Void, String> {
        private String email;
        private String password;
        private LoginCallback callback;

        LoginTask(String email, String password, LoginCallback callback) {
            this.email = email;
            this.password = password;
            this.callback = callback;
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                // Use the correct URL based on the environment (emulator or real device)
                URL url = new URL("http://10.0.2.2:8000/api/login_check"); // Emulator
                // URL url = new URL("http://<YOUR_LOCAL_IP>:8000/api/login_check"); // Real device

                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                connection.setDoOutput(true);
                connection.setDoInput(true);

                JSONObject json = new JSONObject();
                json.put("username", email);
                json.put("password", password);

                Writer writer = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream(), "UTF-8"));
                writer.write(json.toString());
                writer.close();

                int responseCode = connection.getResponseCode();
                Log.d("LoginTask", "Response Code: " + responseCode); // Log the response code

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();
                    Log.d("LoginTask", "Response: " + response.toString()); // Log the response
                    return response.toString();
                } else {
                    Log.e("LoginTask", "Error Response Code: " + responseCode);
                    return null;
                }
            } catch (Exception e) {
                Log.e("LoginTask", "Exception: " + e.getMessage());
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                try {
                    JSONObject jsonResponse = new JSONObject(result);
                    if (jsonResponse.has("token")) {
                        callback.onSuccess(jsonResponse.getString("token"));
                    } else {
                        callback.onError("Invalid response from server");
                    }
                } catch (JSONException e) {
                    callback.onError("JSON parsing error");
                }
            } else {
                callback.onError("Login failed");
            }
        }
    }

    public interface LoginCallback {
        void onSuccess(String token);
        void onError(String errorMessage);
    }
}