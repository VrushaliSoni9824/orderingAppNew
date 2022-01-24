package com.tjcg.menuo.data.response

import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity
class DriverData {

    @PrimaryKey
    @SerializedName("id")
    @NonNull
    var id: String = ""

    @SerializedName("role_id")
    var role_id: String? = null

    @SerializedName("client_id")
    var client_id: String? = null

    @SerializedName("deal_id")
    var deal_id: String? = null

    @SerializedName("firstname")
    var firstname: String? = null

    @SerializedName("lastname")
    var lastname: String? = null

    @SerializedName("about")
    var about: String? = null

    @SerializedName("waiter_kitchenToken")
    var waiter_kitchenToken: String? = null

    @SerializedName("countrycodetel")
    var countrycodetel: String? = null

    @SerializedName("phone_no")
    var phone_no: String? = null

    @SerializedName("email")
    var email: String? = null

    @SerializedName("password")
    var password: String? = null

    @SerializedName("pin")
    var pin: String? = null

    @SerializedName("password_reset_token")
    var password_reset_token: String? = null

    @SerializedName("image")
    var image: String? = null

    @SerializedName("address")
    var address: String? = null


}