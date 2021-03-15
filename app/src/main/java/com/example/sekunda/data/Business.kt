package com.example.sekunda.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.text.SimpleDateFormat
import java.util.*

@Entity(tableName = "business_table")
class Business(var name: String,
               var seconds: Int,
               val timeStart: String = SimpleDateFormat(DMY_PATTERN, Locale.ENGLISH).format(Calendar.getInstance().time),
               @PrimaryKey(autoGenerate = true)
               var _id: Long? = null
                )
    : Comparable<Any?>, Cloneable {

    companion object{
        const val DMY_PATTERN = "dd-MM-yyyy"
    }

    fun addOneSecond() {
        seconds++
    }

    val time: String
        get() {
            val ans: String
            val stringBuffer = StringBuilder("")
            val hours = seconds / 3600
            val minutes = seconds / 60 - hours * 60
            val seconds = seconds % 60
            if (hours > 0) {
                stringBuffer.append(hours).append("h")
            }
            if (minutes >= 0) stringBuffer.append(if (minutes > 9) minutes else "0$minutes").append("m:")
            if (seconds >= 0) stringBuffer.append(seconds).append("s")
            ans = stringBuffer.toString()
            return ans
        }

    override fun compareTo(other: Any?): Int {
        return if (other is Business) {
            seconds * 100 - other.seconds * 100
        } else 0
    }

    @Throws(CloneNotSupportedException::class)
    public override fun clone(): Any {
        super.clone()
        return Business(name, seconds, timeStart)
    }
}