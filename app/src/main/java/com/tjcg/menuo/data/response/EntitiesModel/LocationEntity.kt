package com.tjcg.menuo.data.response.EntitiesModel

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Location")
class LocationEntity(
        @PrimaryKey
        var order_id : Int,
        var lat : Double,
        var lng : Double
) {
}