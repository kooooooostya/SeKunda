package com.example.sekunda.fragments.historyFragment

import com.example.sekunda.data.Business
import com.example.sekunda.data.BusinessListProvider
import com.example.sekunda.data.Day

class HistoryPresenter : BusinessListProvider {
    private val model = HistoryModel()


    override fun getBusinessList(): ArrayList<Business> {
        return model.businessArrayList
    }

    fun getDayList(): ArrayList<Day>{
        return model.dayArrayList
    }


}