package com.austinabell8.studyspace.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.austinabell8.studyspace.R;
import com.austinabell8.studyspace.activities.LoginActivity;
import com.austinabell8.studyspace.activities.RoleActivity;
import com.austinabell8.studyspace.activities.StudentActivity;
import com.austinabell8.studyspace.activities.TutorActivity;
import com.austinabell8.studyspace.helpers.CircleTransform;
import com.austinabell8.studyspace.model.User;
import com.bumptech.glide.Glide;
import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.UUID;


import static android.app.Activity.RESULT_OK;


public class ProfileFragment extends Fragment implements View.OnClickListener {

    private ProfileFragment.OnFragmentInteractionListener mListener;
    private View inflatedProfile;
    private Button mLogoutButton;
    private ImageButton mPhotoPickerButton;
    private TextView mNameText;
    private TextView mUsernameText;
    private ImageView mImageView;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private ValueEventListener mValueListener;
    private StorageReference mStorage;
    private String currentUserId;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mCurrentUserDatabaseReference;

    private static final int GALLERY_INTENT=2;

    public ProfileFragment() {
        // Required empty public constructor
    }

//    public static ProfileFragment newInstance() {
//        ProfileFragment fragment = new ProfileFragment();
//        return fragment;
//    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        inflatedProfile = inflater.inflate(R.layout.fragment_profile, container, false);
        openImageSelector();
        initializeUserInfo();

        mLogoutButton = inflatedProfile.findViewById(R.id.log_out_button);
        mLogoutButton.setOnClickListener(this);
        mNameText = inflatedProfile.findViewById(R.id.name_text);
        mUsernameText = inflatedProfile.findViewById(R.id.username_text);

        mImageView = inflatedProfile.findViewById(R.id.profilePicture);
        Glide.with(getContext())
                .load(R.drawable.com_facebook_profile_picture_blank_portrait)
                .transform(new CircleTransform(getContext()))
                .into(mImageView);


        mCurrentUserDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                String tName = user.getFullName();
                if(user.getAge()!=null){
                    tName += ", " + user.getAge();
                }
                mNameText.setText(tName);

                mUsernameText.setText(user.getUsername());
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
            // ...
        });

        return inflatedProfile;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ProfileFragment.OnFragmentInteractionListener) {
            mListener = (ProfileFragment.OnFragmentInteractionListener) context;
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
        switch (v.getId()) {
            case R.id.log_out_button:
                LoginManager.getInstance().logOut();
                FirebaseAuth.getInstance().signOut();
                break;
        }
    }

    public void openImageSelector(){

        mPhotoPickerButton = (ImageButton) inflatedProfile.findViewById(R.id.imageButton);
        mPhotoPickerButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, GALLERY_INTENT);
                //mView = view;
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, final Intent data){

        mStorage = FirebaseStorage.getInstance().getReference(); //make global
        final ImageView imageView = inflatedProfile.findViewById(R.id.profilePicture);
        super.onActivityResult(requestCode, requestCode, data);

        if(requestCode == GALLERY_INTENT && resultCode == RESULT_OK){

            Uri uri = data.getData();
            //Keep all images for a specific chat grouped together
            final String imageLocation = "Photos/profile_picture/" + currentUserId;
            final String uniqueId = UUID.randomUUID().toString();
            final StorageReference filepath = mStorage.child(imageLocation).child(uniqueId + "/profile_pic");
            final String downloadURl = filepath.getPath();

            filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    //create a new message containing this image
                    mCurrentUserDatabaseReference.child("profilePicLocation").setValue(downloadURl)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            StorageReference storageRef = FirebaseStorage.getInstance()
                                    .getReference().child(downloadURl);
                            storageRef.getBytes(1024*1024 * 5).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                @Override
                                public void onSuccess(byte[] bytes) {
                                    Glide.with(getContext())
                                            .load(bytes)
                                            .transform(new CircleTransform(getContext()))
//                                            .asBitmap()
                                            .into(imageView);
                                }
                            });
                        }
                    });
//                    updateImage();
                }
            });

            mCurrentUserDatabaseReference
                    .child("profilePicLocation").setValue(downloadURl);
        }

    }

    private void initializeUserInfo(){
        final ImageView imageView = inflatedProfile.findViewById(R.id.profilePicture);
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        currentUserId = mFirebaseAuth.getCurrentUser().getUid();
        mCurrentUserDatabaseReference = mFirebaseDatabase
                .getReference().child("users"
                        + "/" + currentUserId);
        mValueListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                try{
                    if(user.getProfilePicLocation() != null){

                        StorageReference storageRef = FirebaseStorage.getInstance()
                                .getReference().child(user.getProfilePicLocation());

                        storageRef.getBytes(1024*1024).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                            @Override
                            public void onSuccess(byte[] bytes) {
                                Glide.with(getContext())
                                        .load(bytes)
                                        .transform(new CircleTransform(getContext()))
//                                            .asBitmap()
                                        .into(imageView);
                            }
                        });

                    }
                }catch (Exception e){
                    Log.e("Err", "glide");
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        mCurrentUserDatabaseReference
                .addValueEventListener(mValueListener);
    }

    private void updateImage(){
        final ImageView imageView = inflatedProfile.findViewById(R.id.profilePicture);

    }



    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }


}
