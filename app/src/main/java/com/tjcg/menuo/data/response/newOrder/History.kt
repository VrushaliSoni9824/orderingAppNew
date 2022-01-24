package com.tjcg.menuo.data.response.newOrder

data class History(
    val created_at: String,
    val `data`: Any,
    val id: Int,
    val order_id: Int,
    val type: Int,
    val updated_at: String
)