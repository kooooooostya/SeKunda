package com.example.sekunda.fragments.secFragment

import android.content.Context
import com.example.sekunda.Data.Business
import com.example.sekunda.Data.BusinessSQLiteOpenHelper
import java.util.*

class SecModel(context: Context) {

    val businessArrayList: ArrayList<Business>
    private val sqLiteOpenHelper = BusinessSQLiteOpenHelper(context)
    private val calendar : Calendar = Calendar.getInstance()

    init {
        businessArrayList = sqLiteOpenHelper.getFulledList(calendar)
    }

    fun insertBusiness(business: Business){
        businessArrayList.add(business)
        sqLiteOpenHelper.insertBusinessAsync(business)
    }

    fun changeBusiness(newBusiness: Business, index: Int) {
        businessArrayList[index] = newBusiness
        sqLiteOpenHelper.changeAsync(newBusiness, businessArrayList[index])
    }

    fun getIndexOfElement(business: Business) : Int {
        return businessArrayList.indexOf(business)
    }

    fun findIncompleteTask(arrayList: ArrayList<Business>): Business? {
        for (business in arrayList){
            if (!business.isComplete) {
                business.seconds = ((Date().time - business.timeStart.time.time) / 1000).toInt()
                return business
            }
        }
        return null
    }

}