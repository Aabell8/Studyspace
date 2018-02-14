package com.austinabell8.studyspace.activities.Tutor

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.MenuItem
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView

import com.austinabell8.studyspace.R
import com.austinabell8.studyspace.model.Post

class PostDetailsActivity : AppCompatActivity() {

    private val TAG = "PostDetailsActivity"

    private lateinit var post: Post
    private lateinit var mNameTxt: TextView
    private lateinit var mDescriptionTxt: TextView
    private lateinit var mCourseTxt: TextView
    private lateinit var mPriceTxt: TextView
    private lateinit var mImageView: ImageView
    private lateinit var mMessageButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_details)

        mNameTxt = findViewById(R.id.text_name)
        mDescriptionTxt = findViewById(R.id.text_description)
        mCourseTxt = findViewById(R.id.text_course)
        mImageView = findViewById(R.id.image_post)
        mPriceTxt = findViewById(R.id.text_price)
        mMessageButton = findViewById(R.id.button_message)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        initActionBar()

        val bundle = intent.extras
        if (bundle != null) {
            post = bundle.getParcelable("post_intent")
            mNameTxt.text = post.name
            mDescriptionTxt.text = post.description
            mCourseTxt.text = post.course
            mPriceTxt.text = post.price
//            Glide.with(mImageView.context).load(post.imageUrl).into(mImageView)

        }
    }

    private fun initActionBar() {
        val actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)
        actionBar?.title = "Post Details"
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
