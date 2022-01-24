package com.tjcg.menuo.data.response.locations

data class UserPermissions(
    val addons_list: Int,
    val all_order: Int,
    val category_list: Int,
    val counter_display: Int,
    val customer_list: Int,
    val customer_management: Int,
    val customer_type: Int,
    val dashboard_analytics: Int,
    val kitchen_display: Int,
    val menus_list: Int,
    val modifier_list: Int,
    val pos: Int,
    val reservation_access: Int,
    val reservation_management: Int,
    val table_management: Int,
    val variants_list: Int,
    val waiting_list: Int
)