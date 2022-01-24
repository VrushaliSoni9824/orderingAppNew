package com.tjcg.menuo.data.repository

class LoginRepository {
//    fun loginAdminUser(email: String?, password: String?,device_token: String? , device_type: String?, listener: ResponseListener) {
//        nentoApi.loginAdminUser(email, password,device_token, device_type).enqueue(object : Callback<String?> {
//            override fun onResponse(call: Call<String?>, response: Response<String?>) {
//                if (response.isSuccessful) {
//                    listener.onResponseReceived(getResponse(response.body()!!)!!, 1)
//                } else {
//                    listener.onResponseReceived("null", 2)
//                    Log.e("tag", "  = = = login error = =  =   " + response.errorBody())
//                }
//            }
//
//            override fun onFailure(call: Call<String?>, t: Throwable) {
//                listener.onResponseReceived("null", 3)
//                Log.e("tag", "  = = = login error = =  =   " + t.message)
//            }
//        })
//    }

    companion object {
        var instance: LoginRepository? = null
            get() {
                if (field == null) {
                    synchronized(LoginRepository::class.java) {
                        if (field == null) {
                            field = LoginRepository()
                        }
                    }
                }
                return field
            }
            private set
    }



}