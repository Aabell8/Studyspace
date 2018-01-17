package com.austinabell8.studyspace.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.facebook.login.LoginManager;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * SplashActivity - Activity that shows while MainActivity is inflated
 * @author  Austin Abell
 */

public class SplashActivity extends AppCompatActivity {

    private FirebaseAuth mFirebaseAuth;
    private DatabaseReference mRootRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LoginManager.getInstance().logOut();
        mFirebaseAuth = FirebaseAuth.getInstance();
        FirebaseApp.initializeApp(this);

        mRootRef = FirebaseDatabase.getInstance().getReference();

        if(mFirebaseAuth.getCurrentUser() != null){
            DatabaseReference ref = mRootRef.child("users").child(mFirebaseAuth.getCurrentUser().getUid()).child("role");
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String role = dataSnapshot.getValue(String.class);
                    if (role == null){
                        Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                    else{
                        Intent intent;
                        switch (role) {
                            case "N":
                                intent = new Intent(SplashActivity.this, RoleActivity.class);
                                startActivity(intent);
                                break;
                            case "S":
                                intent = new Intent(SplashActivity.this, StudentActivity.class);
                                startActivity(intent);
                                break;
                            case "T":
                                intent = new Intent(SplashActivity.this, TutorActivity.class);
                                startActivity(intent);
                                break;
                        }
                        finish();
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Intent intent = new Intent(getParent(), LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            });
        }
        else {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }

        //Activity does not inflate a view, simply shows while MainActivity is inflated
    }

}
