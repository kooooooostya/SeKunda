package com.example.sekunda.fragments.secFragment

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.sekunda.MainActivity
import com.example.sekunda.R
import com.example.sekunda.SeKaundaApplication
import com.example.sekunda.data.Business
import com.example.sekunda.data.BusinessListProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import moxy.MvpPresenter

class SecPresenter : MvpPresenter<SecView>(), BusinessListProvider {

    private val secModel = SecModel(this)
    private var isTimerGoing = false
    private var currentBusiness: Business? = null
    private var indexCurrentBusiness = 0

    init {
        runTimer()
    }

    companion object {
        private const val CHANNEL_ID = 152
    }

    fun startOrStopTimer() {
        if (!isTimerGoing) {
            viewState.showInputDialog()
        } else {
            cancelNotification()
            isTimerGoing = false
            currentBusiness!!.isRunning = false
            secModel.changeBusiness(currentBusiness!!, indexCurrentBusiness)
            viewState.stopTimer(currentBusiness!!, indexCurrentBusiness)
        }
    }

    fun resumeBusiness(business: Business) {
        if (!isTimerGoing) {
            createNotification()
            business.isRunning = true
            currentBusiness = business
            indexCurrentBusiness = secModel.getIndexOfElement(business)
            isTimerGoing = true
        } else {
            viewState.showToast(SeKaundaApplication.instance.getString(R.string.finish_prev_task))
        }
    }

    fun resumeBusiness(millis: Long, index: Long) {
        if (!isTimerGoing) {
            createNotification()
            currentBusiness = secModel.businessArrayList.find { it._id == index }
            indexCurrentBusiness = secModel.getIndexOfElement(currentBusiness!!)
            currentBusiness!!.isRunning = true
            currentBusiness!!.seconds += (millis / 1000).toInt()
            isTimerGoing = true
        } else {
            viewState.showToast(SeKaundaApplication.instance.getString(R.string.finish_prev_task))
        }
    }

    fun startTimer(business: Business) {
        createNotification()
        business.isRunning = true
        currentBusiness = business
        secModel.insertBusiness(business)
        indexCurrentBusiness = secModel.getIndexOfElement(business)
        viewState.notifyItemInserted(indexCurrentBusiness)
        isTimerGoing = true
    }

    private fun runTimer() {
        GlobalScope.launch(Dispatchers.Main) {
            while (true) {
                if (isTimerGoing) {
                    viewState.updateTimer(currentBusiness!!.time)
                    currentBusiness!!.addOneSecond()
                }
                delay(1000)
            }
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val application = SeKaundaApplication.instance
            val name: CharSequence = application.getString(R.string.channel_name)
            val description = application.getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_LOW
            val channel = NotificationChannel(CHANNEL_ID.toString(), name, importance)
            channel.description = description
            val notificationManager = application.getSystemService(NotificationManager::class.java)!!
            notificationManager.createNotificationChannel(channel)
        }
    }

    // Creates notification
    private fun createNotification() {
        val application = SeKaundaApplication.instance
        createNotificationChannel()
        val builder = NotificationCompat.Builder(application, CHANNEL_ID.toString())
        val pendingIntent = PendingIntent.getActivity(application,
                0, MainActivity.newIntent(application), 0)

        builder.setContentText(application.getString(R.string.channel_description))
                .setSmallIcon(R.drawable.ic_play_arrow_black_24dp)
                .setContentIntent(pendingIntent)
                .setContentTitle(application.getString(R.string.app_name)).priority = NotificationCompat.PRIORITY_LOW


        val notificationManager = (application.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
        notificationManager.notify(CHANNEL_ID, builder.build())
    }


    private fun cancelNotification() {
        val notificationManager = (SeKaundaApplication.instance.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
        notificationManager.cancel(CHANNEL_ID)
    }

    override fun getBusinessList(): ArrayList<Business> {
        return secModel.businessArrayList
    }

    fun renameBusiness(oldBusiness: Business, newName: String) {
        val index = secModel.getIndexOfElement(oldBusiness)
        oldBusiness.name = newName
        secModel.changeBusiness(oldBusiness, index)
    }

    fun updateData() {
        viewState.updateRecyclerView()
    }

    fun showError(string: String) {
        viewState.showError(string)
    }

    fun isCurrentBusinessRunning(): Boolean{
        return currentBusiness?.isRunning == true
    }

    fun getCurrentBusinessDbIndex(): Long {
        var res =  currentBusiness?._id
        if (res == null){
            res = secModel.getLastInsertedId()
        }
        return res
    }

    fun getCurrentBusinessMillis(): Long {
        return (currentBusiness?.seconds ?: 0 * 1000).toLong()
    }
}