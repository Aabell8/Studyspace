package com.austinabell8.studyspace.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.Pair;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.austinabell8.studyspace.activities.Tutor.PostDetailsActivity;
import com.austinabell8.studyspace.R;
import com.austinabell8.studyspace.activities.Tutor.PostSearchInputActivity;
import com.austinabell8.studyspace.adapters.Tutor.PostApplicationsRecyclerAdapter;
import com.austinabell8.studyspace.utils.RecyclerViewClickListener;
import com.austinabell8.studyspace.utils.SwipeUtil;
import com.austinabell8.studyspace.model.Post;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class TPostsFragment extends Fragment implements View.OnClickListener {

//    private static final int TYPE_NAME = 0;
//    private static final int TYPE_DATE = 1;

    private OnFragmentInteractionListener mListener;
    private View inflatedPosts;
    private RecyclerView mRecyclerView;
    private List<Post> mPosts;
    private PostApplicationsRecyclerAdapter mPostRecyclerAdapter;
    private LinearLayoutManager llm;
    private FloatingActionButton mFab;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private Snackbar mSnackbar;

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

        mRecyclerView = inflatedPosts.findViewById(R.id.rv_posts);

        mRecyclerView.setHasFixedSize(true);
        llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(),
                llm.getOrientation());
        mRecyclerView.addItemDecoration(dividerItemDecoration);
        mRecyclerView.setLayoutManager(llm);

        mPosts = new ArrayList<>();

        mRecyclerViewClickListener = new RecyclerViewClickListener() {
            @Override
            public void recyclerViewListClicked(View v, int position) {
                if (position != -1){
                    //Retrieve Id from item clicked, and pass it into an intent
                    Intent intent = new Intent(v.getContext(), PostDetailsActivity.class);
                    intent.putExtra("post_intent", mPosts.get(position));
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
                            ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(), namePair,coursePair,pricePair);

                    ActivityCompat.startActivity(v.getContext(), intent, options.toBundle());
                }
            }
            @Override
            public void recyclerViewListLongClicked(View v, int position) {
            }
        };

        mPostRecyclerAdapter = new PostApplicationsRecyclerAdapter(getContext(), mPosts, mRecyclerViewClickListener);
        mRecyclerView.setAdapter(mPostRecyclerAdapter);
        setSwipeForRecyclerView();

        refreshPostApplications();

        mSwipeRefreshLayout = inflatedPosts.findViewById(R.id.swipe_refresh_layout_posts);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (mPostRecyclerAdapter!=null){
                            refreshPostApplications();
                            mPostRecyclerAdapter.clearRemoved();
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
        mFab = inflatedPosts.findViewById(R.id.fab);
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
        mPostRecyclerAdapter.clearRemoved();
    }

    @Override
    public void onClick(View v) {

    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    public void scrollToTop () {
        mRecyclerView.smoothScrollToPosition(0);
    }

    private void setSwipeForRecyclerView() {
        SwipeUtil swipeHelper = new SwipeUtil(0, ItemTouchHelper.LEFT, getActivity()) {
            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int swipedPosition = viewHolder.getAdapterPosition();
//                PostApplicationsRecyclerAdapter adapter = (PostApplicationsRecyclerAdapter) mRecyclerView.getAdapter();
                mPostRecyclerAdapter.remove(swipedPosition);
                //noinspection ConstantConditions
                mSnackbar = Snackbar.make(getActivity().findViewById(R.id.tutor_fragment_coordinator_layout),
                        mPostRecyclerAdapter.removedCount() + " items removed",
                        Snackbar.LENGTH_LONG);
                mSnackbar.setAction("Undo", new View.OnClickListener(){
                    @Override
                    public void onClick(View view) {
                        mPostRecyclerAdapter.undoRemoved();
                    }
                });
                mSnackbar.show();
            }
            @Override
            public int getSwipeDirs(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
//                int position = viewHolder.getAdapterPosition();
//                PostRecyclerAdapter adapter = (PostRecyclerAdapter) mRecyclerView.getAdapter();
//                if (adapter.isPendingRemoval(position)) {
//                    return 0;
//                }
                return super.getSwipeDirs(recyclerView, viewHolder);
            }
        };

        ItemTouchHelper mItemTouchHelper = new ItemTouchHelper(swipeHelper);
        mItemTouchHelper.attachToRecyclerView(mRecyclerView);

        //set swipe background-Color
        swipeHelper.setLeftColorCode(ContextCompat.getColor(getActivity(), R.color.md_red_800));
    }

    public void SearchPost() {
        Intent i = new Intent(this.getActivity(), PostSearchInputActivity.class);
        startActivity(i);
    }

    private void refreshPostApplications () {
        mPostRecyclerAdapter.clearRemoved();
        final DatabaseReference postApplicantRef = mRootRef.child("post_applicants");
        postApplicantRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mPosts.clear();
                for (DataSnapshot post : dataSnapshot.getChildren()) {
                    if (post.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).getValue() != null){
                        final String uId = post.getKey();
                        mPostRef.child(uId).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if(dataSnapshot.getValue()!=null){
                                    mPosts.add(dataSnapshot.getValue(Post.class));
                                    mPostRecyclerAdapter.notifyDataSetChanged();
                                }
                                else {
                                    postApplicantRef.child(uId).removeValue();
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
    }

}
