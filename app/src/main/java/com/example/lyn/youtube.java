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

public class youtube extends AppCompatActivity {
    String  retrievedClass;
    String retrievedMedium;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_youtube);
        retrievedClass = DataManager.getInstance().getRetrievedClass();
        retrievedMedium = DataManager.getInstance().getRetrievedMedium();

        // Log the retrieved values to ensure they are correct
        Log.d("DashboardActivity", "Class Info: " + retrievedClass);
        Log.d("DashboardActivity", "Medium: " + retrievedMedium);


        // Call AsyncTask to fetch data
        FetchDataAsyncTask fetchDataAsyncTask = new FetchDataAsyncTask();
        fetchDataAsyncTask.execute();
    }

    private class FetchDataAsyncTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            String result = null;
            try {
                // URL to fetch data from ConfigTable
                URL url = new URL("https://trial.gssmp.org/MobileApp/Select.php?query=select%20*%20from%20ConfigTable");
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

                    // Iterate through JSON array
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);

                        // Extract relevant fields from JSON object
                        final String configID = jsonObject.getString("ConfigID");
                        final String configKey = jsonObject.getString("ConfigKey");
                        final String configValue = jsonObject.getString("ConfigValue");

                        // Create a new TableRow to display the data
                        final TableRow row = new TableRow(youtube.this);

                        // Set background color based on row index
                        if (i % 2 == 0) {
                            row.setBackgroundColor(blueColor);
                        } else {
                            row.setBackgroundColor(pinkColor);
                        }

                        // Create TextViews to display configID, configKey, and configValue
                        TextView idTextView = new TextView(youtube.this);
                        idTextView.setText("Config ID: " + configID);
                        idTextView.setPadding(10, 10, 10, 10);

                        TextView keyTextView = new TextView(youtube.this);
                        keyTextView.setText("Config Key: " + configKey);
                        keyTextView.setPadding(10, 10, 10, 10);

                        // Add TextViews to the TableRow
                        row.addView(idTextView);
                        row.addView(keyTextView);

                        // Add the TableRow to the TableLayout
                        tableLayout.addView(row);

                        TableRow emptyRow = new TableRow(youtube.this);
                        emptyRow.setMinimumHeight(20); // Adjust the height of the empty row as needed
                        tableLayout.addView(emptyRow);

                        // Set an OnClickListener for the TableRow
                        row.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                // Get the URL from configValue and pass it to the WebViewActivity
                                Intent intent = new Intent(youtube.this, youweb.class);
                                intent.putExtra("url", configValue);
                                startActivity(intent);
                            }
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(youtube.this, "Error processing data", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(youtube.this, "Failed to fetch data", Toast.LENGTH_SHORT).show();
            }
        }
    }
}