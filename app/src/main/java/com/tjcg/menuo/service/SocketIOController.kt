package com.tjcg.menuo.service


import com.google.android.material.internal.ContextUtils.getActivity
import com.tjcg.menuo.utils.Constants
import io.socket.client.IO
import io.socket.client.IO.socket
import io.socket.emitter.Emitter
import java.net.URISyntaxException
import org.json.JSONException

import org.json.JSONObject




class SocketIOController {

    var socktIO: IO? = null
  fun init(){
      try {
          // socktIO = socket(Constants.SOCKET_URL)
      } catch (e: URISyntaxException) {

      }
  }

    fun connectSocketIO(){
       // socktIO!!.
       /* val onNewMessage: Emitter.Listener = object : Emitter.Listener() {
            override fun call(vararg args: Any) {getActivity().runOnUiThread(Runnable {
                    val data = args[0] as JSONObject
                    val username: String
                    val message: String
                    try {
                        username = data.getString("username")
                        message = data.getString("message")
                    } catch (e: JSONException) {
                        return@Runnable
                    }

                    // add the message to view
                    addMessage(username, message)
                })
            }
        }*/
    }

}