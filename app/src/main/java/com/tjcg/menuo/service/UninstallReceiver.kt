package com.tjcg.menuo.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class UninstallReceiver : BroadcastReceiver() {

    private val TAG = "UninstallReceiver"

    // http://developer.android.com/reference/android/content/Intent.html#ACTION_BOOT_COMPLETED
    private val ACTION_PACKAGE_REMOVED = Intent.ACTION_PACKAGE_REMOVED
    private val ACTION_PACKAGE_FULLY_REMOVED = Intent.ACTION_PACKAGE_FULLY_REMOVED

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
//        if (ACTION_PACKAGE_REMOVED.equals(action) || ACTION_PACKAGE_FULLY_REMOVED.equals(action) ) {
        //        if (ACTION_PACKAGE_REMOVED.equals(action) || ACTION_PACKAGE_FULLY_REMOVED.equals(action) ) {
//        val pkg: String = getPackageName(intent)
//        val applicationStatus = intent.getBooleanExtra(Intent.EXTRA_REPLACING, false)
//        Log.d(TAG, "### ############################### ### ")
//        Log.d(TAG, "### ### EventSpy PACKAGE_REMOVED : $action ### ### ")
//        Log.d(TAG, "### ### EventSpy PACKAGE_REMOVED  Package : $pkg ### ### ")
//
//
//        Log.d(TAG, "### ############################### ### ")
//        }
    }

}