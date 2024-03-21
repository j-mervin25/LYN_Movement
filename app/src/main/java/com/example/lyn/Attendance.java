package com.example.lyn;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.TableLayout;
import android.widget.TableRow;
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

public class Attendance extends AppCompatActivity {
    String retrievedSNo;
    String  retrievedClass;
    String retrievedMedium;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance);
        retrievedSNo = DataManager.getInstance().getRetrievedSNo();

        // Log the retrieved values to ensure they are correct
        Log.d("Upload", "Sno: " + retrievedSNo);

        retrievedClass = DataManager.getInstance().getRetrievedClass();
        retrievedMedium = DataManager.getInstance().getRetrievedMedium();

        // Log the retrieved values to ensure they are correct
        Log.d("DashboardActivity", "Class Info: " + retrievedClass);
        Log.d("DashboardActivity", "Medium: " + retrievedMedium);

        TextView classInfoTextView = findViewById(R.id.classInfoTextView);
        TextView mediumTextView = findViewById(R.id.mediumTextView);

        classInfoTextView.setText("Class Info: " + retrievedClass);
        mediumTextView.setText("Medium: " + retrievedMedium);

        FetchAttendanceAsyncTask fetchAttendanceAsyncTask = new FetchAttendanceAsyncTask();
        fetchAttendanceAsyncTask.execute();
    }

    private class FetchAttendanceAsyncTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            String result = null;
            try {
                // URL to fetch data
                URL url = new URL("https://trial.gssmp.org/MobileApp/Select.php?query=select%20*%20from%20v_Attendance");

                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();

                // Read data from input stream
                InputStream inputStream = connection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line);
                }
                result = stringBuilder.toString();

                // Close streams and disconnect connection
                inputStream.close();
                connection.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                try {
                    JSONArray jsonArray = new JSONArray(result);
                    TableLayout tableLayout = findViewById(R.id.tableLayout);

                    int blueColor = getResources().getColor(R.color.llgreen);
                    int pinkColor = getResources().getColor(R.color.tad);

                    // Iterate through JSON array
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);

                        String scheduleDate = jsonObject.getString("ScheduleDate");
                        String classSubjectName = jsonObject.getString("ClassSubjectName");
                        String stat = jsonObject.getString("Stat");
                        String classID = jsonObject.getString("ClassID");
                        String medium = jsonObject.getString("Medium");


                        // Log the retrieved data for debugging
                        Log.d("Attendance", "Schedule Date: " + scheduleDate);
                        Log.d("Attendance", "Class Subject Name: " + classSubjectName);
                        Log.d("Attendance", "Stat: " + stat);
                        Log.d("Attendance", "Class ID: " + classID);
                        Log.d("Attendance", "Medium: " + medium);


                        // Create TableRow and TextViews for each data row
                        TableRow row = new TableRow(Attendance.this);

                        if (i % 2 == 0) {
                            row.setBackgroundColor(blueColor);
                        } else {
                            row.setBackgroundColor(pinkColor);
                        }

                        TextView scheduleDateTextView = new TextView(Attendance.this);
                        scheduleDateTextView.setText(scheduleDate);
                        scheduleDateTextView.setPadding(10, 0, 10, 0); // Add padding to the text view

                        TextView classSubjectTextView = new TextView(Attendance.this);
                        classSubjectTextView.setText(classSubjectName);
                        classSubjectTextView.setPadding(10, 0, 10, 0); // Add padding to the text view

                        TextView statTextView = new TextView(Attendance.this);
                        statTextView.setText(stat);
                        statTextView.setPadding(10, 0, 10, 0); // Add padding to the text view

                        if (stat.equalsIgnoreCase("Present")) {
                            statTextView.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
                        } else {
                            statTextView.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                        }

                        // Add TextViews to TableRow
                        row.addView(scheduleDateTextView);
                        row.addView(classSubjectTextView);
                        row.addView(statTextView);

                        // Add TableRow to TableLayout
                        tableLayout.addView(row);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(Attendance.this, "Error processing data", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(Attendance.this, "Failed to fetch data", Toast.LENGTH_SHORT).show();
            }
        }
    }
}