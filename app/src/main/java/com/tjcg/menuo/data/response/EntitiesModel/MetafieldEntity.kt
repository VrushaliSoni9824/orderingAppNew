package com.tjcg.menuo.data.response.EntitiesModel

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Metafield")
class MetafieldEntity(
        @PrimaryKey
        var id : Int,
        var object_id : Int,
        var model : String,
        var key : String,
        var value : String,
        var value_type : String,
        var created_at : String,
        var updated_at : String,
        var order_id : Int

) {
}