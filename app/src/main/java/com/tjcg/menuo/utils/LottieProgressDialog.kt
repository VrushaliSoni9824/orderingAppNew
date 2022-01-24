package com.tjcg.menuo.utils

import android.app.Dialog
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.view.Window
import com.tjcg.menuo.R

class LottieProgressDialog(var context: Context) {
    var progressDialog: Dialog = Dialog(context)

    fun showDialog() {
        progressDialog.show()
    }

    fun cancelDialog() {
        if (progressDialog.isShowing) {
            progressDialog.dismiss()
        }
    }

    init {
        progressDialog.requestWindowFeature(1)
        progressDialog.window!!.requestFeature(Window.FEATURE_NO_TITLE)
        progressDialog.setContentView(R.layout.layout_progress_bar)
        progressDialog.window!!.setBackgroundDrawable(ColorDrawable(0))
        progressDialog.setCanceledOnTouchOutside(false)
        progressDialog.setCancelable(false)
    }
}