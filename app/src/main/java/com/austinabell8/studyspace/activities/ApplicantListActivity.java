package com.austinabell8.studyspace.activities;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;

import com.austinabell8.studyspace.R;
import com.austinabell8.studyspace.adapters.TutorRecyclerAdapter;
import com.austinabell8.studyspace.model.User;
import com.austinabell8.studyspace.utils.RecyclerViewClickListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ApplicantListActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private ArrayList<User> users;
    private TutorRecyclerAdapter mTutorRecyclerAdapter;
    private LinearLayoutManager llm;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerViewClickListener mRecyclerViewClickListener;
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
        if (getSupportActionBar()!=null){
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
//        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(),
//                llm.getOrientation());
//        mRecyclerView.addItemDecoration(dividerItemDecoration);
        mRecyclerView.setLayoutManager(llm);

        users = new ArrayList<>();

        mRecyclerViewClickListener = new RecyclerViewClickListener() {
            @Override
            public void recyclerViewListClicked(View v, int position) {
                //TODO: Intent to User details activity
            }

            @Override
            public void recyclerViewListLongClicked(View v, int position) {

            }
        };

        mTutorRecyclerAdapter = new TutorRecyclerAdapter(getApplicationContext(), users, mRecyclerViewClickListener);
        mRecyclerView.setAdapter(mTutorRecyclerAdapter);

        refreshTutorApplications();

//        Query tQuery = mUsersRef.orderByChild("course").equalTo(course);
//        tQuery.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot snapshot) {
//                users.clear();
//                mSpinner.setVisibility(View.GONE);
//                Log.e("Count " ,""+snapshot.getChildrenCount());
//                for (DataSnapshot postSnapshot: snapshot.getChildren()) {
//                    User nUser = postSnapshot.getValue(User.class);
//                    users.add(nUser);
//                    (mRecyclerView.getAdapter()).notifyDataSetChanged();
//                }
//            }
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                Log.e("Cancelled", "Post query request cancelled.");
//            }
//        });
    }


    private void refreshTutorApplications () {
        final DatabaseReference currentUserRef = mRootRef.child("tutor_applications");
//                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
//        Query tQuery = currentUserRef.orderByKey();
        currentUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                users.clear();
                for (DataSnapshot user : dataSnapshot.getChildren()) {
                    Object posted = user.child(postId).getValue();
                    if (posted!=null && posted.equals(true)){
                     mUsersRef.child(user.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                         @Override
                         public void onDataChange(DataSnapshot dataSnapshot) {
                             if(dataSnapshot.getValue()!=null){
                                 users.add(dataSnapshot.getValue(User.class));
                                 mTutorRecyclerAdapter.notifyDataSetChanged();
                             }
                             else {
//                                currentUserRef.child(uId).removeValue();
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
