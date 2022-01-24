package com.tjcg.menuo.data.response.order

import java.io.Serializable
import java.util.*

class OngoingOrder : Serializable {
    var order_id: String? = null
    var outlet_id: String? = null
    var saleinvoice: String? = null
    var customer_id: String? = null
    var cutomertype: String? = null
    var isthirdparty: String? = null
    var waiter_id: String? = null
    var kitchen: String? = null
    var order_date: String? = null
    var order_time: String? = null
    var order_accept_date: String? = null
    var cookedtime: String? = null
    var table_no: String? = null
    var tokenno: String? = null
    var discount: String? = null
    var tip_type: String? = null
    var added_tip_amount: String? = null
    var tip_amount: String? = null
    var totalamount: String? = null
    var customerpaid: String? = null
    var customer_note: String? = null
    var anyreason: String? = null
    var order_status: String? = null
    var is_driver_assigned: String? = null
    var driver_user_id: String? = null
    var is_order_delivered: String? = null
    var is_payment_received: String? = null
    var received_payment_amount: String? = null
    var order_pickup_at: String? = null
    var future_order_date: String? = null
    var future_order_time: String? = null
    var nofification: String? = null
    var online_order_notification: String? = null
    var kitchen_notification: String? = null
    var orderacceptreject: String? = null
    var is_frontend_order: String? = null
    var is_qr_order: String? = null
    var customer_name: String? = null
    var customer_type: String? = null
    var first_name: String? = null
    var last_name: String? = null
    var tablename: String? = null
    var bill_status: String? = null
    var keyword: String? = null
    var orderinfo: ArrayList<KitchenOrderInfo>? = null
    var itemlist: ArrayList<KitchenItemList>? = null
    var addoninfo: ArrayList<Addoninfo>? = null
    var selected_mod: ArrayList<KitchenSelectedModifier>? = null

    constructor()

    constructor(ongoingOrder: OngoingOrderData) {
        order_id = ongoingOrder.order_id
        outlet_id = ongoingOrder.outlet_id
        saleinvoice = ongoingOrder.saleinvoice
        customer_id = ongoingOrder.customer_id
        cutomertype = ongoingOrder.cutomertype
        isthirdparty = ongoingOrder.thirdparty
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
        is_driver_assigned = ongoingOrder.driver_assigned
        driver_user_id = ongoingOrder.driver_user_id
        is_order_delivered = ongoingOrder.order_delivered
        is_payment_received = ongoingOrder.payment_received
        received_payment_amount = ongoingOrder.received_payment_amount
        order_pickup_at = ongoingOrder.order_pickup_at
        future_order_date = ongoingOrder.future_order_date
        future_order_time = ongoingOrder.future_order_time
        nofification = ongoingOrder.nofification
        online_order_notification = ongoingOrder.online_order_notification
        kitchen_notification = ongoingOrder.kitchen_notification
        orderacceptreject = ongoingOrder.orderacceptreject
        is_frontend_order = ongoingOrder.frontend_order
        is_qr_order = ongoingOrder.qr_order
        customer_name = ongoingOrder.customer_name
        customer_type = ongoingOrder.customer_type
        first_name = ongoingOrder.first_name
        last_name = ongoingOrder.last_name
        tablename = ongoingOrder.tablename
        bill_status = ongoingOrder.bill_status
        keyword = ongoingOrder.keyword
    }
}