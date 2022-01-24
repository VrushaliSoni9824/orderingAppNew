package com.tjcg.menuo.data.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.tjcg.menuo.data.response.DriverData
import com.tjcg.menuo.data.response.EntitiesModel.*
import com.tjcg.menuo.data.response.newOrder.Result
import com.tjcg.menuo.data.response.order.*
import io.reactivex.Flowable

@Dao
interface OrderDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOnlineOrderData(onlineOrderData: List<OnlineOrderData>)

    @get:Query("SELECT * FROM onlineorderdata order by order_id desc")
    val onlineOrders: LiveData<List<OnlineOrderData?>?>

    //convert above 'onlineOrders' variable to function for apply filer with outlet id
    @Query("select * from OnlineOrderData where outlet_id=:outlet_id order by order_id desc")
    fun getAllOnlineOrder(outlet_id : String): LiveData<List<OnlineOrderData?>?>

    @Query("select id from result order by id desc")
    fun getResult(): List<Int>

    @Query("select id from result where status=0 or status=13 order by id desc")
    fun getPendingOrder(): List<Int>

    @Query("select count(*) from result where status=0 or status=13 order by id desc")
    fun getPendingOrderCount(): Int

    @Query("select id from result where status in (3,4,7,8,9,14,15,18,19,20,21) order by id desc")
    fun getInProgressOrder(): List<Int>

    @Query("select count(*) from result where status in (3,4,7,8,9,14,15,18,19,20,21) order by id desc")
    fun getInProgressOrderCount(): Int

    @Query("select id from result where status in (1,2,5,6,10,11,12,16,17) order by id desc")
    fun getDoneOrder(): List<Int>

    @Query("select delivery_type from result where id=:id")
    fun getDeliveryType(id: String): String

    @Query("select total from summary where order_id=:id")
    fun getOrderTotal(id: String): Int


    @Query("select created_at from result where id=:id")
    fun getOrderDateTime(id: String): String

    @Query("select name from customer where id=:id")
    fun getCustomerName(id: String): String

    @Query("select * from Result where id=:id")
    fun getOrderDataByID(id: String): Result

    @Query("select * from bisiness where order_id=:id")
    fun getBusinessById(id: String): BisinessEntity

    @Query("select * from city where order_id=:id")
    fun getCityById(id: String): CityEntity

    @Query("select * from customer where order_id=:id")
    fun getCustomerById(id: String): CustomerEntity

    @Query("select * from history where order_id=:id")
    fun getHistoryById(id: String): HistoryEntity

    @Query("select * from product where order_id=:id")
    fun getProductById(id: String): List<ProductEntity>

    @Query("select * from summary where order_id=:id")
    fun getSummaryById(id: String): SummaryEntity

