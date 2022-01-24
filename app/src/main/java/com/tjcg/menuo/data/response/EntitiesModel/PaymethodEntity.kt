package com.tjcg.menuo.data.response.EntitiesModel

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Paymethod")
class PaymethodEntity(
        @PrimaryKey
        var id : Int,
        var name : String,
        var gateway : String,
        var enabled : Boolean,
        var deleted_at : String,
        var created_at : String,
        var updated_at : String,
        var order_id : Int

) {
}