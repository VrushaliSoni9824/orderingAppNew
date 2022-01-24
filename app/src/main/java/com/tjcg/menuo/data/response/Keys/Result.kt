package com.tjcg.menuo.data.response.Keys

data class Result(
    val id: Int,
    val key: String,
    val user: User,
    val user_id: Int
)