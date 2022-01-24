package com.tjcg.menuo.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

import com.tjcg.menuo.data.response.DriverData
import com.tjcg.menuo.data.response.EntitiesModel.*
import com.tjcg.menuo.data.response.Login.OutletsRS
import com.tjcg.menuo.data.response.Login.UserDetails
import com.tjcg.menuo.data.response.Login.UserPermissions
import com.tjcg.menuo.data.response.newOrder.Result
import com.tjcg.menuo.data.response.order.*

@Database(entities = [UserDetails::class, OutletsRS::class, UserPermissions::class, OnlineOrderData::class,
    OngoingOrderData::class, KitchenOrderInfo::class,
    KitchenItemList::class, KitchenOrderData::class,
    Addoninfo::class, KitchenSelectedModifier::class,Result::class,
    DriverData::class,BisinessEntity::class,CityEntity::class,CustomerEntity::class,
                     DriverEntity::class,HistoryEntity::class,LocationEntity::class,
                     MetafieldEntity::class,PaymethodEntity::class,ProductEntity::class,
                     ReviewEntity::class,SummaryEntity::class], version = 7, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun adminItemDao(): AdminItemDao
    abstract fun posDao(): PosDao
    abstract fun orderDao(): OrderDao

    companion object {
        private var INSTANCE: AppDatabase? = null
        @JvmStatic
        fun getDatabase(context: Context): AppDatabase? {
            if (INSTANCE == null) {
                synchronized(AppDatabase::class.java) {
                    if (INSTANCE == null) {
                        INSTANCE = Room.databaseBuilder(context.applicationContext,
                                AppDatabase::class.java, "nento_pos")
                                .fallbackToDestructiveMigration()
                                .allowMainThreadQueries()
                                .build()
                    }
                }
            }
            return INSTANCE
        }
    }
}