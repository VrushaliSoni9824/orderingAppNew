package com.tjcg.menuo.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import java.lang.NullPointerException
import android.net.NetworkInfo

import android.net.ConnectivityManager
import android.util.Log
import android.widget.Toast
import cn.pedant.SweetAlert.SweetAlertDialog
import com.tjcg.menuo.R


class NetworkChangeReceiver : BroadcastReceiver() {
    lateinit var sweetAlertDialog : SweetAlertDialog

    override fun onReceive(context: Context, intent: Intent) {
        sweetAlertDialog = SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
        sweetAlertDialog.setCanceledOnTouchOutside(false)
        sweetAlertDialog.setCancelable(false)
        sweetAlertDialog.contentText = context.resources.getString(R.string.lbl_NoInternet) //sweetAlertDialog.contentTextSize = resources.getDimension(R.dimen._7ssp).roundToInt()
        sweetAlertDialog.confirmText = context.resources.getString(R.string.lbl_OK)
        sweetAlertDialog.confirmButtonBackgroundColor = context.resources.getColor(R.color.green)
        // This method is called when the BroadcastReceiver is receiving an Intent broadcast.
        try {
            if (isOnline(context)) {
                sweetAlertDialog.cancel()
            } else {
                sweetAlertDialog.show()
            }
        } catch (e: NullPointerException) {
            e.printStackTrace()
        }

    }
    private fun isOnline(context: Context): Boolean {
        return try {
            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val netInfo = cm.activeNetworkInfo
            //should check null because in airplane mode it will be null
            netInfo != null && netInfo.isConnected
        } catch (e: NullPointerException) {
            e.printStackTrace()
            false
        }
    }

//    fun showInternetAvailabilityDialog(InternetStatus: Boolean, context: Context){
//        if(InternetStatus){
//            val sweetAlertDialog = SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
//
////            sweetAlertDialog.showCancelButton(true)
//            sweetAlertDialog.setConfirmClickListener { sDialog ->
//
//            }.show()
//
//        }else{
//
//        }
//    }
}