package com.example.sekunda.tools

import android.content.Context.MODE_PRIVATE
import androidx.fragment.app.Fragment

const val SHARED_PREF_DB_NAME = "sekunda"

fun Fragment.getLongFromSharedPref(key: String, defValue: Long): Long {
    val sharedPref = requireActivity().getSharedPreferences(SHARED_PREF_DB_NAME, MODE_PRIVATE)
    return sharedPref.getLong(key, defValue)
}

fun Fragment.setLongToSharedPref(key: String, value: Long) {
    val sharedPrefEditor = requireActivity().getSharedPreferences(SHARED_PREF_DB_NAME, MODE_PRIVATE).edit()
    sharedPrefEditor.putLong(key, value)
    sharedPrefEditor.apply()
}

fun Fragment.setIntToSharedPref(key: String, value: Int) {
    val sharedPrefEditor = requireActivity().getSharedPreferences(SHARED_PREF_DB_NAME, MODE_PRIVATE).edit()
    sharedPrefEditor.putInt(key, value)
    sharedPrefEditor.apply()
}

fun Fragment.removeFromSharedPref(key: String) {
    val sharedPrefEditor = requireActivity().getSharedPreferences(SHARED_PREF_DB_NAME, MODE_PRIVATE).edit()
    sharedPrefEditor.remove(key)
    sharedPrefEditor.apply()
}

fun Fragment.isContainedInSharedPref(key: String): Boolean {
    val sharedPref = requireActivity().getSharedPreferences(SHARED_PREF_DB_NAME, MODE_PRIVATE)
    return sharedPref.contains(key)
}


