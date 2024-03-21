package com.example.lyn;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class tamildashboard extends AppCompatActivity {

    private String startTimeString;
    private String endTimeString;
    private long startTime;
    private long endTime;
    private String id;
    String retrievedFirstName;
    String retrievedSNo;
    String  retrievedClass;
    String retrievedMedium;
    private String examSubjectid;
    private boolean isMeetingClicked = false;
    private TextView startTimeTextView;
    private TextView endTimeTextView;

    ImageView i1,i2,i3,i4,i5,i6,i7,i8;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tamildashboard);

        retrievedClass = DataManager.getInstance().getRetrievedClass();
        retrievedMedium = DataManager.getInstance().getRetrievedMedium();

        // Log the retrieved values to ensure they are correct
        Log.d("DashboardActivity", "Class Info: " + retrievedClass);
        Log.d("DashboardActivity", "Medium: " + retrievedMedium);

        startTimeTextView = findViewById(R.id.startTimeTextView);
        endTimeTextView = findViewById(R.id.endTimeTextView);

        retrievedSNo = DataManager.getInstance().getRetrievedSNo();

        // Log the retrieved values to ensure they are correct
        Log.d("Upload", "Sno: " + retrievedSNo);

        retrievedFirstName = DataManager.getInstance().getRetrievedFirstName();


        Log.d("DashboardActivity", "FirstName: " + retrievedFirstName);

        TextView mediumTextView = findViewById(R.id.textView);

        mediumTextView.setText("Welcome " + retrievedFirstName);



        FetchDataAsyncTask fetchDataAsyncTask = new FetchDataAsyncTask();
        fetchDataAsyncTask.execute();

        i1= findViewById(R.id.imageView2);
        i2= findViewById(R.id.imageView10);
        i3= findViewById(R.id.imageView3);
        i4= findViewById(R.id.imageView4);
        i5= findViewById(R.id.imageView23);
        i6= findViewById(R.id.imageView16);
        i7= findViewById(R.id.imageView14);
        i8= findViewById(R.id.imageView12);
        i1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(tamildashboard.this, Classrooms.class);
                startActivity(i);

            }
        });
        i2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(tamildashboard.this, Mentors.class);
                startActivity(i);

            }
        });
        i3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(tamildashboard.this, Online.class);
                startActivity(i);

            }
        });
        i4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(tamildashboard.this, Offline.class);
                startActivity(i);

            }
        });
        i5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(tamildashboard.this, ProfileActivity.class);
                startActivity(i);

            }
        });
        i6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(tamildashboard.this, Attendance.class);
                startActivity(i);

            }
        });
        i7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(tamildashboard.this, youtube.class);
                startActivity(i);

            }
        });
        i8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(tamildashboard.this, material.class);
                startActivity(i);

            }
        });
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

                        String eventClass = jsonObject.getString("Class");
                        String eventMedium = jsonObject.getString("Medium");

                        // Check if the event matches the desired class and medium
                        if (eventClass.equals(retrievedClass) && eventMedium.equals(retrievedMedium)) {
                            String eventType = jsonObject.getString("type"); // Assuming the eventType is obtained from JSON
                            String eventName = jsonObject.getString("EventName");
                            String eventTime = jsonObject.getString("EventTime");
                            String meetingurl = jsonObject.getString("URL");
                            final String eventId = jsonObject.getString("ID"); // Declare id as final here
                            id = eventId;
                            Log.d("Meeting2", "ID: " + id);

                            // Create a new table row
                            final TableRow tableRow = new TableRow(tamildashboard.this);
                            TextView nameTextView = new TextView(tamildashboard.this);
                            nameTextView.setText(eventName);
                            nameTextView.setPadding(10, 10, 10, 10);

                            TextView timeTextView = new TextView(tamildashboard.this);
                            timeTextView.setText(eventTime);
                            timeTextView.setPadding(10, 10, 10, 10);

                            if (eventType.equals("C")) {
                                tableRow.setBackgroundColor(getResources().getColor(android.R.color.holo_purple));
                            } else if (eventType.equals("E")) {
                                tableRow.setBackgroundColor(getResources().getColor(android.R.color.holo_orange_light));
                            } else if (eventType.equals("A")) {
                                tableRow.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_light));
                            } else if (eventType.equals("D")) {
                                tableRow.setBackgroundColor(getResources().getColor(R.color.gred));
                            } else if (eventType.equals("M")) {
                                tableRow.setBackgroundColor(getResources().getColor(R.color.rainday)); // Assign a color for event type "M"
                            }

                            tableRow.addView(nameTextView);
                            tableRow.addView(timeTextView);
                            tableLayout.addView(tableRow);

                            tableRow.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    TextView eventNameTextView = (TextView) tableRow.getChildAt(0); // Assuming eventName is the first column
                                    String eventName = eventNameTextView.getText().toString();

                                    // Get the eventType from the TableRow's background color
                                    int color = ((ColorDrawable) tableRow.getBackground()).getColor();
                                    String eventType = getEventType(color);
                                    String clickedId = eventId;

                                    // Start the corresponding activity based on eventType
                                    if (eventType.equals("C")) {
                                        if (!meetingurl.isEmpty()) {
                                            redirectToGoogleMeet(meetingurl);
                                            isMeetingClicked = true;
                                            startTime = System.currentTimeMillis();
                                            Log.d("Meeting", "Start Time: " + startTime);
                                            updateStartEndTimeViews();
                                            id = eventId;
                                            Log.d("Meeting3", "Event ID: " + id);

                                        } else {
                                            Toast.makeText(tamildashboard.this, "Meeting is not available for the event: " + eventName, Toast.LENGTH_SHORT).show();
                                        }
                                    } else if (eventType.equals("A")) {
                                        if (!meetingurl.isEmpty()) {
                                            Intent intent = new Intent(tamildashboard.this, Assignment.class);
                                            intent.putExtra("url", meetingurl);
                                            DataManager.getInstance().setId(id);
                                            Log.d("Meeting", "ID: " + id);
                                            ;
                                            startActivity(intent);
                                        } else {
                                            Toast.makeText(tamildashboard.this, "URL is not available for the event: " + eventName, Toast.LENGTH_SHORT).show();
                                        }
                                    } else if (eventType.equals("D")) {
                                        if (!meetingurl.isEmpty()) {
                                            // Open QPLink in next activity
                                            Intent intent = new Intent(tamildashboard.this, OfflineWebview.class);
                                            intent.putExtra("url", meetingurl);
                                            Log.d("Online", "URL: " + meetingurl);
                                            examSubjectid = DataManager.getInstance().getExamSubjectid();
                                            Log.d("Meeting", "ExamSubjectId: " + examSubjectid); // Log the ID for event type A
                                            startActivity(intent);
                                        } else {
                                            // Show a message that QPLink is empty
                                            Toast.makeText(tamildashboard.this, "QPLink is empty for this event", Toast.LENGTH_SHORT).show();
                                        }
                                    } else if (eventType.equals("E") || eventType.equals("M")) {
                                        if (!meetingurl.isEmpty()) {
                                            // Open QPLink in WebView
                                            // Inside the FetchDataAsyncTask onPostExecute method
                                            Intent intent = new Intent(tamildashboard.this, OnlineWebview.class);
                                            intent.putExtra("url", meetingurl);
                                            Log.d("Online", "URL: " + meetingurl);
                                            startActivity(intent);

                                        } else {
                                            // Show a message that QPLink is empty
                                            Toast.makeText(tamildashboard.this, "QPLink is empty for this event", Toast.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        // Perform default action or show a message that QPLink is not available for this event type
                                        Toast.makeText(tamildashboard.this, "QPLink is not available for this event type", Toast.LENGTH_SHORT).show();

                                    }
                                }
                            });
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(tamildashboard.this, "Error processing data", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(tamildashboard.this, "Failed to fetch data", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private String convertMillisToDateTime(long millis) {
        Date date = new Date(millis);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return sdf.format(date);
    }
    private void redirectToGoogleMeet(String meetingurl){
        try{
            Intent browserIntent = new Intent(Intent.ACTION_VIEW,Uri.parse(meetingurl));
            startActivity(browserIntent);
        }catch(ActivityNotFoundException e){
            Toast.makeText(tamildashboard.this, "No app can handle this action", Toast.LENGTH_SHORT).show();
        }
    }

    protected void onResume() {
        super.onResume();
        if (isMeetingClicked) {
            endTime = System.currentTimeMillis(); // Update end time when activity resumes
            Log.d("Meeting", "End Time: " + endTime);

            // Reset the flag for the next meeting
            isMeetingClicked = false;
            updateStartEndTimeViews();  // Update the TextViews with start and end times

            endTimeString = convertMillisToDateTime(endTime); // Assign value to class-level field
            startTimeString = convertMillisToDateTime(startTime); // Assign value to class-level field


            // Check if start time and end time strings are not null or empty before executing the task
            if (startTimeString != null && !startTimeString.isEmpty() && endTimeString != null && !endTimeString.isEmpty() && id != null) {
                InsertAttendanceAsyncTask insertAttendanceAsyncTask = new InsertAttendanceAsyncTask(startTime, endTime, id);
                insertAttendanceAsyncTask.execute();
            } else {
                // Handle the case where start time or end time strings are not valid
                Toast.makeText(tamildashboard.this, "Failed to log attendance: Start or end time is invalid", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private class InsertAttendanceAsyncTask extends AsyncTask<Void, Void, Boolean> {

        private long startTime;
        private String id;


        public InsertAttendanceAsyncTask(long startTime, long endTime, String id) {
            this.startTime = startTime;
            this.id = id;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                // Create a URL object with the endpoint for inserting data into AttendanceLog
                URL url = new URL("https://trial.gssmp.org/MobileApp/Insert.php");

                // Create a HttpURLConnection object
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                // Set the request method to POST
                connection.setRequestMethod("POST");

                // Enable writing data to the connection output stream
                connection.setDoOutput(true);

                // Create a parameter string to send attendance data
                // Create a parameter string to send attendance data
                String parameters = "query=INSERT INTO AttendanceLog (ScheduleID, StudentID, ActivityTime, ActivityDesc) VALUES ('"+id+"', '"+retrievedSNo+"', '" + startTimeString + "', 'SignedIn')";

                Log.d("InsertAttendance", "Parameters: " + parameters);
                // Write the parameters to the connection output stream
                connection.getOutputStream().write(parameters.getBytes());


                // Get the response code
                int responseCode = connection.getResponseCode();

                Log.d("InsertAttendance", "Response Code: " + responseCode);

                // Check if the insertion was successful (200 indicates success)
                return responseCode == HttpURLConnection.HTTP_OK;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            // Check if the insertion was successful and show a toast accordingly
            if (success) {
                Toast.makeText(tamildashboard.this, "Attendance logged successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(tamildashboard.this, "Failed to log attendance", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void updateStartEndTimeViews() {
        // Convert start and end times to human-readable format
        String startTimeString = convertMillisToDateTime(startTime);
        String endTimeString = convertMillisToDateTime(endTime);
        // Set the converted times to TextViews
        startTimeTextView.setText("Start Time: " + startTimeString);
        endTimeTextView.setText("End Time: " + endTimeString);
    }

    // Convert milliseconds to human-readable date and time format


    private String getEventType(int color) {
        if (color == getResources().getColor(android.R.color.holo_purple)) {
            return "C";
        } else if (color == getResources().getColor(android.R.color.holo_blue_light)) {
            return "A";
        } else if (color == getResources().getColor(android.R.color.holo_orange_light)) {
            return "E";
        } else if (color == getResources().getColor(R.color.gred)) {
            return "D";
        } else if (color == getResources().getColor(R.color.rainday)) {
            return "M";
        } else {
            return "";
        }
    }
}

