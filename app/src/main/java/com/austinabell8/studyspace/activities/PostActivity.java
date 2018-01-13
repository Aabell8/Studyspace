package com.austinabell8.studyspace.activities;


import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;

import com.austinabell8.studyspace.R;
import com.austinabell8.studyspace.adapters.ApplicantRecyclerAdapter;
import com.austinabell8.studyspace.fragments.PostsFragment;
import com.austinabell8.studyspace.utils.RecyclerViewClickListener;
import com.austinabell8.studyspace.model.Post;
import com.austinabell8.studyspace.model.User;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class PostActivity extends AppCompatActivity {

    private PostsFragment.OnFragmentInteractionListener mListener;
    private RecyclerView mRecyclerView;
    private ArrayList<User> users;

    private ApplicantRecyclerAdapter mApplicantRecyclerAdapter;
    private LinearLayoutManager llm;

    private RecyclerViewClickListener mRecyclerViewClickListener;

    private DatabaseReference mRootRef;
    private DatabaseReference mUsersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        Bundle data = getIntent().getExtras();
        String postId = null;
//        Post post = data.getParcelable("post_item");
        if (data != null){
            postId = data.getString("post_item");
        }

        mRootRef = FirebaseDatabase.getInstance().getReference();
        mUsersRef = mRootRef.child("users");

        mRecyclerView = findViewById(R.id.rvUsers);


        mRecyclerView.setHasFixedSize(true);
        llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(llm);

        users = new ArrayList<>();

        if (postId != null){
            DatabaseReference tRef = mRootRef.child("posts").child(postId).child("applicants");
            tRef.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                }
                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                }
                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {

                }
                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }
//        ref.addListenerForSingleValueEvent(
//                new ValueEventListener() {
//                    @Override
//                    public void onDataChange(DataSnapshot dataSnapshot) {
//                        //Get map of users in datasnapshot
//                        collectPhoneNumbers((Map<String,Object>) dataSnapshot.getValue());
//                    }
//
//                    @Override
//                    public void onCancelled(DatabaseError databaseError) {
//                        //handle databaseError
//                    }
//                });
//
//        mRecyclerViewClickListener = new RecyclerViewClickListener() {
//            @Override
//            public void recyclerViewListClicked(View v, int position) {
//
//            }
//        };

//        mApplicantRecyclerAdapter = new ApplicantRecyclerAdapter(this, posts, mRecyclerViewClickListener);
//        mRecyclerView.setAdapter(mApplicantRecyclerAdapter);

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
