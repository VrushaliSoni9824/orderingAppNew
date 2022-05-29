package com.tjcg.menuo

import android.annotation.SuppressLint
import android.app.*
//import com.tjcg.nentopos.MainActivity.getOutlets
//import com.tjcg.nentopos.data.response.Login.OutletsRS.name
import com.google.firebase.messaging.FirebaseMessagingService
import android.content.SharedPreferences
import com.google.firebase.messaging.RemoteMessage
import android.graphics.BitmapFactory
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.content.Context
import android.graphics.Color
import android.util.Log
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.tjcg.menuo.data.local.AppDatabase
import com.tjcg.menuo.data.local.OrderDao
import com.tjcg.menuo.data.remote.ServiceGenerator
import com.tjcg.menuo.data.response.order.OnlineOrderRS
import com.tjcg.menuo.utils.Constants
import com.tjcg.menuo.utils.SharedPreferencesKeys
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.app.ActivityManager.RunningAppProcessInfo
import android.media.MediaPlayer
import android.view.Window
import android.widget.ImageView
import android.widget.Toast
import com.tjcg.menuo.data.response.EntitiesModel.*
import com.tjcg.menuo.data.response.newOrder.DialogQueue
import com.tjcg.menuo.data.response.newOrder.Result
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.ArrayList
import android.app.ActivityManager
import android.app.ActivityManager.RunningTaskInfo
import android.content.pm.ActivityInfo

import android.content.pm.PackageManager
import android.app.Activity
import android.content.ComponentName
import io.sentry.Sentry


class MyFirebaseMessagingService : FirebaseMessagingService() {
    var db: AppDatabase? = null
    var orderDao: OrderDao? = null
    var orderId="";
    var deliveryDateTime = "";
    var deliverytype = "";
    var status = "";
    var summery = "";
    override fun onNewToken(s: String) {
        Log.e("mtoken", s)
        val sharedPref = getSharedPreferences("com.tjcg.nentopos", MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putString(SharedPreferencesKeys.device_token, s)
        editor.apply()
        super.onNewToken(s)
    }

    /**
     * Called when message is received.
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        Log.e("AAAAA", "on receive")
        Sentry.captureMessage("new order")
        db = AppDatabase.getDatabase(this)
        orderDao = AppDatabase.getDatabase(this)!!.orderDao()
        val data = remoteMessage.data
        var notiValues : MutableCollection<String> = data.values
        var i : Int = 0;
        for (value in notiValues)
        {
            if(i==0){
                summery=value
            }else if(i==1){
                deliverytype=value
            }else if(i==2){
                deliveryDateTime=value
            }else if(i==3){
                orderId=value
            }
            i++
        }
        var urlForFindOrder : String ="https://apiv4.ordering.co/v400/en/menuo/orders/"+orderId+"?mode=dashboard"
        findOrder(urlForFindOrder,Constants.apiKey);
    }

    /**
     * Create and show a custom notification containing the received FCM message.
     * @param notification FCM notification payload received.
     * @param data FCM data payload received.
     */
    private fun sendNotification(body : String,title : String) {
        val icon = BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher_round)
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)
        val notificationBuilder = NotificationCompat.Builder(this, "channel_id")
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            .setContentIntent(pendingIntent)
            .setContentInfo(title)
            .setLargeIcon(icon)
            .setColor(Color.RED)
            .setLights(Color.RED, 1000, 300)
            .setDefaults(Notification.DEFAULT_VIBRATE)
            .setSmallIcon(R.mipmap.ic_launcher_round)
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        // Notification Channel is required for Android O and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "channel_id", "channel_name", NotificationManager.IMPORTANCE_DEFAULT
            )
            channel.description = "channel description"
            channel.setShowBadge(true)
            channel.canShowBadge()
            channel.enableLights(true)
            channel.lightColor = Color.RED
            channel.enableVibration(true)
            channel.vibrationPattern = longArrayOf(100, 200, 300, 400, 500)
            notificationManager.createNotificationChannel(channel)
        }
        notificationManager.notify(0, notificationBuilder.build())
    }

    private fun applicationInForeground(): Boolean {
        val activityManager = getSystemService(ACTIVITY_SERVICE) as ActivityManager
        val services = activityManager.runningAppProcesses
        var isActivityFound = false
        if (services[0].processName.equals(packageName) && services[0].importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
            isActivityFound = true
        }
        return isActivityFound
    }

    fun findOrder(url: String, key: String) {
        var responceString: String = null.toString()
        ServiceGenerator.nentoApi.findOrder(url, key)!!.enqueue(object :
            Callback<String?> {
            @SuppressLint("NewApi", "ResourceAsColor")
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onResponse(call: Call<String?>, response: Response<String?>) {
                if (response.isSuccessful) {
                    responceString = response.body().toString()
                    fetchAndInsetOrder(responceString)
                } else {
                    findOrder(url,key)
                }
            }
            override fun onFailure(call: Call<String?>, t: Throwable) {
                responceString = null.toString()
                findOrder(url,key)
                Toast.makeText(applicationContext, "Error", Toast.LENGTH_SHORT).show()
            }
        })
    }

    fun fetchAndInsetOrder(jsonData: String)  {
        try {
            val jobj = JSONObject(jsonData)
            val jarrResult: JSONObject = jobj.getJSONObject("result")
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
            val order = jarrResult

                var resObj: Result = Result()
                var orderId = order.getString("id");

                status = order.getString("status")

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
                    customerObj.getString("cellphone"),
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
            val dialogEntity = DialogQueue(orderId.toInt(), orderId.toInt().toString())
            orderDao!!.insertQueueData(dialogEntity)
            if(applicationInForeground()){

                val am = this.getSystemService(ACTIVITY_SERVICE) as ActivityManager
                val taskInfo = am.getRunningTasks(1)
                Log.d("topActivity", "CURRENT Activity ::" + taskInfo[0].topActivity!!.className)
                val componentInfo = taskInfo[0].topActivity
                componentInfo!!.packageName
                componentInfo.className
                if(componentInfo.className.equals("com.tjcg.menuo.Expandablectivity")){
                    val inten = Intent()
                    inten.action = "new_order"
                    inten.putExtra("id",orderId)
                    inten.putExtra("deliverytype",deliverytype)
                    inten.putExtra("deliveryDateTime",deliveryDateTime)
                    inten.putExtra("status",status)
                    sendBroadcast(inten)
                }else{
                    Log.d("topActivity", "CURRENT Activity ::" + taskInfo[0].topActivity!!.className)
                    val inten = Intent()
                    inten.action = "new_order_pre"
                    inten.putExtra("id",orderId)
                    inten.putExtra("deliverytype",deliverytype)
                    inten.putExtra("deliveryDateTime",deliveryDateTime)
                    inten.putExtra("status",status)
                    sendBroadcast(inten)
                }

            }else{
                sendNotification("New Order #"+orderId,"New order")
            }

        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }
}