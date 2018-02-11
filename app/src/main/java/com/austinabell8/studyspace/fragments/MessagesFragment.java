package com.austinabell8.studyspace.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.austinabell8.studyspace.R;
import com.austinabell8.studyspace.activities.ConversationActivity;
import com.austinabell8.studyspace.adapters.ConversationRecyclerAdapter;
import com.austinabell8.studyspace.model.Conversation;
import com.austinabell8.studyspace.utils.SearchListListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.UUID;


public class MessagesFragment extends Fragment implements View.OnClickListener {

    private MessagesFragment.OnFragmentInteractionListener mListener;
    private View inflatedMessages;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager llm;
    private ArrayList<Conversation> conversations;
    private ConversationRecyclerAdapter mConversationRecyclerAdapter;

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
        mRecyclerView = inflatedMessages.findViewById(R.id.rvConversations);

        mRootRef = FirebaseDatabase.getInstance().getReference();
        mUsersRef = mRootRef.child("users");

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
                    startActivity(intent);
                }
            }
        };

        mConversationRecyclerAdapter = new ConversationRecyclerAdapter(getContext(), conversations, mRecyclerViewClickListener);
        mRecyclerView.setAdapter(mConversationRecyclerAdapter);
        fillDummyData();

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
                "Want to meet up at Weldon at 6pm?",
                "");
        conversations.add(c);
        mConversationRecyclerAdapter.notifyDataSetChanged();
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

}