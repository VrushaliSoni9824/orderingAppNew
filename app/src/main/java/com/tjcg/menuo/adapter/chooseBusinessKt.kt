package com.tjcg.menuo.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.google.firebase.messaging.FirebaseMessagingService
import com.tjcg.menuo.Expandablectivity
import com.tjcg.menuo.data.response.BusinessList.BusinessItem
import java.util.ArrayList
import com.tjcg.menuo.R
import com.tjcg.menuo.data.remote.ServiceGenerator
import com.tjcg.menuo.data.response.IntermediatorServerAPI.IntermediatorLogin
import com.tjcg.menuo.utils.PrefManager
import com.tjcg.menuo.utils.SharedPreferencesKeys
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class chooseBusinessKt(rList: MutableList<BusinessItem>, context: Context) :
    ArrayAdapter<BusinessItem>(context, android.R.layout.simple_list_item_1) {
    var prefManager: PrefManager? = null
    private var itemList: MutableList<BusinessItem> = ArrayList()

//    override fun add(`object`: BusinessItem?) {
//        itemList.add(`object`)
//        super.add(`object`)
//    }

    private inner class ViewHolder {
        var textViewBusinessName: TextView? = null
    }

    override fun getCount(): Int {
        return itemList.size
    }

    override fun getItem(index: Int): BusinessItem {
        return itemList[index]
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        prefManager = PrefManager(context)
        var holder: ViewHolder = ViewHolder()
        if (convertView == null) {
            val inflater =
                getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            convertView = inflater.inflate(R.layout.list_business_item, parent, false)
            holder.textViewBusinessName =
                convertView.findViewById<View>(R.id.textViewBusinessName) as TextView
            convertView.tag = holder
        } else holder = convertView.tag as ViewHolder
        val item = getItem(position)
        holder.textViewBusinessName!!.text = item.itemName
//        convertView!!.seton{
//            Toast.makeText(context,"Itemclicked", Toast.LENGTH_LONG).show()
//            val item = getItem()
//            var businessOwnerNAme = item.owner_id
//            var businessNAme = item.itemName
//            prefManager!!.setString(SharedPreferencesKeys.businessName, businessNAme)
//            prefManager!!.setString(SharedPreferencesKeys.businessOwner, businessOwnerNAme)
//            prefManager!!.setBoolean("isLogin", true)
//            prefManager!!.setString("businessID", item.id.toString())
//
//            val inten = Intent()
//            inten.action = "businessChoosed"
//            context.sendBroadcast(inten)
//
//        }
        return convertView!!
    }

    init {
        itemList = rList
    }
}


