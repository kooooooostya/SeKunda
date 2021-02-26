package com.example.sekunda

import android.app.Application

class SeKaundaApplication : Application() {

    companion object {
        lateinit var instance: SeKaundaApplication
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}