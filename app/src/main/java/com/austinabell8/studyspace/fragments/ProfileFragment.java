package com.austinabell8.studyspace.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import com.austinabell8.studyspace.model.User;
import com.bumptech.glide.Glide;
import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;


import static android.app.Activity.RESULT_OK;
import static com.facebook.FacebookSdk.getApplicationContext;


public class ProfileFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "ProfileFragment";

    private ProfileFragment.OnFragmentInteractionListener mListener;
    private View inflatedProfile;
    private Button mLogoutButton;
    private ImageButton mPhotoPickerButton;
    private TextView mNameText;
    private TextView mUsernameText;
    private TextView mEmailText;
    private ImageView mImageView;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private ValueEventListener mValueListener;
    private StorageReference mStorage;
    private String currentUserId;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mCurrentUserDatabaseReference;

    private static final int GALLERY_INTENT=2;
    private static final int THUMBNAIL_SIZE = 1024;

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

        mLogoutButton = inflatedProfile.findViewById(R.id.button_log_out);
        mLogoutButton.setOnClickListener(this);
        mNameText = inflatedProfile.findViewById(R.id.text_name);
        mUsernameText = inflatedProfile.findViewById(R.id.text_username);
        mEmailText = inflatedProfile.findViewById(R.id.text_email);

        mImageView = inflatedProfile.findViewById(R.id.image_profile_pic);
        Glide.with(getApplicationContext())
                .load(R.drawable.com_facebook_profile_picture_blank_portrait)
                .into(mImageView);


        mCurrentUserDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                String tName = user.getFullName();
                if(!user.getAge().equals("")){
                    tName += ", " + user.getAge();
                }
                mNameText.setText(tName);

                mUsernameText.setText(user.getUsername());

                mEmailText.setText(user.getEmail());
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
            case R.id.button_log_out:
                LoginManager.getInstance().logOut();
                FirebaseAuth.getInstance().signOut();
                break;
        }
    }

    public void openImageSelector(){

        mPhotoPickerButton = inflatedProfile.findViewById(R.id.button_upload);
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
        final ImageView imageView = inflatedProfile.findViewById(R.id.image_profile_pic);
        super.onActivityResult(requestCode, requestCode, data);

        if(requestCode == GALLERY_INTENT && resultCode == RESULT_OK){

            Uri uri = data.getData();
            Bitmap bitmap = null;
            try {
                bitmap = getThumbnail(uri, this);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] imageData = baos.toByteArray();
            //Keep all images for a specific chat grouped together
            final String imageLocation = "Photos/profile_picture/" + currentUserId;
            final String uniqueId = UUID.randomUUID().toString();
            final StorageReference filepath = mStorage.child(imageLocation).child(uniqueId + "/profile_pic");
            final String downloadURl = filepath.getPath();

            filepath.putBytes(imageData).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
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
                                    Glide.with(getApplicationContext())
                                            .load(bytes)
//                                            .transform(new CircleTransform(getContext()))
                                            .into(imageView);
                                }
                            });
                        }
                    });
                }
            });

            mCurrentUserDatabaseReference
                    .child("profilePicLocation").setValue(downloadURl);
        }

    }



    private void initializeUserInfo(){
        final ImageView imageView = inflatedProfile.findViewById(R.id.image_profile_pic);
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
                                Glide.with(getApplicationContext())
                                        .load(bytes)
//                                        .transform(new CircleTransform(getContext()))
//                                            .asBitmap()
                                        .into(imageView);
                            }
                        });

                    }
                }catch (Exception e){
                    Log.e(TAG, "glide");
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        mCurrentUserDatabaseReference
                .addValueEventListener(mValueListener);
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    public static Bitmap decodeUri(Context c, Uri uri, final int requiredSize)
            throws FileNotFoundException {
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(c.getContentResolver().openInputStream(uri), null, o);

        int width_tmp = o.outWidth
                , height_tmp = o.outHeight;
        int scale = 1;

        while(true) {
            if(width_tmp / 2 < requiredSize || height_tmp / 2 < requiredSize)
                break;
            width_tmp /= 2;
            height_tmp /= 2;
            scale *= 2;
        }

        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;
        return BitmapFactory.decodeStream(c.getContentResolver().openInputStream(uri), null, o2);
    }

    public static Bitmap getThumbnail(Uri uri, ProfileFragment fragment) throws IOException {
        InputStream input = fragment.getContext().getContentResolver().openInputStream(uri);

        BitmapFactory.Options onlyBoundsOptions = new BitmapFactory.Options();
        onlyBoundsOptions.inJustDecodeBounds = true;
        onlyBoundsOptions.inDither=true;//optional
        onlyBoundsOptions.inPreferredConfig=Bitmap.Config.ARGB_8888;//optional
        BitmapFactory.decodeStream(input, null, onlyBoundsOptions);
        input.close();

        if ((onlyBoundsOptions.outWidth == -1) || (onlyBoundsOptions.outHeight == -1)) {
            return null;
        }

        int originalSize = (onlyBoundsOptions.outHeight > onlyBoundsOptions.outWidth) ? onlyBoundsOptions.outHeight : onlyBoundsOptions.outWidth;

        double ratio = (originalSize > THUMBNAIL_SIZE) ? (originalSize / THUMBNAIL_SIZE) : 1.0;

        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        bitmapOptions.inSampleSize = getPowerOfTwoForSampleRatio(ratio);
        bitmapOptions.inDither = true; //optional
        bitmapOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;//
        input = fragment.getContext().getContentResolver().openInputStream(uri);
        Bitmap bitmap = BitmapFactory.decodeStream(input, null, bitmapOptions);
        input.close();
        return bitmap;
    }

    private static int getPowerOfTwoForSampleRatio(double ratio){
        int k = Integer.highestOneBit((int)Math.floor(ratio));
        if(k==0) return 1;
        else return k;
    }


}
