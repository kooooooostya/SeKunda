package com.example.sekunda.fragments.historyFragment

import com.example.sekunda.SeKaundaApplication
import com.example.sekunda.data.Business
import com.example.sekunda.data.Day
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class HistoryModel {

    suspend fun getDays(): ArrayList<Day>{
        val calendar = Calendar.getInstance()
        val dayArrayList = ArrayList<Day>()
        for (i in 0 until 7) {
            dayArrayList.add(Day(calendar.clone() as Calendar, getBusinessList(calendar)))
            calendar.add(Calendar.DAY_OF_YEAR, -1)
        }
        return dayArrayList
    }

    private fun getBusinessList(calendar: Calendar): ArrayList<Business> {
        return ArrayList(SeKaundaApplication.db.businessDao().getFulledList(
                SimpleDateFormat(Business.DMY_PATTERN, Locale.ENGLISH).format(calendar.time)))
    }
}
