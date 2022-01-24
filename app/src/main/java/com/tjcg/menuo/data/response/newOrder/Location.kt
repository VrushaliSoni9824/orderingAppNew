package com.tjcg.menuo.data.response.newOrder

data class Location(
    val lat: Double,
    val lng: Double,
    val zipcode: Int,
    val zoom: Int
)