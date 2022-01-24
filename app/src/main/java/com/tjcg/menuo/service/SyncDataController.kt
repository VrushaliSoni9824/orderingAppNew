package com.tjcg.menuo.service

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.tjcg.menuo.data.local.OrderDao
import com.tjcg.menuo.data.remote.ServiceGenerator
import com.tjcg.menuo.data.response.EntitiesModel.*
import com.tjcg.menuo.data.response.newOrder.Result
import com.tjcg.menuo.utils.Constants
import com.tjcg.menuo.utils.PrefManager
import com.tjcg.menuo.utils.SharedPreferencesKeys
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.ArrayList

class SyncDataController(context: Context) {
    private var prefManager: PrefManager? = null
    var orderDao: OrderDao? = null
    var applicationContext = context
    lateinit var orderResult: String
    var count: Int = 0
    val paginationLink = ArrayList<String>()
    var businessId: String = ""
    var linkCount: Int = 0
    var resultDataList = ArrayList<Result>()


    companion object {
        private var instance: SyncDataController? = null
        fun getInstance(context: Context): SyncDataController? {
            if (instance == null) {
                synchronized(SyncDataController::class.java) {
                    if (instance == null) {
                        instance = SyncDataController(context)
                    }
                }
            }
            return instance
        }
    }


    /*class ClearOrderItemDataAsyncTask() : CoroutineAsyncTask<Void, Void, Void>(){
 n
        override fun doInBackground(vararg params: Void?): Void? {
            try {
                resultDataList = ArrayList<Result>()
                if (MainApp.mainApp!!.getDao() != null) {
                    resultDataList =  MainApp.mainApp!!.getDao()!!.getAllResult()
                }
            }catch (ex: Exception){}
            return null
        }

    }*/



    fun getDataBase() {
        if (!prefManager!!.getBoolean(SharedPreferencesKeys.isFromLogin)) {
            prefManager!!.setBoolean(SharedPreferencesKeys.isFromLogin, true)
            var url = Constants.URL
            url = url + "page_size=" + "10" + "&mode=" + "dashboard" + "&page=" + "1" + "&where=[{\"attribute\":\"business_id\",\"value\":[" + businessId + "]}]"
            getPaginationInfor(url, Constants.apiKey)
        }
    }


    fun getPaginationInfor(url: String, key: String) {
        //lottieProgressDialog!!.showDialog()
        var responceString: String = ""

        ServiceGenerator.nentoApi.getUsers(url, key)!!.enqueue(object :
                Callback<String?> {
            @SuppressLint("NewApi", "ResourceAsColor")
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onResponse(call: Call<String?>, response: Response<String?>) {
                if (response.isSuccessful) {
//                    lottieProgressDialog!!.cancelDialog()
                    Toast.makeText(applicationContext, "Login Successfully", Toast.LENGTH_SHORT).show()
                    orderResult = response.body()!!.toString()
                    parsePaginationInfoJson(orderResult, true)

//                    responceString=orderResult
//                    expandableDetailList = ExpandableListDataItems.getData(responceString)
//                    expandableTitleList = ArrayList(expandableDetailList!!.keys)
//                    expandableListAdapter =
//                        CustomizedExpandableListAdapter(applicationContext, expandableTitleList, expandableDetailList)
//                    expandableListViewExample!!.setAdapter(expandableListAdapter)

                } else {
                    //lottieProgressDialog!!.cancelDialog()
                    responceString = "error"
                    Toast.makeText(applicationContext, "Wrong detail", Toast.LENGTH_SHORT).show()
//                    Toast.makeText(mainActivity,"Error while fetching invoice detail..",Toast.LENGTH_SHORT).show()
                    Log.e("tag", " =  = = =error = ==  " + response.message())
                }
            }

            override fun onFailure(call: Call<String?>, t: Throwable) {
                //lottieProgressDialog!!.cancelDialog()
                getOrders(url, Constants.apiKey)
                responceString = "error"
                Toast.makeText(applicationContext, "Error", Toast.LENGTH_SHORT).show()
                Log.e("tag", " =  = = =error = ==  " + t.message)
            }
        })
    }

