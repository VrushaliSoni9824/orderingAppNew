package com.tjcg.menuo.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.lifecycle.ViewModelProviders
import butterknife.internal.Utils
import com.tjcg.menuo.viewmodel.SyncViewModel
import java.io.BufferedWriter

class Utils {

    var posViewModel: SyncViewModel? = null

    companion object {
        private var mInstance: com.tjcg.menuo.utils.Utils? = null
        private lateinit var out: BufferedWriter


        val instance: Utils
            @Synchronized get() {
                if (mInstance == null) mInstance = Utils()
                return mInstance as Utils
            }
    }

    public fun isInternetAvailable(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val n = cm.activeNetwork
            if (n != null) {
                val nc = cm.getNetworkCapabilities(n)
                return nc!!.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) || nc.hasTransport(
                    NetworkCapabilities.TRANSPORT_WIFI) || nc.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
            }
            return false
        } else {
            val netInfo = cm.activeNetworkInfo
            return netInfo != null && netInfo.isConnectedOrConnecting
        }
    }

//    public fun getOutlets() : List<OutletsRS>{
//        val outletsRS = db!!.adminItemDao().outlets
//        return outletsRS;
//    }




}