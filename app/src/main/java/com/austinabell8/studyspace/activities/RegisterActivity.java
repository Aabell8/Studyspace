package com.austinabell8.studyspace.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.austinabell8.studyspace.R;
import com.austinabell8.studyspace.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class RegisterActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = "RegisterActivity";

    private Button mSignupButton;
    private TextView mLoginButton;
    private EditText mNameText;
    private EditText mAgeText;
    private EditText mPasswordText;
    private EditText mRePasswordText;
    private EditText mUsernameText;
    private EditText mEmailText;

    private TextInputLayout mNameTil;
    private TextInputLayout mAgeTil;
    private TextInputLayout mPasswordTil;
    private TextInputLayout mRePasswordTil;
    private TextInputLayout mUsernameTil;
    private TextInputLayout mEmailTil;
    private TextInputLayout mOtherTil;
    private boolean mFacebook;
    private Spinner mSpinner;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mRootRef;

    private SharedPreferences mPreferences;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mPreferences = getSharedPreferences("LoginData", MODE_PRIVATE);
        mDatabase = FirebaseDatabase.getInstance();
        mRootRef = FirebaseDatabase.getInstance().getReference();

        mSignupButton = findViewById(R.id.button_sign_up);
        mSignupButton.setOnClickListener(this);
        mLoginButton = findViewById(R.id.text_login_link);
        mLoginButton.setOnClickListener(this);
        mNameText = findViewById(R.id.edit_text_name);
        mUsernameText = findViewById(R.id.edit_text_username);
        mAgeText = findViewById(R.id.edit_text_age);
        mPasswordText = findViewById(R.id.edit_text_password);
        mRePasswordText = findViewById(R.id.edit_text_re_password);
        mEmailText = findViewById(R.id.edit_text_email);

        mSpinner = findViewById(R.id.spinner_course);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(RegisterActivity.this,
                R.layout.register_spinner_item, getResources().getStringArray(R.array.referred_array));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner.setAdapter(adapter);
        mOtherTil = findViewById(R.id.til_other);
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (adapterView.getItemAtPosition(i).toString().equals("Other")){
                    mOtherTil.setVisibility(View.VISIBLE);
                }
                else {
                    mOtherTil.setVisibility(View.GONE);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        Bundle data = getIntent().getExtras();
        if(data!=null){
            mFacebook = false;
            mFacebook = data.getBoolean("facebook");
            if(mFacebook){
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
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_sign_up:
                signup();
                break;
            case R.id.text_login_link:
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

            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setDisplayName(name).build();
            user.updateProfile(profileUpdates);

            progressDialog.hide();
            DatabaseReference usersRef = mRootRef.child("users");
            usersRef.child(uid).setValue(new User(username, "None", name, age));
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

                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                        .setDisplayName(name).build();
                                user.updateProfile(profileUpdates);

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

        mNameTil = findViewById(R.id.til_ame);
        mNameTil.setErrorEnabled(true);
        mEmailTil = findViewById(R.id.til_email);
        mEmailTil.setErrorEnabled(true);
        mUsernameTil = findViewById(R.id.til_username);
        mUsernameTil.setErrorEnabled(true);
        mAgeTil = findViewById(R.id.til_age);
        mAgeTil.setErrorEnabled(true);
        mPasswordTil = findViewById(R.id.til_password);
        mPasswordTil.setErrorEnabled(true);
        mRePasswordTil = findViewById(R.id.til_re_password);
        mRePasswordTil.setErrorEnabled(true);

        String name = mNameText.getText().toString().trim();
        String username = mUsernameText.getText().toString().trim();
        String email;
        if(mEmailText.getVisibility()==View.GONE){
            email = "";
        }
        else {
            email = mEmailText.getText().toString().trim();
        }
        String password = mPasswordText.getText().toString().trim();
        String rePassword = mRePasswordText.getText().toString().trim();

        if (name.isEmpty() || name.length() < 3) {
            mNameTil.setError("must be at least 3 characters");
            valid = false;
        } else {
            mNameTil.setError(null);
        }

        if (username.isEmpty() || username.length() < 5) {
            mUsernameTil.setError("must be at least 5 characters");
            valid = false;
        } else {
            mUsernameTil.setError(null);
        }

        if (!mFacebook && (email.isEmpty() /*|| !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()*/)) {
            mEmailTil.setError("enter a valid email address");
            valid = false;
        } else if (!mFacebook) {
            mEmailTil.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            mPasswordTil.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        }
        else if(!password.equals(rePassword)){
            mPasswordTil.setError("between 4 and 10 alphanumeric characters");
        }
        else {
            mPasswordTil.setError(null);
        }

        return valid;
    }
}