package com.tjcg.menuo.data.response.IntermediatorServerAPI

data class Data(
    val business_id: String,
    val created_at: String,
    val device_id: String,
    val device_name: String,
    val id: Int,
    val platform: String,
    val pushtoken: String,
    val updated_at: String
)