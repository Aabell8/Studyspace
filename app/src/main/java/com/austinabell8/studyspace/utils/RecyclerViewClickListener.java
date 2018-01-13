package com.austinabell8.studyspace.utils;

import android.view.View;

/**
 * Created by aabell on 7/18/2017.
 */

public interface RecyclerViewClickListener {
    void recyclerViewListClicked(View v, int position);
    void recyclerViewListLongClicked(View v, int position);
}
