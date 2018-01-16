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
import com.austinabell8.studyspace.activities.PostSearchActivity;
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


public class TPostsFragment extends Fragment implements View.OnClickListener {

//    private static final int TYPE_NAME = 0;
//    private static final int TYPE_DATE = 1;

    private OnFragmentInteractionListener mListener;
    private View inflatedPosts;
    private RecyclerView mRecyclerView;
    private ArrayList<Post> mPosts;
    private PostRecyclerAdapter mPostRecyclerAdapter;
    private LinearLayoutManager llm;
    private FloatingActionButton mFab;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private RecyclerViewClickListener mRecyclerViewClickListener;

    private DatabaseReference mRootRef;
    private DatabaseReference mPostRef;
//    private Menu optionsMenu;

    public TPostsFragment() {
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
        inflatedPosts = inflater.inflate(R.layout.fragment_t_posts, container, false);

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

        mPosts = new ArrayList<>();

        mPostRecyclerAdapter = new PostRecyclerAdapter(getContext(), mPosts, mRecyclerViewClickListener);
        mRecyclerView.setAdapter(mPostRecyclerAdapter);
        setSwipeForRecyclerView();

        DatabaseReference currentUserRef = mRootRef.child("tutor_applications")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        currentUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mPosts.clear();
                for (DataSnapshot post : dataSnapshot.getChildren()){
                    String pId = post.getKey();
                    mPostRef.child(pId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            mPosts.add(dataSnapshot.getValue(Post.class));
                            mPostRecyclerAdapter.notifyDataSetChanged();
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });
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
                SearchPost();
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
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_logout:
                return false;
            default:
                break;
        }
        return false;
    }

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
            if(resultCode == Activity.RESULT_OK) {
                Bundle b = data.getExtras();
                final String pId = b.getString("Pid");
                DatabaseReference ref = mRootRef.child("posts").child(pId);
                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Post app = dataSnapshot.getValue(Post.class);
                        mPosts.add(app);
                        mPostRecyclerAdapter.notifyDataSetChanged();
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.e("TPostsFrag", "Failed to retrieve applied post.");
                    }
                });
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }

    public void SearchPost() {
        Intent i = new Intent(this.getActivity(), PostSearchActivity.class);
        startActivityForResult(i, 1);
    }

    private void getAllPosts(){
//        for (String app: mApplied) {
//            Query tQuery = mPostRef.orderByChild("uid").equalTo(app);
//            tQuery.addValueEventListener(new ValueEventListener() {
//                @Override
//                public void onDataChange(DataSnapshot dataSnapshot) {
//
//                }
//
//                @Override
//                public void onCancelled(DatabaseError databaseError) {
//
//                }
//            });
//        }
    }
}
