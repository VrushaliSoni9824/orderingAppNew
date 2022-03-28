package com.tjcg.menuo.data.response.newOrder

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class DialogQueue(
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    var OrderId: String
){

}