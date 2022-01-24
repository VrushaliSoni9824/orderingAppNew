package com.tjcg.menuo.data.response.locations

data class Data(
    val address: String,
    val email: String,
    val is_default: String,
    val name: String,
    val outlet_id: String,
    val phoneno: String,
    val unique_id: String,
    val user_permissions: UserPermissions
)