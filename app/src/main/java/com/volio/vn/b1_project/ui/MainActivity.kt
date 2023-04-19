package com.volio.vn.b1_project.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.volio.vn.b1_project.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}
