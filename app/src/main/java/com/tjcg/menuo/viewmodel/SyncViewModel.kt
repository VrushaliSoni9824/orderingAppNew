package com.tjcg.menuo.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.tjcg.menuo.data.local.AppDatabase
import com.tjcg.menuo.data.local.PosDao
import com.tjcg.menuo.data.repository.SyncRepository

import io.reactivex.disposables.Disposable

class SyncViewModel(application: Application) : AndroidViewModel(application) {
    private val syncRepository: SyncRepository = SyncRepository.getInstance(application)!!
    private var posDao: PosDao = AppDatabase.getDatabase(application)!!.posDao()

    private var disposable: Disposable? = null


//    fun getAddons(outlet_id: String?, unique_id: String?,is_all_data: String) {
//        // cancelRequest = false;
//        val repositorySource = syncRepository.getAddons(outlet_id,is_all_data)
//        subUserData.addSource(repositorySource) { listResource: Resource<List<ProductAddonsData?>?> ->
////            if (!cancelRequest) {
//            if (listResource.status == Resource.Status.SUCCESS) {
//                // isPerformingQuery = false;
//                if (listResource.data != null && listResource.data.isEmpty()) {
//                    Log.e("TAG111", "Data not received...   " + listResource.message)
//                     subUserData.value = Resource(
//                            Resource.Status.ERROR,
//                            listResource.data,
//                            QUERY_EXHAUSTED
//                    )
//                }
//                subUserData.removeSource(repositorySource)
//            } else if (listResource.status == Resource.Status.ERROR) {
//                // isPerformingQuery = false;
//                Log.e("tagerr", " = = =  = sub user api error  = ==  " + listResource.message)
//                subUserData.removeSource(repositorySource)
//            }
//            subUserData.setValue(listResource)
//        }
//    }

    companion object {
        const val QUERY_EXHAUSTED = "No more results."
    }

}