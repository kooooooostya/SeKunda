package com.example.sekunda.db

import android.content.Context
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteOpenHelper
import com.example.sekunda.data.Business

@Database(entities = [Business::class], version = 1, exportSchema = false)
abstract class BusinessRoomDb : RoomDatabase() {

    abstract fun businessDao(): BusinessDao

    companion object {
        @Volatile
        private var INSTANCE: BusinessRoomDb? = null

        fun getDatabase(context: Context): BusinessRoomDb {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                        context.applicationContext,
                        BusinessRoomDb::class.java,
                        "business_database"
                ).build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }
}