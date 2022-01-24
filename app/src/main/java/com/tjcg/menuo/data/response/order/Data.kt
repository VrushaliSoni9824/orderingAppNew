package com.tjcg.menuo.data.response.order

data class Data(
    val floor_id: String,
    val floor_name: String,
    val tables: List<Table>
)