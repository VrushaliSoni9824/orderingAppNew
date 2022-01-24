package com.tjcg.menuo.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
//import androidx.test.core.app.ApplicationProvider.getApplicationContext
import com.tjcg.menuo.data.ResponseListener
import com.tjcg.menuo.data.local.AppDatabase
import com.tjcg.menuo.data.local.OrderDao
import com.tjcg.menuo.data.repository.OrderRepository
import com.tjcg.menuo.data.response.DriverData
import com.tjcg.menuo.data.response.order.OnlineOrderData
import com.tjcg.menuo.utils.Resource
import com.tjcg.menuo.viewmodel.SyncViewModel.Companion.QUERY_EXHAUSTED
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers


class OnlineOrderViewModel(application: Application) : AndroidViewModel(application) {
    private val orderRepository: OrderRepository = OrderRepository.getInstance(application)!!
    private val orderDao: OrderDao = AppDatabase.getDatabase(application)!!.orderDao()
    private val onlineOrderDataList = MediatorLiveData<List<OnlineOrderData>>()
    private val driverDataList = MediatorLiveData<List<DriverData>>()
    private val onlineOrderData = MediatorLiveData<Resource<List<OnlineOrderData?>?>>()
    private val driverData = MediatorLiveData<Resource<List<DriverData?>?>>()
    private var disposableProducts: Disposable? = null
    private var isPerformingQuery = false
    private var cancelRequest = false
    fun getOnlineOrder(outlet_id: String?, unique_id: String?,is_all_data: String,responseListener: ResponseListener) {
        cancelRequest = false
        val repositorySource = orderRepository.getOnlineOrders(outlet_id, unique_id,is_all_data)
        onlineOrderData.addSource(repositorySource) { listResource: Resource<List<OnlineOrderData?>?> ->
            if (!cancelRequest) {
                if (listResource.status == Resource.Status.SUCCESS) {
                    isPerformingQuery = false
                    if (listResource.data != null && listResource.data.isEmpty()) {
                        Log.e("TAG", " online order ...  11 " + listResource.message)
                        responseListener.onResponseReceived("null", 1)
                        onlineOrderData.value = Resource(
                                Resource.Status.ERROR,
                                listResource.data,
                                QUERY_EXHAUSTED
                        )
                    }
                    onlineOrderData.removeSource(repositorySource)
                } else if (listResource.status == Resource.Status.ERROR) {
                    isPerformingQuery = false
                    responseListener.onResponseReceived(listResource.message!!, 1)
                    onlineOrderData.removeSource(repositorySource)
                }
                onlineOrderData.setValue(listResource)
            } else {
                onlineOrderData.removeSource(repositorySource)
            }
        }
    }

    fun getOnlineOrderSync(outlet_id: String?, unique_id: String?,is_all_data: String,responseListener: ResponseListener) {
        cancelRequest = false
        val repositorySource = orderRepository.getOnlineOrdersSync(outlet_id, unique_id,is_all_data)
        onlineOrderData.addSource(repositorySource) { listResource: Resource<List<OnlineOrderData?>?> ->
            if (!cancelRequest) {
                if (listResource.status == Resource.Status.SUCCESS) {
                    isPerformingQuery = false
                    if (listResource.data != null && listResource.data.isEmpty()) {
                        Log.e("TAG", " online order ...  11 " + listResource.message)
                        responseListener.onResponseReceived("null", 1)
                        onlineOrderData.value = Resource(
                            Resource.Status.ERROR,
                            listResource.data,
                            QUERY_EXHAUSTED
                        )
                    }
                    onlineOrderData.removeSource(repositorySource)
                } else if (listResource.status == Resource.Status.ERROR) {
                    isPerformingQuery = false
                    responseListener.onResponseReceived(listResource.message!!, 1)
                    onlineOrderData.removeSource(repositorySource)
                }
                onlineOrderData.setValue(listResource)
            } else {
                onlineOrderData.removeSource(repositorySource)
            }
        }
    }

//    fun getInvoice(outlet_id: String?, order_id: String?, responseListener: ResponseListener) {
//
//        cancelRequest = false
//        val repositorySource = orderRepository.getInvoiceData(outlet_id, order_id)
//        data.addSource(repositorySource) { listResource: Resource<List<Data?>?> ->
//            if (!cancelRequest) {
//                if (listResource.status == Resource.Status.SUCCESS) {
//                    isPerformingQuery = false
//                    if (listResource.data != null && listResource.data.isEmpty()) {
//                        Log.e("TAG", " online order ...  11 " + listResource.message)
//                        responseListener.onResponseReceived("null", 1)
//                        data.value = Resource(
//                                Resource.Status.ERROR,
//                                listResource.data,
//                                PosViewModel.QUERY_EXHAUSTED
//                        )
//                    }
//                    data.removeSource(repositorySource)
//                } else if (listResource.status == Resource.Status.ERROR) {
//                    isPerformingQuery = false
//                    responseListener.onResponseReceived(listResource.message!!, 1)
//                    data.removeSource(repositorySource)
//                }
//                data.setValue(listResource)
//            } else {
//                data.removeSource(repositorySource)
//            }
//        }
//    }

    fun getOnlineOrderByStatus(orderStatus: String?, outlet_id: String?): LiveData<List<OnlineOrderData>> {
        disposableProducts = orderDao.getOnlineOrdersByStatus(orderStatus,outlet_id!!.toInt())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { value: List<OnlineOrderData> -> onlineOrderDataList.setValue(value) }
        return onlineOrderDataList
    }


    val onlineOrderDataObserver: LiveData<Resource<List<OnlineOrderData?>?>>
        get() = onlineOrderData




    fun getDriverList(): LiveData<List<DriverData>> {
        disposableProducts = orderDao.getDriverList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { value: List<DriverData> -> driverDataList.setValue(value) }
        return driverDataList
    }

    val getDriverData: LiveData<Resource<List<DriverData?>?>>
        get() = driverData

}