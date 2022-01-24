package com.tjcg.menuo.data.response.Keys

data class KeyResponce(
    val error: Boolean,
    val pagination: Pagination,
    val result: List<Result>
)