package com.austinabell8.studyspace.adapters;

import android.content.Context;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.austinabell8.studyspace.R;
import com.austinabell8.studyspace.helpers.RecyclerViewClickListener;
import com.austinabell8.studyspace.model.User;
import java.util.List;

/**
 * Created by aabell on 7/18/2017.
 */

public class ApplicantRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<User> users;

    private Context mContext;
    private static RecyclerViewClickListener itemListener;

    public ApplicantRecyclerAdapter(Context context, List<User> users,
                                    RecyclerViewClickListener itemListener) {
        this.mContext = context;
        this.itemListener = itemListener;
        this.users = users;
    }



    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View mView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(mView);


    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        final User q = users.get(position);
        UserViewHolder uHolder = (UserViewHolder) holder;

            uHolder.regularLayout.setVisibility(View.VISIBLE);

            uHolder.name.setText(q.getFullName());
            uHolder.username.setText(q.getUsername());
            uHolder.rating.setText(q.getRating());

            final UserViewHolder mViewHolder = uHolder;
            uHolder.regularLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemListener.recyclerViewListClicked(v, mViewHolder.getLayoutPosition());
                }

            });

        uHolder.message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(mContext, "Message " + q.getFullName(), Toast.LENGTH_SHORT).show();
                // Do things
            }
        });

    }
    @Override
    public int getItemCount() {
        return users.size();
    }

    private class UserViewHolder extends RecyclerView.ViewHolder {

        public LinearLayout regularLayout;

        public TextView name;
        public TextView username;
        public TextView rating;
        public ImageButton message;


        public UserViewHolder(View view) {
            super(view);
            regularLayout = view.findViewById(R.id.item_user);
            name = view.findViewById(R.id.user_name);
            username = (TextView) view.findViewById(R.id.user_username);
            rating = (TextView) view.findViewById(R.id.user_rating);
        }

    }

}

