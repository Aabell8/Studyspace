package com.austinabell8.studyspace.activities;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.austinabell8.studyspace.R;
import com.austinabell8.studyspace.adapters.PostSearchRecyclerAdapter;
import com.austinabell8.studyspace.utils.RecyclerViewClickListener;
import com.austinabell8.studyspace.model.Post;
import com.austinabell8.studyspace.utils.SearchPostClickListener;
import com.bumptech.glide.Glide;
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
    private SearchPostClickListener mRecyclerViewClickListener;
    private ProgressBar mSpinner;
    private Toolbar mToolbar;

    private DatabaseReference mRootRef;
    private DatabaseReference mPostRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        if (getSupportActionBar()!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Posts");
        }

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

        mRecyclerViewClickListener = new SearchPostClickListener() {
            @Override
            public void recyclerViewListClicked(View v, int position) {
                //TODO: Intent to post details activity
            }

            @Override
            public void applyButtonClicked(View v, int position) {
                final Post clicked = posts.get(position);
                FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                DatabaseReference ref = firebaseDatabase
                        .getReference().child("tutor_applications");
                Map<String,Object> taskMap = new HashMap<>();
                taskMap.put(clicked.getPid(), true);
                ref.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .updateChildren(taskMap);
                ref.getParent().child("post_applicants");
//                Glide.with(mRecyclerView.getContext()).pauseRequests();
//                Intent intent = new Intent();
//                intent.putExtra("Pid", clicked.getPid());
                setResult(Activity.RESULT_OK);
//                finish();
            }
        };

        mPostRecyclerAdapter = new PostSearchRecyclerAdapter(getApplicationContext(), posts, mRecyclerViewClickListener);
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
                Log.e("Cancelled", "Post query request cancelled.");
            }
        });
    }
}
