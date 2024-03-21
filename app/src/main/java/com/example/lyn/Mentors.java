package com.example.lyn;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.view.View;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class Mentors extends AppCompatActivity {
    private static final String PHP_SCRIPT_URL = "https://trial.gssmp.org/MobileApp/Select.php?query=select%20*%20from%20Mentors";
    TextView t1, t2, t3, t4;
    String retrievedMentorID;
    FloatingActionButton callbtn;
    static int PERMISSION_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mentors);

        t1 = findViewById(R.id.textView);
        t2 = findViewById(R.id.textView1);
        t3 = findViewById(R.id.textView2);
        t4 = findViewById(R.id.textView3);

        retrievedMentorID = DataManager.getInstance().getRetrievedMentorID();

        // Log the retrieved values to ensure they are correct
        Log.d("Upload", "MentorID: " + retrievedMentorID);

        // Execute the AsyncTask to fetch mentor details
        new FetchDataAsyncTask().execute();

        t3 = findViewById(R.id.textView2);
        callbtn=findViewById(R.id.floatingActionButton3);

        callbtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String phoneno = t3.getText().toString();
                Intent i = new Intent(Intent.ACTION_CALL);
                i.setData(Uri.parse("tel:" + phoneno));
                startActivity(i);
            }
        });

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, PERMISSION_CODE);
        }
    }

    private class FetchDataAsyncTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            try {
                URL url = new URL(PHP_SCRIPT_URL);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();

                InputStream inputStream = connection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line);
                }
                return stringBuilder.toString();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        protected void onPostExecute(String result) {
            if (result != null) {
                try {
                    JSONArray jsonArray = new JSONArray(result);
                    boolean mentorFound = false; // Flag to track if mentor with matching ID is found

                    // Iterate over the JSON array of mentors
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String MentorID = jsonObject.getString("MentorID");

                        // Check if fetched MentorID matches the desired MentorID
                        if (MentorID.equals(retrievedMentorID)) {
                            mentorFound = true;
                            // Extract other details from JSON object
                            String MentorName = jsonObject.getString("MentorName");
                            String MentorPhoneNo = jsonObject.getString("MentorPhoneNo");
                            String MentorEmail = jsonObject.getString("MentorEmail");

                            // Update TextViews with details
                            t1.setText(MentorID);
                            t2.setText(MentorName);
                            t3.setText(MentorPhoneNo);
                            t4.setText(MentorEmail);

                            // Exit the loop as we found the desired mentor
                            break;
                        }
                    }

                    // If mentor with matching ID is not found, display a message
                    if (!mentorFound) {
                        Toast.makeText(Mentors.this, "Mentor with ID " + retrievedMentorID + " not found", Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(Mentors.this, "Failed to fetch data", Toast.LENGTH_SHORT).show();
            }
        }
    }
}