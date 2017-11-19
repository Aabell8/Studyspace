package com.austinabell8.studyspace.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.austinabell8.studyspace.R;
import com.austinabell8.studyspace.fragments.MessagesFragment;
import com.austinabell8.studyspace.fragments.PostsFragment;
import com.austinabell8.studyspace.fragments.ProfileFragment;
import com.austinabell8.studyspace.helpers.BottomNavigationViewHelper;
import com.austinabell8.studyspace.helpers.LockableViewPager;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;

public class StudentActivity extends AppCompatActivity
        implements MessagesFragment.OnFragmentInteractionListener,
        PostsFragment.OnFragmentInteractionListener,
        ProfileFragment.OnFragmentInteractionListener,
        View.OnClickListener {

    private PostsFragment mPostsFragment;
    private MessagesFragment mMessagesFragment;
    private ProfileFragment mProfileFragment;

    private static final int GALLERY_INTENT=2;

    private LockableViewPager viewPager;
    private BottomNavigationView navigation;

    private boolean doubleBackToExitPressedOnce = false;

    private SectionsPagerAdapter mSectionsPagerAdapter;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private StorageReference mStorage;
    private String currentUserId;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mCurrentUserDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_student);

        initView();
        initializeData();

        mFirebaseAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    logout();
                } else {
                    // User is signed out
                }

            }
        };

        //default discover tab on open
        View view = navigation.findViewById(R.id.navigation_posts);
        view.performClick();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.actionbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
//            case R.id.action_logout:
//                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        //you can leave it empty
    }

    @Override
    protected void onResume(){
        super.onResume();
        mFirebaseAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mFirebaseAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mFirebaseAuth.removeAuthStateListener(mAuthListener);
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }

    //create view
    private void initView() {
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        viewPager = findViewById(R.id.view_pager_bottom_navigation);
        viewPager.setAdapter(mSectionsPagerAdapter);
        viewPager.addOnPageChangeListener(pageChangeListener);

        navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        BottomNavigationViewHelper.removeShiftMode(navigation);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_posts:
                    viewPager.setCurrentItem(0);
                    viewPager.setSwipeLocked(true);
                    return true;
//                case R.id.navigation_create:
//                    viewPager.setCurrentItem(1);
//                    viewPager.setSwipeLocked(false);
//                    return true;
                case R.id.navigation_messages:
                    viewPager.setCurrentItem(1);
                    viewPager.setSwipeLocked(false);
                    return true;
                case R.id.navigation_profile:
                    viewPager.setCurrentItem(2);
                    viewPager.setSwipeLocked(false);
                    return true;
            }
            return false;
        }
    };

    private ViewPager.OnPageChangeListener pageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            switch (position) {
                case 0:
                    navigation.setSelectedItemId(R.id.navigation_posts);
                    break;
//                case 1:
//                    navigation.setSelectedItemId(R.id.navigation_create);
//                    break;
                case 1:
                    navigation.setSelectedItemId(R.id.navigation_messages);
                    break;
                case 2:
                    navigation.setSelectedItemId(R.id.navigation_profile);
                    break;
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };
    public void logout() {
        LoginManager.getInstance().logOut();

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            Toast.makeText(StudentActivity.this, "Logout failed.",
                    Toast.LENGTH_SHORT).show();
        } else {
            Intent openIntent = new Intent(StudentActivity.this, LoginActivity.class);
            finish();
            startActivity(openIntent);

        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.log_out_button:
                logout();
                break;
        }
    }

    private void initializeData(){
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        currentUserId = mFirebaseAuth.getCurrentUser().getUid();
        mCurrentUserDatabaseReference = mFirebaseDatabase
                .getReference().child("users"
                        + "/" + currentUserId);
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = null;
            switch(position) {
                case 0:
                    fragment = new PostsFragment();
                    break;
//                case 1:
//                    fragment = new CreateFragment();
//                    break;
                case 1:
                    fragment = new MessagesFragment();
                    break;
                case 2:
                    fragment = new ProfileFragment();
            }
            return fragment;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Fragment createdFragment = (Fragment) super.instantiateItem(container, position);
            // save the appropriate reference depending on position
            switch (position) {
                case 0:
                    mPostsFragment = (PostsFragment) createdFragment;
                    break;
//                case 1:
//                    mCreateFragment = (CreateFragment) createdFragment;
//                    break;
                case 1:
                    mMessagesFragment = (MessagesFragment) createdFragment;
                    break;
                case 2:
                    mProfileFragment = (ProfileFragment) createdFragment;
                    break;
            }
            return createdFragment;
        }

        @Override
        public int getCount() {
            return 3;
        }


    }

}