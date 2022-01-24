package com.tjcg.menuo.data

interface ResponseListener {
    fun onResponseReceived(responseObject: Any, requestType: Int)
}