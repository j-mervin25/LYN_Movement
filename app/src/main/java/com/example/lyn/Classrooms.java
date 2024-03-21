package com.example.lyn;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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

public class Classrooms extends AppCompatActivity {

    String  retrievedClass;
    String retrievedMedium;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classrooms);

// Retrieve classInfo and medium from DataManager
        retrievedClass = DataManager.getInstance().getRetrievedClass();
        retrievedMedium = DataManager.getInstance().getRetrievedMedium();

        // Log the retrieved values to ensure they are correct
        Log.d("DashboardActivity", "Class Info: " + retrievedClass);
        Log.d("DashboardActivity", "Medium: " + retrievedMedium);

        // Display classInfo and medium in TextViews
        TextView classInfoTextView = findViewById(R.id.classInfoTextView);
        TextView mediumTextView = findViewById(R.id.mediumTextView);

        classInfoTextView.setText("Class Info: " + retrievedClass);
        mediumTextView.setText("Medium: " + retrievedMedium);


        // Call AsyncTask to fetch data
        FetchDataAsyncTask fetchDataAsyncTask = new FetchDataAsyncTask();
        fetchDataAsyncTask.execute();
    }

    private class FetchDataAsyncTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            String result = null;
            try {
                // URL to fetch data
                URL url = new URL("https://trial.gssmp.org/MobileApp/Select.php?query=select%20*%20from%20ClassSubject");
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
                    // Parse JSON response
                    JSONArray jsonArray = new JSONArray(result);
                    TableLayout tableLayout = findViewById(R.id.tableLayout);

                    // Define colors
                    int blueColor = getResources().getColor(R.color.material);
                    int pinkColor = getResources().getColor(R.color.onion_color);

                    // Track row index for alternating colors
                    int rowIndex = 0;

                    // Iterate through JSON array
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);

                        // Extract all fields
                        String classSubjectID = jsonObject.getString("ClassSubjectID");
                        String subjectID = jsonObject.getString("SubjectID");
                        String classID = jsonObject.getString("ClassID");
                        String classSubjectName = jsonObject.getString("ClassSubjectName");
                        String medium = jsonObject.getString("Medium");
                        String meetingID = jsonObject.getString("MeetingID");
                        String startTime = jsonObject.getString("StartTime");
                        String endTime = jsonObject.getString("EndTime");
                        String academicYear = jsonObject.getString("AcademicYear");

                        if (retrievedClass.equals(classID) && Classrooms.this.retrievedMedium.equals(medium)) {
                            // Create TableRow and TextViews for each data row
                            TableRow row = new TableRow(Classrooms.this);


                            row.setMinimumHeight(80);

                            // Set background color based on row index
                            if (rowIndex % 2 == 0) {
                                row.setBackgroundColor(blueColor);
                            } else {
                                row.setBackgroundColor(pinkColor);
                            }

                            // Create TextViews for each column
                            TextView classSubjectNameTextView = new TextView(Classrooms.this);
                            classSubjectNameTextView.setText(classSubjectName);
                            classSubjectNameTextView.setPadding(10, 10, 10, 10); // Set padding for the TextView

                            TextView startTimeTextView = new TextView(Classrooms.this);
                            startTimeTextView.setText(startTime);
                            startTimeTextView.setPadding(10, 10, 10, 10); // Set padding for the TextView

                            TextView endTimeTextView = new TextView(Classrooms.this);
                            endTimeTextView.setText(endTime);
                            endTimeTextView.setPadding(10, 10, 10, 10); // Set padding for the TextView

                            TableRow emptyRow = new TableRow(Classrooms.this);
                            emptyRow.setMinimumHeight(20); // Adjust the height of the empty row as needed
                            tableLayout.addView(emptyRow);


                            // Add click listener to the row
                            row.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    // Open URL associated with meetingID
                                    String url = "https://meet.google.com/" + meetingID;
                                    Intent intent = new Intent(Intent.ACTION_VIEW);
                                    intent.setData(Uri.parse(url));
                                    startActivity(intent);
                                }
                            });

                            row.addView(classSubjectNameTextView);
                            row.addView(startTimeTextView);
                            row.addView(endTimeTextView);


                            // Increment row index
                            rowIndex++;

                            // Add TableRow to TableLayout
                            tableLayout.addView(row);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(Classrooms.this, "Error processing data", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(Classrooms.this, "Failed to fetch data", Toast.LENGTH_SHORT).show();
            }
        }
    }
}