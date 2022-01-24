package com.tjcg.menuo

import android.annotation.SuppressLint
import android.app.Notification
//import com.tjcg.nentopos.MainActivity.getOutlets
//import com.tjcg.nentopos.data.response.Login.OutletsRS.name
import com.google.firebase.messaging.FirebaseMessagingService
import android.content.SharedPreferences
import com.google.firebase.messaging.RemoteMessage
import android.graphics.BitmapFactory
import android.content.Intent
import android.app.PendingIntent
import android.media.RingtoneManager
import android.app.NotificationManager
import android.os.Build
import android.app.NotificationChannel
import android.content.Context
import android.graphics.Color
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.tjcg.menuo.data.local.AppDatabase
import com.tjcg.menuo.data.remote.ServiceGenerator
import com.tjcg.menuo.data.repository.SyncRepository
import com.tjcg.menuo.data.response.order.OnlineOrderRS
import com.tjcg.menuo.fragment.OnlineOrderFragment
import com.tjcg.menuo.utils.Constants
import com.tjcg.menuo.viewmodel.SyncViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.net.URL

class MyFirebaseMessagingService : FirebaseMessagingService() {
    var db: AppDatabase? = null
    var posViewModel: SyncViewModel? = null
    var outlet : String? = null
    var uniqueId : String? = null
    var action : String? = null


