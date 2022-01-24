package com.tjcg.menuo.data.response.syncResponceClasses.Category

data class Data(
    val active_categories: List<ActiveCategory>,
    val all_categories: List<AllCategory>,
    val categories_inactive: List<CategoriesInactive>
)