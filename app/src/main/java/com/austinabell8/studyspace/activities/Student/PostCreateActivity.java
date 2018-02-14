package com.austinabell8.studyspace.activities.Student;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;

import com.austinabell8.studyspace.R;
import com.austinabell8.studyspace.model.Post;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class PostCreateActivity extends AppCompatActivity {

    private static final String TAG = "PostCreateActivity";

    private EditText mPriceText;
    private Button mCreateButton;
    private SearchableSpinner mCourseSpinner;
    private EditText mDescriptionText;
    private String current;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_create);

        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        if (getSupportActionBar()!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Create Post");
        }


        current = "";

        mPriceText = findViewById(R.id.edit_text_price);
        mPriceText.setRawInputType(Configuration.KEYBOARD_12KEY);
        mPriceText.addTextChangedListener(new TextWatcher(){
//            DecimalFormat dec = new DecimalFormat("0.00");
            @Override
            public void afterTextChanged(Editable arg0) {
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!s.toString().equals(current)){
                    mPriceText.removeTextChangedListener(this);

                    String cleanString = s.toString().replaceAll("[$,.]", "");

                    double parsed = Double.parseDouble(cleanString);
                    String formatted = NumberFormat.getCurrencyInstance().format((parsed/100));

                    current = formatted;
                    mPriceText.setText(formatted);
                    mPriceText.setSelection(formatted.length());

                    mPriceText.addTextChangedListener(this);
                }
            }
        });
//        mPriceText.addTextChangedListener(new NumberTextWatcher(mPriceText, "#,###"));
        mCourseSpinner = findViewById(R.id.spinner_course);
        mCreateButton = findViewById(R.id.button_create);
        mDescriptionText = findViewById(R.id.edit_text_description);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(PostCreateActivity.this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.subject_arrays));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mCourseSpinner.setAdapter(adapter);

        mCreateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //stores values of spinner and editText on a searchButton event
                Post newPost = new Post();
                newPost.setDescription(mDescriptionText.getText().toString());
                newPost.setCourse(mCourseSpinner.getItemAtPosition(mCourseSpinner.getSelectedItemPosition()).toString());
                newPost.setPrice(mPriceText.getText().toString());
                Intent returnIntent = new Intent();
                returnIntent.putExtra("post_item", newPost);
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
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
