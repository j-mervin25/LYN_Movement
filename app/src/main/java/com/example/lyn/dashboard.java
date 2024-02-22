package com.example.lyn;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
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

public class dashboard extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        FetchDataAsyncTask fetchDataAsyncTask = new FetchDataAsyncTask();
        fetchDataAsyncTask.execute();
    }

    private class FetchDataAsyncTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            String result = null;
            try {
                // Create URL object
                URL url = new URL("https://trial.gssmp.org/MobileApp/Select.php?query=select%20*%20from%20v_EventDashboard");

                // Create HttpURLConnection
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                // Connect to the URL
                connection.connect();

                // Read data from the URL
                InputStream inputStream = connection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line);
                }
                result = stringBuilder.toString();

                // Close InputStream and HttpURLConnection
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

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String eventType = jsonObject.getString("type"); // Assuming the eventType is obtained from JSON
                        String eventName = jsonObject.getString("EventName");
                        String eventTime = jsonObject.getString("EventTime");

                        // Create a new table row
                        final TableRow tableRow = new TableRow(dashboard.this);

                        // Create text views for each column
                        TextView nameTextView = new TextView(dashboard.this);
                        nameTextView.setText(eventName);
                        nameTextView.setPadding(10, 10, 10, 10);

                        TextView timeTextView = new TextView(dashboard.this);
                        timeTextView.setText(eventTime);
                        timeTextView.setPadding(10, 10, 10, 10);

                        // Set background color based on eventType
                        if (eventType.equals("C")) {
                            tableRow.setBackgroundColor(getResources().getColor(android.R.color.holo_purple)); // Pink color
                        } else if (eventType.equals("T")) {
                            tableRow.setBackgroundColor(getResources().getColor(android.R.color.holo_orange_light)); // Yellow color
                        } else if (eventType.equals("A")) {
                            tableRow.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_light)); // Blue color
                        }

                        // Add text views to the table row
                        tableRow.addView(nameTextView);
                        tableRow.addView(timeTextView);

                        // Add the table row to the table layout
                        tableLayout.addView(tableRow);

                        // Set OnClickListener for the TableRow
                        tableRow.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                // Perform your action here when the TableRow is clicked
                                // For example, you can display a Toast message
                                TextView eventNameTextView = (TextView) tableRow.getChildAt(0); // Assuming eventName is the first column
                                String eventName = eventNameTextView.getText().toString();
                                Toast.makeText(dashboard.this, "Clicked on event: " + eventName, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(dashboard.this, "Error processing data", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(dashboard.this, "Failed to fetch data", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
