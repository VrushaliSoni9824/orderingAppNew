package com.tjcg.menuo.utils

object Constants {
    const val BASE_URL = "https://apiv4.ordering.co/v400/en/menuo/"
    const val BASE_URL_2 = "https://apiv4.ordering.co/v400/en/menuo/users/1/"
    const val CONNECTION_TIMEOUT = 100 // 10 seconds
    const val READ_TIMEOUT = 20 // 2 seconds
    const val WRITE_TIMEOUT = 20 // 2 seconds
    const val RECIPE_REFRESH_TIME = 60 * 60 * 24 * 30 // 30 days (in seconds)
    const val SOCKET_URL = "https://socket.ordering.co/"

    @JvmField
    var authorization_key = "Authorization"

    @JvmStatic
    var CUREENCY = "$ "

    @JvmStatic
    var Authorization = ""

    @JvmStatic
    var bearrToken = ""

    const val URL = "http://apiv4.ordering.co/v400/en/menuo/orders?"
    const val NOTIFY_API_URL = "https://menuo.area81-ns.se/menuo-notify/public/api/v1/"
//    const val BUSINESS_URL = "https://apiv4.ordering.co/v400/en/menuo/users/"
//    const val BUSINESS_URL = "https://apiv4.ordering.co/v400/en/menuo/business?mode=dashboard&params=email,name"
    const val BUSINESS_URL = "https://apiv4.ordering.co/v400/en/menuo/business?mode=dashboard&params=name,email,owner_id"
    const val NOTIFICATION_ENDPOINTS = "/notification_tokens"

//    const val GET_NOTI_URL = "https://apiv4.ordering.co/v400/en/menuo/users/52/notification_tokens"
//    const val URL = "http://apiv4.ordering.co/v400/en/menuo/orders?page_size=10&mode=dashboard&page=1"


    const val ORDER_STATUS_PENDING = "1"
    const val ORDER_STATUS_PROCESSING = "2"
    const val ORDER_STATUS_READY = "3"
    const val ORDER_STATUS_SERVED = "4"
    const val ORDER_STATUS_CANCELED = "5"
    const val PAYMENT_METHOD_CASH = "1"
    const val PAYMENT_METHOD_DEBIT = "2"
    const val PAYMENT_METHOD_CREDIT = "3"

    const val SERVICE_PACKAGE = "woyou.aidlservice.jiuiv5"
    const val SERVICE_ACTION = "woyou.aidlservice.jiuiv5.IWoyouService"
    const val ESC : Byte = 0x1B// Escape

    const val apiKey="g-eSpEOsu17RRq4Z2EtO7V2wj3QwiWiNL17vJhPWEzsQG_ejD4-ciowEp7rm8Cnxo"
    const val dateFormat_a = "MMM dd,yyyy hh:mm a"

    const val rejectStatus = "6"
    const val acceptStatus = "7"
    const val readyForPickUp = "4"


}