//    @Query("select * from product where order_id=:id")
//    fun getProductById(id: String): List<ProductEntity>

    @get:Query("SELECT * FROM onlineorderdata order by order_id desc")
    val onlineOrders2: Flowable<List<OnlineOrderData>>

    @Query("SELECT count(*) FROM OnlineOrderData where order_status=1 and outlet_id=:outlet_id")
    fun getOnlineOrdersCount(outlet_id : String): Int

    @Query("SELECT * FROM OnlineOrderData where order_status=1 and outlet_id=:outlet_id")
    fun getOnlineOrdersCountMain(outlet_id : String): List<OnlineOrderData>

    @Query("SELECT count(*) FROM OnlineOrderData where order_status=1 and future_order_date!='NULL' and outlet_id=:outlet_id")
    fun getOnlineFutureOrdersCount(outlet_id : String): Int

    @Query("SELECT count(*) FROM OngoingOrderData")
    fun getOngoingOrdersCount(): Int


    // AND is_frontend_order =:isFrontendOrder
    @Query("SELECT * FROM OnlineOrderData WHERE order_status =:orderStatus and outlet_id=:outlet_id order by order_id desc")
    fun getOnlineOrdersByStatus(orderStatus: String?, outlet_id: Int): Flowable<List<OnlineOrderData>>

    @Query("SELECT * FROM OnlineOrderData WHERE order_status =:orderStatus and outlet_id=:outlet_id order by order_id desc")
    fun getOrdersByStatus(orderStatus: String?,outlet_id: String): Flowable<List<OnlineOrderData>>

    @Query("SELECT count(*) FROM OnlineOrderData WHERE order_status =:orderStatus and outlet_id=:outlet_id")
    fun getOrdersCountByStatus(orderStatus: String?,outlet_id: String): Int

    @Query("UPDATE onlineorderdata SET order_status =:orderStatus WHERE order_id =:orderId")
    fun updateOrderStatus(orderId: String?, orderStatus: String?)

    @Query("UPDATE onlineorderdata SET driver_user_id =:driverID WHERE order_id =:orderId")
    fun updateDriverID(orderId: String?, driverID: String?)

    @Query("UPDATE addoninfo SET food_status =:orderStatus WHERE row_id =:orderId")
    fun updateKitchenAddonStatus(orderId: String?, orderStatus: String?)

    @Query("UPDATE kitchenitemlist SET food_status =:orderStatus WHERE row_id =:orderId")
    fun updateKitchenItemStatus(orderId: String?, orderStatus: String?)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOngoingOrderData(onlineOrderData: List<OngoingOrderData>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertKitchenOrderData(onlineOrderData: List<KitchenOrderData>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertKitchenOrderInfo(onlineOrderData: List<KitchenOrderInfo>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertKitchenAddonInfo(addoninfos: List<Addoninfo>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertKitchenItemList(onlineOrderData: List<KitchenItemList>)

    @get:Query("SELECT * FROM ongoingorderdata order by order_id desc")
    val ongoingOnlineOrders: LiveData<List<OngoingOrderData>>

    @get:Query("SELECT * FROM ongoingorderdata order by order_id desc")
    val ongoingOnlineOrders2: Flowable<List<OngoingOrderData>>

    @get:Query("SELECT * FROM kitchenorderdata")
    val kitchenOrders: LiveData<List<KitchenOrderData>>

    @get:Query("SELECT * FROM kitchenorderdata")
    val kitchenOrders2: Flowable<List<KitchenOrderData>>

    @Query("SELECT * FROM kitchenitemlist WHERE order_id =:orderId")
    fun getKitchenItemList(orderId: String?): List<KitchenItemList>

    @Query("SELECT * FROM addoninfo WHERE order_id =:orderId")
    fun getKitchenAddonInfo(orderId: String?): List<Addoninfo>

    @Query("SELECT * FROM kitchenselectedmodifier WHERE order_id =:orderId")
    fun getKitchenSelectedModifier(orderId: String?): List<KitchenSelectedModifier>


    @Query("SELECT * FROM kitchenitemlist")
    fun kitchenItemList(): List<KitchenItemList>

//    @get:Query("SELECT * FROM kitchenitemlist")
//    val kitchenItemList: Flowable<List<KitchenItemList>>

    //    @Query("SELECT * FROM cartproductsubmodifiers")
    @get:Query("SELECT * FROM kitchenorderinfo")
    val kitchenOrdersInfo: Flowable<List<KitchenOrderInfo>>
    //    CartProductSubModifiers getCartModifierData(String subModifierId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertDriver(driverData: List<DriverData>)

    @get:Query("SELECT * FROM driverdata")
    val getDrivers: LiveData<List<DriverData?>?>

//    @get:Query("SELECT * FROM kitchenorderinfo")
//    val getData: LiveData<List<DriverData?>?>


    @Query("SELECT * FROM driverdata")
    fun getDriverList(): Flowable<List<DriverData>>



    @Query("UPDATE ongoingorderdata SET order_status =:orderStatus WHERE order_id =:orderId")
    fun updateOngoigOrderStatus(orderId: String?, orderStatus: String?)

    @get:Query("SELECT * FROM kitchenorderdata")
    val kitchenOrdersNew: List<KitchenOrderData>

    @Query("SELECT count(*) FROM kitchenitemlist where food_status=0 and order_id=:orderId")
    fun getPendingItemsInKitchenByOrderId(orderId: String?): Int

    @Query("SELECT count(*) FROM Addoninfo where food_status=0 and order_id=:orderId")
    fun getPendingAddonsItemsInKitchenByOrderId(orderId: String?): Int

    @Query("delete FROM Addoninfo where order_id=:orderId")
    fun deleteKitchenAddons(orderId: String?): Int

    @Query("delete FROM KitchenItemList where order_id=:orderId")
    fun deleteKitchenItemList(orderId: String?): Int

    @Query("delete FROM KitchenOrderData where order_id=:orderId")
    fun deleteKitchenOrderData(orderId: String?): Int

    @Query("delete FROM KitchenOrderInfo where order_id=:orderId")
    fun deleteKitchenOrderInfo(orderId: String?): Int

    @Query("delete FROM KitchenSelectedModifier where order_id=:orderId")
    fun deleteKitchenSelectedModifier(orderId: String?): Int


    @Query("SELECT count(*) FROM kitchenorderdata")
    fun getKitchenOrderDataCount(): Int

    @Query("SELECT count(*) FROM OnlineOrderData where order_status=1 and outlet_id=:outlet_id")
    fun getTotalNewOrder(outlet_id : String): Int

    @Query("select * from OnlineOrderData where outlet_id=:outlet_id order by order_id desc")
    fun getOnlineOrderNEw(outlet_id : String): List<OnlineOrderData>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrderResult(OrderData: List<Result>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertBisinessData(bisinessData: List<BisinessEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCityData(cityData: List<CityEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCustomerData(customerData: List<CustomerEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertDriverData(driverData: List<DriverEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertHistoryData(historyData: List<HistoryEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertLocationData(locationData: List<LocationEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMetafieldData(metafieldData: List<MetafieldEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPaymethodData(paymethodData: List<PaymethodEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertProductData(productData: List<ProductEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertReviewData(reviewData: List<ReviewEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSummaryData(summaryData: List<SummaryEntity>)


    @Query("select * from Result")
    fun getAllResult():List<Result>

    @Query("select status from result where id=:id")
    fun getStatus(id: String): String

    @Query("DELETE FROM Result")
    abstract fun deleteTblResult()

    @Query("DELETE FROM Bisiness")
    abstract fun deleteTblBisiness()

    @Query("DELETE FROM City")
    abstract fun deleteTblCity()

    @Query("DELETE FROM Customer")
    abstract fun deleteTblCustomer()

    @Query("DELETE FROM Driver")
    abstract fun deleteTblDriver()

    @Query("DELETE FROM DriverData")
    abstract fun deleteTblDriverData()

    @Query("DELETE FROM History")
    abstract fun deleteTblHistory()

    @Query("DELETE FROM KitchenItemList")
    abstract fun deleteTblKitchenItemList()

    @Query("DELETE FROM KitchenSelectedModifier")
    abstract fun deleteTblKitchenSelectedModifier()

    @Query("DELETE FROM KitchenOrderData")
    abstract fun deleteTblKitchenOrderData()

    @Query("DELETE FROM KitchenOrderInfo")
    abstract fun deleteTblKitchenOrderInfo()

    @Query("DELETE FROM Location")
    abstract fun deleteTblLocation()

    @Query("DELETE FROM Metafield")
    abstract fun deleteTblMetafield()

    @Query("DELETE FROM OngoingOrderData")
    abstract fun deleteTblOngoingOrderData()

    @Query("DELETE FROM OnlineOrderData")
    abstract fun deleteTblOnlineOrderData()

    @Query("DELETE FROM OutletsRS")
    abstract fun deleteTblOutletsRS()

//    @Query("DELETE FROM PaymentMethod")
//    abstract fun deleteTblPaymentMethod()

    @Query("DELETE FROM Paymethod")
    abstract fun deleteTblPaymethod()

    @Query("DELETE FROM Product")
    abstract fun deleteTblProduct()

    @Query("DELETE FROM Review")
    abstract fun deleteTblReview()

    @Query("DELETE FROM UserDetails")
    abstract fun deleteTblUserDetails()

    @Query("DELETE FROM UserPermissions")
    abstract fun deleteTblUserPermissions()

    @Query("update result set status=:status where id=:id")
    abstract fun changeOrderStatus(id: String,status: String)

    @Query("update result set prepared_in=:prepared_in where id=:id")
    abstract fun setPreparedTime(id: String,prepared_in: String)



}