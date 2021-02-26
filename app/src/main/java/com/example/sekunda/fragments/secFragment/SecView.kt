package com.example.sekunda.fragments.secFragment

import com.example.sekunda.Data.Business
import moxy.MvpView
import moxy.viewstate.strategy.alias.AddToEndSingle

@AddToEndSingle
interface SecView : MvpView {
    fun updateTimer(time: String)
    fun showInputDialog()
    fun stopTimer(business: Business, businessIndex: Int)
    fun showToast(message: String)
}