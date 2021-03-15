package com.example.sekunda

import android.app.Application
import com.example.sekunda.db.BusinessRoomDb

class SeKaundaApplication : Application() {

    companion object {
        lateinit var instance: SeKaundaApplication
        lateinit var db: BusinessRoomDb
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        db = BusinessRoomDb.getDatabase(applicationContext)
    }
}