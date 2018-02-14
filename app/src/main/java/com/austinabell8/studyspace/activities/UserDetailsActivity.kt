package com.austinabell8.studyspace.activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.austinabell8.studyspace.R
import com.austinabell8.studyspace.adapters.TagRecyclerAdapter
import com.austinabell8.studyspace.model.User
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage

class UserDetailsActivity : AppCompatActivity() {

    private val TAG = "UserDetailsActivity"

    private lateinit var user: User
    private lateinit var mNameTxt: TextView
    private lateinit var mRateTxt: TextView
    private lateinit var mRatingTxt: TextView
    private lateinit var mUsernameTxt: TextView
    private lateinit var mImageView: ImageView
    private lateinit var mMessageButton: Button
    private lateinit var mEmailButton: Button
    private lateinit var mTagsRecyclerView: RecyclerView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_details)

        mNameTxt = findViewById(R.id.text_name)
        mRateTxt = findViewById(R.id.text_rate)
        mRatingTxt = findViewById(R.id.text_rating)
        mImageView = findViewById(R.id.iv_user)
        mUsernameTxt = findViewById(R.id.text_username)
        mMessageButton = findViewById(R.id.button_message)
        mEmailButton = findViewById(R.id.button_email)
        mTagsRecyclerView = findViewById(R.id.rv_courses)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        initActionBar()

        val bundle = intent.extras
        if (bundle != null) {
            user = bundle.getParcelable("user_intent")
            mNameTxt.text = user.fullName
            mRateTxt.text = user.rate
            mRatingTxt.text = user.rating
            mUsernameTxt.text = user.username
            val storageRef = FirebaseStorage.getInstance()
                    .reference.child(user.profilePicLocation)
            storageRef.getBytes((2048 * 2048).toLong()).addOnSuccessListener { bytes ->
                Glide.with(application.applicationContext)
                        .load(bytes)
                        .into(mImageView)
            }


//            val layoutManager = GridLayoutManager(this, 2, GridLayoutManager.HORIZONTAL, false)
            mTagsRecyclerView.layoutManager = GridLayoutManager(applicationContext, 2)
//            mTagsRecyclerView.isNestedScrollingEnabled = false

            var courseTags = mutableListOf<String>()
            courseTags.add("ECONOMIC 1021")
            courseTags.add("ECONOMIC 1022")
            courseTags.add("CALCULUS 1000")
            courseTags.add("MOS 2275")
            courseTags.add("MOS 2181")

            val mSearchAdapter = TagRecyclerAdapter(courseTags)
            mTagsRecyclerView.adapter = mSearchAdapter

        }
    }

    private fun initActionBar() {
        val actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)
        actionBar?.title = ""
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return false
    }
}
