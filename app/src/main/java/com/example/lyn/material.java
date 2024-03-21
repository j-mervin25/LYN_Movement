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


public class material extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_material);

        FetchAttendanceAsyncTask fetchAttendanceAsyncTask = new FetchAttendanceAsyncTask();
        fetchAttendanceAsyncTask.execute();
    }

    private class FetchAttendanceAsyncTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            String result = null;
            try {
                // URL to fetch data
                URL url = new URL("https://trial.gssmp.org/MobileApp/Select.php?query=select%20*%20from%20v_StudyMaterials");
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

                    // Iterate through JSON array
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);

                        int blueColor = getResources().getColor(R.color.agreen);
                        int pinkColor = getResources().getColor(R.color.ayellow);

                        // Modify this part according to your JSON structure
                        String subjectName = jsonObject.getString("SubjectName");
                        String fileID = jsonObject.getString("FileID");
                        String classSubjectID = jsonObject.getString("ClassSubjectID");
                        String materialID = jsonObject.getString("MaterialID");
                        String classID = jsonObject.getString("ClassID");
                        String fileLocation = jsonObject.getString("FileLocation");

                        // Create TableRow and TextViews for each data row
                        TableRow row = new TableRow(material.this);

                        row.setClickable(true);
                        row.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                // Create intent to launch the WebView activity
                                Intent intent = new Intent(material.this, materialweb.class);
                                // Pass the link to the WebView activity
                                intent.putExtra("fileLocation", fileLocation);
                                // Start the activity
                                startActivity(intent);
                            }
                        });

                        TextView subjectNameTextView = new TextView(material.this);
                        subjectNameTextView.setText(subjectName);
                        subjectNameTextView.setPadding(10, 0, 10, 0); // Add padding to the text view



                        TextView materialIDTextView = new TextView(material.this);
                        materialIDTextView.setText(materialID);
                        materialIDTextView.setPadding(10, 0, 10, 0); // Add padding to the text view

                        TextView classIDTextView = new TextView(material.this);
                        classIDTextView.setText(classID);
                        classIDTextView.setPadding(10, 0, 10, 0); // Add padding to the text view



                        // Add TextViews to TableRow
                        row.addView(subjectNameTextView);
                        row.addView(materialIDTextView);
                        row.addView(classIDTextView);

                        // Set row color based on odd or even position
                        if (i % 2 == 0) {
                            row.setBackgroundColor(blueColor);
                        } else {
                            row.setBackgroundColor(pinkColor);
                        }



                        // Add TableRow to TableLayout
                        tableLayout.addView(row);

                        TableRow emptyRow = new TableRow(material.this);
                        emptyRow.setMinimumHeight(20); // Adjust the height of the empty row as needed
                        tableLayout.addView(emptyRow);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(material.this, "Error processing data", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(material.this, "Failed to fetch data", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
