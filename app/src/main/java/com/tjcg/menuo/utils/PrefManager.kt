package com.tjcg.menuo.utils

import android.content.Context
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor

/**
 * Created by Tamim on 30/09/2017.
 */
class PrefManager(var _context: Context) {
    var pref: SharedPreferences
    var editor: Editor

    // shared pref mode
    var PRIVATE_MODE = 0
    fun setBoolean(PREF_NAME: String?, `val`: Boolean?) {
        editor.putBoolean(PREF_NAME, `val`!!)
        editor.commit()
    }

    fun setString(PREF_NAME: String?, VAL: String?) {
        editor.putString(PREF_NAME, VAL)
        editor.commit()
    }

    fun setInt(PREF_NAME: String?, VAL: Int) {
        editor.putInt(PREF_NAME, VAL)
        editor.commit()
    }

    fun getBoolean(PREF_NAME: String?): Boolean {
        return pref.getBoolean(PREF_NAME, false)
    }

    fun remove(PREF_NAME: String?) {
        if (pref.contains(PREF_NAME)) {
            editor.remove(PREF_NAME)
            editor.commit()
        }
    }

    fun getString(PREF_NAME: String?): String? {
        return if (pref.contains(PREF_NAME)) {
            pref.getString(PREF_NAME, null)
        } else ""
    }

    fun getInt(key: String?): Int {
        return pref.getInt(key, 0)
    }

    companion object {
        // Shared preferences file name
        private const val PREF_NAME = "status_app"
        private const val IS_FIRST_TIME_LAUNCH = "IsFirstTimeLaunch"
    }

    init {
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE)
        editor = pref.edit()
    }
}