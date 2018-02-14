package com.austinabell8.studyspace.activities.Tutor;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.austinabell8.studyspace.R;

public class PostSearchInputActivity extends AppCompatActivity {

    private static final String TAG = "PostSearchInputActivity";

    private EditText mPriceText;
    private Button mSearchButton;
    private Spinner mCourseSpinner;
    private EditText mSearchText;
    private Toolbar mToolbar;
//    private String current;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_search);

        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        if (getSupportActionBar()!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Search Posts");
        }

        mCourseSpinner = findViewById(R.id.spinner_course);
        mSearchButton = findViewById(R.id.button_search);
        mSearchText = findViewById(R.id.edit_text_search);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.subject_arrays));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mCourseSpinner.setAdapter(adapter);

        mSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //stores values of spinner and editText on a searchButton event
                Intent intent = new Intent(PostSearchInputActivity.this, SearchActivity.class);
                intent.putExtra("course", mCourseSpinner.getItemAtPosition(mCourseSpinner.getSelectedItemPosition()).toString());
                intent.putExtra("search_text", mSearchText.getText().toString());
                startActivity(intent);
            }
        });
    }

}
