package com.tjcg.menuo.data.response.EntitiesModel

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "City")
class CityEntity(
        @PrimaryKey
        var id: Int,
        var name: String,
        var country_id: Int,
        var administrator_id: Int,
        var enabled: Boolean,
        var order_id : Int
) {
}