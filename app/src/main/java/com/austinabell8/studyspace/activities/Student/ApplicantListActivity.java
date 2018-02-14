package com.austinabell8.studyspace.activities.Student;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.austinabell8.studyspace.R;
import com.austinabell8.studyspace.activities.ConversationActivity;
import com.austinabell8.studyspace.activities.UserDetailsActivity;
import com.austinabell8.studyspace.adapters.Student.ApplicantRecyclerAdapter;
import com.austinabell8.studyspace.model.User;
import com.austinabell8.studyspace.utils.ApplicantClickListener;
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

    private static final String TAG = "ApplicantListActivity";

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
        setContentView(R.layout.activity_applicant_list);

        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Genius Applications");
        }

        Bundle data = getIntent().getExtras();
        postId = data.getString("post_id");

        mRecyclerView = findViewById(R.id.rv_applicants);
        mRootRef = FirebaseDatabase.getInstance().getReference();
        mUsersRef = mRootRef.child("users");
        mSpinner = findViewById(R.id.progress_bar_search);

        mRecyclerView.setHasFixedSize(true);
        llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(llm);

        users = new ArrayList<>();

        // Click views for each applicant item
        mRecyclerViewClickListener = new ApplicantClickListener() {
            @Override
            public void acceptClick(@NotNull View v, int position) {
                // User long clicked view
                User clickedUser = users.get(position);
                clickedUser.setAccepted(true);
                DatabaseReference ref = mRootRef.child("post_applicants");
                Map<String,Object> taskMap = new HashMap<>();
                taskMap.put(clickedUser.getUid(), true);
                ref.child(postId).updateChildren(taskMap);
            }
            @Override
            public void messageClick(@NotNull View v, int position) {
                // User clicked profile pic
                User clickedUser = users.get(position);
                Intent intent = new Intent(v.getContext(), ConversationActivity.class);
                intent.putExtra("recipient_id", clickedUser.getUid());
                intent.putExtra("recipient_name", clickedUser.getFullName());
                startActivity(intent);
            }
            @Override
            public void userDetailsClick(@NonNull View v, int position) {
                // User clicked view
                Intent intent = new Intent(v.getContext(), UserDetailsActivity.class);
                intent.putExtra("user_intent", users.get(position));
                TextView placeName = v.findViewById(R.id.text_name);
                TextView placeRate = v.findViewById(R.id.text_rate);
                TextView placeRating = v.findViewById(R.id.text_rating);
//                ImageView placeImage = v.findViewById(R.id.iv_tutor);

                Pair<View,String> namePair = Pair.create((View)placeName, "tName");
                Pair<View,String> ratePair = Pair.create((View)placeRate, "tRate");
                Pair<View,String> ratingPair = Pair.create((View)placeRating, "tRating");
//                Pair<View,String> imagePair = Pair.create((View)placeImage, "tImage");


                ArrayList<Pair> pairs = new ArrayList<>();
                pairs.add(namePair);
                pairs.add(ratePair);
                pairs.add(ratingPair);
//                pairs.add(imagePair);

                ActivityOptionsCompat options =
                        ActivityOptionsCompat.makeSceneTransitionAnimation(
                                ApplicantListActivity.this, namePair, ratePair,
                                ratingPair);
                // Open user details activity with transition
                ActivityCompat.startActivity(v.getContext(), intent, options.toBundle());
            }

        };

        mApplicantRecyclerAdapter = new ApplicantRecyclerAdapter(getApplicationContext(),
                users, mRecyclerViewClickListener, postId);
        mRecyclerView.setAdapter(mApplicantRecyclerAdapter);

        refreshTutorApplications();
    }

    private void refreshTutorApplications() {
        final DatabaseReference applicantsRef = mRootRef.child("post_applicants").child(postId);
        // Query all applicants of a given post
        applicantsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                users.clear();
                for (DataSnapshot applSnap : dataSnapshot.getChildren()) {
                    if (applSnap.getValue() != null) {
                        // True False value for if student has accepted application
                        final boolean accepted = (boolean)applSnap.getValue();
                        // Uid of tutor that has applied
                        final String uid = applSnap.getKey();
                        // Retrieve each user from database
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
                Log.e(TAG, "Failed to retrieve applications");
            }
        });
        mSpinner.setVisibility(View.GONE);
    }
}
