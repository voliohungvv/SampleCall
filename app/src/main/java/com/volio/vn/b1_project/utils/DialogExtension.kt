package com.volio.vn.b1_project.utils

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.WindowManager
import com.volio.vn.b1_project.R

fun Dialog.openLoading() {
    this.let {
        it.setContentView(R.layout.dialog_loading)
        it.window?.apply {
            setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT
            )
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            attributes.apply {
                y = -170
                gravity = Gravity.CENTER
            }
        }
        it.setCancelable(false)
        it.show()
    }
}