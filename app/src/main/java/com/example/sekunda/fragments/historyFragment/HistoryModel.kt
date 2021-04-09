package com.example.sekunda.fragments.historyFragment

import com.example.sekunda.SeKaundaApplication
import com.example.sekunda.data.Business
import com.example.sekunda.data.Day
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class HistoryModel(val presenter: HistoryPresenter) {

    val businessArrayList: ArrayList<Business> = ArrayList()

    val dayArrayList = ArrayList<Day>()


    init {
        val calendar = Calendar.getInstance()

        for (i in 0 until 7) {
            dayArrayList.add(Day(calendar, getBusinessList(calendar)))
            calendar.add(Calendar.DAY_OF_YEAR, -1)
        }
    }

    private fun getBusinessList(calendar: Calendar): ArrayList<Business> {
        val list: ArrayList<Business> = ArrayList()
        GlobalScope.launch {
            list.addAll(SeKaundaApplication.db.businessDao().getFulledList(
                    SimpleDateFormat(Business.DMY_PATTERN).format(calendar.time)))
        }
        return list
    }
}