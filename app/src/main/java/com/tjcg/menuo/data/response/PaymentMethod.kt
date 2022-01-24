package com.tjcg.menuo.data.response

import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity
class PaymentMethod {

    @PrimaryKey
    @SerializedName("id")
    @NonNull
    var id: String = ""

    @SerializedName("role_id")
    var role_id: String? = null


}