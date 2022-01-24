package com.tjcg.menuo.data.response.order

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Entity
class KitchenItemList {

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

    @SerializedName("sub_mod_id_string")
    @Expose
    var sub_mod_id_string: String? = null

    @SerializedName("modifier_type_string")
    @Expose
    var modifier_type_string: String? = null

    @SerializedName("menu_id")
    @Expose
    var menu_id: String? = null

    @SerializedName("menuqty")
    @Expose
    var menuqty: String? = null

    @SerializedName("add_on_id")
    @Expose
    var add_on_id: String? = null

    @SerializedName("addonsqty")
    @Expose
    var addonsqty: String? = null

    @SerializedName("varientid")
    @Expose
    var varientid: String? = null

    @SerializedName("food_status")
    @Expose
    var food_status: String? = null

    @SerializedName("isupdate")
    @Expose
    var isupdate: String? = null

    @SerializedName("ProductName")
    @Expose
    var productName: String? = null

    @SerializedName("ProductPrice")
    @Expose
    var productPrice: String? = null

    @SerializedName("is_half_and_half")
    @Expose
    var half_and_half: String? = null

    @SerializedName("variantid")
    @Expose
    var variantid: String? = null

    @SerializedName("variantName")
    @Expose
    var variantName: String? = null

    @SerializedName("price")
    @Expose
    var price: String? = null

    @SerializedName("order_notes")
    @Expose
    var order_notes: String? = null

    @SerializedName("selected_mod")
    @Expose
    var selected_mod: String? = null

    constructor(row_id: String, order_id: String?, unique_record_id: String?, combo_unique_record_id: String?, sub_mod_id_string: String?, modifier_type_string: String?, menu_id: String?, menuqty: String?, add_on_id: String?, addonsqty: String?, varientid: String?, food_status: String?, isupdate: String?, productName: String?, productPrice: String?, half_and_half: String?, variantid: String?, variantName: String?, price: String?, order_notes: String?,selected_mod
    :String?) {
        this.row_id = row_id
        this.order_id = order_id
        this.unique_record_id = unique_record_id
        this.combo_unique_record_id = combo_unique_record_id
        this.sub_mod_id_string = sub_mod_id_string
        this.modifier_type_string = modifier_type_string
        this.menu_id = menu_id
        this.menuqty = menuqty
        this.add_on_id = add_on_id
        this.addonsqty = addonsqty
        this.varientid = varientid
        this.food_status = food_status
        this.isupdate = isupdate
        this.productName = productName
        this.productPrice = productPrice
        this.half_and_half = half_and_half
        this.variantid = variantid
        this.variantName = variantName
        this.price = price
        this.order_notes = order_notes
        this.selected_mod=selected_mod
    }
}