package com.tjcg.menuo.data.response.IntermediatorServerAPI

data class IntermediatorLogin(
    val code: Int,
    val `data`: Data,
    val msg: String,
    val status: Boolean,
    val version: String
)