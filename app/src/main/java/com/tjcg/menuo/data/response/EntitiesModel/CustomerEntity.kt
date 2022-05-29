package com.tjcg.menuo.data.response.EntitiesModel

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Customer")
class CustomerEntity(
        @PrimaryKey
        var id: Int,
        var order_id: Int,
        var name: String,
        var photo: String,
        var lastname: String,
        var email: String,
        var dropdown_option_id: String,
        var address: String,
        var address_notes: String,
        var zipcode: String,
        var cellphone: String,
        var phone: String,
        var location : String,
        //var location : Location,
        var internal_number: String,
        var map_data: String,
        var tag: String,
        var middle_name: String,
        var second_lastname: String,
        var country_phone_code: String,
        var dropdown_option: String
) {
}