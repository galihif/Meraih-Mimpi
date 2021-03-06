package com.giftech.meraihmimpi

import android.content.Context
import android.content.SharedPreferences

/*
Class sharedpreferences untuk menyimpan data akun user dalam local storage,
berguna agar setiap login atau register bisa langsung diarahkan ke home
 */

class Preferences(val context: Context) {
    companion object {
        const val MEETING_PREF = "USER_PREF"
    }

    val sharedPref = context.getSharedPreferences(MEETING_PREF, 0)

    fun setValues(key: String, value: String) {
        val editor: SharedPreferences.Editor = sharedPref.edit()
        editor.putString(key, value)
        editor.apply()
    }

    fun getValues(key: String): String? {
        return sharedPref.getString(key, "")
    }
}