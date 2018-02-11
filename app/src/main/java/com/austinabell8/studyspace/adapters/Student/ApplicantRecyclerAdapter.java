package com.austinabell8.studyspace.adapters.Student;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.austinabell8.studyspace.R;
import com.austinabell8.studyspace.model.User;
import com.austinabell8.studyspace.utils.ApplicantClickListener;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

/**
 * ApplicantRecyclerAdapter - Adapter for recycler view of users from MainActivity
 * @author  Austin Abell
 */

public class ApplicantRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<User> users;

    private Context mContext;
    private String postId;
    private static ApplicantClickListener itemListener;
    private DatabaseReference mApplicantRef;

    public ApplicantRecyclerAdapter(Context context, List<User> users,
                                    ApplicantClickListener itemListener, String postId) {
        this.mContext = context;
        this.itemListener = itemListener;
        this.users = users;
        this.postId = postId;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View mView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_tutor, parent, false);

        final TutorViewHolder mViewHolder = new TutorViewHolder(mView);
        mApplicantRef = FirebaseDatabase.getInstance().getReference().child("post_applicants");

        return mViewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        User user = users.get(position);
//        DatabaseReference postRef = mApplicantRef.child(postId)
        TutorViewHolder pHolder = (TutorViewHolder) holder;

        //Update data in TutorViewHolder
        pHolder.name.setText(user.getFullName());
        if(user.getRating()!=null){
            pHolder.rating.setText(user.getRating());
        }
        pHolder.note.setText(user.getUsername());
        if(user.getRate()!=null){
            pHolder.rate.setText(user.getRate());
        }
        else{
            pHolder.rate.setText(R.string.not_set);
        }
        if(user.isAccepted()){
            pHolder.cardView
                    .setCardBackgroundColor(mContext.getResources().getColor(R.color.md_teal_100));
        }


        final TutorViewHolder mViewHolder = pHolder;

        if (user.getProfilePicLocation() != null){

            StorageReference storageRef = FirebaseStorage.getInstance()
                    .getReference().child(user.getProfilePicLocation());
            storageRef.getBytes(2048*2048).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    Glide.with(mContext)
                            .load(bytes)
                            .into(mViewHolder.profilePic);
                }
            });
        }

        if(itemListener != null) {
            pHolder.viewLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemListener.messageClick(v, mViewHolder.getLayoutPosition());
                }
            });
            pHolder.viewLayout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    itemListener.acceptClick(v, mViewHolder.getLayoutPosition());
                    mViewHolder.cardView
                            .setCardBackgroundColor(mContext.getResources().getColor(R.color.md_teal_100));
                    return true;
                }
            });
            pHolder.profilePic.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    itemListener.userDetailsClick(v, mViewHolder.getLayoutPosition());
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return users.size();
    }


    private class TutorViewHolder extends RecyclerView.ViewHolder {
        LinearLayout viewLayout;

        CardView cardView;
        ImageView profilePic;
        TextView name;
        TextView rating;
        TextView note;
        TextView rate;

        TutorViewHolder(View view) {
            super(view);
            viewLayout = view.findViewById(R.id.item_post);
            cardView = view.findViewById(R.id.card_view_item_tutor);
            profilePic = view.findViewById(R.id.iv_post_item);
            name = view.findViewById(R.id.tutor_name);
            rating = view.findViewById(R.id.tutor_rating);
            note = view.findViewById(R.id.tutor_note);
            rate = view.findViewById(R.id.suggested_rate_value);



        }

    }
}
