package com.tjcg.menuo.ExpandableList

import android.content.Context
import java.util.*

object ExpandableListDataItems {
    fun getData(jsonData: String?,context: Context,orderIdsPending : List<String>,orderIdsProcessing : List<String>, orderIDsComplete : List<String>): HashMap<String, List<String>> {
        val expandableDetailList = HashMap<String, List<String>>()

        // As we are populating List of fruits, vegetables and nuts, using them here
        // We can modify them as per our choice.
        // And also choice of fruits/vegetables/nuts can be changed
        val arrOdate = ArrayList<String>()
        val arrOId = ArrayList<String>()
//        try {
//            val jobj = JSONObject(jsonData)
//            val jarrResult = jobj.getJSONArray("result")
//            //            for (order:jarrResult) {
////
////            }
//            for (i in 0..jarrResult.length()) {
//                val order = jarrResult.getJSONObject(i)
//                arrOdate.add(order.getString("delivery_datetime"))
//                arrOId.add(order.getString("id"))
//            }
//        } catch (e: JSONException) {
//            e.printStackTrace()
//        }
        val fruits: MutableList<String> = ArrayList()
        fruits.add("Apple")
        fruits.add("Orange")
        fruits.add("Guava")
        fruits.add("Papaya")
        fruits.add("Pineapple")
        val vegetables: MutableList<String> = ArrayList()
        vegetables.add("Tomato")
        vegetables.add("Potato")
        vegetables.add("Carrot")
        vegetables.add("Cabbage")
        vegetables.add("Cauliflower")
        val nuts: MutableList<String> = ArrayList()
        nuts.add("Cashews")
        nuts.add("Badam")
        nuts.add("Pista")
        nuts.add("Raisin")
        nuts.add("Walnut")

        // Fruits are grouped under Fruits Items. Similarly the rest two are under
        // Vegetable Items and Nuts Items respecitively.
        // i.e. expandableDetailList object is used to map the group header strings to
        // their respective children using an ArrayList of Strings.

//        var orderDao: OrderDao = getDatabase(context!!)!!.orderDao()
//        var orderId = orderDao.getResult()
       /* expandableDetailList["Done"] = orderIdsPending
        expandableDetailList["Inprogress Orders"] = orderIdsProcessing
        expandableDetailList["Pending Orders"] = orderIDsComplete*/
//        expandableDetailList["Done"] = orderIDsComplete
        expandableDetailList["Pending Orders"] = orderIdsPending
        expandableDetailList["Inprogress Orders"] = orderIdsProcessing



        return expandableDetailList
    }
}