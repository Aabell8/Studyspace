package com.austinabell8.studyspace.activities.Tutor;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
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
import android.widget.TextView;

import com.austinabell8.studyspace.R;
import com.austinabell8.studyspace.adapters.Tutor.PostSearchRecyclerAdapter;
import com.austinabell8.studyspace.model.Post;
import com.austinabell8.studyspace.utils.SearchPostClickListener;
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

    private static final String TAG = "SearchActivity";

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

        mRecyclerView = findViewById(R.id.rv_search);
        mRootRef = FirebaseDatabase.getInstance().getReference();
        mPostRef = mRootRef.child("posts");
        mSpinner = findViewById(R.id.progress_bar_search);

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
                if (position != -1){
                    //Retrieve Id from item clicked, and pass it into an intent
                    Intent intent = new Intent(v.getContext(), PostDetailsActivity.class);
                    intent.putExtra("post_intent", posts.get(position));
                    TextView placeName = v.findViewById(R.id.text_name);
                    TextView placeCourse = v.findViewById(R.id.text_course);
                    TextView placePrice = v.findViewById(R.id.text_price);

                    Pair<View,String> namePair = Pair.create((View)placeName, "tName");
                    Pair<View,String> coursePair = Pair.create((View)placeCourse, "tCourse");
                    Pair<View,String> pricePair = Pair.create((View)placePrice, "tPrice");

                    ArrayList<Pair> pairs = new ArrayList<>();
                    pairs.add(namePair);
                    pairs.add(coursePair);
                    pairs.add(pricePair);

                    ActivityOptionsCompat options =
                            ActivityOptionsCompat.makeSceneTransitionAnimation(SearchActivity.this, namePair,coursePair,pricePair);

                    ActivityCompat.startActivity(v.getContext(), intent, options.toBundle());
                }
            }

            @Override
            public void applyButtonClicked(View v, int position) {
                final Post clicked = posts.get(position);
                FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                DatabaseReference ref = firebaseDatabase
                        .getReference().child("post_applicants");
                Map<String,Object> taskMap = new HashMap<>();
                taskMap.put(FirebaseAuth.getInstance().getCurrentUser().getUid(), false);
                ref.child(clicked.getPid()).updateChildren(taskMap);
                setResult(Activity.RESULT_OK);
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
                for (DataSnapshot postSnapshot: snapshot.getChildren()) {
                    Post nPost = postSnapshot.getValue(Post.class);
                    posts.add(nPost);
                    (mRecyclerView.getAdapter()).notifyDataSetChanged();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "Post query request cancelled.");
            }
        });
    }
}
