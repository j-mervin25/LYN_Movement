package com.example.lyn;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.os.AsyncTask;
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

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;

public class ProfileActivity extends AppCompatActivity {
    TextView t1;
    private static final String PHP_SCRIPT_URL = "https://trial.gssmp.org/MobileApp/Select.php?query=SELECT * FROM Students";
    private TextView t2, t3, t4, t5, t6, t7;
    ImageButton b1,b2;

    ImageView profileImageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        t1= findViewById(R.id.textView);
        t2 = findViewById(R.id.textView1);
        t3 = findViewById(R.id.textView2);
        t4 = findViewById(R.id.textView3);
        t5 = findViewById(R.id.textView4);
        t6 = findViewById(R.id.textView5);
        t7 = findViewById(R.id.textView6);

        b1 = findViewById(R.id.button1);
        b2 = findViewById(R.id.button2);

        profileImageView = findViewById(R.id.profileImageView);

        String email = getIntent().getStringExtra("email");

        // Set email to TextView
        t1.setText(email);

        setProfileImage(email);

        FetchDataAsyncTask fetchDataAsyncTask = new FetchDataAsyncTask();
        fetchDataAsyncTask.execute();

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ProfileActivity.this, SecondActivity.class);
                startActivity(i);
            }
        });
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String medium = t4.getText().toString().trim();
                if ("Medium: Tamil".equalsIgnoreCase(medium)) {
                    Intent intent = new Intent(ProfileActivity.this, tamildashboard.class);
                    startActivity(intent);
                } else if ("Medium: English".equalsIgnoreCase(medium)) {
                    Intent intent = new Intent(ProfileActivity.this, dashboard.class);
                    startActivity(intent);
                } else {
                    // Handle other cases if needed
                    Toast.makeText(ProfileActivity.this, "Unknown medium: " + medium, Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void setProfileImage(String email) {
        // Extract the first letter of the email
        String firstLetter = email.substring(0, 1).toUpperCase();

        // Create a bitmap with the first letter
        Bitmap bitmap = textAsBitmap(firstLetter, 100, Color.WHITE);

        // Set the bitmap to the ImageView
        profileImageView.setImageBitmap(bitmap);
    }

    private Bitmap textAsBitmap(String text, float textSize, int textColor) {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setTextSize(textSize);
        paint.setColor(textColor);
        paint.setTypeface(Typeface.DEFAULT_BOLD);

        float baseline = -paint.ascent(); // ascent() is negative
        int width = (int) (paint.measureText(text) + 0.5f); // round
        int height = (int) (baseline + paint.descent() + 0.5f);

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawText(text, 0, baseline, paint);
        return bitmap;
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

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                // Parse JSON response
                try {
                    JSONArray jsonArray = new JSONArray(result);
                    String emailToFind = t1.getText().toString().trim();

                    // Search for the email in the JSON array
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String email = jsonObject.getString("emailid");

                        if (emailToFind.equals(email)) {
                            // Extract details from JSON object
                            String firstName = jsonObject.getString("FirstName");
                            String lastName = jsonObject.getString("LastName");
                            String medium = jsonObject.getString("Medium");
                            String classInfo = jsonObject.getString("Class");
                            String sno = jsonObject.getString("SNo");
                            String mentorID = jsonObject.getString("MentorID");


                            // Update TextViews with details
                            t2.setText("First Name: " + firstName);
                            t3.setText("Last Name: " + lastName);
                            t4.setText("Medium: " + medium);
                            t5.setText("Class: " + classInfo);
                            t6.setText("SNo: " + sno);
                            t7.setText("Mentor ID: " + mentorID);


                            DataManager.getInstance().setClassInfo(classInfo);
                            DataManager.getInstance().setMedium(medium);

                            Log.d("ProfileActivity", "Class Info: " + classInfo);
                            Log.d("ProfileActivity", "Medium: " + medium);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(ProfileActivity.this, "Failed to fetch data", Toast.LENGTH_SHORT).show();
            }
        }
    }
}