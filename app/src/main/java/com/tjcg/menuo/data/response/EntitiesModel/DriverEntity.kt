package com.tjcg.menuo.data.response.EntitiesModel

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Driver")
class DriverEntity(
        @PrimaryKey
        var id : Int,
        var name : String,
        var lastname : String,
        var email : String,
        var login_type : Int,
        var social_id : String,
        var photo : String,
        var birthdate : String,
        var phone : String,
        var cellphone : Int,
        var city_id : String,
        var dropdown_option_id : String,
        var address : String,
        var address_notes : String,
        var zipcode : String,
        //var location : Location,
        var location : String,
        var level : Int,
        var language_id : Int,
        var push_notifications : Boolean,
        var busy : Boolean,
        var available : Boolean,
        var enabled : Boolean,
        var created_at : String,
        var updated_at : String,
        var deleted_at : String,
        var internal_number : String,
        var map_data : String,
        var middle_name : String,
        var second_lastname : String,
        var country_phone_code : String,
        var priority : Int,
        var last_order_assigned_at : String,
        var last_location_at : String,
        var phone_verified : Boolean,
        var email_verified : Boolean,
        var driver_zone_restriction : Boolean,
        var pin : String,
        var business_id : String,
        var franchise_id : String,
        var register_site_id : String,
        var ideal_orders : String,
        var order_id : Int
) {
}