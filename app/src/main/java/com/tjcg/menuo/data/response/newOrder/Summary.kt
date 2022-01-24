package com.tjcg.menuo.data.response.newOrder

data class Summary(
    val delivery_price: Int,
    val delivery_price_with_discount: Int,
    val discount: Int,
    val driver_tip: Int,
    val driver_tip_rate: Int,
    val service_fee: Double,
    val service_fee_rate: Int,
    val service_fee_with_discount: Double,
    val subtotal: Int,
    val subtotal_with_discount: Int,
    val tax: Double,
    val tax_rate: Int,
    val tax_with_discount: Double,
    val total: Double
)