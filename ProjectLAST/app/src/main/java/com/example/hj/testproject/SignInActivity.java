package com.example.hj.testproject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignInActivity extends AppCompatActivity {


    private Button buttonSignIn;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private TextView textViewRegistration;

    private ProgressDialog progressDialog;


    private static final int RC_SIGN_IN = 123;
    private FirebaseAuth auth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() != null) {
            //user already signed in

            Intent toMainActivity = new Intent(SignInActivity.this, MainActivity.class);
            startActivity(toMainActivity);

        } else {

            startActivityForResult(AuthUI.getInstance()
                    .createSignInIntentBuilder()
                    .setIsSmartLockEnabled(false)
                    .setProviders(
                            AuthUI.FACEBOOK_PROVIDER,
                            AuthUI.GOOGLE_PROVIDER,
                            AuthUI.EMAIL_PROVIDER
                            )
                    .build(), RC_SIGN_IN);
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);
            //successfully signed in
            if (resultCode == RESULT_OK) {
                Log.d("AUTH", auth.getCurrentUser().getEmail());
                Intent toMainActivity = new Intent(SignInActivity.this, MainActivity.class);
                startActivity(toMainActivity);
                finish();
                return;
            } else {
                //user not authenticated
                Log.d("AUTH", "NOT AUTHENTICATED");
            }

        }
    }





}
