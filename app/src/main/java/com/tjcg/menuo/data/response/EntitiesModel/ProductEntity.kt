package com.tjcg.menuo.data.response.EntitiesModel

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Product")
class ProductEntity(
        @PrimaryKey
        var id : Int,
        var product_id : Int,
        var order_id : Int,
        var name : String,
        var price : Int,
        var quantity : Int,
        var comment : String,
        //var ingredients : List<String> = ArrayList<String>(),
        //var options : List<String> = ArrayList<String>(),
        var ingredients : String,
        var options : String,
        var featured : Boolean,
        var upselling : Boolean,
        var in_offer : Boolean,
        var offer_price : String,
        var images : String,
        var offer_rate : Int,
        var offer_rate_type : Int,
        var offer_include_options : Boolean,
        var status : Int,
        var priority : Int,
        var reporting_data : String,
        var fee_id : String,
        var tax_id : String,
        var summary : String,
        var category_id : Int,
        var total : Int,
        var tax : String,
        var fee : String

) {
}