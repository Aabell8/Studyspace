package com.austinabell8.studyspace.activities.Student;

import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;

import com.austinabell8.studyspace.R;
import com.austinabell8.studyspace.adapters.Student.ApplicantRecyclerAdapter;
import com.austinabell8.studyspace.model.User;
import com.austinabell8.studyspace.utils.ApplicantClickListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ApplicantListActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private ArrayList<User> users;
    private ApplicantRecyclerAdapter mApplicantRecyclerAdapter;
    private LinearLayoutManager llm;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ApplicantClickListener mRecyclerViewClickListener;
    private ProgressBar mSpinner;
    private String postId;
    private Toolbar mToolbar;

    private DatabaseReference mRootRef;
    private DatabaseReference mUsersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Tutor Applications");
        }

        Bundle data = getIntent().getExtras();
        postId = data.getString("post_id");

        mRecyclerView = findViewById(R.id.rvPosts);
        mRootRef = FirebaseDatabase.getInstance().getReference();
        mUsersRef = mRootRef.child("users");
        mSpinner = findViewById(R.id.progressBarSearch);

        mRecyclerView.setHasFixedSize(true);
        llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(llm);

        users = new ArrayList<>();

        mRecyclerViewClickListener = new ApplicantClickListener() {
            @Override
            public void acceptClick(@NotNull View v, int position) {
                User clickedUser = users.get(position);
                clickedUser.setAccepted(true);
                DatabaseReference ref = mRootRef.child("post_applicants");
                Map<String,Object> taskMap = new HashMap<>();
                taskMap.put(clickedUser.getUid(), true);
                ref.child(postId).updateChildren(taskMap);
            }
            @Override
            public void messageClick(@NotNull View v, int position) {
            }
            @Override
            public void userDetailsClick(@NonNull View v, int position) {
                //TODO: Intent to User details activity
            }

        };

        mApplicantRecyclerAdapter = new ApplicantRecyclerAdapter(getApplicationContext(),
                users, mRecyclerViewClickListener, postId);
        mRecyclerView.setAdapter(mApplicantRecyclerAdapter);

        refreshTutorApplications();
    }

    private void refreshTutorApplications() {
        final DatabaseReference applicantsRef = mRootRef.child("post_applicants").child(postId);
        applicantsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                users.clear();
                for (DataSnapshot applSnap : dataSnapshot.getChildren()) {
                    if (applSnap.getValue() != null) {
                        final boolean accepted = (boolean)applSnap.getValue();
                        final String uid = applSnap.getKey();
                        mUsersRef.child(applSnap.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.getValue() != null) {
                                    User addItem = dataSnapshot.getValue(User.class);
                                    if(addItem!=null){
                                        if (accepted){
                                            addItem.setAccepted(true);
                                        }
                                        addItem.setUid(uid);
                                    }
                                    users.add(addItem);
                                    mApplicantRecyclerAdapter.notifyDataSetChanged();
                                }
                            }
                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        mSpinner.setVisibility(View.GONE);
    }
}
