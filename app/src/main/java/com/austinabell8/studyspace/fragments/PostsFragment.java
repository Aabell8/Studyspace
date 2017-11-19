package com.austinabell8.studyspace.fragments;

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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.austinabell8.studyspace.R;
import com.austinabell8.studyspace.activities.PostCreateActivity;
import com.austinabell8.studyspace.adapters.PostRecyclerAdapter;
import com.austinabell8.studyspace.helpers.RecyclerViewClickListener;
import com.austinabell8.studyspace.helpers.SwipeUtil;
import com.austinabell8.studyspace.model.Post;

import java.util.ArrayList;


public class PostsFragment extends Fragment implements View.OnClickListener {

//    private static final int TYPE_NAME = 0;
//    private static final int TYPE_DATE = 1;

    private OnFragmentInteractionListener mListener;
    private View inflatedPosts;
    private RecyclerView mRecyclerView;
    private ArrayList<Post> arrayOfPosts;
    private PostRecyclerAdapter mPostRecyclerAdapter;
    private LinearLayoutManager llm;
    private FloatingActionButton mFab;
    private SwipeRefreshLayout mSwipeRefreshLayout;
//    private Menu optionsMenu;

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

        populatePostsList();

        mSwipeRefreshLayout = (SwipeRefreshLayout) inflatedPosts.findViewById(R.id.swipe_refresh_layout_posts);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mPostRecyclerAdapter.removePending();
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                }, 100);

            }
        });

        mPostRecyclerAdapter = new PostRecyclerAdapter(getContext(),
                arrayOfPosts, new RecyclerViewClickListener() {
            @Override
            public void recyclerViewListClicked(View v, int position) {
//                Intent intent = new Intent(getActivity(), PostDetailsActivity.class);
//                intent.putExtra("post_item", arrayOfposts.get(position));
//                startActivity(intent);
            }
        });

        mRecyclerView.setAdapter(mPostRecyclerAdapter);
        setSwipeForRecyclerView();

        mPostRecyclerAdapter.sortByName();

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
//                mpostRecyclerAdapter.filter(newText);
//                return true;
//            }
//        });
//        optionsMenu = menu;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_logout:
                return false;
//            case R.id.action_post_sort_name:
//                if (!item.isChecked()){
//                    mpostRecyclerAdapter.sortByName();
//                    item.setChecked(true);
//                }
//                scrollToTop();
//                return true;
//            case R.id.action_post_sort_ID:
//                if (!item.isChecked()){
//                    mpostRecyclerAdapter.sortById();
//                    item.setChecked(true);
//                }
//                scrollToTop();
//                return true;
//            case R.id.action_post_sort_date:
//                if (!item.isChecked()){
//                    mpostRecyclerAdapter.sortByDate();
//                    item.setChecked(true);
//                }
//                scrollToTop();
//                return true;
            default:
                break;
        }
        return false;
    }


    private void populatePostsList(){
        // Construct the data source
        if (arrayOfPosts == null){
            arrayOfPosts = Post.getPosts();
        }

        mRecyclerView = inflatedPosts.findViewById(R.id.rvPosts);

        mRecyclerView.setHasFixedSize(true);
        llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(llm);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(
                mRecyclerView.getContext(), llm.getOrientation());
        mRecyclerView.addItemDecoration(dividerItemDecoration);
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

    public void CreatePost() {
        Intent intent = new Intent(this.getActivity(), PostCreateActivity.class);
        startActivity(intent);
    }
}
