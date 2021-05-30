package com.example.sekunda.views

import android.app.Activity
import android.app.AlertDialog
import android.graphics.drawable.ColorDrawable
import android.view.Window
import com.example.sekunda.R


class WaitingDialog(var activity: Activity) {

    lateinit var dialog: AlertDialog

    fun showDownloadingDialog() {
        val builder = AlertDialog.Builder(activity)

        builder.setView(R.layout.waiting_dialog)
        builder.setCancelable(false)
        dialog = builder.create()

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setBackgroundDrawable(ColorDrawable(android.graphics.Color.TRANSPARENT))
        dialog.show()
    }

    fun hideDownloadingDialog() {
        dialog.dismiss()
    }
}