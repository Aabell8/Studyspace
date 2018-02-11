package com.austinabell8.studyspace.adapters.Tutor;

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
import com.austinabell8.studyspace.model.Post;
import com.austinabell8.studyspace.utils.SearchPostClickListener;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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
    private static SearchPostClickListener itemListener;

    public PostSearchRecyclerAdapter(Context context, List<Post> posts,
                                     SearchPostClickListener itemListener) {
        this.mContext = context;
        this.itemListener = itemListener;
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
        final PostSearchViewHolder pHolder = (PostSearchViewHolder) holder;

        //Update data in PostSearchViewHolder
        pHolder.name.setText(p.getName());
        pHolder.course.setText(p.getCourse());
        pHolder.description.setText(p.getDescription());
        pHolder.price.setText(p.getPrice());

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();

        ref.child("post_applicants").child(p.getPid())
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue()!=null){
                    pHolder.applyButton.setText(R.string.applied);
                    pHolder.applyButton.setTextColor(mContext.getResources().getColor(R.color.colorAccent));
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("PostSearch: ", "Failed to retrieve applied value");
            }
        });

        final PostSearchViewHolder mViewHolder = pHolder;

        ref.child("users/"+p.getUid()+"/profilePicLocation")
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String location = dataSnapshot.getValue(String.class);
                if (location != null){

                    StorageReference storageRef = FirebaseStorage.getInstance()
                            .getReference().child(location);
                    storageRef.getBytes(2048*2048).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                        @Override
                        public void onSuccess(byte[] bytes) {
                            Glide.with(mContext)
                                    .load(bytes)
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

        if(itemListener != null){
            pHolder.regularLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemListener.recyclerViewListClicked(v, mViewHolder.getLayoutPosition());
                }

            });

            pHolder.applyButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    itemListener.applyButtonClicked(view, mViewHolder.getLayoutPosition());
                    mViewHolder.applyButton.setText(R.string.applied);
                    mViewHolder.applyButton.setTextColor(mContext.getResources().getColor(R.color.colorAccent));
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return posts.size();
    }


    private class PostSearchViewHolder extends RecyclerView.ViewHolder {

        LinearLayout regularLayout;
        ImageView profilePic;
        TextView name;
        TextView course;
        TextView description;
        TextView price;
        Button applyButton;

        PostSearchViewHolder(View view) {
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
