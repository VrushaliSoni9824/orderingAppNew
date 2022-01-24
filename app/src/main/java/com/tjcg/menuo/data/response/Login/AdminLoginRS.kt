package com.tjcg.menuo.data.response.Login

import com.google.gson.annotations.SerializedName
import java.util.*

class AdminLoginRS {
    @SerializedName("status")
    var status: String? = null

    @SerializedName("message")
    var message: String? = null

    @SerializedName("data")
    var data = Data()

    class Data {
        @SerializedName("user_details")
        var userDetails: UserDetails? = null

        @SerializedName("outlets")
        var outletsRS: ArrayList<OutletsRS>? = null

        @SerializedName("user_permissions")
        var userPermissions: UserPermissions? = null

        @SerializedName("Authorization")
        var authorization: String? = null
    }
}