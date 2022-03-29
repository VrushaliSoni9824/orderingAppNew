package com.tjcg

import android.app.Application
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkRequest
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.multidex.MultiDex
import com.tjcg.menuo.data.local.AppDatabase
import com.tjcg.menuo.data.local.OrderDao
import com.tjcg.menuo.service.SocketController
import com.tjcg.menuo.utils.Default

private lateinit var INSTANCE: Application
class MainApp: Application() {

    override fun onCreate() {
        super.onCreate()
        mainApp = this
        INSTANCE = this
        isAidl = true
        ConnectivityLiveData().observeForever {
            isConnected = it ?: false
        }

        setAidl(true)
        //Utils.instance.connectPrinterService(this)
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(base)
    }

    fun setDao(myDao: OrderDao?): OrderDao? {
        dao = myDao
        return dao
    }

    fun getDao(): OrderDao? {
        if (dao == null) {
            dao = AppDatabase.getDatabase(applicationContext)?.orderDao()
        }
        return dao
    }

    fun isAidl(): Boolean {
        return isAidl
    }

    fun setAidl(aidl: Boolean) {
        isAidl = aidl
    }

    fun getMainApp(): MainApp? {
        return mainApp
    }

    companion object {
        var mainApp: MainApp? = null
        var dao: OrderDao? = null
        var isAidl = false
        var isConnected = false
    }
}

object AppContext : ContextWrapper(INSTANCE)

class ConnectivityLiveData : LiveData<Boolean>() {
    val cm = AppContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    private val networkCallback = @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    object : ConnectivityManager.NetworkCallback() {

        override fun onLost(network: Network) {
            super.onLost(network)
            postValue(false)
            INSTANCE.sendBroadcast(Intent(Default.WIFI).putExtra(Default.IS_CONNECTED, false))
        }

        override fun onAvailable(network: Network) {
            super.onAvailable(network)
            postValue(true)
            INSTANCE.sendBroadcast(Intent(Default.WIFI).putExtra(Default.IS_CONNECTED, true))

            // call itemSync
//            if (!SocketController().getInstance()!!.isSockConn()) {

//            }
        }

    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onActive() {
        super.onActive()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            cm.registerDefaultNetworkCallback(networkCallback)
        } else {
            val networkRequest = NetworkRequest.Builder().build()
            cm.registerNetworkCallback(networkRequest, networkCallback)
        }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onInactive() {
        super.onInactive()
        cm.unregisterNetworkCallback(networkCallback)
        INSTANCE.sendBroadcast(Intent(Default.WIFI).putExtra(Default.IS_CONNECTED, false))
    }

}