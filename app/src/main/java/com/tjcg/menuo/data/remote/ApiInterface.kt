package com.tjcg.menuo.data.remote

import androidx.lifecycle.LiveData
import com.tjcg.menuo.data.repository.ApiResponse
import com.tjcg.menuo.data.response.IntermediatorServerAPI.IntermediatorLogin
import com.tjcg.menuo.data.response.IntermediatorServerAPI.IntermediatorLogout
import com.tjcg.menuo.data.response.Keys.KeyResponce
import com.tjcg.menuo.data.response.LoginNew.LoginRs
import com.tjcg.menuo.data.response.order.OnlineOrderRS
import retrofit2.Call
import retrofit2.http.*


interface ApiInterface {
    @POST("auth")
    @FormUrlEncoded
    fun loginAdminUser(@Field("email") emailId: String?,
                       @Field("password") password: String?): Call<LoginRs>

    @GET
    fun getUsers(@Url url: String?,@Header("x-api-key") x_api_key: String?): Call<String?>?

    @GET
    fun findOrder(@Url url: String?,@Header("x-api-key") x_api_key: String?): Call<String?>?

    @FormUrlEncoded
    @PUT("https://apiv4.ordering.co/v400/en/menuo/orders/{order_id}")
    fun RejectORder(@Header("x-api-key") x_api_key: String,@Path("order_id") order_id : String, @Field("status") status : String = "6"): Call<String>;

    @FormUrlEncoded
    @PUT("https://apiv4.ordering.co/v400/en/menuo/orders/{order_id}")
    fun AcceptORder(@Header("x-api-key") x_api_key: String,@Path("order_id") order_id : String, @Field("status") status : String = "6", @Field("prepared_in") prepared_in: String="30"): Call<String>;

    @GET
    fun getBusinessUsers(@Url url: String?,@Header("x-api-key") x_api_key: String?): Call<String?>?

    @POST
    fun forgetPassword(@Url url: String?): Call<String?>?

    @GET("orders?page_size=10&mode=dashboard&page=")
    fun getOrders(@Header("x-api-key") x_api_key: String?,
                  @Field("page_size") page_size: String?,
                  @Field("mode") mode: String?,
                  @Field("page") page: String?): Call<String?>

    @POST("getLocationBaseOnlineOrderList")
    @FormUrlEncoded
    fun getOnlineOrderList(@Field("outlet_id") outlet_id: String?,
                           @Field("unique_id") unique_id: String?,
                           @Field("device_id") device_id: String?,
                           @Field("is_all_data") is_all_data: String?="1",
                           @Header("Authorization") authHeader: String?): LiveData<ApiResponse<OnlineOrderRS?>>



    @POST("getLocationBaseOnlineOrderList")
    @FormUrlEncoded
    fun getOnlineOrderListSync(@Field("outlet_id") outlet_id: String?,
                               @Field("unique_id") unique_id: String?,
                               @Field("device_id") device_id: String?,
                               @Field("is_all_data") is_all_data: String?="1",
                               @Header("Authorization") authHeader: String?): Call<OnlineOrderRS?>


    @POST("getLocationBaseOnlineOrderList")
    @FormUrlEncoded
    fun getOnlineOrderCount(@Field("outlet_id") outlet_id: String?,
                            @Field("unique_id") unique_id: String?,
                            @Header("Authorization") authHeader: String?): Call<OnlineOrderRS?>

    //apis for notify

    @POST("https://menuo.area81-ns.se/menuo-notify/public/api/v1/login")
    @FormUrlEncoded
    fun loginAtIntermediateServer(@Field("business_id") business_id: String?,
                           @Field("device_name") device_name: String?,
                           @Field("platform") platform: String?,
                           @Field("device_id") device_id: String?,
                           @Field("pushtoken") pushtoken: String?): Call<IntermediatorLogin?>

    @POST("logout")
    @FormUrlEncoded
    fun logoutAtIntermediateServer(@Field("business_id") business_id: String?,
                                  @Field("pushtoken") pushtoken: String?): Call<IntermediatorLogout?>



}