package com.example.lyn;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.BeginSignInResult;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Login extends AppCompatActivity {

    private SignInClient oneTapClient;
    private BeginSignInRequest signUpRequest;
    private static final int REQ_ONE_TAP = 2;
    private boolean showOneTapUI = true;

    private TextView t1;

    SignInButton signInButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        SignInButton signInButton = findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);

        oneTapClient = Identity.getSignInClient(this);
        signUpRequest = BeginSignInRequest.builder()
                .setGoogleIdTokenRequestOptions(BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                        .setSupported(true)
                        // Your server's client ID, not your Android client ID.
                        .setServerClientId("219542334215-b3jcb2foa4jctvkm4o1cb1o54vi6j83j.apps.googleusercontent.com")
                        // Show all accounts on the device.
                        .setFilterByAuthorizedAccounts(false)
                        .build())
                .build();

        ActivityResultLauncher<IntentSenderRequest> activityResultLauncher =
                registerForActivityResult(new ActivityResultContracts.StartIntentSenderForResult(), new ActivityResultCallback<ActivityResult>(){
                    public void onActivityResult(ActivityResult result){
                        try {
                            SignInCredential credential = oneTapClient.getSignInCredentialFromIntent(result.getData());
                            String idToken = credential.getGoogleIdToken();
                            if (idToken !=  null) {
                                // Got an ID token from Google. Use it to authenticate
                                // with your backend.
                                String email = credential.getId();
                                String username = credential.getDisplayName();


                                Log.d("TAG", "Got ID token.");

                                // Validate the email with your server
                                validateEmailWithServer(email);
                            }
                        } catch (ApiException e) {
                            e.printStackTrace();
                        }
                    }
                });



        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                oneTapClient.beginSignIn(signUpRequest)
                        .addOnSuccessListener(Login.this, new OnSuccessListener<BeginSignInResult>() {
                            @Override
                            public void onSuccess(BeginSignInResult result) {
                                IntentSenderRequest intentSenderRequest = new IntentSenderRequest.Builder(result.getPendingIntent().getIntentSender()).build();

                                activityResultLauncher.launch(intentSenderRequest);

                            }
                        })
                        .addOnFailureListener(Login.this, new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // No Google Accounts found. Just continue presenting the signed-out UI.
                                Log.d("TAG", e.getLocalizedMessage());
                            }
                        });
            }
        });
    }
    private void handleGoogleSignInResult(Intent data) {
        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
        try {
            GoogleSignInAccount account = task.getResult(ApiException.class);
            String email = account.getEmail();
            validateEmailWithServer(email);
        } catch (ApiException e) {
            Log.e("TAG", "Failed to sign in with Google", e);
            showToast("Failed to sign in with Google");
        }
    }

    private void handleOneTapSignInResult(Intent data) {
        try {
            SignInCredential credential = oneTapClient.getSignInCredentialFromIntent(data);
            String email = credential.getId();
            validateEmailWithServer(email);
        } catch (Exception e) {
            Log.e("TAG", "Error processing One Tap response", e);
            showToast("Error processing One Tap response");
        }
    }


    private void validateEmailWithServer(String email) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://trial.gssmp.org/MobileApp/Select.php?query=SELECT emailid, medium, FirstName, LastName, SNo, Class, GroupID, MentorID, RegID FROM Students WHERE emailid='" + email + "'";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Response", response); // Log the response here
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            if (jsonArray.length() > 0) {
                                JSONObject jsonObject = jsonArray.getJSONObject(0);
                                String retrievedSNo = jsonObject.getString("SNo");
                                String retrievedFirstName = jsonObject.getString("FirstName");
                                String retrievedLastName = jsonObject.getString("LastName");
                                String retrievedEmail = jsonObject.getString("emailid");
                                String retrievedMedium = jsonObject.getString("medium");
                                String retrievedClass = jsonObject.getString("Class");
                                String retrievedGroupID = jsonObject.getString("GroupID");
                                String retrievedMentorID = jsonObject.getString("MentorID");
                                String retrievedRegID = jsonObject.getString("RegID");

                                DataManager.getInstance().setRetrievedSNo(retrievedSNo);
                                DataManager.getInstance().setRetrievedFirstName(retrievedFirstName);
                                DataManager.getInstance().setRetrievedLastName(retrievedLastName);
                                DataManager.getInstance().setRetrievedEmail(retrievedEmail);
                                DataManager.getInstance().setRetrievedMedium(retrievedMedium);
                                DataManager.getInstance().setRetrievedClass(retrievedClass);
                                DataManager.getInstance().setRetrievedGroupID(retrievedGroupID);
                                DataManager.getInstance().setRetrievedMentorID(retrievedMentorID);
                                DataManager.getInstance().setRetrievedRegID(retrievedRegID);



                                // Check the retrieved medium and navigate accordingly
                                if (retrievedMedium.equalsIgnoreCase("English")) {
                                    startActivity(new Intent(Login.this, dashboard.class));
                                } else if (retrievedMedium.equalsIgnoreCase("Tamil")) {
                                    startActivity(new Intent(Login.this, tamildashboard.class));
                                } else {
                                    showToast("There is no meeting");
                                }
                            } else {
                                // Handle the case where no data is returned
                                showToast("No data found for the given email");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            // Handle JSON parsing error
                            showToast("Error parsing JSON response");
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Log and show an error message
                Log.e("TAG", "Error validating email", error);
                showToast("Error validating email");
            }
        });

        queue.add(stringRequest);
    }

    private void showToast(String message) {
        Toast.makeText(Login.this, message, Toast.LENGTH_SHORT).show();
    }

}