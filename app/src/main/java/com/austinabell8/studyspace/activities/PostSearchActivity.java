package com.austinabell8.studyspace.activities;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.austinabell8.studyspace.R;
import com.austinabell8.studyspace.model.Post;

public class PostSearchActivity extends AppCompatActivity {

    private EditText mPriceText;
    private Button mSearchButton;
    private Spinner mCourseSpinner;
    private EditText mSearchText;
//    private String current;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_search);

        mCourseSpinner = findViewById(R.id.course_spinner);
        mSearchButton = findViewById(R.id.search_button);
        mSearchText = findViewById(R.id.search_text);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.subject_arrays));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mCourseSpinner.setAdapter(adapter);

        mSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //stores values of spinner and editText on a searchButton event
                Intent intent = new Intent(PostSearchActivity.this, SearchActivity.class);
                intent.putExtra("course", mCourseSpinner.getItemAtPosition(mCourseSpinner.getSelectedItemPosition()).toString());
                intent.putExtra("search_text", mSearchText.getText().toString());
                startActivity(intent);
            }
        });
    }

//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (requestCode == 1) {
//            if (resultCode == Activity.RESULT_OK) {
//                Bundle b = data.getExtras();
//                final String pId = b.getString("Pid");
//                Intent intent = new Intent();
//                intent.putExtra("Pid", pId);
//                setResult(Activity.RESULT_OK, intent);
//                finish();
//            }
//        }
//    }
}
