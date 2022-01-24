package com.tjcg.menuo.data.response.order

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Entity
class OnlineOrderData {
    @PrimaryKey
    @SerializedName("order_id")
    @Expose
    var order_id: Int = 0

    @SerializedName("outlet_id")
    @Expose
    var outlet_id: String? = null

    @SerializedName("menu_id")
    @Expose
    var menu_id: String? = null

    @SerializedName("saleinvoice")
    @Expose
    var saleinvoice: String? = null

    @SerializedName("customer_id")
    @Expose
    var customer_id: String? = null

    @SerializedName("cutomertype")
    @Expose
    var cutomertype: String? = null

    @SerializedName("isthirdparty")
    @Expose
    var isthirdparty: String? = null

    @SerializedName("waiter_id")
    @Expose
    var waiter_id: String? = null

    @SerializedName("kitchen")
    @Expose
    var kitchen: String? = null

    @SerializedName("order_date")
    @Expose
    var order_date: String? = null

    @SerializedName("order_time")
    @Expose
    var order_time: String? = null

    @SerializedName("order_accept_date")
    @Expose
    var order_accept_date: String? = null

    @SerializedName("cookedtime")
    @Expose
    var cookedtime: String? = null

    @SerializedName("table_no")
    @Expose
    var table_no: String? = null

    @SerializedName("tokenno")
    @Expose
    var tokenno: String? = null

    @SerializedName("discount")
    @Expose
    var discount: String? = null

    @SerializedName("tip_type")
    @Expose
    var tip_type: String? = null

    @SerializedName("added_tip_amount")
    @Expose
    var added_tip_amount: String? = null

    @SerializedName("tip_amount")
    @Expose
    var tip_amount: String? = null

    @SerializedName("totalamount")
    @Expose
    var totalamount: String? = null

    @SerializedName("customerpaid")
    @Expose
    var customerpaid: String? = null

    @SerializedName("customer_note")
    @Expose
    var customer_note: String? = null

    @SerializedName("anyreason")
    @Expose
    var anyreason: String? = null

    @SerializedName("order_status")
    @Expose
    var order_status: String? = null

    @SerializedName("is_driver_assigned")
    @Expose
    var pis_driver_assigned: String? = null

    @SerializedName("driver_user_id")
    @Expose
    var driver_user_id: String? = null

    @SerializedName("is_order_delivered")
    @Expose
    var pis_order_delivered: String? = null

    @SerializedName("is_payment_received")
    @Expose
    var pis_payment_received: String? = null

    @SerializedName("received_payment_amount")
    @Expose
    var received_payment_amount: String? = null

    @SerializedName("order_pickup_at")
    @Expose
    var order_pickup_at: String? = null

    @SerializedName("future_order_date")
    @Expose
    var future_order_date: String? = null

    @SerializedName("future_order_time")
    @Expose
    var future_order_time: String? = null

    @SerializedName("nofification")
    @Expose
    var nofification: String? = null

    @SerializedName("online_order_notification")
    @Expose
    var online_order_notification: String? = null

    @SerializedName("kitchen_notification")
    @Expose
    var kitchen_notification: String? = null

    @SerializedName("orderacceptreject")
    @Expose
    var orderacceptreject: String? = null

    @SerializedName("is_frontend_order")
    @Expose
    var pis_frontend_order: String? = null

    @SerializedName("is_qr_order")
    @Expose
    var pis_qr_order: String? = null

    @SerializedName("customer_name")
    @Expose
    var customer_name: String? = null

    @SerializedName("customer_type")
    @Expose
    var customer_type: String? = null

    @SerializedName("first_name")
    @Expose
    var first_name: String? = null

    @SerializedName("last_name")
    @Expose
    var last_name: String? = null

    @SerializedName("tablename")
    @Expose
    var tablename: String? = null

    @SerializedName("bill_status")
    @Expose
    var bill_status: String? = null

    @SerializedName("keyword")
    @Expose
    var keyword: String? = null
}