package com.tjcg.menuo.data.response.newOrder

data class orderRs(
    val error: Boolean,
    val pagination: Pagination,
    val result: List<Result>
)