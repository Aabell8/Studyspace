package com.austinabell8.studyspace.activities;

import android.content.Intent;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.austinabell8.studyspace.R;
import com.austinabell8.studyspace.adapters.PostRecyclerAdapter;
import com.austinabell8.studyspace.helpers.RecyclerViewClickListener;
import com.austinabell8.studyspace.helpers.RecyclerViewLongClickListener;
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

public class SearchActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private ArrayList<Post> posts;
    private PostRecyclerAdapter mPostRecyclerAdapter;
    private LinearLayoutManager llm;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerViewLongClickListener mRecyclerViewLongClickListener;

    private DatabaseReference mRootRef;
    private DatabaseReference mPostRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        mRecyclerView = findViewById(R.id.rvPosts);
        mRootRef = FirebaseDatabase.getInstance().getReference();
        mPostRef = mRootRef.child("posts");

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

        Query tQuery = mPostRef.orderByChild("course").equalTo(course);
        tQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                posts.clear();
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

        mSwipeRefreshLayout = findViewById(R.id.swipe_refresh_layout_posts);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (mPostRecyclerAdapter!=null){
                            mPostRecyclerAdapter.removePending();
                        }
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                }, 100);

            }
        });

        mRecyclerViewLongClickListener = new RecyclerViewLongClickListener() {
            @Override
            public void recyclerViewListLongClicked(View v, int position) {
                final Post clicked = posts.get(position);
                FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
                DatabaseReference mCurrentUserDatabaseReference = mFirebaseDatabase
                        .getReference().child("users"
                                + "/" + FirebaseAuth.getInstance().getCurrentUser().getUid());
                Map<String,Object> taskMap = new HashMap<>();
                taskMap.put( clicked.getPid(), clicked.getPid());
                mCurrentUserDatabaseReference.child("applied").updateChildren(taskMap);
                finish();
            }
        };


        mPostRecyclerAdapter = new PostRecyclerAdapter(this, posts, null, mRecyclerViewLongClickListener);
        mRecyclerView.setAdapter(mPostRecyclerAdapter);

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener(){
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy)
            {
                if (dy!=0){
                    mPostRecyclerAdapter.removePending();
                }
            }
        });

    }
}
