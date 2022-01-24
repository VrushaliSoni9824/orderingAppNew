package com.tjcg.menuo.data.response.EntitiesModel

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Review")
class ReviewEntity(
        @PrimaryKey
        var id : Int,
        var order_id : Int,
        var quality : Int,
        var delivery : Int,
        var service : Int,
        var pack : Int,
        var user_id : Int,
        var comment : String,
        var enabled : Boolean,
        var created_at : String,
        var updated_at : String
) {
}