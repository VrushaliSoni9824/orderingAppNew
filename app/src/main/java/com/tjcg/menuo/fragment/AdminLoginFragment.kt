package com.tjcg.menuo.fragment

import android.annotation.SuppressLint
import com.tjcg.menuo.data.ResponseListener
import com.tjcg.menuo.activity.LoginActivity
import com.tjcg.menuo.data.local.AppDatabase
import com.tjcg.menuo.utils.PrefManager
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import com.tjcg.menuo.data.response.Login.AdminLoginRS
import android.content.Intent
import android.util.Log
import android.view.View
import com.tjcg.menuo.MainActivity
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.tjcg.menuo.databinding.FragmentAdminLoginBinding
import com.tjcg.menuo.utils.Constants
import com.tjcg.menuo.utils.Constants.Authorization
import com.tjcg.menuo.utils.LottieProgressDialog
import android.content.SharedPreferences

import android.os.Build
import android.provider.Settings
import com.google.firebase.messaging.FirebaseMessaging
import android.text.TextUtils
import android.util.Base64
import androidx.annotation.RequiresApi
import com.google.android.gms.tasks.*
import com.tjcg.menuo.Expandablectivity
import com.tjcg.menuo.data.remote.ServiceGenerator
import com.tjcg.menuo.data.response.Keys.KeyResponce
import com.tjcg.menuo.data.response.Login.OutletsRS
import com.tjcg.menuo.data.response.LoginNew.LoginRs
import com.tjcg.menuo.utils.SharedPreferencesKeys
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception
import java.lang.StringBuilder
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException


class AdminLoginFragment : Fragment(), ResponseListener {

    var binding: FragmentAdminLoginBinding? = null
    private lateinit var loginActivity: LoginActivity
    var db: AppDatabase? = null
    var prefManager: PrefManager? = null
    private var lottieProgressDialog: LottieProgressDialog? = null
    var email : String = ""
    var businessId : String = ""

    override fun onAttach(context: Context) {
        super.onAttach(context)
        loginActivity = context as LoginActivity
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentAdminLoginBinding.inflate(inflater)
        lottieProgressDialog = LottieProgressDialog(loginActivity as Context)
        db = AppDatabase.getDatabase(loginActivity)
        prefManager = PrefManager(loginActivity)

        getTokenFirebase()

        val sharedPreferences: SharedPreferences = loginActivity.getSharedPreferences("com.tjcg.nentopos", Context.MODE_PRIVATE)
        var fisrtSyncDone : Boolean = sharedPreferences.getBoolean("fisrtSyncDone",false)
        if(fisrtSyncDone){
            binding!!.cashierLoginButton.visibility=View.VISIBLE
        }

        binding!!.adminLogin.setOnClickListener {

            email = binding!!.emailEt.text.toString()
            var password = binding!!.passwordEt.text.toString()
            Login(email,password)
//            if(fisrtSyncDone){
//                password= md5(password).toString();
//                var loginstatus=db!!.adminItemDao().localLogin(email!!,password)
//                if(loginstatus.toInt()> 0){
////                    code for local login
////                    startActivity(Intent(loginActivity, MainActivity::class.java))
////                    loginActivity.finish()
//
//                    lottieProgressDialog!!.showDialog()
//
//                    getToken()
//                    getDeviceID()
//                    val sharedPreferences: SharedPreferences = loginActivity.getSharedPreferences("com.tjcg.nentopos", Context.MODE_PRIVATE)
//                    var tokennn= sharedPreferences.getString("device_token","").toString()
//                    LoginRepository.instance!!.loginAdminUser(email, password,tokennn, "android",this@AdminLoginFragment)
//                }
//            }else{
//                lottieProgressDialog!!.showDialog()
//
//                getToken()
//                getDeviceID()
//                val sharedPreferences: SharedPreferences = loginActivity.getSharedPreferences("com.tjcg.nentopos", Context.MODE_PRIVATE)
//                var tokennn= sharedPreferences.getString("device_token","").toString()
//                LoginRepository.instance!!.loginAdminUser(email, password,tokennn, "android",this@AdminLoginFragment)
//            }

        }
        return binding!!.root
    }

