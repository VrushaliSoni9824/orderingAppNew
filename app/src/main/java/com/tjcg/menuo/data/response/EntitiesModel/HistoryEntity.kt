package com.tjcg.menuo.data.response.EntitiesModel

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "History")
class HistoryEntity(
        @PrimaryKey
        var id : Int,
        var order_id : Int,
        var type : Int,
        //var data : List<Data>,
        var data : String,
        var created_at : String,
        var updated_at : String

)
{
}