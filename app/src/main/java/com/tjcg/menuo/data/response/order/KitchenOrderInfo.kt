package com.tjcg.menuo.data.response.order

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Entity
class KitchenOrderInfo {
    @PrimaryKey
    @SerializedName("row_id")
    @Expose
    var row_id: String = ""

    @SerializedName("order_id")
    @Expose
    var order_id: String? = null

    @SerializedName("unique_record_id")
    @Expose
    var unique_record_id: String? = null

    @SerializedName("menuqty")
    @Expose
    var menuqty: String? = null

    @SerializedName("add_on_id")
    @Expose
    var add_on_id: String? = null

    @SerializedName("addonsqty")
    @Expose
    var addonsqty: String? = null

    @SerializedName("food_status")
    @Expose
    var food_status: String? = null

    @SerializedName("isupdate")
    @Expose
    var isupdate: String? = null

    @SerializedName("add_on_name")
    @Expose
    var add_on_name: String? = null

    @SerializedName("price")
    @Expose
    var price: String? = null

    @SerializedName("tax_id")
    @Expose
    var tax_id: String? = null

    @SerializedName("tax_percentage")
    @Expose
    var tax_percentage: String? = null

}