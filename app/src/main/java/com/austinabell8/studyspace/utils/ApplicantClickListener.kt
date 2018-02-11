package com.austinabell8.studyspace.utils

import android.view.View

/**
 * Created by austi on 2018-02-10.
 */

interface ApplicantClickListener {
    fun userDetailsClick(v: View, position: Int)
    fun messageClick(v: View, position: Int)
    fun acceptClick(v: View, position: Int)
}