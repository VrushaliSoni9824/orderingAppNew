package com.tjcg.menuo.service

import android.app.Activity
import android.util.Log
import io.socket.client.IO
import io.socket.client.IO.socket
import io.socket.client.Socket
import io.socket.emitter.Emitter
import java.net.URISyntaxException
import com.tjcg.MainApp
import com.tjcg.menuo.utils.PrefManager
import org.json.JSONObject
import org.json.JSONException


class SocketIOController {

    private var instance: SocketIOController? = null
    var socketIO: Socket? = null
    private var isConnected = true
    private var socketURL = "https://socket.ordering.co/socket.io/?"
    var userToken = ""//""eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJodHRwOlwvXC9hcGl2NC5vcmRlcmluZy5jb1wvdjQwMFwvZW5cL21lbnVvXC9hdXRoIiwiaWF0IjoxNjUwMzU5NDEzLCJleHAiOjE2ODE4OTU0MTMsIm5iZiI6MTY1MDM1OTQxMywianRpIjoic21tS2NRNEZLQUJIZVFEbCIsInN1YiI6MTEsImxldmVsIjoyfQ.C3RydodvJbxhTe5VHw2FAkR5dZ60i1EWWUnM9aLAQNI"
    private var projectName = "menuo"
    var orders_room = projectName + "_orders"//_orders_52 //id is not necesary for superadmins and use '_orders' instead of '_orders_'
//    var orders_room = projectName + "_orders" //id is not necesary for superadmins and use '_orders' instead of '_orders_'
    var drivers_room = projectName + "_drivers"
    var prefManager: PrefManager? = null
    lateinit var context: Activity
    private val TRANSPORTS = arrayOf(
        "websocket"
    )


    fun getInstance(context : Activity): SocketIOController? {
        prefManager = PrefManager(context)
        this.context = context
        userToken= prefManager!!.getString("auth_token").toString();
        if (instance == null) {
            synchronized(SocketIOController::class.java) {
                if (instance == null) instance = SocketIOController()
            }
        }
        return instance
    }

    fun init(context: Activity) {
        //mSocket.connect()
        run {
            try {
                prefManager = PrefManager(context)
                userToken= prefManager!!.getString("auth_token").toString();
                val opts: IO.Options = IO.Options()
                opts.forceNew = true;
                opts.transports=TRANSPORTS;
//                opts.port=443;
                opts.query = "token=$userToken"+ "&project=$projectName"+"&EIO=3&transport=websocket"
                socketIO = socket(socketURL,opts)
                Log.e("SocketIO URL:",socketURL+opts.query.toString())
                connect()

            } catch (e: URISyntaxException) {
                Log.e("SocketConnection Error:",e.input.toString() + e.reason.toString())
            }
        }
    }
    fun getSocket(): Socket? {
        return socketIO
    }


    fun connect(){ // direct call this method
        if(socketIO!! != null){
            if (!socketIO!!.connected()){
                socketIO!!.connect()

                socketIO?.on(Socket.EVENT_CONNECT, onConnect)
                socketIO?.on(Socket.EVENT_DISCONNECT, onDisconnect)
                socketIO?.on(Socket.EVENT_CONNECT_ERROR, onConnectError)
            }
        }
    }


    private val onConnect = Emitter.Listener {
        Log.e("aa","Connected");
//        Toast.makeText(context,"Socket connected",Toast.LENGTH_LONG).show();
       // getActivity()!!.runOnUiThread(Runnable { //PLease getActivity() is use as a mainApp get means context
            if (isConnected) {
                socketIO?.emit("join", orders_room)
                socketIO?.emit("join", drivers_room)
                //socketIO?.on("orders_register", orders_register)


                socketIO?.on("orders_register", Emitter.Listener { args ->
                    Log.e("socketee","new order");
                    val data = args[0] as JSONObject

                    //JSON Format will be received
                    //Toast.makeText(MainActivity.this, data.toString(), Toast.LENGTH_SHORT).show()

                })

                socketIO?.on("update_order", Emitter.Listener { args ->
                    val data = args[0] as JSONObject
                        val username: String
                        val message: String
                        try {
                            username = data.getString("username")
                            message = data.getString("message")
                        } catch (e: JSONException) {
                            Log.e("Socket Error",e.toString())
                        }

                        // add the message to view

                    })
                    //JSON Format will be received
                    //Toast.makeText(MainActivity.this, data.toString(), Toast.LENGTH_SHORT).show()

               // Toast.makeText(context, "Connect", Toast.LENGTH_LONG).show()
                isConnected = true
            }else{
                connect()
            }
        //})
    }

    private val onDisconnect = Emitter.Listener {
        Log.e("aaa","disconnect")
          if (socketIO != null){
              if (!socketIO?.connected()!!){
                  isConnected = false
                  connect()
              }
          }
//        connect();
        //SocketIOController().getInstance(context)!!.init(context)

    }


     private val onConnectError = Emitter.Listener {

         it.size
      Log.e("aaa","error")
         socketIO?.on("orders_register", Emitter.Listener { args ->
             val data = args[0] as JSONObject
             //JSON Format will be received
             //Toast.makeText(MainActivity.this, data.toString(), Toast.LENGTH_SHORT).show()

         })

    }

    fun socktDisconnect(){
        socketIO?.disconnect()
        socketIO?.off(Socket.EVENT_CONNECT, onConnect)
        socketIO?.off(Socket.EVENT_DISCONNECT, onDisconnect)
        socketIO?.off(Socket.EVENT_CONNECT_ERROR, onConnectError)
      //  socketIO?.off("orders_register", orders_register)
        //socketIO?.off("update_order", update_order)
//
    }


}