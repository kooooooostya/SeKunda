package com.example.sekunda.fragments.secFragment

import com.example.sekunda.SeKaundaApplication
import com.example.sekunda.data.Business
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
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
        val observer = getBusinessFromDb(calendar)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    if (it != null) {
                        businessArrayList = it
                    }
                }, {
                    presenter.showError(it.message!!)
                }, {
                    presenter.updateData()
                })
    }

    fun insertBusiness(business: Business) {
        businessArrayList.add(0, business)
        GlobalScope.launch {
            idLastInsertedBusiness = SeKaundaApplication.db.businessDao().insert(business)
        }
    }

    fun changeBusiness(newBusiness: Business, index: Int) {
        if(idLastInsertedBusiness != null){
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

    private fun getBusinessFromDb(calendar: Calendar): Observable<ArrayList<Business>> {
        return Observable.create {
            val businesses = SeKaundaApplication.db.businessDao().getFulledList(SimpleDateFormat(Business.DMY_PATTERN, Locale.ENGLISH).format(calendar.time))

            if (businesses.isNotEmpty()) {
                it.onNext(ArrayList(businesses))
                it.onComplete()
            }
        }
    }
}

