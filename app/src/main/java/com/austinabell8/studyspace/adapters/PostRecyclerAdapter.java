package com.austinabell8.studyspace.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.austinabell8.studyspace.R;
import com.austinabell8.studyspace.helpers.RecyclerViewClickListener;
import com.austinabell8.studyspace.model.Post;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by aabell on 7/18/2017.
 */

public class PostRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Post> posts;
    private List<Post> staticPosts;
    private List<Post> itemsPendingRemoval;

    private static final int PENDING_REMOVAL_TIMEOUT = 3000; // 3 seconds
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    private Handler handler = new Handler(); // hanlder for running delayed runnables
    HashMap<Post, Runnable> pendingRunnables = new HashMap<>(); // map of items to pending runnables, so we can cancel a removal if need be


    private Context mContext;
    private static RecyclerViewClickListener itemListener;

    public PostRecyclerAdapter(Context context, List<Post> posts,
                               RecyclerViewClickListener itemListener){
        this.mContext = context;
        this.itemListener = itemListener;
        this.posts = posts;
        this.staticPosts = new ArrayList<>(posts);
        itemsPendingRemoval = new ArrayList<>();
    }

    @Override
    public int getItemViewType(int position) {
        if (isPositionHeader(position))
            return TYPE_HEADER;
        return TYPE_ITEM;
    }

    private boolean isPositionHeader (int position) {
        if (posts.get(position).getId() == null)
            return true;
        return false;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder (ViewGroup parent, int viewType){
        if (viewType == TYPE_ITEM) {
            View mView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_post_compound, parent, false);
            return new PostViewHolder(mView);
        }
//        else if (viewType == TYPE_HEADER){
//            View mView = LayoutInflater.from(parent.getContext())
//                    .inflate(R.layout.item_divider, parent, false);
//            return new DividerViewHolder(mView);
//        }

        throw new RuntimeException("There is no type that matches the type "+ viewType);

    }

    @SuppressLint("SimpleDateFormat")
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof PostViewHolder){
            final Post q = posts.get(position);
            PostViewHolder qHolder = (PostViewHolder) holder;

            if(itemsPendingRemoval.contains(q)){
                qHolder.regularLayout.setVisibility(View.INVISIBLE);
                qHolder.swipeLayout.setVisibility(View.VISIBLE);
                qHolder.undo.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick (View v){
                        undoOpt(q);
                    }
                });

            }
            else {
                qHolder.regularLayout.setVisibility(View.VISIBLE);
                qHolder.swipeLayout.setVisibility(View.INVISIBLE);

                qHolder.name.setText(q.getName());
                qHolder.tag.setText(q.getCourse());
                qHolder.dateId.setText(q.getId());
                qHolder.price.setText(q.getPrice());
                qHolder.status.setText(q.getStatus());

                final PostViewHolder mViewHolder = qHolder;
                qHolder.regularLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        itemListener.recyclerViewListClicked(v, mViewHolder.getLayoutPosition());
                    }

                });


            }
        }
        if (holder instanceof DividerViewHolder){
            Post q = posts.get(position);
            DividerViewHolder dHolder = (DividerViewHolder) holder;
            dHolder.name.setText(q.getName());
        }

    }

    private void undoOpt(Post post) {
        Runnable pendingRemovalRunnable = pendingRunnables.get(post);
        pendingRunnables.remove(post);
        if (pendingRemovalRunnable != null)
            handler.removeCallbacks(pendingRemovalRunnable);
        itemsPendingRemoval.remove(post);
        // this will rebind the row in "normal" state
        notifyItemChanged(posts.indexOf(post));
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public void pendingRemoval(int position) {

        final Post q = posts.get(position);
        if (!itemsPendingRemoval.contains(q)) {
            itemsPendingRemoval.add(q);
            // this will redraw row in "undo" state
            notifyItemChanged(position);
            // let's create, store and post a runnable to remove the data
//            Runnable pendingRemovalRunnable = new Runnable() {
//                @Override
//                public void run() {
//                    remove(posts.indexOf(q));
//                }
//            };
//            handler.postDelayed(pendingRemovalRunnable, PENDING_REMOVAL_TIMEOUT);
//            pendingRunnables.put(q, pendingRemovalRunnable);
        }
    }

    public void remove(int position) {
        Post data = posts.get(position);
        if (itemsPendingRemoval.contains(data)) {
            itemsPendingRemoval.remove(data);
        }
        if (posts.contains(data)) {
            posts.remove(position);
             staticPosts.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void removePending(){
        while (itemsPendingRemoval.size()>0){
            Post removal = itemsPendingRemoval.get(0);
            if (posts.contains(removal)) {
                int position = posts.indexOf(removal);
                posts.remove(removal);
                itemsPendingRemoval.remove(0);
                notifyItemRemoved(position);
            }
        }
    }

    public boolean isPendingRemoval(int position) {
        Post data = posts.get(position);
        return itemsPendingRemoval.contains(data);
    }

    private class PostViewHolder extends RecyclerView.ViewHolder  {

        public LinearLayout regularLayout;
        public LinearLayout swipeLayout;

        public TextView name;
        public TextView tag;
        public TextView dateId;
        public TextView price;
        public TextView status;

        public TextView undo;

        public PostViewHolder(View view) {
            super(view);
            regularLayout = (LinearLayout) view.findViewById(R.id.item_post);
            name = (TextView) view.findViewById(R.id.post_name);
            tag = (TextView) view.findViewById(R.id.post_tag);
            dateId = (TextView) view.findViewById(R.id.post_date_id);
            price = (TextView) view.findViewById(R.id.post_price);
            status = (TextView) view.findViewById(R.id.post_status);

            swipeLayout = (LinearLayout) view.findViewById(R.id.item_post_remove);
            undo = (TextView) view.findViewById(R.id.undo);
        }

    }

    private class DividerViewHolder extends RecyclerView.ViewHolder  {

        public TextView name;

        public DividerViewHolder(View view) {
            super(view);
//            name = (TextView) view.findViewById(R.id.divider_name);
        }

    }

    public void filter(String text) {
        posts.clear();
        if(text.isEmpty()){
            posts.addAll(staticPosts);
        } else{
            text = text.toLowerCase();
            for (Post item: staticPosts) {
                if(item.getName().toLowerCase().contains(text)
                        || item.getId().toLowerCase().contains(text)
                        || item.getCourse().toLowerCase().contains(text)){
                    posts.add(item);
                }
            }
        }
        notifyDataSetChanged();
    }

    public void sortByName () {
//        Collections.sort(posts, new Comparator<post>() {
//            @Override
//            public int compare(post c1, post c2) {
//                // ascending order
//                int result = c1.getName().compareTo(c2.getName());
//                if (result == 0){
//                    return c2.getDate().compareTo(c1.getDate());
//                }
//                else {
//                    return result;
//                }
//
//            }
//        });
//        staticPosts = new ArrayList<>(posts);
//        notifyDataSetChanged();
    }

    public void sortById () {
//        Collections.sort(posts, new Comparator<post>() {
//            @Override
//            public int compare(post c1, post c2) {
//                // ascending order
//                int result =  c1.getCourse().compareTo(c2.getCourse());
//                if (result == 0){
//                    return c2.getDate().compareTo(c1.getDate());
//                }
//                else {
//                    return result;
//                }
//            }
//        });
//        staticPosts = new ArrayList<>(posts);
//        notifyDataSetChanged();
    }

    public void sortByDate () {
//        Collections.sort(posts, new Comparator<post>() {
//            @Override
//            public int compare(post c1, post c2) {
//                // ascending order
//                int result =  c2.getDate().compareTo(c1.getDate());
//                if (result == 0){
//                    return c1.getName().compareTo(c2.getName());
//                }
//                else {
//                    return result;
//                }
//            }
//        });
//        staticPosts = new ArrayList<>(posts);
//        notifyDataSetChanged();
    }

}

