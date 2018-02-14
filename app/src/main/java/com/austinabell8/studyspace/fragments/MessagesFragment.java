package com.austinabell8.studyspace.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.austinabell8.studyspace.R;
import com.austinabell8.studyspace.activities.ConversationActivity;
import com.austinabell8.studyspace.adapters.ConversationRecyclerAdapter;
import com.austinabell8.studyspace.model.Conversation;
import com.austinabell8.studyspace.utils.SearchListListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.UUID;


public class MessagesFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "MessagesFragment";

    private MessagesFragment.OnFragmentInteractionListener mListener;
    private View inflatedMessages;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager llm;
    private ArrayList<Conversation> conversations;
    private ConversationRecyclerAdapter mConversationRecyclerAdapter;

    private String mCurrentUserId;

    private DatabaseReference mRootRef;
    private DatabaseReference mUsersRef;

    private SearchListListener mRecyclerViewClickListener;

    public MessagesFragment() {
        // Required empty public constructor
    }

    public static MessagesFragment newInstance() {
        MessagesFragment fragment = new MessagesFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        inflatedMessages = inflater.inflate(R.layout.fragment_messages, container, false);
        mRecyclerView = inflatedMessages.findViewById(R.id.rv_conversations);

        mRootRef = FirebaseDatabase.getInstance().getReference();
        mUsersRef = mRootRef.child("users");
        mCurrentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        mRecyclerView.setHasFixedSize(true);
        llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(llm);

        conversations = new ArrayList<>();

        mRecyclerViewClickListener = new SearchListListener() {
            @Override
            public void recyclerViewListClicked(View v, int position) {
                if (position != -1){
                    //Retrieve Id from item clicked, and pass it into an intent
                    Intent intent = new Intent(v.getContext(), ConversationActivity.class);
                    intent.putExtra("conversation_id", conversations.get(position).getConversationId());
                    intent.putExtra("recipient_name", conversations.get(position).getFrom());
                    startActivity(intent);
                }
            }
        };

        mConversationRecyclerAdapter = new ConversationRecyclerAdapter(getContext(), conversations, mRecyclerViewClickListener);
        mRecyclerView.setAdapter(mConversationRecyclerAdapter);
//        fillDummyData();
        retrieveConversations();

        return inflatedMessages;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MessagesFragment.OnFragmentInteractionListener) {
            mListener = (MessagesFragment.OnFragmentInteractionListener) context;
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
    public void onClick(View v) {

    }

    private void fillDummyData(){
        Conversation c = new Conversation(
                UUID.randomUUID().toString(),
                "Austin Abell",
                "Want to meet up at Weldon at 6pm?");
        conversations.add(c);
        mConversationRecyclerAdapter.notifyDataSetChanged();
    }

    private void retrieveConversations() {
        DatabaseReference conversationRef =  mRootRef.child("conversations");
        conversationRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                conversations.clear();
                for (DataSnapshot convItem: dataSnapshot.getChildren()){
                    if (convItem.child(mCurrentUserId).getValue()!=null){
                        for (DataSnapshot convDetails: convItem.getChildren()){
                            if (!convDetails.getKey().equals(mCurrentUserId)
                                    && !convDetails.getKey().equals("conversationId")
                                    && !convDetails.getKey().equals("messages")
                                    && !convDetails.getKey().equals("preview")){ //TODO: preview may change
                                String conversationId =
                                        convItem.child("conversationId").getValue(String.class);
                                String otherName = convDetails.getValue(String.class);
                                Conversation newConversation =
                                        new Conversation(conversationId, otherName, "");
                                conversations.add(newConversation);
                                break;
                            }
                        }
                    }
                }
                mConversationRecyclerAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "Failed to retrieve conversation list");
            }
        });
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

}