package com.tjcg.menuo.data.repository

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import com.tjcg.menuo.data.local.AppDatabase.Companion.getDatabase
import com.tjcg.menuo.data.local.OrderDao
import com.tjcg.menuo.data.remote.ServiceGenerator.nentoApi

import com.tjcg.menuo.data.response.order.*
import com.tjcg.menuo.utils.AppExecutors
import com.tjcg.menuo.utils.Constants.Authorization
import com.tjcg.menuo.utils.NetworkBoundResource
import com.tjcg.menuo.utils.Resource
import java.util.concurrent.Executors

class OrderRepository(context: Context?) {
    var orderDao: OrderDao = getDatabase(context!!)!!.orderDao()
    var outlet_id:String= context!!.getSharedPreferences("com.tjcg.nentopos", Context.MODE_PRIVATE).getInt("",1).toString()
    val sharedPreferences: SharedPreferences = context!!.getSharedPreferences("com.tjcg.nentopos", Context.MODE_PRIVATE)
    val deviceID=sharedPreferences.getString("device_ID","0")
    fun getOnlineOrders(outlet_id: String?, unique_id: String?,is_all_data: String): LiveData<Resource<List<OnlineOrderData?>?>> {
        return object : NetworkBoundResource<List<OnlineOrderData?>?, OnlineOrderRS?>(AppExecutors.instance) {
            override fun saveCallResult(item: OnlineOrderRS) {
                if (item.status == "true") {
                    if (item.data != null) {
                        val executorService = Executors.newSingleThreadExecutor()
                        executorService.execute {
                            orderDao.insertOnlineOrderData(item.data!!.new_order!!)
                            orderDao.insertOnlineOrderData(item.data!!.completed_order!!)
                            orderDao.insertOnlineOrderData(item.data!!.cancelled_order!!)
                            orderDao.insertOnlineOrderData(item.data!!.accepted_order!!)
                        }
                    }
                }
            }

            override fun shouldFetch(data: List<OnlineOrderData?>?): Boolean {
                return true
            }

            override fun loadFromDb(): LiveData<List<OnlineOrderData?>?> {
                return orderDao.getAllOnlineOrder(outlet_id.toString())
            }

            override fun createCall(): LiveData<ApiResponse<OnlineOrderRS?>> {
                return nentoApi.getOnlineOrderList(outlet_id, unique_id,deviceID,is_all_data, Authorization)
            }
        }.asLiveData
    }

    fun getOnlineOrdersSync(outlet_id: String?, unique_id: String?,is_all_data: String): LiveData<Resource<List<OnlineOrderData?>?>> {
        return object : NetworkBoundResource<List<OnlineOrderData?>?, OnlineOrderRS?>(AppExecutors.instance) {
            override fun saveCallResult(item: OnlineOrderRS) {
                if (item.status == "true") {
                    if (item.data != null) {
                        val executorService = Executors.newSingleThreadExecutor()
                        executorService.execute {
//                            orderDao.insertOnlineOrderData(item.data!!.new_order!!)
//                            orderDao.insertOnlineOrderData(item.data!!.completed_order!!)
//                            orderDao.insertOnlineOrderData(item.data!!.cancelled_order!!)
//                            orderDao.insertOnlineOrderData(item.data!!.accepted_order!!)
                        }
                    }
                }
            }

            override fun shouldFetch(data: List<OnlineOrderData?>?): Boolean {
                return true
            }

            override fun loadFromDb(): LiveData<List<OnlineOrderData?>?> {
                return orderDao.getAllOnlineOrder(outlet_id.toString())
            }

            override fun createCall(): LiveData<ApiResponse<OnlineOrderRS?>> {
                return nentoApi.getOnlineOrderList(outlet_id, unique_id,deviceID,is_all_data, Authorization)
            }
        }.asLiveData
    }


//    fun getInvoiceData(outlet_id: String?, order_id: String?): LiveData<Resource<List<Data?>?>> {
//        return object : NetworkBoundResource<List<Data?>?, InvoiceData?>(AppExecutors.instance) {
//            override fun saveCallResult(item: InvoiceData) {
//                if (item.status == true) {
//                    if (item.data != null) {
//                        val executorService = Executors.newSingleThreadExecutor()
//                        executorService.execute {
////                            orderDao.insertOnlineOrderData(item.data!!.new_order!!)
////                            orderDao.insertOnlineOrderData(item.data!!.completed_order!!)
////                            orderDao.insertOnlineOrderData(item.data!!.cancelled_order!!)
////                            orderDao.insertOnlineOrderData(item.data!!.accepted_order!!)
//                        }
//                    }
//                }
//            }
//
//            override fun shouldFetch(data: List<Data?>?): Boolean {
//                return true
//            }
//
////            override fun loadFromDb(): LiveData<List<Data?>?> {
////                return orderDao.getin
////            }
//
//            override fun createCall(): LiveData<ApiResponse<InvoiceData?>> {
//                return nentoApi.getInvoiceData(outlet_id, order_id, Authorization)
//            }
//
//            override fun loadFromDb(): LiveData<List<Data?>?> {
//                val mutableLiveData = MutableLiveData<List<Data?>?>()
//                val data = ArrayList<Data>()
//                mutableLiveData.value = data
//                return mutableLiveData
//            }
//        }.asLiveData
//    }

    companion object {
        private var instance: OrderRepository? = null
        fun getInstance(context: Context?): OrderRepository? {
            if (instance == null) {
                synchronized(OrderRepository::class.java) {
                    if (instance == null) {
                        instance = OrderRepository(context)
                    }
                }
            }
            return instance
        }
    }

}