    fun convertIntToString(objInt: List<Int>) : List<String>{
//        var stringObj : List<String> = arrayListOf()
        val stringObj: MutableList<String> = ArrayList()
        for(obj in objInt){
            stringObj.add(obj.toString())
        }
        return stringObj
    }

    fun parsePaginationInfoJson(jsonString: String, isFirstTime: Boolean) {

        try {
            val jobj = JSONObject(jsonString)
            val jsnPagination = jobj.getJSONObject("pagination")!!
            val totalRecord = jsnPagination.getString("total")!!
            val from = jsnPagination.getString("from")!!
            val to = jsnPagination.getString("to")!!
            val current_page = jsnPagination.getString("current_page")!!
            val page_size = jsnPagination.getString("page_size")!!
            val total_pages = jsnPagination.getString("total_pages")!!
            val fisrt_page = jsnPagination.getString("fisrt_page")!!
            val back_page = jsnPagination.getString("back_page")!!
            val next_page = jsnPagination.getString("next_page")!!
            val last_page = jsnPagination.getString("last_page")!!


            for (i in 1..total_pages.toInt()) {
                var url: String = makeUrl(i.toString(), "52")
                paginationLink!!.add(url)
            }
            paginationLink

            for (link in paginationLink) {
                getOrders(link, Constants.apiKey)
            }

        } catch (e: JSONException) {
            e.printStackTrace()
        }

    }


    fun getOrders(url: String, key: String) {
//        lottieProgressDialog!!.showDialog()
        var responceString: String = ""

        ServiceGenerator.nentoApi.getUsers(url, key)!!.enqueue(object :
                Callback<String?> {
            @SuppressLint("NewApi", "ResourceAsColor")
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onResponse(call: Call<String?>, response: Response<String?>) {
                if (response.isSuccessful) {
                    count = count + 1
                    responceString = response.body().toString()

                    parseOrderDataJson(responceString)

//                    expandableDetailList = ExpandableListDataItems.getData(responceString)
//                    expandableTitleList = ArrayList(expandableDetailList!!.keys)
//                    expandableListAdapter =
//                        CustomizedExpandableListAdapter(applicationContext, expandableTitleList, expandableDetailList)
//                    expandableListViewExample!!.setAdapter(expandableListAdapter)

                } else {
                    //lottieProgressDialog!!.cancelDialog()
                    responceString = "error"
                    Toast.makeText(applicationContext, "Wrong detail", Toast.LENGTH_SHORT).show()
//                    Toast.makeText(mainActivity,"Error while fetching invoice detail..",Toast.LENGTH_SHORT).show()
                    Log.e("tag", " =  = = =error = ==  " + response.message())
                }
            }

            override fun onFailure(call: Call<String?>, t: Throwable) {
                //lottieProgressDialog!!.cancelDialog()
                getOrders(url, Constants.apiKey)
                responceString = "error"
                Toast.makeText(applicationContext, "Error", Toast.LENGTH_SHORT).show()
                Log.e("tag", " =  = = =error = ==  " + t.message)
            }
        })
    }


    fun makeUrl(pageNo: String, businessId: String): String {
        var url: String = Constants.URL
        url = url + "page_size=" + "10" + "&mode=" + "dashboard" + "&page=" + pageNo + "&where=[{\"attribute\":\"business_id\",\"value\":[" + businessId + "]}]"
        return url
    }

