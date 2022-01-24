package com.tjcg.menuo.data.response.Login

import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Entity
class UserDetails {

    @PrimaryKey
    @SerializedName("client_id")
    @Expose
    @NonNull
    var client_id: String = ""

    @SerializedName("first_name")
    @Expose
    var first_name: String? = null

    @SerializedName("last_name")
    @Expose
    var last_name: String? = null

    @SerializedName("password")
    @Expose
    var password: String? = null

    @SerializedName("profile_pic")
    @Expose
    var profile_pic: String? = null

    @SerializedName("username")
    @Expose
    var username: String? = null

    @SerializedName("email")
    @Expose
    var email: String? = null

    @SerializedName("phone")
    @Expose
    var phone: String? = null

    @SerializedName("company_name")
    @Expose
    var company_name: String? = null

    @SerializedName("company_logo")
    @Expose
    var company_logo: String? = null

//    @SerializedName("is_blocked")
//    @Expose
//    var is_blocked: String? = null

    @SerializedName("active")
    @Expose
    var active: String? = null

    @SerializedName("created")
    @Expose
    var created: String? = null
}