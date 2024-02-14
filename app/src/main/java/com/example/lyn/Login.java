package com.example.lyn;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.BeginSignInResult;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.OnFailureListener;



public class Login extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private static final int REQ_ONE_TAP = 100;
    private static final int RC_SIGN_IN = 101;

    private SignInClient oneTapClient;
    private BeginSignInRequest signInRequest;

    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        setupGoogleSignIn();
        setupSignInButton();
    }

    private void setupGoogleSignIn() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    private void setupSignInButton() {
        SignInButton signInButton = findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });
    }

    private void signIn() {
        oneTapClient = Identity.getSignInClient(this);
        signInRequest = BeginSignInRequest.builder()
                .setPasswordRequestOptions(BeginSignInRequest.PasswordRequestOptions.builder()
                        .setSupported(true)
                        .build())
                .setGoogleIdTokenRequestOptions(BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                        .setSupported(true)
                        .setServerClientId("511184308897-el9e5ebr54pu2v0n5ar41hoke15csn0s.apps.googleusercontent.com")
                        .setFilterByAuthorizedAccounts(true)
                        .build())
                .setAutoSelectEnabled(false)
                .build();

        // Start the One Tap UI
        oneTapClient.beginSignIn(signInRequest)
                .addOnSuccessListener(this, new OnSuccessListener<BeginSignInResult>() {
                    @Override
                    public void onSuccess(BeginSignInResult result) {
                        try {
                            startIntentSenderForResult(
                                    result.getPendingIntent().getIntentSender(), REQ_ONE_TAP,
                                    null, 0, 0, 0);
                        } catch (Exception e) {
                            Log.e(TAG, "Error starting One Tap UI", e);
                            showToast("Error starting One Tap UI");
                        }
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Error starting One Tap UI", e);
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            handleGoogleSignInResult(data);
        } else if (requestCode == REQ_ONE_TAP) {
            handleOneTapSignInResult(data);
        }
    }

    private void handleGoogleSignInResult(Intent data) {
        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
        try {
            GoogleSignInAccount account = task.getResult(ApiException.class);
            String email = account.getEmail();
            validateEmailWithServer(email);
        } catch (ApiException e) {
            Log.e(TAG, "Failed to sign in with Google", e);
            showToast("Failed to sign in with Google");
        }
    }

    private void handleOneTapSignInResult(Intent data) {
        try {
            SignInCredential credential = oneTapClient.getSignInCredentialFromIntent(data);
            String email = credential.getId();
            validateEmailWithServer(email);
        } catch (Exception e) {
            Log.e(TAG, "Error processing One Tap response", e);
            showToast("Error processing One Tap response");
        }
    }

    private void validateEmailWithServer(String email) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://trial.gssmp.org/MobileApp/Select.php?query=SELECT * FROM Students WHERE EmailID='" + email + "'";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.contains(email)) {
                            startActivity(new Intent(Login.this, dashboard.class));
                        } else {
                            showToast("Your Email is invalid");
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Error validating email", error);
                showToast("Error validating email");
            }
        });
        queue.add(stringRequest);
    }

    private void showToast(String message) {
        Toast.makeText(Login.this, message, Toast.LENGTH_SHORT).show();
    }
}