    override fun onNewToken(s: String) {
        Log.e("mtoken", s)
        val sharedPref = getSharedPreferences("com.tjcg.nentopos", MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putString("device_token", s)
        editor.apply()
        super.onNewToken(s)
    }



    /**
     * Called when message is received.
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    @SuppressLint("WrongThread")
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        db = AppDatabase.getDatabase(this)
        val syncRepository: SyncRepository = SyncRepository.getInstance(applicationContext)!!
        val notification = remoteMessage.notification
        val data = remoteMessage.data
        Log.e("AAAAA", "on receive")
        sendNotification(notification, data)
        var notiValues : MutableCollection<String> = data.values
        var count : Int =0;
        for (value in notiValues)
        {
            if(count.equals(1)){
                action=value
            }
            if(count.equals(6)){
                outlet=value
            }
            count++
        }
        uniqueId=db!!.adminItemDao().getUniqueId(outlet!!.toInt())
//        for (outlet in outlets) {
//            Log.e("outletvvvv", outlet.name!!)

//            getAddonsList(outlet.outlet_id,getResources().getString(R.string.is_all_data_no))
//            posViewModel!!.getAddons(outlet.outlet_id, outlet.unique_id,is_all_data = getResources().getString(R.string.is_all_data_yes))

            val key=notification!!.body!!.toString()
            when {
                key.equals(getResources().getString(R.string.CATEGORY_UPDATE)) -> { // Check title or body
//                    syncRepository.getItemCategoryList("1",getResources().getString(R.string.is_all_data_no))
                }
                key.equals(getResources().getString(R.string.CATEGORY_INSTRUCTION_UPDATE)) -> { // Check title or body

                }
                key.equals(getResources().getString(R.string.ADDON_UPDATE)) -> { // Check title or body

                }

                key.equals(getResources().getString(R.string.VARIATION_UPDATE)) -> { // Check title or body

                }
                key.equals(getResources().getString(R.string.MODIFIER_UPDATE)) -> { // Check title or body

                }
                key.equals(getResources().getString(R.string.RESERVATION_UPDATE)) -> { // Check title or body

                }
                key.equals(getResources().getString(R.string.WAITINGLIST_UPDATE)) -> { // Check title or body

                }
                key.equals(getResources().getString(R.string.CUSTOMER_CATEGORY_UPDATE)) -> { // Check title or body

                }
                key.equals(getResources().getString(R.string.CARD_TYPE_UPDATE)) -> { // Check title or body

                }
                key.equals(getResources().getString(R.string.ALL_ORDER_UPDATE)) -> { // Check title or body

                }
                key.equals(getResources().getString(R.string.KITCHEN_UPDATE)) -> { // Check title or body

                }
                key.equals(getResources().getString(R.string.COUNTER_DISPLAY_UPDATE)) -> { // Check title or body

                }
                key.equals(getResources().getString(R.string.SUB_USER_UPDATE)) -> { // Check title or body

                }
                key.equals(getResources().getString(R.string.PRODUCT_UPDATE)) -> { // Check title or body

                }
                key.equals(getResources().getString(R.string.FLOOR_UPDATE)) -> { // Check title or body

                }
                key.equals(getResources().getString(R.string.TABLE_UPDATE)) -> { // Check title or body

                }
                key.equals(getResources().getString(R.string.PAYMENT_METHOD_UPDATE)) -> { // Check title or body

                }
                key.equals(getResources().getString(R.string.ONLINE_ORDER_UPDATE)) -> { // Check title or body

                    getOnlineORderList(outlet!!,uniqueId!!,getResources().getString(R.string.False))
                    val sharedPreferences: SharedPreferences = application!!.getSharedPreferences("com.tjcg.nentopos", Context.MODE_PRIVATE)
                    val editor:SharedPreferences.Editor =  sharedPreferences.edit()
                    editor.putBoolean("IsNeworder", true)
                    editor.apply()
                    editor.commit()
                    var ofragment = OnlineOrderFragment.newInstance()
                    ofragment.showToast()
                }
            }

//        }
    }

    /**
     * Create and show a custom notification containing the received FCM message.
     * @param notification FCM notification payload received.
     * @param data FCM data payload received.
     */
    private fun sendNotification(
        notification: RemoteMessage.Notification?,
        data: Map<String, String>
    ) {
        val icon = BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher_round)
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)
        val notificationBuilder = NotificationCompat.Builder(this, "channel_id")
            .setContentTitle(notification!!.title)
            .setContentText(notification.body)
            .setAutoCancel(true)
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            .setContentIntent(pendingIntent)
            .setContentInfo(notification.title)
            .setLargeIcon(icon)
            .setColor(Color.RED)
            .setLights(Color.RED, 1000, 300)
            .setDefaults(Notification.DEFAULT_VIBRATE)
            .setSmallIcon(R.mipmap.ic_launcher_round)
        try {
            val picture_url = data["picture_url"]
            if (picture_url != null && "" != picture_url) {
                val url = URL(picture_url)
                val bigPicture = BitmapFactory.decodeStream(url.openConnection().getInputStream())
                notificationBuilder.setStyle(
                    NotificationCompat.BigPictureStyle().bigPicture(bigPicture).setSummaryText(
                        notification.body
                    )
                )
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
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


    fun getOnlineORderList(outletID: String,uniqueId: String, is_all_data: String) {
        val sharedPreferences: SharedPreferences = application!!.getSharedPreferences("com.tjcg.nentopos", Context.MODE_PRIVATE)
        val deviceID=sharedPreferences.getString("device_ID","0")
//        Log.e("tag", " = = = 1= = = $orderId")
//        Log.e("tag", " = = = 3= = = $deliveryBoyID")

//        lottieProgressDialog!!.showDialog()
        ServiceGenerator.nentoApi.getOnlineOrderListSync(outletID,uniqueId, deviceID, is_all_data,Constants.Authorization).enqueue(object :
            Callback<OnlineOrderRS?> {
            @SuppressLint("NewApi", "ResourceAsColor")
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onResponse(call: Call<OnlineOrderRS?>, response: Response<OnlineOrderRS?>) {
                if (response.isSuccessful) {
                    if (response.body() != null && response.body()!!.status.equals("true")) {
                        if(response.body()!!.data!!.new_order!!.size>0){
                            db!!.orderDao().insertOnlineOrderData(response.body()!!.data!!.new_order!!)
                        }
                    }
                } else {

//                    Toast.makeText(mainActivity,"Error while fetching invoice detail..",Toast.LENGTH_SHORT).show()
                    Log.e("tag", " =  = = =error = ==  " + response.message())
                }
            }

            override fun onFailure(call: Call<OnlineOrderRS?>, t: Throwable) {
                Log.e("tag", " =  = = =error = ==  " + t.message)
            }
        })
    }
}