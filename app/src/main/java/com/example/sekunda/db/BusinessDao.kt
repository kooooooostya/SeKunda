package com.example.sekunda.db

import androidx.room.*
import com.example.sekunda.data.Business

@Dao
interface BusinessDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(business: Business) : Long

    @Update
    suspend fun update(business: Business)

    @Query("SELECT * FROM business_table WHERE timeStart == :shortDate")
    fun getFulledList(shortDate: String) : List<Business>

    @Query("SELECT * FROM business_table")
    fun getAll(): List<Business>
}