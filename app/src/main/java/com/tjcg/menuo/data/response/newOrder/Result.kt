package com.tjcg.menuo.data.response.newOrder

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Entity
class Result {

    @PrimaryKey
    @SerializedName("id")
    @Expose
    var id: Int = 0

    @SerializedName("app_id")
    @Expose
    var app_id: String? = null

    //    var attachments: List<String>,
//    var business: Business,

    @SerializedName("business_id")
    @Expose
    var business_id: String? = null

    @SerializedName("cash")
    @Expose
    var cash: String? = null

    @SerializedName("comment")
    @Expose
    var comment: String? = null

    @SerializedName("coupon")
    @Expose
    var coupon: String? = null

    @SerializedName("created_at")
    @Expose
    var created_at: String? = null

    //    var customer: Customer,

    @SerializedName("customer_id")
    @Expose
    var customer_id: String? = null

    @SerializedName("delivered_in")
    @Expose
    var delivered_in: String? = null

    @SerializedName("delivery_datetime")
    @Expose
    var delivery_datetime: String? = null

    @SerializedName("delivery_datetime_utc")
    @Expose
    var delivery_datetime_utc: String? = null

    @SerializedName("delivery_type")
    @Expose
    var delivery_type: String? = null

    @SerializedName("delivery_zone_id")
    @Expose
    var delivery_zone_id: String? = null

    @SerializedName("delivery_zone_price")
    @Expose
    var delivery_zone_price: String? = null

    @SerializedName("discount")
    @Expose
    var discount: String? = null

    @SerializedName("driver")
    @Expose
    var driver: String? = null

    @SerializedName("driver_compString_id")
    @Expose
    var driver_compString_id: String? = null

    @SerializedName("driver_id")
    @Expose
    var driver_id: String? = null

    @SerializedName("driver_tip")
    @Expose
    var driver_tip: String? = null

    @SerializedName("expired_at")
    @Expose
    var expired_at: String? = null

    @SerializedName("external_driver")
    @Expose
    var external_driver: String? = null

    @SerializedName("external_driver_id")
    @Expose
    var external_driver_id: String? = null

    //    var history: List<History>,

    @SerializedName("language_id")
    @Expose
    var language_id: String? = null

    @SerializedName("last_direct_message_at")
    @Expose
    var last_direct_message_at: String? = null

    @SerializedName("last_driver_assigned_at")
    @Expose
    var last_driver_assigned_at: String? = null

    @SerializedName("last_general_message_at")
    @Expose
    var last_general_message_at: String? = null

    @SerializedName("last_logistic_attempted_at")
    @Expose
    var last_logistic_attempted_at: String? = null

    @SerializedName("last_message_at")
    @Expose
    var last_message_at: String? = null

    @SerializedName("locked")
    @Expose
    var locked: String? = null

    @SerializedName("logistic_attemps")
    @Expose
    var logistic_attemps: String? = null

    @SerializedName("logistic_status")
    @Expose
    var logistic_status: String? = null

    //    var metafields: List<String>,

    @SerializedName("offer")
    @Expose
    var offer: String? = null

    @SerializedName("offer_id")
    @Expose
    var offer_id: String? = null

    @SerializedName("offer_rate")
    @Expose
    var offer_rate: String? = null

    @SerializedName("offer_type")
    @Expose
    var offer_type: String? = null

    @SerializedName("order_group")
    @Expose
    var order_group: String? = null

    @SerializedName("order_group_id")
    @Expose
    var order_group_id: String? = null

    @SerializedName("pay_data")
    @Expose
    var pay_data: String? = null

    //    var paymethod: Paymethod,

    @SerializedName("paymethod_id")
    @Expose
    var paymethod_id: String? = null

    @SerializedName("place")
    @Expose
    var place: String? = null

    @SerializedName("place_id")
    @Expose
    var place_id: String? = null

    @SerializedName("prepared_in")
    @Expose
    var prepared_in: String? = null

    @SerializedName("priority")
    @Expose
    var priority: String? = null

    //    var products: List<Product>,

    @SerializedName("refund_data")
    @Expose
    var refund_data: String? = null

    @SerializedName("resolved_at")
    @Expose
    var resolved_at: String? = null

