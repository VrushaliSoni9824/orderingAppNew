package com.tjcg.menuo.data.response.EntitiesModel

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Summary")
class SummaryEntity(
        @PrimaryKey
        var order_id : Int,
        var total : Int,
        var discount : Int,
        var subtotal : Double,
        var subtotal_with_discount : Double,
        var service_fee_rate : Int,
        var service_fee : Int,
        var service_fee_with_discount : Int,
        var delivery_price : Int,
        var delivery_price_with_discount : Int,
        var tax_rate : Int,
        var tax : Double,
        var tax_with_discount : Double,
        var driver_tip_rate : Int,
        var driver_tip : Int
) {
}