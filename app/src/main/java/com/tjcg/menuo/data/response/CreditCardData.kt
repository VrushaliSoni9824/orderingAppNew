package com.tjcg.menuo.data.response

import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity
class CreditCardData {

    @PrimaryKey
    @SerializedName("card_terminalid")
    @NonNull
    var card_terminalid: String = ""

    @SerializedName("outlet_id")
    var outlet_id: String? = null

    @SerializedName("terminal_name")
    var terminal_name: String? = null


}