package com.tjcg.menuo.data.response.Login

import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Entity
class OutletsRS {

    @PrimaryKey
    @SerializedName("outlet_id")
    @Expose
    @NonNull
    var outlet_id: String = ""

    @SerializedName("unique_id")
    @Expose
    var unique_id: String? = null

    @SerializedName("name")
    @Expose
    var name: String? = null

    @SerializedName("email")
    @Expose
    var email: String? = null

    @SerializedName("phoneno")
    @Expose
    var phoneno: String? = null

    @SerializedName("address")
    @Expose
    var address: String? = null

    @SerializedName("is_default")
    @Expose
    var pis_default: String? = null

    constructor(outlet_id: String, unique_id: String, name: String, email: String, phoneno: String, address: String, is_default: String) {
        this.outlet_id = outlet_id
        this.unique_id = unique_id
        this.name = name
        this.email = email
        this.phoneno = phoneno
        this.address = address
        this.pis_default = is_default
    }

    constructor()


}