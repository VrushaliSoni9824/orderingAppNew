package com.tjcg.menuo.data.response.newOrder

data class Pagination(
    val back_page: Any,
    val current_page: Int,
    val fisrt_page: Any,
    val from: Int,
    val last_page: Any,
    val next_page: Any,
    val page_size: Int,
    val to: Int,
    val total: Int,
    val total_pages: Int
)