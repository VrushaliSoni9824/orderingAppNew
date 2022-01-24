package com.tjcg.menuo.data.response

import com.google.gson.annotations.SerializedName

class BaseRS {
    @SerializedName("status")
    var status: String? = null

    @SerializedName("message")
    var message: String? = null

    @SerializedName("data")
    var data = Data()

    class Data {
        @SerializedName("order_id")
        var order_id: String? = null
    }
}