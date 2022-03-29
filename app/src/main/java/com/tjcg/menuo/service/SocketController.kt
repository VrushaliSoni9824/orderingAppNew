package com.tjcg.menuo.service

import android.net.TrafficStats
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.tjcg.menuo.utils.Constants
import org.java_websocket.client.WebSocketClient
import org.java_websocket.enums.ReadyState
import org.java_websocket.handshake.ServerHandshake
import org.json.JSONObject
import java.lang.Exception
import java.net.URI
import java.net.URISyntaxException

class SocketController {
    private var instance: SocketController? = null
    private var mWebSocketClient: WebSocketClient? = null
    private var count: Int = 0

    fun getInstance(): SocketController? {
        if (instance == null) {
            synchronized(SocketController::class.java) {
                if (instance == null) instance = SocketController()
            }
        }
        return instance
    }

    fun init() {
        connectSocket()
    }


    private fun connectSocket() {
        if (mWebSocketClient != null && mWebSocketClient!!.readyState == ReadyState.OPEN) mWebSocketClient!!.close()
        TrafficStats.setThreadStatsTag(0xF00D)
        try { // Make network request using HttpClient.execute()
            connectWebSocket()
        } finally {
            TrafficStats.clearThreadStatsTag()
            Log.e("test", "")
        }
    }


    private fun connectWebSocket() {
        val uri: URI = try {
            URI(Constants.SOCKET_URL)
        } catch (e: URISyntaxException) {
            e.printStackTrace()
            Log.e("test", e.toString())
            return

        }
        mWebSocketClient = object : WebSocketClient(uri) {
            override fun onOpen(handshakedata: ServerHandshake?) {
                Log.e("test", "OPEN")
            }

            override fun onMessage(message: String?) {
                val mHandler = Handler(Looper.getMainLooper())
                mHandler.post {
                    val jsonObject = JSONObject(message)
//                    if (jsonObject.getString("").equals("")){
//
//                    }
                }
            }

            override fun onClose(code: Int, reason: String?, remote: Boolean) {
                Log.e("test", "OPEN")
            }

            override fun onError(ex: Exception?) {
                Log.e("test", "OPEN")
            }

        }
    }
}