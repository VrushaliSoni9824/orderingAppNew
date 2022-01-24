package com.tjcg.menuo.data.remote

import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.tjcg.menuo.data.response.Login.AdminLoginRS

object Parser {
    @JvmStatic
    fun getResponse(response: String): AdminLoginRS? {
        try {
            Log.d("tag", " =  response = = $response")
            val gson = Gson()
            val type = object : TypeToken<AdminLoginRS?>() {}.type
            return gson.fromJson<AdminLoginRS>(response, type)
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("tag", "= = error in parser  = = " + e.message)
        }
        return null
    }
}