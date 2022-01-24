package com.tjcg.menuo.fragment

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.tjcg.menuo.data.local.AppDatabase
import com.tjcg.menuo.data.response.Login.OutletsRS

open class BaseFragment : Fragment() {

    lateinit var appDatabase: AppDatabase
    protected lateinit var uniqueId: String
    protected lateinit var outletId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appDatabase = AppDatabase.getDatabase(requireContext())!!
        val outletsRS = getOutlets()
        val sharedPreferences: SharedPreferences =requireContext().getSharedPreferences("com.tjcg.nentopos", Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor =  sharedPreferences.edit()
        editor.putInt("current_outlets",outletsRS[0].outlet_id.toInt())
        editor.putString("current_outlets_unique_id", outletsRS[0].unique_id!!.toString())
        editor.apply()
        editor.commit()

        uniqueId = sharedPreferences.getString("current_outlets_unique_id",outletsRS[0].unique_id!!).toString()
        outletId = sharedPreferences.getInt("current_outlets",outletsRS[0].outlet_id!!.toInt()).toString()

    }

    public fun getOutlets() : List<OutletsRS>{
        val outletsRS = appDatabase.adminItemDao().outlets
        return outletsRS;
    }

}