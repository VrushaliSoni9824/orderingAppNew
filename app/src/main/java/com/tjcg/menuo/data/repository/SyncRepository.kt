package com.tjcg.menuo.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.tjcg.menuo.data.local.AppDatabase.Companion.getDatabase
import com.tjcg.menuo.data.local.PosDao

class SyncRepository private constructor(context: Context) {
    var posDao: PosDao = getDatabase(context)!!.posDao()
    val sharedPreferences: SharedPreferences = context.getSharedPreferences("com.tjcg.nentopos", Context.MODE_PRIVATE)
    val deviceID=sharedPreferences.getString("device_ID","0")



    companion object {
        private var instance: SyncRepository? = null
        fun getInstance(context: Context): SyncRepository? {
            if (instance == null) {
                synchronized(SyncRepository::class.java) {
                    if (instance == null) {
                        instance = SyncRepository(context)
                    }
                }
            }
            return instance
        }
    }

}