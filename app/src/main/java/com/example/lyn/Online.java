package com.example.lyn;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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

public class Online extends AppCompatActivity {
    String  retrievedClass;
    String retrievedMedium;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online);
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
                URL url = new URL("https://trial.gssmp.org/MobileApp/Select.php?query=select%20*%20from%20v_Exams");
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
                    int blueColor = getResources().getColor(R.color.glpink);
                    int pinkColor = getResources().getColor(R.color.rainday);

                    // Track row index for alternating colors
                    int rowIndex = 0;

                    // Iterate through JSON array
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);

                        // Extract all fields
                        String subjectName = jsonObject.getString("SubjectName");
                        String examName = jsonObject.getString("ExamName");
                        String examSubjectID = jsonObject.getString("ExamSubjectID");
                        String examType = jsonObject.getString("ExamType");
                        String examStartDate = jsonObject.getString("ExamStartDate");
                        String qpLink = jsonObject.optString("QPLink", "");
                        String examClass = jsonObject.optString("Class", "");
                        String examMedium = jsonObject.optString("Medium", "");


                        if (retrievedMedium.equals(examMedium) && retrievedClass.equals(examClass) && ("Evalbee".equals(examType) || "MCQ".equals(examType))) {

                            // Create TableRow and TextViews for each data row
                            TableRow row = new TableRow(Online.this);

                            // Set background color based on row index
                            if (rowIndex % 2 == 0) {
                                row.setBackgroundColor(blueColor);
                            } else {
                                row.setBackgroundColor(pinkColor);
                            }


                            // Create TextViews for each column and set padding
                            TextView subjectNameTextView = new TextView(Online.this);
                            subjectNameTextView.setText(subjectName);
                            subjectNameTextView.setPadding(10, 10, 10, 10); // Set padding for the TextView

                            TextView examNameTextView = new TextView(Online.this);
                            examNameTextView.setText(examName);
                            examNameTextView.setPadding(10, 10, 10, 10); // Set padding for the TextView

                            TextView examDateTextView = new TextView(Online.this);
                            examDateTextView.setText(examStartDate);
                            examDateTextView.setPadding(10, 10, 10, 10); // Set padding for the TextView

                            // Set the tag for the row
                            row.setTag(qpLink);

                            // Add TextViews to the TableRow
                            row.addView(subjectNameTextView);
                            row.addView(examNameTextView);
                            row.addView(examDateTextView);

                            // Increment row index
                            rowIndex++;

                            // Add TableRow to the TableLayout
                            tableLayout.addView(row);

                            // Add an empty row after each data row
                            TableRow emptyRow = new TableRow(Online.this);
                            emptyRow.setMinimumHeight(20); // Adjust the height of the empty row as needed
                            tableLayout.addView(emptyRow);


                            row.setOnClickListener(new View.OnClickListener() {
                                public void onClick(View v) {

                                    String qpLink = (String) v.getTag();

                                    Log.d("Online", "QPLink: " + qpLink);

                                    if ("Evalbee".equals(examType) || "MCQ".equals(examType)) {
                                        if (!qpLink.isEmpty()) {
                                            // Open QPLink in WebView
                                            // Inside the FetchDataAsyncTask onPostExecute method
                                            Intent intent = new Intent(Online.this, OnlineWebview.class);
                                            intent.putExtra("url", qpLink);
                                            Log.d("Online", "URL: " + qpLink);
                                            startActivity(intent);

                                        } else {
                                            // Show a message that QPLink is empty
                                            Toast.makeText(Online.this, "QPLink is empty for this event", Toast.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        // Perform default action or show a message that QPLink is not available for this event type
                                        Toast.makeText(Online.this, "QPLink is not available for this event type", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(Online.this, "Error processing data", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(Online.this, "Failed to fetch data", Toast.LENGTH_SHORT).show();
            }
        }
    }
}