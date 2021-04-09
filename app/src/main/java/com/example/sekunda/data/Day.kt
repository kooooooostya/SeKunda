package com.example.sekunda.data

import com.example.sekunda.SeKaundaApplication
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class Day(private val mCalendar: Calendar, val businessArrayList: ArrayList<Business>) {

    private val mAllSeconds: Int

    private fun findTotalTime(): Int {
        var sum = 0
        for (business in businessArrayList) {
            sum += business.seconds
        }
        return sum
    }

    val hmsTime: String
        get() {
            val ans: String
            val stringBuffer = StringBuilder()
            val hours = mAllSeconds / 3600
            val minutes = mAllSeconds / 60 - hours * 60
            val seconds = mAllSeconds % 60
            if (hours > 0) {
                stringBuffer.append(hours).append("h")
            }
            if (minutes >= 0) stringBuffer.append(if (minutes > 9) minutes else "0$minutes").append("m:")
            if (seconds >= 0) stringBuffer.append(seconds).append("s")
            ans = stringBuffer.toString()
            return ans
        }
    val dmyTime: String
        get() {
            val simpleDateFormat = SimpleDateFormat(Business.DMY_PATTERN, Locale.ENGLISH)
            return simpleDateFormat.format(mCalendar.time)
        }

    fun addEmptyBusiness() {
        businessArrayList.add(Business("Nothing Today", 0))
    }

    init {
        mAllSeconds = findTotalTime()
    }
}