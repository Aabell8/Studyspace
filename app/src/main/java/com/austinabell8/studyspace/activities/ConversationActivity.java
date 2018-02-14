package com.austinabell8.studyspace.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import com.austinabell8.studyspace.R;
import com.austinabell8.studyspace.adapters.MessageListAdapter;
import com.austinabell8.studyspace.model.Message;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ConversationActivity extends AppCompatActivity {

    private static final String TAG = "ConversationActivity";

    public static final int DEFAULT_MSG_LENGTH_LIMIT = 1000;

    private ListView mMessageListView;
    private MessageListAdapter mMessageAdapter;
//    private ProgressBar mProgressBar;
//    private ImageButton mPhotoPickerButton;
    private EditText mMessageEditText;
    private ImageButton mSendButton;
    private Toolbar mToolbar;
//    private String mConversationId;

    private String mUsername;

    // Firebase instance variables
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mMessagesDatabaseReference;
    private ChildEventListener mChildEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);

        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        if (getSupportActionBar()!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("");

        }

        mUsername = FirebaseAuth.getInstance().getCurrentUser().getUid();

        Bundle data = getIntent().getExtras();
        String conversationId = "";
        String rId = "";
        String rName = "";
        if (data != null) {
            rId = data.getString("recipient_id");
            rName = data.getString("recipient_name");
            if(rName!=null){
                getSupportActionBar().setTitle(rName);
            }
            conversationId = data.getString("conversation_id");
        }

        final String recipientId = rId;
        final String recipientName = rName;

        mChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Message friendlyMessage = dataSnapshot.getValue(Message.class);
                mMessageAdapter.add(friendlyMessage);
            }

            public void onChildChanged(DataSnapshot dataSnapshot, String s) {}
            public void onChildRemoved(DataSnapshot dataSnapshot) {}
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
            public void onCancelled(DatabaseError databaseError) {}
        };

        // Initialize Firebase components
        mFirebaseDatabase = FirebaseDatabase.getInstance();


        if(conversationId!=null && !conversationId.equals("")){
            mMessagesDatabaseReference = mFirebaseDatabase.getReference()
                    .child("conversations").child(conversationId).child("messages");
            mMessagesDatabaseReference.addChildEventListener(mChildEventListener);
        }
        else if (recipientId!=null && !recipientId.equals("")) {
            final DatabaseReference conversationRef =
                    mFirebaseDatabase.getReference().child("conversations");
            conversationRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot convSnap : dataSnapshot.getChildren()) {
//                        final String recipient = recipientId;
                        if (convSnap.child(recipientId).getValue()!=null
                                && convSnap.child(mUsername).getValue()!=null){
                            mMessagesDatabaseReference = mFirebaseDatabase.getReference()
                                    .child("conversations")
                                    .child(convSnap.getKey())
                                    .child("messages");
                            mMessagesDatabaseReference.addChildEventListener(mChildEventListener);
                        }
                    }
                    if (mMessagesDatabaseReference==null){
                        Map<String,Object> taskMap = new HashMap<>();
                        taskMap.put(recipientId, recipientName);
                        taskMap.put(mUsername,
                                FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
                        String newId = UUID.randomUUID().toString();
                        taskMap.put("conversationId", newId);
                        mFirebaseDatabase.getReference()
                                .child("conversations")
                                .child(newId)
                                .updateChildren(taskMap);
                        mMessagesDatabaseReference = mFirebaseDatabase.getReference()
                                .child("conversations")
                                .child(newId)
                                .child("messages");
                        mMessagesDatabaseReference.addChildEventListener(mChildEventListener);
                    }

                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.e(TAG, "Retrieving conversations");
                }
            });
        }


        // Initialize references to views
        mMessageListView = findViewById(R.id.list_messages);
//        mPhotoPickerButton = findViewById(R.id.photoPickerButton);
        mMessageEditText = findViewById(R.id.edit_text_message);
        mSendButton = findViewById(R.id.button_send);


        // Initialize message ListView and its adapter
        List<Message> friendlyMessages = new ArrayList<>();
        mMessageAdapter = new MessageListAdapter(this, friendlyMessages);
        mMessageListView.setAdapter(mMessageAdapter);


        // Initialize progress bar
//        mProgressBar.setVisibility(ProgressBar.INVISIBLE);

        // ImagePickerButton shows an image picker to upload a image for a message
//        mPhotoPickerButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                // Image picker intent
//            }
//        });

        // Enable Send button when there's text to send
        mMessageEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() > 0) {
                    mSendButton.setEnabled(true);
                } else {
                    mSendButton.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        mMessageEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(DEFAULT_MSG_LENGTH_LIMIT)});

        // Send button sends a message and clears the EditText
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Message friendlyMessage = new Message(mMessageEditText.getText().toString(),
                        null, FirebaseAuth.getInstance().getCurrentUser().getUid());
                mMessagesDatabaseReference.push().setValue(friendlyMessage);

                // Clear input box
                mMessageEditText.setText("");
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return false;
    }

}