    override fun onResponseReceived(responseObject: Any, requestType: Int) {
        if (requestType == 1) {
            val adminLoginRS = responseObject as AdminLoginRS
            if (adminLoginRS.status == "true") {
                val userPermissions = adminLoginRS.data.userPermissions
                userPermissions!!.client_id = adminLoginRS.data.userDetails!!.client_id
                val sharedPreferences: SharedPreferences = loginActivity.getSharedPreferences("com.tjcg.nentopos", Context.MODE_PRIVATE)
                val editor:SharedPreferences.Editor =  sharedPreferences.edit()
                editor.putString("userName", adminLoginRS.data.userDetails!!.first_name.toString())
                editor.putString("outLetName", adminLoginRS.data.userDetails!!.company_name.toString())
                editor.putString(SharedPreferencesKeys.Is_Pos, userPermissions.pos)
                editor.putString(SharedPreferencesKeys.all_order,userPermissions.all_order)
                editor.putString(SharedPreferencesKeys.dashboard_analytics, userPermissions.dashboard_analytics)
                editor.putString(SharedPreferencesKeys.kitchen_display, userPermissions.kitchen_display)
                editor.putString(SharedPreferencesKeys.counter_display, userPermissions.counter_display)
                editor.putString(SharedPreferencesKeys.reservation_management, userPermissions.reservation_management)
                editor.putString(SharedPreferencesKeys.customer_list, userPermissions.customer_list)
                editor.putString(SharedPreferencesKeys.report_list, "0")
                editor.putString(SharedPreferencesKeys.table_management, userPermissions.table_management)
                editor.putString(SharedPreferencesKeys.menu_management, "0")
                editor.apply()
                editor.commit()
                db!!.adminItemDao().insertOutlets(adminLoginRS.data.outletsRS!!)
                db!!.adminItemDao().insertUserDetails(adminLoginRS.data.userDetails)

                db!!.adminItemDao().insertUserPermission(userPermissions)
                prefManager!!.setString(Constants.authorization_key, adminLoginRS.data.authorization)
                Authorization = adminLoginRS.data.authorization!!
                prefManager!!.setString("auth_token", adminLoginRS.data.authorization)
                prefManager!!.setBoolean("isLogin", true)

//                md5("12345")
                startActivity(Intent(loginActivity, MainActivity::class.java))
                loginActivity.finish()
            } else {
                Toast.makeText(loginActivity, adminLoginRS.message, Toast.LENGTH_SHORT).show()
            }
        } else {
            Log.e("tag", " = = =  data =  ==  $responseObject")
        }
        lottieProgressDialog!!.cancelDialog()
    }

    companion object {
//        private var woyouService: IWoyouService? = null

        @JvmStatic
        fun newInstance(): AdminLoginFragment {
            return AdminLoginFragment()
        }
    }



    fun getToken() {
//        var token : String="";
//        FirebaseInstallations.getInstance().getToken(true).addOnCompleteListener {
//            var fbToken = it.result
//            Log.e("tokennewwwww", fbToken!!.token.toString())
//            val sharedPreferences: SharedPreferences = loginActivity.getSharedPreferences("com.tjcg.nentopos", Context.MODE_PRIVATE)
//            val editor: SharedPreferences.Editor =  sharedPreferences.edit()
//            editor.putString("device_token", fbToken!!.token.toString())
//            editor.apply()
//            editor.commit()
//
//            // DO your thing with your firebase token
//        }

        FirebaseMessaging.getInstance().token.addOnSuccessListener { token: String ->
            if (!TextUtils.isEmpty(token)) {

                val sharedPreferences: SharedPreferences = loginActivity.getSharedPreferences("com.tjcg.nentopos", Context.MODE_PRIVATE)
                val editor: SharedPreferences.Editor =  sharedPreferences.edit()
                editor.putString("device_token", token)
                editor.apply()
                editor.commit()

                Log.d("QQQToken", "retrieve token successful : $token")
            } else {
                Log.w("QQQ", "token should not be null...")
            }
        }.addOnFailureListener { e: Exception? -> }.addOnCanceledListener {}
            .addOnCompleteListener { task: Task<String> ->
                Log.v(
                    "QQQ",
                    "This is the token : " + task.result
                )
            }

//        val sharedPreferences: SharedPreferences = loginActivity.getSharedPreferences("com.tjcg.nentopos", Context.MODE_PRIVATE)
//        token= sharedPreferences.getString("device_token","").toString()
//        Log.e("tokennento",token)


//        return token;
    }

