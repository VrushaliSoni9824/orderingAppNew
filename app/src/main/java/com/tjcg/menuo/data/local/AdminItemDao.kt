package com.tjcg.menuo.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.tjcg.menuo.data.response.Login.OutletsRS
import com.tjcg.menuo.data.response.Login.UserDetails
import com.tjcg.menuo.data.response.Login.UserPermissions


@Dao
interface AdminItemDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUserDetails(userDetails: UserDetails?)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUserPermission(userPermissions: UserPermissions?)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOutlets(outletsRS: List<OutletsRS>)

    @get:Query("SELECT * FROM OutletsRS")
    val outlets: List<OutletsRS>

    @Query("SELECT name FROM OutletsRS where outlet_id=:outletId")
    fun getOutletName(outletId: Int) : String

    @Query("SELECT unique_id FROM OutletsRS where outlet_id=:outletId")
    fun getUniqueId(outletId: Int) : String

    @Query("SELECT count(*) FROM UserDetails where email=:email and password=:password")
    fun localLogin(email: String,password: String) : String


}