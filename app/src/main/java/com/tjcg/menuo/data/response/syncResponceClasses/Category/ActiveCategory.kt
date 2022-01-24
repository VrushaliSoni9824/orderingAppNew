package com.tjcg.menuo.data.response.syncResponceClasses.Category

data class ActiveCategory(
    val CategoryID: String,
    val CategoryImage: Any,
    val CategoryIsActive: String,
    val DateInserted: String,
    val DateLocked: String,
    val DateUpdated: String,
    val Name: String,
    val Position: Any,
    val UserIDInserted: String,
    val UserIDLocked: String,
    val UserIDUpdated: String,
    val created_at: String,
    val image_path: String,
    val is_deleted: String,
    val is_miscellaneous: String,
    val isoffer: String,
    val itemmenu_id: String,
    val outlet_id: String,
    val parentid: String,
    val updated_at: String,
    val upsale_addon_id: Any
)