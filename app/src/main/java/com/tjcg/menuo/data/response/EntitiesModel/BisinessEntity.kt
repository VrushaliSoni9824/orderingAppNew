package com.tjcg.menuo.data.response.EntitiesModel

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Bisiness")
class BisinessEntity(
        @PrimaryKey
        var id : Int,
        var order_id : Int,
        var name : String,
        var logo : String,
        var email : String,
        var city_id : Int,
        var address : String,
        var address_notes : String,
        var zipcode : String,
        var cellphone : Int,
        var phone : Int,
        var location : String,
//        var location : Location,
        var header : String,
        var pickup_time : String,
        var delivery_time : String,
        var city : String
        //var city : City

) {

}