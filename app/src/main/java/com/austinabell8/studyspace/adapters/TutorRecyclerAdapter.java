package com.austinabell8.studyspace.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.austinabell8.studyspace.R;
import com.austinabell8.studyspace.model.User;
import com.austinabell8.studyspace.utils.RecyclerViewClickListener;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

/**
 * TutorRecyclerAdapter - Adapter for recycler view of users from MainActivity
 * @author  Austin Abell
 */

public class TutorRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<User> users;

    private Context mContext;
    private static RecyclerViewClickListener itemListener;

    public TutorRecyclerAdapter(Context context, List<User> users,
                                     RecyclerViewClickListener itemListener) {
        this.mContext = context;
        this.itemListener = itemListener;
        this.users = users;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View mView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_tutor, parent, false);

        final TutorViewHolder mViewHolder = new TutorViewHolder(mView);

        return mViewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        User p = users.get(position);
        TutorViewHolder pHolder = (TutorViewHolder) holder;

        //Update data in TutorViewHolder
        pHolder.name.setText(p.getFullName());
        if(p.getRating()!=null){
            pHolder.rating.setText(p.getRating());
        }
        pHolder.note.setText(p.getUsername());
        if(p.getRate()!=null){
            pHolder.rate.setText(p.getRate());
        }
        else{
            pHolder.rate.setText(R.string.not_set);
        }

        final TutorViewHolder mViewHolder = pHolder;

        if (p.getProfilePicLocation() != null){

            StorageReference storageRef = FirebaseStorage.getInstance()
                    .getReference().child(p.getProfilePicLocation());
            storageRef.getBytes(2048*2048).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    Glide.with(mContext)
                            .load(bytes)
                            .into(mViewHolder.profilePic);
                }
            });
        }

        if(itemListener != null){
            pHolder.regularLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemListener.recyclerViewListClicked(v, mViewHolder.getLayoutPosition());
                }

            });
        }

    }

    @Override
    public int getItemCount() {
        return users.size();
    }


    private class TutorViewHolder extends RecyclerView.ViewHolder {

        LinearLayout regularLayout;
        ImageView profilePic;
        TextView name;
        TextView rating;
        TextView note;
        TextView rate;

        TutorViewHolder(View view) {
            super(view);
            regularLayout = view.findViewById(R.id.item_post);
            profilePic = view.findViewById(R.id.iv_post_item);
            name = view.findViewById(R.id.tutor_name);
            rating = view.findViewById(R.id.tutor_rating);
            note = view.findViewById(R.id.tutor_note);
            rate = view.findViewById(R.id.suggested_rate_value);

        }

    }
}
