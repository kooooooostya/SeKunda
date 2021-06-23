package com.example.sekunda.fragments.secFragment

import com.example.sekunda.SeKaundaApplication
import com.example.sekunda.data.Business
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class SecModel(private val presenter: SecPresenter) {

    var businessArrayList: ArrayList<Business> = ArrayList()
    private val calendar: Calendar = Calendar.getInstance()
    private var idLastInsertedBusiness: Long? = null


    init {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                businessArrayList = getBusinessFromDb(calendar)
                presenter.updateData()
            }catch (e: Exception){
                e.message?.let { presenter.showError(it) }
            }
        }
    }

    fun insertBusiness(business: Business) {
        businessArrayList.add(0, business)
        GlobalScope.launch (Dispatchers.IO) {
            idLastInsertedBusiness = SeKaundaApplication.db.businessDao().insert(business)
        }
    }

    fun changeBusiness(newBusiness: Business, index: Int) {
        if (newBusiness._id == null) {
            newBusiness._id = idLastInsertedBusiness
            idLastInsertedBusiness = null
        }
        businessArrayList[index] = newBusiness
        GlobalScope.launch {
            SeKaundaApplication.db.businessDao().update(newBusiness)
        }
    }

    fun getIndexOfElement(business: Business): Int {
        return businessArrayList.indexOf(business)
    }

    private fun getBusinessFromDb(calendar: Calendar): ArrayList<Business> {
        return ArrayList(
                SeKaundaApplication.db.businessDao().getFulledList(
                        SimpleDateFormat(Business.DMY_PATTERN, Locale.ENGLISH).format(calendar.time)
                )
        )
    }

    fun getLastInsertedId(): Long {
        return idLastInsertedBusiness ?: businessArrayList.last()._id?.plus(1) ?: 0
    }
}

