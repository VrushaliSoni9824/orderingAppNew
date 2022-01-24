package com.tjcg.menuo.data.response.order

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Entity
class KitchenSelectedModifier {

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

    @SerializedName("combo_unique_record_id")
    @Expose
    var combo_unique_record_id: String? = null

    @SerializedName("menu_id")
    @Expose
    var menu_id: String? = null

    @SerializedName("combomain_menu_id")
    @Expose
    var combomain_menu_id: String? = null

    @SerializedName("item_menuid_string")
    @Expose
    var item_menuid_string: String? = null

    @SerializedName("menuqty")
    @Expose
    var menuqty: String? = null

    @SerializedName("total_qty")
    @Expose
    var total_qty: String? = null

    @SerializedName("add_on_id")
    @Expose
    var add_on_id: String? = null

    @SerializedName("addonsqty")
    @Expose
    var addonsqty: String? = null

    @SerializedName("varientid")
    @Expose
    var varientid: String? = null

    @SerializedName("modifier_row_id")
    @Expose
    var modifier_row_id: String? = null

    @SerializedName("modifier_id")
    @Expose
    var modifier_id: String = ""

    @SerializedName("sub_mod_id_string")
    @Expose
    var sub_mod_id_string: String? = null

    @SerializedName("sub_mod_id")
    @Expose
    var sub_mod_id: String? = null

    @SerializedName("modifier_type")
    @Expose
    var modifier_type: String? = null

    @SerializedName("modifier_type_string")
    @Expose
    var modifier_type_string: String? = null

    @SerializedName("modifierqty")
    @Expose
    var modifierqty: String? = null

    @SerializedName("is_2x_mod")
    @Expose
    var productIs_2x_mod: String? = null

    @SerializedName("modifier_price")
    @Expose
    var modifier_price: String? = null

    @SerializedName("tax_id")
    @Expose
    var tax_id: String? = null

    @SerializedName("tax_percentage")
    @Expose
    var tax_percentage: String? = null

    @SerializedName("discount_type")
    @Expose
    var discount_type: String? = null

    @SerializedName("item_discount")
    @Expose
    var item_discount: String? = null

    @SerializedName("food_status")
    @Expose
    var food_status: String? = null

    @SerializedName("isupdate")
    @Expose
    var isupdate: String? = null

    @SerializedName("order_notes")
    @Expose
    var order_notes: String? = null

}