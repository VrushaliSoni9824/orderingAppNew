package com.tjcg.menuo.data.response.Login

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Entity
class UserPermissions {

    @ForeignKey(entity = UserDetails::class, parentColumns = ["client_id"], childColumns = ["client_id"], onDelete = ForeignKey.CASCADE)
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "client_id")
    var client_id = ""


    @SerializedName("pos")
    @Expose
    var pos: String? = null

    @SerializedName("all_order")
    @Expose
    var all_order: String? = null

    @SerializedName("dashboard_analytics")
    @Expose
    var dashboard_analytics: String? = null

    @SerializedName("counter_display")
    @Expose
    var counter_display: String? = null

    @SerializedName("kitchen_display")
    @Expose
    var kitchen_display: String? = null

    @SerializedName("menus_list")
    @Expose
    var menus_list: String? = null

    @SerializedName("category_list")
    @Expose
    var category_list: String? = null

    @SerializedName("addons_list")
    @Expose
    var addons_list: String? = null

    @SerializedName("variants_list")
    @Expose
    var variants_list: String? = null

    @SerializedName("modifier_list")
    @Expose
    var modifier_list: String? = null

    @SerializedName("reservation_access")
    @Expose
    var reservation_access: String? = null

    @SerializedName("reservation_management")
    @Expose
    var reservation_management: String? = null

    @SerializedName("table_management")
    @Expose
    var table_management: String? = null

    @SerializedName("waiting_list")
    @Expose
    var waiting_list: String? = null

    @SerializedName("customer_management")
    @Expose
    var customer_management: String? = null

    @SerializedName("customer_list")
    @Expose
    var customer_list: String? = null

    @SerializedName("customer_type")
    @Expose
    var customer_type: String? = null
}