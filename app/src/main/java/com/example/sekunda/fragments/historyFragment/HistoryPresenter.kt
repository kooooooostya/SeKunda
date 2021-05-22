package com.example.sekunda.fragments.historyFragment

import com.example.sekunda.data.Day
import java.util.*
import kotlin.collections.ArrayList

class HistoryPresenter {
    private val model = HistoryModel()


    suspend fun getDays(): ArrayList<Day> {
        return model.getDays()
    }

    fun getEmptyDayList(): java.util.ArrayList<Day> {
        val days = ArrayList<Day>()

        val calendar = Calendar.getInstance()
        for (i in 0 until 7) {
            days.add(Day(calendar.clone() as Calendar))
            calendar.add(Calendar.DAY_OF_YEAR, -1)
        }
        return days
    }

}