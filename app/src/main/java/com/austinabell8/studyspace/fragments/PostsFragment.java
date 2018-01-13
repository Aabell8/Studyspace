package com.austinabell8.studyspace.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.austinabell8.studyspace.R;
import com.austinabell8.studyspace.activities.PostActivity;
import com.austinabell8.studyspace.activities.PostCreateActivity;
import com.austinabell8.studyspace.adapters.PostRecyclerAdapter;
import com.austinabell8.studyspace.utils.RecyclerViewClickListener;
import com.austinabell8.studyspace.utils.SwipeUtil;
import com.austinabell8.studyspace.model.Post;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.UUID;


public class PostsFragment extends Fragment implements View.OnClickListener {

    private OnFragmentInteractionListener mListener;
    private View inflatedPosts;
    private RecyclerView mRecyclerView;
    private ArrayList<Post> posts;
    private PostRecyclerAdapter mPostRecyclerAdapter;
    private LinearLayoutManager llm;
    private FloatingActionButton mFab;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private RecyclerViewClickListener mRecyclerViewClickListener;

    private DatabaseReference mRootRef;
    private DatabaseReference mPostRef;

    public PostsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        inflatedPosts = inflater.inflate(R.layout.fragment_posts, container, false);

        mRootRef = FirebaseDatabase.getInstance().getReference();
        mPostRef = mRootRef.child("posts");

        mRecyclerView = inflatedPosts.findViewById(R.id.rvPosts);

        mRecyclerView.setHasFixedSize(true);
        llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(),
                llm.getOrientation());
        mRecyclerView.addItemDecoration(dividerItemDecoration);
        mRecyclerView.setLayoutManager(llm);

        posts = new ArrayList<>();

        Query tQuery = mPostRef.orderByChild("uid").equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid());
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

        mSwipeRefreshLayout = inflatedPosts.findViewById(R.id.swipe_refresh_layout_posts);
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

        mRecyclerViewClickListener = new RecyclerViewClickListener() {
            @Override
            public void recyclerViewListClicked(View v, int position) {
                final Post clicked = posts.get(position);
                Intent intent = new Intent(getActivity(), PostActivity.class);
                intent.putExtra("post_item", clicked);
                startActivity(intent);
            }

            @Override
            public void recyclerViewListLongClicked(View v, int position) {
            }
        };


        mPostRecyclerAdapter = new PostRecyclerAdapter(getContext(), posts, mRecyclerViewClickListener, null);
        mRecyclerView.setAdapter(mPostRecyclerAdapter);
        setSwipeForRecyclerView();

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


        return inflatedPosts;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        mFab = getActivity().findViewById(R.id.fab);
        mFab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                CreatePost();
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        mFab.setOnClickListener(null);
        mPostRecyclerAdapter.removePending();
    }

    @Override
    public void onClick(View v) {

    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        MenuInflater inflater = getMenuInflater();
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
//        inflater.inflate(R.menu.posts_actionbar_menu, menu);
//        MenuItem item = menu.findItem(R.id.action_search);
//        SearchView searchView = new SearchView((getActivity()).getSupportActionBar().getThemedContext());
//        MenuItemCompat.setShowAsAction(item, MenuItemCompat.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW | MenuItemCompat.SHOW_AS_ACTION_IF_ROOM);
//        MenuItemCompat.setActionView(item, searchView);
//        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                return false;
//            }
//            @Override
//            public boolean onQueryTextChange(String newText) {
//                mPostRecyclerAdapter.filter(newText);
//                return true;
//            }
//        });
//        optionsMenu = menu;
    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle item selection
//        switch (item.getItemId()) {
//            case R.id.action_logout:
//                return false;
//            default:
//                break;
//        }
//        return false;
//    }


//    private void populatePostsList(){
//        // Construct the data source
//        if (posts == null){
//            posts = Post.getPosts();
//        }
//
//        mRecyclerView = inflatedPosts.findViewById(R.id.rvPosts);
//
//        mRecyclerView.setHasFixedSize(true);
//        llm = new LinearLayoutManager(getActivity());
//        llm.setOrientation(LinearLayoutManager.VERTICAL);
//        mRecyclerView.setLayoutManager(llm);
//
//        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(
//                mRecyclerView.getContext(), llm.getOrientation());
//        mRecyclerView.addItemDecoration(dividerItemDecoration);
//    }

    public void scrollToTop () {
        mRecyclerView.smoothScrollToPosition(0);
    }

    private void setSwipeForRecyclerView() {
        SwipeUtil swipeHelper = new SwipeUtil(0, ItemTouchHelper.LEFT, getActivity()) {
            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int swipedPosition = viewHolder.getAdapterPosition();
                PostRecyclerAdapter adapter = (PostRecyclerAdapter) mRecyclerView.getAdapter();
                adapter.pendingRemoval(swipedPosition);
            }
            @Override
            public int getSwipeDirs(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                int position = viewHolder.getAdapterPosition();
                PostRecyclerAdapter adapter = (PostRecyclerAdapter) mRecyclerView.getAdapter();
                if (adapter.isPendingRemoval(position)) {
                    return 0;
                }
                return super.getSwipeDirs(recyclerView, viewHolder);
            }
        };

        ItemTouchHelper mItemTouchHelper = new ItemTouchHelper(swipeHelper);
        mItemTouchHelper.attachToRecyclerView(mRecyclerView);

        //set swipe background-Color
        swipeHelper.setLeftColorCode(ContextCompat.getColor(getActivity(), R.color.md_red_800));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if(resultCode == Activity.RESULT_OK){
                Bundle b = data.getExtras();
                final Post post = b.getParcelable("post_item");
                post.setUid(FirebaseAuth.getInstance().getCurrentUser().getUid());
                DatabaseReference ref = mRootRef.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("fullName");
                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String name = dataSnapshot.getValue(String.class);
                        post.setName(name);
                        post.setStatus("Active");
                        final String uniqueId = UUID.randomUUID().toString();
                        post.setPid(uniqueId);
                        mPostRef.child(uniqueId).setValue(post);
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }

    public void CreatePost() {
        Intent i = new Intent(this.getActivity(), PostCreateActivity.class);
        startActivityForResult(i, 1);
    }

    private void getAllPosts(DataSnapshot dataSnapshot){
        for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){
            Post nPost = singleSnapshot.getValue(Post.class);
            posts.add(nPost);
            mPostRecyclerAdapter = new PostRecyclerAdapter(getContext(), posts, mRecyclerViewClickListener, null);
            mRecyclerView.setAdapter(mPostRecyclerAdapter);
        }
    }
//    private void deletePost(DataSnapshot dataSnapshot){
//        for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
//            String taskTitle = singleSnapshot.getValue(String.class);
//            for(int i = 0; i < posts.size(); i++){
//                if(posts.get(i).getTask().equals(taskTitle)){
//                    posts.remove(i);
//                }
//            }
//            mPostRecyclerAdapter.notifyDataSetChanged();
////            mPostRecyclerAdapter = new PostRecyclerAdapter(getContext(), posts, mRecyclerViewClickListener);
////            mRecyclerView.setAdapter(mPostRecyclerAdapter);
//        }
//    }
}
