package com.austinabell8.studyspace.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.austinabell8.studyspace.R;
import com.austinabell8.studyspace.model.Post;
import com.austinabell8.studyspace.utils.RecyclerViewClickListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by aabell on 7/18/2017.
 */

public class PostApplicationsRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Post> posts;
    private List<Post> mRemovedPosts;

    private Context mContext;
    private static RecyclerViewClickListener itemListener;

    public PostApplicationsRecyclerAdapter(Context context, List<Post> posts,
                                           RecyclerViewClickListener itemListener){
        this.mContext = context;
        this.itemListener = itemListener;
        this.posts = posts;
        mRemovedPosts = new ArrayList<>();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder (ViewGroup parent, int viewType){
            View mView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_post, parent, false);
            return new PostViewHolder(mView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof PostViewHolder){
            final Post q = posts.get(position);
            PostViewHolder qHolder = (PostViewHolder) holder;

            if (q!=null) {
                qHolder.regularLayout.setVisibility(View.VISIBLE);

                qHolder.name.setText(q.getName());
                qHolder.tag.setText(q.getCourse());
                qHolder.dateId.setText(q.getDescription());
                qHolder.price.setText(q.getPrice());
                qHolder.status.setText(q.getStatus());

                final PostViewHolder mViewHolder = qHolder;
                if(itemListener != null){
                    qHolder.regularLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            itemListener.recyclerViewListClicked(v, mViewHolder.getLayoutPosition());
                        }

                    });

                    qHolder.regularLayout.setOnLongClickListener(new View.OnLongClickListener() {

                        @Override
                        public boolean onLongClick(View view) {
                            itemListener.recyclerViewListLongClicked(view, mViewHolder.getLayoutPosition());
                            return true;
                        }
                    });
                }

            }
        }
        if (holder instanceof DividerViewHolder){
            Post q = posts.get(position);
            DividerViewHolder dHolder = (DividerViewHolder) holder;
            dHolder.name.setText(q.getName());
        }

    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public void remove(int position) {
        Post data = posts.get(position);
        mRemovedPosts.add(data);
        posts.remove(data);
        notifyItemRemoved(position);
    }

    private void removeFromFirebase(final Post rPost){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                .child("tutor_applications")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(rPost.getPid());
        ref.removeValue();
    }

    public void clearRemoved(){
        for (Post p: mRemovedPosts){
            removeFromFirebase(p);
        }
        mRemovedPosts.clear();
    }

    public int removedCount(){
        return mRemovedPosts.size();
    }

    public void undoRemoved(){
        posts.addAll(mRemovedPosts);
        mRemovedPosts.clear();
        notifyDataSetChanged();
    }

    private class PostViewHolder extends RecyclerView.ViewHolder  {

        public LinearLayout regularLayout;

        public TextView name;
        public TextView tag;
        public TextView dateId;
        public TextView price;
        public TextView status;

        public PostViewHolder(View view) {
            super(view);
            regularLayout = view.findViewById(R.id.item_post);
            name = view.findViewById(R.id.post_name);
            tag = view.findViewById(R.id.post_tag);
            dateId = view.findViewById(R.id.post_date_id);
            price = view.findViewById(R.id.post_price);
            status = view.findViewById(R.id.post_status);

        }

    }

    private class DividerViewHolder extends RecyclerView.ViewHolder  {

        public TextView name;

        public DividerViewHolder(View view) {
            super(view);
//            name = (TextView) view.findViewById(R.id.divider_name);
        }

    }


}

