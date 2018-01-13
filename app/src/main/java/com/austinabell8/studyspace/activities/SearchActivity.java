package com.austinabell8.studyspace.activities;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.austinabell8.studyspace.R;
import com.austinabell8.studyspace.adapters.PostSearchRecyclerAdapter;
import com.austinabell8.studyspace.utils.RecyclerViewClickListener;
import com.austinabell8.studyspace.model.Post;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SearchActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private ArrayList<Post> posts;
    private PostSearchRecyclerAdapter mPostRecyclerAdapter;
    private LinearLayoutManager llm;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerViewClickListener mRecyclerViewClickListener;
    private ProgressBar mSpinner;

    private DatabaseReference mRootRef;
    private DatabaseReference mPostRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        mRecyclerView = findViewById(R.id.rvPosts);
        mRootRef = FirebaseDatabase.getInstance().getReference();
        mPostRef = mRootRef.child("posts");
        mSpinner = findViewById(R.id.progressBarSearch);

        Bundle data = getIntent().getExtras();
        String course = data.getString("course");
        String searchText = data.getString("search_text");

        mRecyclerView.setHasFixedSize(true);
        llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(),
                llm.getOrientation());
        mRecyclerView.addItemDecoration(dividerItemDecoration);
        mRecyclerView.setLayoutManager(llm);

        posts = new ArrayList<>();

        mRecyclerViewClickListener = new RecyclerViewClickListener() {
            @Override
            public void recyclerViewListClicked(View v, int position) {
            }

            @Override
            public void recyclerViewListLongClicked(View v, int position) {
                final Post clicked = posts.get(position);
                FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
                String uniqueId = UUID.randomUUID().toString();
                DatabaseReference mApplicationsDatabaseReference = mFirebaseDatabase
                        .getReference().child(uniqueId);
                Map<String,Object> taskMap = new HashMap<>();
                taskMap.put( "Post", clicked.getPid());
                taskMap.put("Tutor", FirebaseAuth.getInstance().getCurrentUser().getUid());
                mApplicationsDatabaseReference.child("applied").updateChildren(taskMap);
                finish();
            }
        };

        mPostRecyclerAdapter = new PostSearchRecyclerAdapter(this, posts, mRecyclerViewClickListener);
        mRecyclerView.setAdapter(mPostRecyclerAdapter);

        Query tQuery = mPostRef.orderByChild("course").equalTo(course);
        tQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                posts.clear();
                mSpinner.setVisibility(View.GONE);
                Log.e("Count " ,""+snapshot.getChildrenCount());
                for (DataSnapshot postSnapshot: snapshot.getChildren()) {
                    Post nPost = postSnapshot.getValue(Post.class);
                    posts.add(nPost);
                    (mRecyclerView.getAdapter()).notifyDataSetChanged();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });



    }
}
