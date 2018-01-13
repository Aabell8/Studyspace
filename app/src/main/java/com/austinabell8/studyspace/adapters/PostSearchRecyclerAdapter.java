package com.austinabell8.studyspace.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.austinabell8.studyspace.R;
import com.austinabell8.studyspace.utils.RecyclerViewClickListener;
import com.austinabell8.studyspace.model.Post;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

/**
 * PostSearchRecyclerAdapter - Adapter for recycler view of posts from MainActivity
 * @author  Austin Abell
 */

public class PostSearchRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Post> posts;

    private Context mContext;
    private static RecyclerViewClickListener itemListener;
//    private static RecyclerViewLongClickListener itemLongListener;

    public PostSearchRecyclerAdapter(Context context, List<Post> posts,
                                     RecyclerViewClickListener itemListener) {
        this.mContext = context;
        this.itemListener = itemListener;
//        this.itemLongListener = itemLongListener;
        this.posts = posts;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View mView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_post_search, parent, false);

        final PostSearchViewHolder mViewHolder = new PostSearchViewHolder(mView);

        return mViewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Post p = posts.get(position);
        PostSearchViewHolder pHolder = (PostSearchViewHolder) holder;

        //Update data in PostSearchViewHolder
        pHolder.name.setText(p.getName());
        pHolder.course.setText(p.getCourse());
        pHolder.description.setText(p.getDescription());
        pHolder.price.setText(p.getPrice());

        final PostSearchViewHolder mViewHolder = pHolder;

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                .child("users/"+p.getUid()+"/profilePicLocation");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String location = dataSnapshot.getValue(String.class);
                if (location != null){

                    StorageReference storageRef = FirebaseStorage.getInstance()
                            .getReference().child(location);
//                    storageRef.getPath();
                    storageRef.getBytes(2048*2048).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                        @Override
                        public void onSuccess(byte[] bytes) {
                            Glide.with(mContext)
                                    .load(bytes)
//                                        .transform(new CircleTransform(getContext()))
//                                            .asBitmap()
                                    .into(mViewHolder.profilePic);
                        }
                    });
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("PostSearch", "Error retrieving user");
            }
        });
//        Glide.with(pHolder.productThumbnail)
//                .load(p.getImage().getSrc())
//                .into(pHolder.productThumbnail);

        if(itemListener != null){
            pHolder.regularLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemListener.recyclerViewListClicked(v, mViewHolder.getLayoutPosition());
                }

            });

            pHolder.regularLayout.setOnLongClickListener(new View.OnLongClickListener(){

                @Override
                public boolean onLongClick(View view) {
                    itemListener.recyclerViewListLongClicked(view, mViewHolder.getLayoutPosition());
                    return true;
                }
            });
        }

//        if(itemLongListener != null){
//            pHolder.regularLayout.setOnLongClickListener(new View.OnLongClickListener() {
//                @Override
//                public boolean onLongClick(View v) {
//                    itemLongListener.recyclerViewListLongClicked(v, mViewHolder.getLayoutPosition());
//                    return true;
//                }
//
//            });
//        }
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }


    private class PostSearchViewHolder extends RecyclerView.ViewHolder {

        public LinearLayout regularLayout;
        public ImageView profilePic;
        public TextView name;
        public TextView course;
        public TextView description;
        public TextView price;
        public Button applyButton;

        public PostSearchViewHolder(View view) {
            super(view);
            regularLayout = view.findViewById(R.id.item_post);
            profilePic = view.findViewById(R.id.iv_post_item);
            name = view.findViewById(R.id.post_name);
            course = view.findViewById(R.id.post_course);
            description = view.findViewById(R.id.post_description);
            price = view.findViewById(R.id.post_price);
            applyButton = view.findViewById(R.id.btn_apply);

        }

    }
}
