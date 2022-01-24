package com.tjcg.menuo.data.response.order

import com.google.gson.annotations.SerializedName
import java.util.ArrayList

class OnlineOrderRS {
    @SerializedName("status")
    var status: String? = null

    @SerializedName("message")
    var message: String? = null

    @SerializedName("data")
    var data: OnlineOrderList? = null

    class OnlineOrderList {
        @SerializedName("new_order")
        var new_order: ArrayList<OnlineOrderData>? = null

        @SerializedName("accepted_order")
        var accepted_order: ArrayList<OnlineOrderData>? = null

        @SerializedName("completed_order")
        var completed_order: ArrayList<OnlineOrderData>? = null

        @SerializedName("cancelled_order")
        var cancelled_order: ArrayList<OnlineOrderData>? = null

        @SerializedName("all_orders")
        var all_orders: ArrayList<OnlineOrderData>? = null

        @SerializedName("pending_orders")
        var pending_orders: ArrayList<OnlineOrderData>? = null

        @SerializedName("in_process_orders")
        var in_process_orders: ArrayList<OnlineOrderData>? = null

        @SerializedName("ready_orders")
        var ready_orders: ArrayList<OnlineOrderData>? = null
    }
}