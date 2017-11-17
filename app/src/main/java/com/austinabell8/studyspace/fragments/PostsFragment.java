package com.austinabell8.studyspace.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.austinabell8.studyspace.R;


public class PostsFragment extends Fragment implements View.OnClickListener {

    private PostsFragment.OnFragmentInteractionListener mListener;
    private View inflatedCustomerLookup;

    public PostsFragment() {
        // Required empty public constructor
    }

    public static PostsFragment newInstance() {
        PostsFragment fragment = new PostsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        inflatedCustomerLookup = inflater.inflate(R.layout.fragment_posts, container, false);

        return inflatedCustomerLookup;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof PostsFragment.OnFragmentInteractionListener) {
            mListener = (PostsFragment.OnFragmentInteractionListener) context;
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

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

}