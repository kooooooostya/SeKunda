package com.example.sekunda.fragments.secFragment

import android.os.Handler
import com.example.sekunda.data.Business
import com.example.sekunda.R
import com.example.sekunda.SeKaundaApplication
import moxy.MvpPresenter
class SecPresenter : MvpPresenter<SecView>(){

    private val secModel = SecModel(this)
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
            secModel.changeBusiness(currentBusiness!!, indexCurrentBusiness)
            viewState.stopTimer(currentBusiness!!, indexCurrentBusiness)
        }
    }

    fun startTimer(business: Business){
        currentBusiness = business
        secModel.insertBusiness(business)
        indexCurrentBusiness = secModel.getIndexOfElement(business)
        viewState.notifyItemInserted(indexCurrentBusiness)
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
        return secModel.businessArrayList
    }

    fun resumeBusiness(business: Business) {
        if (!isTimerGoing) {
            currentBusiness = business
            indexCurrentBusiness = secModel.getIndexOfElement(business)
            isTimerGoing = true
        } else {
            viewState.showToast(SeKaundaApplication.instance.getString(R.string.finish_prev_task))
        }
    }

    fun renameBusiness(newBusiness: Business, oldBusiness: Business) {
        secModel.changeBusiness(newBusiness, secModel.getIndexOfElement(oldBusiness))
    }

    fun updateData() {
        viewState.updateRecyclerView()
    }
    fun showError(string: String){
        viewState.showError(string)
    }

}