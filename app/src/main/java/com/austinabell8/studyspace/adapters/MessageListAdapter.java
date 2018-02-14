package com.austinabell8.studyspace.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.austinabell8.studyspace.R;
import com.austinabell8.studyspace.model.Message;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

/**
 * Created by austi on 2018-02-10.
 */

public class MessageListAdapter extends BaseAdapter {

    private List<Message> messages;
    private Context context;

    public MessageListAdapter(Context context, List<Message> m) {
        this.context = context;
        messages = m;
    }


    public void add(Message message) {
        this.messages.add(message);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return messages.size();
    }

    @Override
    public Object getItem(int i) {
        return messages.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        MessageViewHolder holder = new MessageViewHolder();
        LayoutInflater messageInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        Message message = messages.get(i);

        if (message.getSenderId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
            convertView = messageInflater.inflate(R.layout.item_message_sent, null);
            holder.messageBody = convertView.findViewById(R.id.text_message_body);
            convertView.setTag(holder);
            holder.messageBody.setText(message.getText());
        } else {
            convertView = messageInflater.inflate(R.layout.item_message_received, null);
            holder.profileImage = convertView.findViewById(R.id.iv_profile_pic);
//            holder.name = convertView.findViewById(R.id.name);
            holder.messageBody = convertView.findViewById(R.id.text_message_body);
            convertView.setTag(holder);

//            holder.name.setText(message.getName());
            holder.messageBody.setText(message.getText());

            final MessageViewHolder mViewHolder = holder;
//            StorageReference storageRef = FirebaseStorage.getInstance()
//                    .getReference().child("/Photos/profile_picture/2odB6ZaCrZU507gmpzvATc7Hmlt1/54a90d3d-2503-4443-a6ea-55dc861bc661/profile_pic");
//            //TODO: Get specific profile pic
//            storageRef.getBytes(2048*2048).addOnSuccessListener(new OnSuccessListener<byte[]>() {
//                @Override
//                public void onSuccess(byte[] bytes) {
//                    Glide.with(context)
//                            .load(bytes)
//                            .into(mViewHolder.profileImage);
//                }
//            });
        }

        return convertView;
    }

    class MessageViewHolder {
        public ImageView profileImage;
        public TextView name;
        public TextView messageBody;
    }
}

