package com.tjcg.menuo.data.local

import androidx.room.Dao
import androidx.room.Query

@Dao
interface PosDao {

    @Query("SELECT firstname FROM driverdata WHERE id =:id")
    fun getDriverName(id: String?): String

    @Query("SELECT lastname FROM driverdata WHERE id =:id")
    fun getDriverLastNAme(id: String?): String

//    @Query("INSERT INTO categoriesdata VALUES(:menu_name,:referance_category_id,:category_id,:category_name,:status,:position')")
//    fun insertCategories(menu_name: String,referance_category_id: String,category_id: String,category_name: String,status: String,position: String)

//    @Query("SELECT * FROM productaddonsdata")
//    fun getAllAddons(): List<ProductAddonsData>

}