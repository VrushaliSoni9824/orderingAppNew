package com.tjcg.menuo.data.response.order

import com.google.gson.annotations.SerializedName
import java.util.*

class KitchenOrderRs {
    @SerializedName("status")
    var status: String? = null

    @SerializedName("message")
    var message: String? = null

    @SerializedName("data")
    var data: KitchenOrderList? = null

    class KitchenOrderList {

        @SerializedName("kitchen_data")
        var kitchen_orders: ArrayList<KitchenOrderData>? = null

    }
}