    fun getDeviceID(){
        val deviceId = Settings.Secure.getString(requireActivity().contentResolver, Settings.Secure.ANDROID_ID)
        val sharedPreferences: SharedPreferences = loginActivity.getSharedPreferences("com.tjcg.nentopos", Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor =  sharedPreferences.edit()
        editor.putString("device_ID", deviceId)
        editor.apply()
        editor.commit()
        Log.e("deviceIDV",deviceId.toString());
    }

    public fun getOutlets() : List<OutletsRS>{
        val outletsRS = db!!.adminItemDao().outlets
        return outletsRS;
    }

    fun getSomeText(): String? {
        var text: String? = "827ccb0eea8a706c4c34a16891f84e7b"
        text = String(Base64.decode(text, Base64.DEFAULT))
        return text
    }

    fun md5(s: String): String? {
        val MD5 = "MD5"
        try {
            // Create MD5 Hash
            val digest: MessageDigest = MessageDigest
                .getInstance(MD5)
            digest.update(s.toByteArray())
            val messageDigest: ByteArray = digest.digest()

            // Create Hex String
            val hexString = StringBuilder()
            for (aMessageDigest in messageDigest) {
                var h = Integer.toHexString(0xFF and aMessageDigest.toInt())
                while (h.length < 2) h = "0$h"
                hexString.append(h)
            }
            return hexString.toString()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        }
        return ""
    }

    fun Login(email: String, password: String) {
        lottieProgressDialog!!.showDialog()
        ServiceGenerator.nentoApi.loginAdminUser(email,password).enqueue(object :
            Callback<LoginRs?> {
            @SuppressLint("NewApi", "ResourceAsColor")
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onResponse(call: Call<LoginRs?>, response: Response<LoginRs?>) {
                if (response.isSuccessful) {
                    Toast.makeText(activity,"siccess",Toast.LENGTH_SHORT).show()
                    var accessToken=response.body()!!.result.session.access_token
                    Constants.bearrToken=accessToken
                    prefManager!!.setString("auth_token", accessToken)
                    getUsers(Constants.apiKey)
                } else {
                    lottieProgressDialog!!.cancelDialog()
                    Toast.makeText(activity,"Wrong detail",Toast.LENGTH_SHORT).show()
                    Log.e("tag", " =  = = =error = ==  " + response.message())
                }
            }

            override fun onFailure(call: Call<LoginRs?>, t: Throwable) {
                lottieProgressDialog!!.cancelDialog()
                Toast.makeText(activity,"Error",Toast.LENGTH_SHORT).show()
                Log.e("tag", " =  = = =error = ==  " + t.message)
            }
        })
    }

    fun getKey(email: String) {
//        lottieProgressDialog!!.showDialog()
        ServiceGenerator.nentoApi2.getkeys().enqueue(object :
            Callback<KeyResponce?> {
            @SuppressLint("NewApi", "ResourceAsColor")
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onResponse(call: Call<KeyResponce?>, response: Response<KeyResponce?>) {
                if (response.isSuccessful) {
//                    lottieProgressDialog!!.cancelDialog()
                    Toast.makeText(activity,"siccess",Toast.LENGTH_SHORT).show()
                    var key =response.body()!!.result[0].key
                    getOrders(key)

                } else {
//                    lottieProgressDialog!!.cancelDialog()
                    Toast.makeText(activity,"Wrong detail",Toast.LENGTH_SHORT).show()
//                    Toast.makeText(mainActivity,"Error while fetching invoice detail..",Toast.LENGTH_SHORT).show()
                    Log.e("tag", " =  = = =error = ==  " + response.message())
                }
            }

            override fun onFailure(call: Call<KeyResponce?>, t: Throwable) {
//                lottieProgressDialog!!.cancelDialog()
                Toast.makeText(activity,"Error",Toast.LENGTH_SHORT).show()
                Log.e("tag", " =  = = =error = ==  " + t.message)
            }
        })
    }

    fun getOrders(key: String) {
        var url =Constants.URL
        url=url+"page_size="+"5000"+"&mode="+"dashboard"+"&page="+"1"
        ServiceGenerator.nentoApi.getUsers(url,key)!!.enqueue(object :
            Callback<String?> {
            @SuppressLint("NewApi", "ResourceAsColor")
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onResponse(call: Call<String?>, response: Response<String?>) {
                if (response.isSuccessful) {
                    lottieProgressDialog!!.cancelDialog()
                    Toast.makeText(activity,"siccess",Toast.LENGTH_SHORT).show()
                    var orderResult : String =response.body()!!.toString()
                    orderResult

//                    var jobj = JSONObject(orderResult)
//                    jobj
                    prefManager!!.setBoolean(SharedPreferencesKeys.isFromLogin, true)
                    var i = Intent(context,Expandablectivity::class.java)
                    i.putExtra("odata",orderResult)
                    startActivity(i)


                } else {
                    lottieProgressDialog!!.cancelDialog()
                    Toast.makeText(activity,"Wrong detail",Toast.LENGTH_SHORT).show()
//                    Toast.makeText(mainActivity,"Error while fetching invoice detail..",Toast.LENGTH_SHORT).show()
                    Log.e("tag", " =  = = =error = ==  " + response.message())
                }
            }

            override fun onFailure(call: Call<String?>, t: Throwable) {
                lottieProgressDialog!!.cancelDialog()
                Toast.makeText(activity,"Error",Toast.LENGTH_SHORT).show()
                Log.e("tag", " =  = = =error = ==  " + t.message)
            }
        })
    }

    fun getUsers(key: String) {
        var url =Constants.BUSINESS_URL
        ServiceGenerator.nentoApi.getBusinessUsers(url,key)!!.enqueue(object :
            Callback<String?> {
            @SuppressLint("NewApi", "ResourceAsColor")
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onResponse(call: Call<String?>, response: Response<String?>) {
                if (response.isSuccessful) {
                    Toast.makeText(activity,"success",Toast.LENGTH_SHORT).show()
                    var orderResult : String =response.body()!!.toString()
                    val jobj = JSONObject(orderResult)
                    val jsnResult : JSONArray = jobj.getJSONArray("result")!!
                    for (result in 0..jsnResult.length()-1) {
                        var jsonUser : JSONObject = jsnResult.getJSONObject(result)
                        var emailNew =  jsonUser.getString("email")
                        if(emailNew.equals(email)){
                            var businessDetail : JSONArray = jsonUser.getJSONArray("businesses")
                            if(businessDetail.length()>0){
                                for(i in 0..businessDetail.length()-1){
                                    var businessobj : JSONObject = businessDetail.getJSONObject(i)
                                    if(businessobj!=null){
                                        var businessOwnerNAme = jsonUser.getString("name");
                                        var businessNAme = businessobj.getString("name");
                                        businessId = businessobj.getString("id")
                                        businessOwnerNAme
                                        businessNAme

                                        prefManager!!.setString(SharedPreferencesKeys.businessName, businessNAme)
                                        prefManager!!.setString(SharedPreferencesKeys.businessOwner, businessOwnerNAme)
                                    }
                                }
                            }

                        }

                    }
                    lottieProgressDialog!!.cancelDialog()
                    prefManager!!.setBoolean("isLogin", true)
                    prefManager!!.setString("businessID", businessId.toString())

                    //PASS TOKEN
                    var getTokenURl : String = Constants.BUSINESS_URL+businessId+ Constants.NOTIFICATION_ENDPOINTS
                    getTokenURl
                    var i = Intent(context,Expandablectivity::class.java)
                    i.putExtra("businessID",businessId)
                    startActivity(i)
                } else {
                    lottieProgressDialog!!.cancelDialog()
                    Toast.makeText(activity,"Wrong detail",Toast.LENGTH_SHORT).show()
                    Log.e("tag", " =  = = =error = ==  " + response.message())
                }
            }

            override fun onFailure(call: Call<String?>, t: Throwable) {
                lottieProgressDialog!!.cancelDialog()
                Toast.makeText(activity,"Error",Toast.LENGTH_SHORT).show()
                Log.e("tag", " =  = = =error = ==  " + t.message)
            }
        })
    }

    fun getToken(url : String){
        ServiceGenerator.nentoApi.getNotificationToken(url,Constants.apiKey)!!.enqueue(object :
            Callback<String?> {
            @SuppressLint("NewApi", "ResourceAsColor")
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onResponse(call: Call<String?>, response: Response<String?>) {
                if (response.isSuccessful) {
                    val jobj = JSONObject(response.body())
                    val jsonArray = jobj.getJSONArray("result")
                    val tokenObj : JSONObject = jsonArray.getJSONObject(0)
                    val token : String = tokenObj.getString("token")
                    val app : String = tokenObj.getString("app")
                    val user_token : String = tokenObj.getString("user_token")

                } else {
                    lottieProgressDialog!!.cancelDialog()
                    Toast.makeText(activity,"Wrong detail",Toast.LENGTH_SHORT).show()
                    Log.e("tag", " =  = = =error = ==  " + response.message())
                }
            }

            override fun onFailure(call: Call<String?>, t: Throwable) {
                lottieProgressDialog!!.cancelDialog()
                Toast.makeText(activity,"Error",Toast.LENGTH_SHORT).show()
                Log.e("tag", " =  = = =error = ==  " + t.message)
            }
        })
    }

    private fun getTokenFirebase() {
        FirebaseMessaging.getInstance().token.addOnSuccessListener { token ->
            Log.d("AdminLogin", "token : $token")
//            firebaseToken = token
//            MainActivity.sharedPreferences.edit().apply {
//                putString(SharedPreferencesKeys.firebase_token, token)
//            }.apply()
        }
    }






}
