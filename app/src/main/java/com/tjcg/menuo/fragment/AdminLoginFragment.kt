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
import android.view.MotionEvent
import androidx.annotation.RequiresApi
import com.google.android.gms.tasks.*
import com.tjcg.menuo.Expandablectivity
import com.tjcg.menuo.ForgetPasswordActivity
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
import android.text.method.PasswordTransformationMethod

import android.text.method.HideReturnsTransformationMethod

import com.google.firebase.messaging.FirebaseMessagingService
import com.tjcg.menuo.data.response.order.OnlineOrderRS
import android.bluetooth.BluetoothAdapter
import android.content.ContentResolver
import com.tjcg.menuo.data.response.IntermediatorServerAPI.IntermediatorLogin


class AdminLoginFragment : Fragment() {

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
        binding!!.passwordEt.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                when (event?.action) {
                    MotionEvent.ACTION_DOWN ->{
                        binding!!.passwordEt.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    }
                    MotionEvent.ACTION_UP->{
                        binding!!.passwordEt.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    }
                }
                return v?.onTouchEvent(event) ?: true
            }
        })
        binding!!.textView4.setOnClickListener {
            startActivity(Intent(loginActivity, ForgetPasswordActivity::class.java))
        }
        binding!!.adminLogin.setOnClickListener {

            email = binding!!.emailEt.text.toString()
            var password = binding!!.passwordEt.text.toString()
            Login(email,password)
        }
        return binding!!.root
    }
    companion object {
        @JvmStatic
        fun newInstance(): AdminLoginFragment {
            return AdminLoginFragment()
        }
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
                    var owner_id =response.body()!!.result.id
                    Constants.bearrToken=accessToken
                    prefManager!!.setString("auth_token", accessToken)
                    prefManager!!.setString("owner_id", owner_id.toString())
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
//                    Toast.makeText(activity,"siccess",Toast.LENGTH_SHORT).show()
                    var orderResult : String =response.body()!!.toString()
                    orderResult

//                    var jobj = JSONObject(orderResult)
//                    jobj
                    prefManager!!.setBoolean(SharedPreferencesKeys.isFromLogin, true)
                    var i = Intent(context,Expandablectivity::class.java)
                    i.putExtra("odata",orderResult)
                    i.putExtra(SharedPreferencesKeys.isDBLoadRequired,true)
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
                    var owner_id_pref : String = prefManager!!.getString("owner_id").toString()
                    var orderResult : String =response.body()!!.toString()
                    val jobj = JSONObject(orderResult)
                    val jsnResult : JSONArray = jobj.getJSONArray("result")!!
                    for (result in 0..jsnResult.length()-1) {
                        var jsonUser : JSONObject = jsnResult.getJSONObject(result)
                        var emailNew =  jsonUser.getString("email")
                        var owner_id_api =  jsonUser.getString("owner_id")
                        if(emailNew.equals(email) && owner_id_pref.equals(owner_id_api)){
                            var businessOwnerNAme = jsonUser.getString("name");
                            var businessNAme = jsonUser.getString("name");
                            businessId = jsonUser.getString("id")
                            prefManager!!.setString(SharedPreferencesKeys.businessName, businessNAme)
                            prefManager!!.setString(SharedPreferencesKeys.businessOwner, businessOwnerNAme)
                        }
                    }


                    //lottieProgressDialog!!.cancelDialog()
                    prefManager!!.setBoolean("isLogin", true)
                    prefManager!!.setString("businessID", businessId.toString())

                    //PASS TOKEN
                    var getTokenURl : String = Constants.BUSINESS_URL+businessId+ Constants.NOTIFICATION_ENDPOINTS
                    Toast.makeText(context,"sending tocken",Toast.LENGTH_LONG).show()
                    sendTokenAtLogin(businessId);

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
        }
    }

    fun sendTokenAtLogin(businessId: String) {

        val sharedPref = requireContext().getSharedPreferences("com.tjcg.nentopos", FirebaseMessagingService.MODE_PRIVATE)
        val push_token= sharedPref.getString(SharedPreferencesKeys.device_token,null)
        val device_name=getPhoneName()
        val platform = "android"
        val deviceID = Settings.Secure.getString(requireContext().getContentResolver(), Settings.Secure.ANDROID_ID);

        ServiceGenerator.nentoApiIntermediator.loginAtIntermediateServer(businessId,device_name,platform,deviceID,push_token).enqueue(object : Callback<IntermediatorLogin?> {
            @SuppressLint("NewApi", "ResourceAsColor")
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onResponse(call: Call<IntermediatorLogin?>, response: Response<IntermediatorLogin?>) {
                if (response.isSuccessful) {
                    if (response.body() != null && response.body()!!.status) {
                        Toast.makeText(activity,"token sent",Toast.LENGTH_LONG).show()
                        lottieProgressDialog!!.cancelDialog()
                        var i = Intent(context,Expandablectivity::class.java)
                        i.putExtra("businessID",businessId)
                        i.putExtra(SharedPreferencesKeys.isDBLoadRequired,true)
                        startActivity(i)

                    }
                } else {
                    lottieProgressDialog!!.cancelDialog()
                    Log.e("tag", " =  = = =error12 = ==  " + response.message())
                }
            }

            override fun onFailure(call: Call<IntermediatorLogin?>, t: Throwable) {
                lottieProgressDialog!!.cancelDialog()

                Log.e("tag", " =  = = =error 3= ==  " + t.message)
            }
        })
    }

    fun getPhoneName(): String? {
        val deviceModel = Build.MODEL
        return deviceModel
    }
}
