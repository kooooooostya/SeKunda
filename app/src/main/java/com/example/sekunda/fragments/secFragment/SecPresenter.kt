package com.example.sekunda.fragments.secFragment

import android.os.Handler
import com.example.sekunda.Data.Business
import com.example.sekunda.R
import com.example.sekunda.SeKaundaApplication
import moxy.MvpPresenter
class SecPresenter : MvpPresenter<SecView>(){

    private val secModel = SecModel(SeKaundaApplication.instance.applicationContext)
    private var isTimerGoing = false
    private var currentBusiness: Business? = null
    private var indexCurrentBusiness = 0

    init {
        runTimer()
    }

    fun startOrStopTimer() {
        if (!isTimerGoing) {
            viewState.showInputDialog()
        } else {
            isTimerGoing = false
            currentBusiness?.isComplete = true
            secModel.changeBusiness(currentBusiness!!, indexCurrentBusiness)
            viewState.stopTimer(currentBusiness!!, indexCurrentBusiness)
        }
    }

    fun startTimer(business: Business){
        currentBusiness = business
        secModel.insertBusiness(business)
        indexCurrentBusiness = secModel.getIndexOfElement(business)
        isTimerGoing = true
    }

    private fun runTimer() {
        val handler = Handler()
        handler.post(object : Runnable {
            override fun run() {
                if (isTimerGoing) {
                    viewState.updateTimer(currentBusiness!!.time)
                    currentBusiness!!.addOneSecond()
                }
                handler.postDelayed(this, 1000)
            }
        })
    }

    fun getBusinessList() : ArrayList<Business>{
        val arrayList = secModel.businessArrayList
        val business = secModel.findIncompleteTask(arrayList)
        if (business != null){
            currentBusiness = business
            indexCurrentBusiness = secModel.getIndexOfElement(currentBusiness!!)
            isTimerGoing = true
        }
        return arrayList
    }

    fun resumeBusiness(business: Business) {
        if (!isTimerGoing) {
            currentBusiness = business
            currentBusiness!!.isComplete = false
            indexCurrentBusiness = secModel.getIndexOfElement(business)
            secModel.changeBusiness(currentBusiness!!, indexCurrentBusiness)
            isTimerGoing = true
        } else {
            viewState.showToast(SeKaundaApplication.instance.getString(R.string.finish_prev_task))
        }
    }

    fun renameBusiness(newBusiness: Business, oldBusiness: Business) {
        secModel.changeBusiness(newBusiness, secModel.getIndexOfElement(oldBusiness))
    }

}