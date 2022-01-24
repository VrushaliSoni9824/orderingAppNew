package com.tjcg.menuo.data.response

import com.google.gson.annotations.SerializedName

class DriverRS {

    @SerializedName("status")
    var status: String? = null

    @SerializedName("message")
    var message: String? = null

    @SerializedName("data")
    var data: List<DriverData>? = null
}