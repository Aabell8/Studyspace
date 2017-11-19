package com.austinabell8.studyspace.activities;


import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.austinabell8.studyspace.R;
import com.austinabell8.studyspace.adapters.ApplicantRecyclerAdapter;
import com.austinabell8.studyspace.fragments.PostsFragment;
import com.austinabell8.studyspace.helpers.RecyclerViewClickListener;
import com.austinabell8.studyspace.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class PostActivity extends AppCompatActivity {

    private PostsFragment.OnFragmentInteractionListener mListener;
    private RecyclerView mRecyclerView;
    private ArrayList<User> users;
    private ApplicantRecyclerAdapter mApplicantRecyclerAdapter;
    private LinearLayoutManager llm;

    private RecyclerViewClickListener mRecyclerViewClickListener;

    private DatabaseReference mRootRef;
    private DatabaseReference mPostRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        mRootRef = FirebaseDatabase.getInstance().getReference();
        mPostRef = mRootRef.child("posts");

        mRecyclerView = findViewById(R.id.rvPosts);

        mRecyclerView.setHasFixedSize(true);
        llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(llm);

        users = new ArrayList<>();

        mPostRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                users.clear();
                Log.e("Count " ,""+snapshot.getChildrenCount());
                for (DataSnapshot postSnapshot: snapshot.getChildren()) {
                    User nUser = postSnapshot.getValue(User.class);
                    users.add(nUser);
                    (mRecyclerView.getAdapter()).notifyDataSetChanged();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        mRecyclerViewClickListener = new RecyclerViewClickListener() {
            @Override
            public void recyclerViewListClicked(View v, int position) {

            }
        };

        mApplicantRecyclerAdapter = new ApplicantRecyclerAdapter(this, users, mRecyclerViewClickListener);
        mRecyclerView.setAdapter(mApplicantRecyclerAdapter);

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return false;
    }
}