    @SerializedName("review")
    @Expose
    var review: String? = null

    @SerializedName("service_fee")
    @Expose
    var service_fee: String? = null

    @SerializedName("status")
    @Expose
    var status: String? = null

    //    var summary: Summary,

    @SerializedName("summary_version")
    @Expose
    var summary_version: String? = null

    @SerializedName("tax")
    @Expose
    var tax: String? = null

    @SerializedName("tax_type")
    @Expose
    var tax_type: String? = null

    @SerializedName("to_print")
    @Expose
    var to_print: String? = null

    @SerializedName("unread_count")
    @Expose
    var unread_count: String? = null

    @SerializedName("unread_direct_count")
    @Expose
    var unread_direct_count: String? = null

    @SerializedName("unread_general_count")
    @Expose
    var unread_general_count: String? = null

    @SerializedName("updated_at")
    @Expose
    var updated_at: String? = null

    @SerializedName("user_review")
    @Expose
    var user_review: String? = null

    @SerializedName("uuid")
    @Expose
    var uuid: String? = null

    constructor() {}

    constructor(orderResult: Result) {
//        this.row_id=orderResult.id
        this.id = orderResult.id
        this.app_id = orderResult.app_id
        this.business_id = orderResult.business_id
        this.cash = orderResult.cash
        this.comment = orderResult.comment
        this.coupon = orderResult.coupon
        this.created_at = orderResult.created_at

        //    this.customer = orderResultCustomer,
        this.customer_id = orderResult.customer_id
        this.delivered_in = orderResult.delivered_in
        this.delivery_datetime = orderResult.delivery_datetime
        this.delivery_datetime_utc = orderResult.delivery_datetime_utc
        this.delivery_type = orderResult.delivery_type
        this.delivery_zone_id = orderResult.delivery_zone_id
        this.delivery_zone_price = orderResult.delivery_zone_price
        this.discount = orderResult.discount
        this.driver = orderResult.driver
        this.driver_compString_id = orderResult.driver_compString_id
        this.driver_id = orderResult.driver_id
        this.driver_tip = orderResult.driver_tip
        this.expired_at = orderResult.expired_at
        this.external_driver = orderResult.external_driver
        this.external_driver_id = orderResult.external_driver_id

        //    this.history = orderResultList<History>,

        this.language_id = orderResult.language_id
        this.last_direct_message_at = orderResult.last_direct_message_at
        this.last_driver_assigned_at = orderResult.last_driver_assigned_at
        this.last_general_message_at = orderResult.last_general_message_at
        this.last_logistic_attempted_at = orderResult.last_logistic_attempted_at
        this.last_message_at = orderResult.last_message_at
        this.locked = orderResult.locked
        this.logistic_attemps = orderResult.logistic_attemps
        this.logistic_status = orderResult.logistic_status

        //    this.metafields = orderResultList<String>,
        this.offer = orderResult.offer
        this.offer_id = orderResult.offer_id
        this.offer_rate = orderResult.offer_rate
        this.offer_type = orderResult.offer_type
        this.order_group = orderResult.order_group
        this.order_group_id = orderResult.order_group_id
        this.pay_data = orderResult.pay_data

        //    this.paymethod = orderResultPaymethod,
        this.paymethod_id = orderResult.paymethod_id
        this.place = orderResult.place
        this.place_id = orderResult.place_id
        this.prepared_in = orderResult.prepared_in
        this.priority = orderResult.priority

        //    this.products = orderResultList<Product>,
        this.refund_data = orderResult.refund_data
        this.resolved_at = orderResult.resolved_at
        this.review = orderResult.review
        this.service_fee = orderResult.service_fee
        this.status = orderResult.status

        //    this.summary = orderResultSummary,
        this.summary_version = orderResult.summary_version
        this.tax = orderResult.tax
        this.tax_type = orderResult.tax_type
        this.to_print = orderResult.to_print
        this.unread_count = orderResult.unread_count
        this.unread_direct_count = orderResult.unread_direct_count
        this.unread_general_count = orderResult.unread_general_count
        this.updated_at = orderResult.updated_at
        this.user_review = orderResult.user_review
        this.uuid = orderResult.uuid
    }

}