package com.tjcg.menuo.data.response.order

import com.google.gson.annotations.SerializedName
import java.util.*

class OngoingOrderRs {
    @SerializedName("status")
    var status: String? = null

    @SerializedName("message")
    var message: String? = null

    @SerializedName("data")
    var data: OngoingOrderList? = null

    class OngoingOrderList {
        @SerializedName("ongoingorder")
        var ongoingorder: ArrayList<OngoingOrder>? = null

        @SerializedName("ongoing_orders")
        var ongoing_orders: ArrayList<OngoingOrder>? = null
    }
}