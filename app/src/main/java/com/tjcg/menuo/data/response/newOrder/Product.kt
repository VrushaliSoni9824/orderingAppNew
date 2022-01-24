package com.tjcg.menuo.data.response.newOrder

data class Product(
    val category_id: Int,
    val comment: Any,
    val featured: Boolean,
    val fee_fixed: String,
    val fee_percentage: String,
    val id: Int,
    val images: Any,
    val in_offer: Boolean,
    val ingredients: List<Any>,
    val name: String,
    val offer_include_options: Boolean,
    val offer_price: Any,
    val offer_rate: Int,
    val offer_rate_type: Int,
    val options: List<Any>,
    val order_id: Int,
    val price: Int,
    val priority: Int,
    val product_id: Int,
    val quantity: Int,
    val reporting_data: Any,
    val status: Int,
    val tax_rate: Int,
    val tax_total: String,
    val tax_type: String,
    val total: Int,
    val upselling: Boolean
)