package com.tjcg.menuo.data.response.newOrder

data class Business(
    val address: String,
    val address_notes: Any,
    val cellphone: String,
    val city: City,
    val city_id: Int,
    val delivery_time: String,
    val email: String,
    val header: String,
    val id: Int,
    val location: Location,
    val logo: String,
    val name: String,
    val order_id: Int,
    val phone: String,
    val pickup_time: String,
    val zipcode: Any
)