package com.austinabell8.studyspace.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.austinabell8.studyspace.R;
import com.austinabell8.studyspace.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class RegisterActivity extends AppCompatActivity implements View.OnClickListener{
    private static final String TAG = "RegisterActivity";

    private Button mSignupButton;
    private TextView mLoginButton;
    private EditText mNameText;
    private EditText mAgeText;
    private EditText mPasswordText;
    private EditText mUsernameText;
    private EditText mEmailText;
    private boolean mFacebook;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mRootRef;

    private SharedPreferences mPreferences;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        String fb = getIntent().getStringExtra("facebook");

        mFirebaseAuth = FirebaseAuth.getInstance();
        mPreferences = getSharedPreferences("LoginData", MODE_PRIVATE);
        mDatabase = FirebaseDatabase.getInstance();
        mRootRef = FirebaseDatabase.getInstance().getReference();

        mSignupButton = findViewById(R.id.signup_button);
        mSignupButton.setOnClickListener(this);
        mLoginButton = findViewById(R.id.login_link);
        mLoginButton.setOnClickListener(this);
        mNameText = findViewById(R.id.input_name);
        mUsernameText = findViewById(R.id.input_username);
        mAgeText = findViewById(R.id.input_age);
        mPasswordText = findViewById(R.id.input_password);
        mEmailText = findViewById(R.id.input_email);

        Bundle data = getIntent().getExtras();
        if(data!=null){
            mFacebook = data.getBoolean("facebook");
            String name = data.getString("name");
            mNameText.setText(name);
            mNameText.setEnabled(false);
            mUsernameText.requestFocus();
            String email = data.getString("email");
            if(email!=null && !email.equals("")){
                mEmailText.setText(email);
                mEmailText.setEnabled(false);
            }
            else{
                mEmailText.setVisibility(View.GONE);
            }
            String birthday = data.getString("birthday");

        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.signup_button:
                signup();
                break;
            case R.id.login_link:
                Intent intent = new Intent (RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
                break;
        }
    }


    public void signup() {
        Log.d(TAG, "Signup");

        if (!validate()) {
            onSignupFailed();
            return;
        }

        mSignupButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(RegisterActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creating Account...");
        progressDialog.show();

        if(mFacebook){
            final String name = mNameText.getText().toString().trim();
            final String username = mUsernameText.getText().toString().trim();
            final String age = mAgeText.getText().toString().trim();
            final String uid = mFirebaseAuth.getCurrentUser().getUid();

            progressDialog.hide();
            DatabaseReference usersRef = mRootRef.child("users");
            usersRef.child(uid).setValue(new User(username, "facebook", name, age));
            onSignupSuccess();
        }
        else {
            mFirebaseAuth.createUserWithEmailAndPassword(
                    mEmailText.getText().toString(), mPasswordText.getText().toString())
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                final String name = mNameText.getText().toString().trim();
                                final String username = mUsernameText.getText().toString().trim();
                                final String age = mAgeText.getText().toString().trim();
                                final String email = mEmailText.getText().toString().trim();
                                final String uid = mFirebaseAuth.getCurrentUser().getUid();
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "createUserWithEmail:success");
                                SharedPreferences.Editor prefEditor = mPreferences.edit();
                                prefEditor.putString("email", mEmailText.getText().toString());
                                prefEditor.apply();
                                progressDialog.hide();
                                DatabaseReference usersRef = mRootRef.child("users");
                                usersRef.child(uid).setValue(new User(username, email, name, age));
                                onSignupSuccess();
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                Toast.makeText(RegisterActivity.this, "Sign up failed.",
                                        Toast.LENGTH_SHORT).show();
                                progressDialog.hide();
                                mSignupButton.setEnabled(true);
                            }
                        }

                    });
        }

    }


    public void onSignupSuccess() {
        mSignupButton.setEnabled(true);
        setResult(RESULT_OK, null);

        Intent intent = new Intent (RegisterActivity.this, RoleActivity.class);
        startActivity(intent);
        finish();

    }

    public void onSignupFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();
        mSignupButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String name = mNameText.getText().toString().trim();
        String username = mUsernameText.getText().toString().trim();
        String email;
        if(mEmailText.getVisibility()!=View.GONE){
            email = "";
        }
        else {
            email = mEmailText.getText().toString().trim();
        }
        String password = mPasswordText.getText().toString().trim();

        if (name.isEmpty() || name.length() < 3) {
            mNameText.setError("must be at least 3 characters");
            valid = false;
        } else {
            mNameText.setError(null);
        }

        if (username.isEmpty() || username.length() < 5) {
            mUsernameText.setError("must be at least 5 characters");
            valid = false;
        } else {
            mUsernameText.setError(null);
        }

        if (!mFacebook && (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches())) {
            mEmailText.setError("enter a valid email address");
            valid = false;
        } else if (!mFacebook) {
            mEmailText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            mPasswordText.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            mPasswordText.setError(null);
        }

        return valid;
    }
}