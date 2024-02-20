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
        String url = "https://trial.gssmp.org/MobileApp/Select.php?query=SELECT * FROM Students WHERE emailid='" + email + "'";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("TAG",response);
                        if (response.contains(email)) {

                            // Email exists in the database
                            // Proceed to the second activity
                            Intent intent = new Intent(Login.this, ProfileActivity.class);
                            intent.putExtra("email", email); // Pass email as an extra
                            startActivity(intent);
                        } else {
                            // Email does not exist in the database
                            // Show an appropriate message or take further actions as needed
                            showToast("Invalid Email ID");
                            // You may want to prevent proceeding to the second activity here
                            // For example, you can remove the startActivity line from here
                            // Remove the following line
                            // startActivity(new Intent(MainActivity.this, SecondActivity.class));
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