    fun parseOrderDataJson(jsonData: String) {

        // As we are populating List of fruits, vegetables and nuts, using them here
        // We can modify them as per our choice.
        // And also choice of fruits/vegetables/nuts can be changed
        val arrOdate = ArrayList<String>()
        val arrOId = ArrayList<String>()

        try {
            val jobj = JSONObject(jsonData)
            val jarrResult: JSONArray = jobj.getJSONArray("result")
            //            for (order:jarrResult) {
//
//            }
            var resultList: ArrayList<Result>? = arrayListOf()
            val bisinessTblList: ArrayList<BisinessEntity> = ArrayList()
            val cityTblList: ArrayList<CityEntity> = ArrayList()
            val customerTblList: ArrayList<CustomerEntity> = ArrayList()
            val driverTblList: ArrayList<DriverEntity> = ArrayList()
            val historyTblList: ArrayList<HistoryEntity> = ArrayList()
            val locationTblList: ArrayList<LocationEntity> = ArrayList()
            val metafieldTblList: ArrayList<MetafieldEntity> = ArrayList()
            val paymethodTblList: ArrayList<PaymethodEntity> = ArrayList()
            val productTblList: ArrayList<ProductEntity> = ArrayList()
            val reviewTblList: ArrayList<ReviewEntity> = ArrayList()
            val summaryTblList: ArrayList<SummaryEntity> = ArrayList()

//            var latein resultList : List<Result>

            for (i in 0..jarrResult.length() - 1) {
                val order = jarrResult.getJSONObject(i)

                var resObj: Result = Result()
                var orderId = order.getString("id");
                //  //result
                resObj!!.id = order.getString("id").toInt()
                resObj!!.app_id = order.getString("app_id")
                resObj!!.business_id = order.getString("business_id")
                resObj!!.cash = order.getString("cash")
                resObj!!.comment = order.getString("comment")
                resObj!!.coupon = order.getString("coupon")
                resObj!!.created_at = order.getString("created_at")
                //fetch customer
                resObj!!.customer_id = order.getString("customer_id")
                resObj!!.delivered_in = order.getString("delivered_in")
                resObj!!.delivery_datetime = order.getString("delivery_datetime")
                resObj!!.delivery_datetime_utc = order.getString("delivery_datetime_utc")
                resObj!!.delivery_type = order.getString("delivery_type")
                resObj!!.delivery_zone_id = order.getString("delivery_zone_id")
                resObj!!.delivery_zone_price = order.getString("delivery_zone_price")
                resObj!!.discount = order.getString("discount")
                resObj!!.driver = order.getString("driver")
                resObj!!.driver_compString_id = "0"
                resObj!!.driver_id = order.getString("driver_id")
                resObj!!.driver_tip = order.getString("driver_tip")
                resObj!!.expired_at = order.getString("expired_at")
                resObj!!.external_driver = order.getString("external_driver")
                resObj!!.external_driver_id = order.getString("external_driver_id")
                //load histor
                resObj!!.language_id = order.getString("language_id")
                resObj!!.last_direct_message_at = order.getString("last_direct_message_at")
                resObj!!.last_driver_assigned_at = order.getString("last_driver_assigned_at")
                resObj!!.last_general_message_at = order.getString("last_general_message_at")
                resObj!!.last_logistic_attempted_at = order.getString("last_logistic_attempted_at")
                resObj!!.last_message_at = order.getString("last_message_at")
                resObj!!.locked = order.getString("locked")
                resObj!!.logistic_attemps = order.getString("logistic_attemps")
                resObj!!.logistic_status = order.getString("logistic_status")
                //load meta fiels
                resObj!!.offer = order.getString("offer")
                resObj!!.offer_id = order.getString("offer_id")
                resObj!!.offer_rate = order.getString("offer_rate")
                resObj!!.offer_type = order.getString("offer_type")
                resObj!!.order_group = order.getString("order_group")
                resObj!!.order_group_id = order.getString("order_group_id")
                resObj!!.pay_data = order.getString("pay_data")

                resObj!!.paymethod_id = order.getString("paymethod_id")
                resObj!!.place = order.getString("place")
                resObj!!.place_id = order.getString("place_id")
                resObj!!.prepared_in = order.getString("prepared_in")
                resObj!!.priority = order.getString("priority")
//                paymethod load
//                product

                resObj!!.refund_data = order.getString("refund_data")
                resObj!!.resolved_at = order.getString("resolved_at")
                resObj!!.review = order.getString("review")
                resObj!!.service_fee = order.getString("service_fee")
                resObj!!.status = order.getString("status")
                //    this.summary = orderResultSummary,
                resObj!!.summary_version = order.getString("summary_version")
                resObj!!.tax = order.getString("tax")
                resObj!!.tax_type = order.getString("tax_type")
                resObj!!.to_print = order.getString("to_print")
                resObj!!.unread_count = order.getString("unread_count")
                resObj!!.unread_direct_count = order.getString("unread_direct_count")
                resObj!!.unread_general_count = order.getString("unread_general_count")

                resObj!!.updated_at = order.getString("updated_at")
                resObj!!.user_review = order.getString("user_review")
                resObj!!.uuid = order.getString("uuid")

                resultList!!.add(resObj)


                val buisnessJson = order.getJSONObject("business")
                val batchDetailsEntity = BisinessEntity(
                        buisnessJson.getInt("id"),
                        buisnessJson.getInt("order_id"),
                        buisnessJson.getString("name"),
                        buisnessJson.getString("logo"),
                        buisnessJson.getString("email"),
                        buisnessJson.getInt("city_id"),
                        buisnessJson.getString("address"),
                        buisnessJson.getString("address_notes"),
                        buisnessJson.getString("zipcode"),
                        buisnessJson.getInt("cellphone"),
                        buisnessJson.getInt("phone"),
                        buisnessJson.getString("location"),
                        buisnessJson.getString("header"),
                        buisnessJson.getString("pickup_time"),
                        buisnessJson.getString("delivery_time"),
                        buisnessJson.getString("city")
                )
                bisinessTblList.add(batchDetailsEntity)

                val cityJson = buisnessJson.getJSONObject("city")
                val cityDetailsEntity = CityEntity(
                        cityJson.getInt("id"),
                        cityJson.getString("name"),
                        cityJson.getInt("country_id"),
                        cityJson.getInt("administrator_id"),
                        cityJson.getBoolean("enabled"),
                        orderId.toInt()
                )
                cityTblList.add(cityDetailsEntity)

                val customerObj = order.getJSONObject("customer")
                val customerDetailsEntity = CustomerEntity(
                        customerObj.getInt("id"),
                        customerObj.getInt("order_id"),
                        customerObj.getString("name"),
                        customerObj.getString("photo"),
                        customerObj.getString("lastname"),
                        customerObj.getString("email"),
                        customerObj.getString("dropdown_option"),
                        customerObj.getString("address"),
                        customerObj.getString("address_notes"),
                        customerObj.getString("zipcode"),
                        customerObj.getInt("cellphone"),
                        customerObj.getString("phone"),
                        customerObj.getString("location"),
                        customerObj.getString("internal_number"),
                        customerObj.getString("map_data"),
                        customerObj.getString("tag"),
                        customerObj.getString("middle_name"),
                        customerObj.getString("second_lastname"),
                        customerObj.getString("country_phone_code"),
                        customerObj.getString("dropdown_option_id")
                )
                customerTblList.add(customerDetailsEntity)


//                val driverDetailsEntity = DriverEntity(
//                        order.getInt("id"),
//                        order.getString("name"),
//                        order.getString("lastname"),
//                        order.getString("email"),
//                        order.getInt("login_type"),
//                        order.getString("social_id"),
//                        order.getString("photo"),
//                        order.getString("birthdate"),
//                        order.getString("phone"),
//                        order.getInt("cellphone"),
//                        order.getString("city_id"),
//                        order.getString("dropdown_option_id"),
//                        order.getString("address"),
//                        order.getString("address_notes"),
//                        order.getString("zipcode"),
//                        order.getString("location"),
//                        order.getInt("level"),
//                        order.getInt("language_id"),
//                        order.getBoolean("push_notifications"),
//                        order.getBoolean("busy"),
//                        order.getBoolean("available"),
//                        order.getBoolean("enabled"),
//                        order.getString("created_at"),
//                        order.getString("updated_at"),
//                        order.getString("deleted_at"),
//                        order.getString("internal_number"),
//                        order.getString("map_data"),
//                        order.getString("middle_name"),
//                        order.getString("second_lastname"),
//                        order.getString("country_phone_code"),
//                        order.getInt("priority"),
//                        order.getString("last_order_assigned_at"),
//                        order.getString("last_location_at"),
//                        order.getBoolean("phone_verified"),
//                        order.getBoolean("email_verified"),
//                        order.getBoolean("driver_zone_restriction"),
//                        order.getString("pin"),
//                        order.getString("business_id"),
//                        order.getString("franchise_id"),
//                        order.getString("register_site_id"),
//                        order.getString("ideal_orders"),
//                        order.getInt("order_id")
//
//                )
//                driverTblList.add(driverDetailsEntity)

                val historyArray : JSONArray = order.getJSONArray("history")
                if(historyArray.length()>0){
                    for(i in 0..historyArray!!.length()-1){
                        var historyObj : JSONObject = historyArray.getJSONObject(i)
                        val historyDetailsEntity = HistoryEntity(
                                historyObj.getInt("id"),
                                historyObj.getInt("order_id"),
                                historyObj.getInt("type"),
                                historyObj.getString("data"),
                                historyObj.getString("created_at"),
                                historyObj.getString("updated_at")
                        )
                        historyTblList.add(historyDetailsEntity)
                    }
                }

//                val locationDetailsEntity = LocationEntity(
//                        order.getInt("order_id"),
//                        order.getDouble("lat"),
//                        order.getDouble("lng")
//                )
//                locationTblList.add(locationDetailsEntity)

//                val metafieldDetailsEntity = MetafieldEntity(
//                        order.getInt("id"),
//                        order.getInt("object_id"),
//                        order.getString("model"),
//                        order.getString("key"),
//                        order.getString("value"),
//                        order.getString("value_type"),
//                        order.getString("created_at"),
//                        order.getString("updated_at"),
//                        order.getInt("order_id")
//
//                )
//                metafieldTblList.add(metafieldDetailsEntity)


//                val paymethodDetailsEntity = PaymethodEntity(
//                        order.getInt("id"),
//                        order.getString("name"),
//                        order.getString("gateway"),
//                        order.getBoolean("enabled"),
//                        order.getString("deleted_at"),
//                        order.getString("created_at"),
//                        order.getString("updated_at"),
//                        order.getInt("order_id")
//                )
//                paymethodTblList.add(paymethodDetailsEntity)

                val productsArray : JSONArray = order.getJSONArray("products")
                for(i in 0..productsArray.length()-1){
                    val productJson : JSONObject = productsArray.getJSONObject(i)
                    val productDetailsEntity = ProductEntity(
                            productJson.getInt("id"),
                            productJson.getInt("product_id"),
                            productJson.getInt("order_id"),
                            productJson.getString("name"),
                            productJson.getInt("price"),
                            productJson.getInt("quantity"),
                            productJson.getString("comment"),
                            productJson.getString("ingredients"),
                            productJson.getString("options"),
                            productJson.getBoolean("featured"),
                            productJson.getBoolean("upselling"),
                            productJson.getBoolean("in_offer"),
                            productJson.getString("offer_price"),
                            productJson.getString("images"),
                            productJson.getInt("offer_rate"),
                            productJson.getInt("offer_rate_type"),
                            productJson.getBoolean("offer_include_options"),
                            productJson.getInt("status"),
                            productJson.getInt("priority"),
                            productJson.getString("reporting_data"),
                            productJson.getString("fee_id"),
                            productJson.getString("tax_id"),
                            productJson.getString("summary"),
                            productJson.getInt("category_id"),
                            productJson.getInt("total"),
                            productJson.getString("tax"),
                            productJson.getString("fee")
                    )
                    productTblList.add(productDetailsEntity)
                }
//                val reviewDetailsEntity = ReviewEntity(
//                        order.getInt("id"),
//                        order.getInt("order_id"),
//                        order.getInt("quality"),
//                        order.getInt("delivery"),
//                        order.getInt("service"),
//                        order.getInt("package"),
//                        order.getInt("user_id"),
//                        order.getString("comment"),
//                        order.getBoolean("enabled"),
//                        order.getString("created_at"),
//                        order.getString("updated_at")
//
//                )
//                reviewTblList.add(reviewDetailsEntity)

                val summeryObj : JSONObject = order.getJSONObject("summary")
                val summaryDetailsEntity = SummaryEntity(
                        orderId.toInt(),
                        summeryObj.getInt("total"),
                        summeryObj.getInt("discount"),
                        summeryObj.getDouble("subtotal"),
                        summeryObj.getDouble("subtotal_with_discount"),
                        summeryObj.getInt("service_fee_rate"),
                        summeryObj.getInt("service_fee"),
                        summeryObj.getInt("service_fee_with_discount"),
                        summeryObj.getInt("delivery_price"),
                        summeryObj.getInt("delivery_price_with_discount"),
                        summeryObj.getInt("tax_rate"),
                        summeryObj.getDouble("tax"),
                        summeryObj.getDouble("tax_with_discount"),
                        summeryObj.getInt("driver_tip_rate"),
                        summeryObj.getInt("driver_tip")

                )
                summaryTblList.add(summaryDetailsEntity)

            }

            orderDao!!.insertOrderResult(resultList!!)
            orderDao!!.insertBisinessData(bisinessTblList!!)
            orderDao!!.insertCityData(cityTblList!!)
            orderDao!!.insertCustomerData(customerTblList!!)
            orderDao!!.insertDriverData(driverTblList!!)
            orderDao!!.insertHistoryData(historyTblList!!)
            orderDao!!.insertLocationData(locationTblList!!)
            orderDao!!.insertMetafieldData(metafieldTblList!!)
            orderDao!!.insertPaymethodData(paymethodTblList!!)
            orderDao!!.insertProductData(productTblList!!)
            orderDao!!.insertReviewData(reviewTblList!!)
            orderDao!!.insertSummaryData(summaryTblList!!)

            linkCount = linkCount + 1

            if (linkCount.equals(paginationLink.size)) {
                var orderListPending : List<Int> = orderDao!!.getPendingOrder()
                var orderListInprocess : List<Int> = orderDao!!.getInProgressOrder()
                var orderListDone : List<Int> = orderDao!!.getDoneOrder()

                var orderListPendingString : List<String> = convertIntToString(orderListPending)
                var orderListInprocessString : List<String> = convertIntToString(orderListInprocess)
                var orderListDoneString : List<String> = convertIntToString(orderListDone)

                /*expandableDetailList = ExpandableListDataItems.getData("", applicationContext,orderListPendingString,orderListInprocessString,orderListDoneString)
                expandableTitleList = ArrayList(expandableDetailList!!.keys)
                expandableListAdapter =
                        CustomizedExpandableListAdapter(
                                applicationContext,
                                expandableTitleList,
                                expandableDetailList
                        )
                expandableListViewExample!!.setAdapter(expandableListAdapter)
               // lottieProgressDialog!!.cancelDialog()
*/

            }

//            parsePaginationInfoJson(jsonData,false)

        } catch (e: JSONException) {
            e.printStackTrace()
        }

    }
}