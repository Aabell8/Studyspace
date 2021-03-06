package com.austinabell8.studyspace.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.austinabell8.studyspace.R;
import com.austinabell8.studyspace.activities.Student.StudentActivity;
import com.austinabell8.studyspace.activities.Tutor.TutorActivity;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "LoginActivity";

    private LoginButton FBLoginButton;

    private CallbackManager callbackManager;

    ProgressDialog progressDialog;
    private Button mLoginBtn;
    private TextView mSignupButton;
    private EditText mEmail;
    private EditText mPassword;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mRootRef;

    private SharedPreferences mPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LoginManager.getInstance().logOut();
        mFirebaseAuth = FirebaseAuth.getInstance();

        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();

        FirebaseApp.initializeApp(this);

        mRootRef = FirebaseDatabase.getInstance().getReference();

        setContentView(R.layout.activity_login);
        FBLoginButton = findViewById(R.id.button_facebook_login);
        mLoginBtn = findViewById(R.id.button_login);
        mLoginBtn.setOnClickListener(this);
        mSignupButton = findViewById(R.id.text_sign_up);
        mSignupButton.setOnClickListener(this);
        mEmail = findViewById(R.id.edit_text_email);
        mPassword = findViewById(R.id.edit_text_password);
        mPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    mLoginBtn.performClick();
                    hideKeyboard(LoginActivity.this);
                    return true;
                }
                return false;
            }
        });

        mPreferences = getSharedPreferences("LoginData", MODE_PRIVATE);
        mEmail.setText(mPreferences.getString("email", ""));

        // Callback registration
        FBLoginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                Log.d(TAG, "Austin8onFBCallbackSuccess:");
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                progressDialog = new ProgressDialog(LoginActivity.this, R.style.AppTheme_Dark_Dialog);
                progressDialog.setMessage(getString(R.string.authenticating));
                progressDialog.setCancelable(false);
                progressDialog.show();
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.e(TAG, "fb login cancelled");
            }
            @Override
            public void onError(FacebookException exception) {
                Log.e(TAG, "fb login failed");
            }
        });

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Log.d(TAG, "Austin8onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "Austin8onAuthStateChanged:signed_out");
                }
            }
        };
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_login:
                if (!mEmail.getText().toString().isEmpty()
                        && !mPassword.getText().toString().isEmpty()){
                    progressDialog =
                            new ProgressDialog(LoginActivity.this, R.style.AppTheme_Dark_Dialog);
                    progressDialog.setMessage(getString(R.string.authenticating));
                    progressDialog.setCancelable(false);
                    progressDialog.show();

                    mFirebaseAuth.signInWithEmailAndPassword(
                            mEmail.getText().toString(), mPassword.getText().toString())
                            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information
                                        Log.d(TAG, "signInWithEmail:success");
                                        SharedPreferences.Editor prefEditor = mPreferences.edit();
                                        prefEditor.putString("email", mEmail.getText().toString());
                                        prefEditor.apply();
                                        progressDialog.hide();
                                        completeIntent();
                                    } else {
                                        // If sign in fails, display a message to the user.
                                        Log.w(TAG, "signInWithEmail:failure", task.getException());
                                        Toast.makeText(LoginActivity.this, "Authentication failed.",
                                                Toast.LENGTH_SHORT).show();
                                        progressDialog.hide();
                                    }
                                }
                            });
                }
                break;
            case R.id.text_sign_up:
                Intent intent = new Intent (LoginActivity.this, RegisterActivity.class);
                intent.putExtra("facebook", false);
                startActivity(intent);
                finish();
                break;
        }
    }

    @Override
    protected void onResume(){
        super.onResume();
        mFirebaseAuth.addAuthStateListener(mAuthListener);
        if (progressDialog != null) {
            //If process is cancelled by leaving app, be able to cancel dialog
            progressDialog.setCancelable(true);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mFirebaseAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mFirebaseAuth.removeAuthStateListener(mAuthListener);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "Austin8handleFacebookStart:" + token);

        final AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mFirebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "Austin8handleFacebookSuccess:success");
                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                            progressDialog.hide();
                            completeFBIntent();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "Austin8signInWithCredential:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            LoginManager.getInstance().logOut();
                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                            progressDialog.hide();
                        }
                    }
                });
    }

    private void completeIntent() {
        DatabaseReference ref = mRootRef.child("users")
                .child(mFirebaseAuth.getCurrentUser().getUid()).child("role");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String role = dataSnapshot.getValue(String.class);
                if (role == null){
                    return;
                }
                Intent intent;
                switch (role) {
                    case "N":
                        intent = new Intent(LoginActivity.this, RoleActivity.class);
                        startActivity(intent);
                        break;
                    case "S":
                        intent = new Intent(LoginActivity.this, StudentActivity.class);
                        startActivity(intent);
                        break;
                    case "T":
                        intent = new Intent(LoginActivity.this, TutorActivity.class);
                        startActivity(intent);
                        break;
                }

                finish();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "Database error when retrieving role");
            }
        });
    }

    private void completeFBIntent() {
        DatabaseReference ref = mRootRef.child("users")
                .child(mFirebaseAuth.getCurrentUser().getUid()).child("role");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String role = dataSnapshot.getValue(String.class);
                if (role == null){
                    GraphRequest request = GraphRequest.newMeRequest(
                            AccessToken.getCurrentAccessToken(),
                            new GraphRequest.GraphJSONObjectCallback() {
                                @Override
                                public void onCompleted(JSONObject object, GraphResponse response) {
                                    String name = "",lastName = "", email = "",birthday = "",gender;
                                    // Application code
                                    try {
                                        if(object.has("name"))
                                            name = object.getString("name");
                                        if(object.has("last_name"))
                                            lastName = object.getString("last_name");
                                        if (object.has("email"))
                                            email = object.getString("email");
                                        if (object.has("birthday"))
                                            birthday = object.getString("birthday");

                                        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                                        intent.putExtra("facebook", true);
                                        intent.putExtra("name",name);
                                        intent.putExtra("email", email);
                                        intent.putExtra("birthday", birthday);
                                        startActivity(intent);
                                        finish();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                    Bundle parameters = new Bundle();
                    parameters.putString("fields", "email,name,link,birthday");
                    request.setParameters(parameters);
                    request.executeAsync();
                    return;
                }
                Intent intent;
                switch (role) {
                    case "N":
                        intent = new Intent(LoginActivity.this, RoleActivity.class);
                        startActivity(intent);
                        break;
                    case "S":
                        intent = new Intent(LoginActivity.this, StudentActivity.class);
                        startActivity(intent);
                        break;
                    case "T":
                        intent = new Intent(LoginActivity.this, TutorActivity.class);
                        startActivity(intent);
                        break;
                }
                finish();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "Database error when retrieving role");
            }
        });
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

}
