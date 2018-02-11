package com.austinabell8.studyspace.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.austinabell8.studyspace.R;
import com.austinabell8.studyspace.activities.Student.StudentActivity;
import com.austinabell8.studyspace.activities.Tutor.TutorActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RoleActivity extends AppCompatActivity {

    private FirebaseAuth mFirebaseAuth;

    private Button mStudentButton;
    private Button mTutorButton;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_role);

        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        if (getSupportActionBar()!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Role Selection");
        }
        mStudentButton = findViewById(R.id.student_button);
        mTutorButton = findViewById(R.id.tutor_button);

        mFirebaseAuth = FirebaseAuth.getInstance();

        mStudentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                        .child("users").child(mFirebaseAuth.getCurrentUser().getUid()).child("role");
                ref.setValue("S");
                Intent intent = new Intent (RoleActivity.this, StudentActivity.class);
                startActivity(intent);
                finish();
            }
        });

        mTutorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                        .child("users").child(mFirebaseAuth.getCurrentUser().getUid()).child("role");
                ref.setValue("T");
                Intent intent = new Intent (RoleActivity.this, TutorActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent (RoleActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
                return true;
        }
        return false;
    }
}
