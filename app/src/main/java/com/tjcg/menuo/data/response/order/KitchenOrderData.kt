package com.tjcg.menuo.data.response.order

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Entity
class KitchenOrderData {
    @PrimaryKey
    @SerializedName("order_id")
    @Expose
    var order_id: String = ""

    @SerializedName("outlet_id")
    @Expose
    var outlet_id: String? = null

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
    var thirdparty: String? = null

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
    var driver_assigned: String? = null

    @SerializedName("driver_user_id")
    @Expose
    var driver_user_id: String? = null

    @SerializedName("is_order_delivered")
    @Expose
    var order_delivered: String? = null

    @SerializedName("is_payment_received")
    @Expose
    var payment_received: String? = null

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
    var frontend_order: String? = null

    @SerializedName("is_qr_order")
    @Expose
    var qr_order: String? = null

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

//    @SerializedName("item_info")
//    @Expose
//    var item_info: ArrayList<KitchenOrderInfo>? = null

    constructor() {}

    constructor(ongoingOrder: KitchenOrderData) {
        order_id = ongoingOrder.order_id.toString()
        outlet_id = ongoingOrder.outlet_id
        saleinvoice = ongoingOrder.saleinvoice
        customer_id = ongoingOrder.customer_id
        cutomertype = ongoingOrder.cutomertype
        thirdparty = ongoingOrder.thirdparty
        waiter_id = ongoingOrder.waiter_id
        kitchen = ongoingOrder.kitchen
        order_date = ongoingOrder.order_date
        order_time = ongoingOrder.order_time
        order_accept_date = ongoingOrder.order_accept_date
        cookedtime = ongoingOrder.cookedtime
        table_no = ongoingOrder.table_no
        tokenno = ongoingOrder.tokenno
        discount = ongoingOrder.discount
        tip_type = ongoingOrder.tip_type
        added_tip_amount = ongoingOrder.added_tip_amount
        tip_amount = ongoingOrder.tip_amount
        totalamount = ongoingOrder.totalamount
        customerpaid = ongoingOrder.customerpaid
        customer_note = ongoingOrder.customer_note
        anyreason = ongoingOrder.anyreason
        order_status = ongoingOrder.order_status
        driver_assigned = ongoingOrder.driver_assigned
        driver_user_id = ongoingOrder.driver_user_id
        order_delivered = ongoingOrder.order_delivered
        payment_received = ongoingOrder.payment_received
        received_payment_amount = ongoingOrder.received_payment_amount
        order_pickup_at = ongoingOrder.order_pickup_at
        future_order_date = ongoingOrder.future_order_date
        future_order_time = ongoingOrder.future_order_time
        nofification = ongoingOrder.nofification
        online_order_notification = ongoingOrder.online_order_notification
        kitchen_notification = ongoingOrder.kitchen_notification
        orderacceptreject = ongoingOrder.orderacceptreject
        frontend_order = ongoingOrder.frontend_order
        qr_order = ongoingOrder.qr_order
        customer_name = ongoingOrder.customer_name
        customer_type = ongoingOrder.customer_type
        first_name = ongoingOrder.first_name
        last_name = ongoingOrder.last_name
        tablename = ongoingOrder.tablename
        bill_status = ongoingOrder.bill_status
        keyword = ongoingOrder.keyword
//        item_info=ongoingOrder.item_info
    }
}