package com.example.sekunda.views


import android.app.Activity
import android.app.AlertDialog
import android.graphics.drawable.ColorDrawable
import android.view.Window
import android.widget.SeekBar
import com.example.sekunda.R
import kotlinx.android.synthetic.main.worked_so_mutch_dialog.view.*
import java.util.*


class HowMuchWorkedDialog(private var activity: Activity) {

    private lateinit var dialog: AlertDialog

    var millis: Long = 0
    var isMinutes = true

    fun showDialog(milliseconds: Long, callback: ((Long) -> Unit)) {

        val minStr = activity.resources.getString(R.string.minutes)
        val hourStr = activity.resources.getString(R.string.hour)

        millis = Date().time - milliseconds

        val textMin: String
        val textMax: String

        if (millis <= 1000 * 60 * 60) {
            isMinutes = true
            textMin = "0 $minStr"
            textMax = (millisToMin(millis)) + minStr
        } else {
            isMinutes = false
            textMin = "0 $hourStr"
            textMax = (millisToHour(millis)) + " " + hourStr
        }

        val view = activity.layoutInflater.inflate(R.layout.worked_so_mutch_dialog, null)

        view.text_min.text = textMin
        view.text_max.text = textMax
        view.text_current.text = textMin
        view.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (isMinutes) {
                    view.text_current.text =
                        (millisToMin((millis * (progress.toDouble() / 100)).toLong()) + minStr)
                } else {
                    view.text_current.text =
                        (millisToHour((millis * (progress.toDouble() / 100)).toLong()) + hourStr)
                }
                millis = (millis.toInt() * (progress.toDouble() / 100)).toLong()

            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })

        view.cancel_button.setOnClickListener {
            millis = 0
            callback(millis)
            hideDialog()
        }
        view.confirm_button.setOnClickListener {
            callback(millis)
            hideDialog()
        }

        val builder = AlertDialog.Builder(activity)
        builder.setView(view)
        builder.setCancelable(false)

        dialog = builder.create()
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setBackgroundDrawable(ColorDrawable(android.graphics.Color.TRANSPARENT))
        dialog.show()
    }



    private fun hideDialog() {
        dialog.dismiss()
    }

    private fun millisToHour(milliseconds: Long): String {
        return String.format("%.2f", milliseconds / (1000.0 * 60 * 60))
    }

    private fun millisToMin(m: Long): String {
        return String.format("%.2f", m / (1000.0 * 60))
    }
}