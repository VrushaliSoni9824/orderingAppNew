package com.tjcg.menuo.data.remote

import com.tjcg.menuo.utils.Constants
import com.tjcg.menuo.utils.Constants.CONNECTION_TIMEOUT
import com.tjcg.menuo.utils.Constants.READ_TIMEOUT
import com.tjcg.menuo.utils.Constants.WRITE_TIMEOUT
import com.tjcg.menuo.utils.LiveDataCallAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit


object ServiceGenerator {
    private val client = OkHttpClient.Builder() // establish connection to server
        .connectTimeout(CONNECTION_TIMEOUT.toLong(), TimeUnit.SECONDS) // time between each byte read from the server
        .readTimeout(READ_TIMEOUT.toLong(), TimeUnit.SECONDS) // time between each byte sent to server
        .writeTimeout(WRITE_TIMEOUT.toLong(), TimeUnit.SECONDS)
        .retryOnConnectionFailure(false)
        .build()
    private val retrofitBuilder = Retrofit.Builder()
        .baseUrl(Constants.BASE_URL)
        .client(client)
        .addCallAdapterFactory(LiveDataCallAdapterFactory())
        .addConverterFactory(ScalarsConverterFactory.create())
        .addConverterFactory(GsonConverterFactory.create())
    private val retrofit = retrofitBuilder.build()
    @JvmStatic
    val nentoApi = retrofit.create(ApiInterface::class.java)


    //new

    var interceptor = TokenInterceptor()
    var client1 = OkHttpClient.Builder().addInterceptor(interceptor).build()

    var retrofitBuilder2 = Retrofit.Builder()
        .client(client1)
        .baseUrl(Constants.BASE_URL_2)
        .addConverterFactory(GsonConverterFactory.create())

    private val retrofit2 = retrofitBuilder2.build()


    @JvmStatic
    val nentoApi2 = retrofit2.create(ApiInterface::class.java)
}