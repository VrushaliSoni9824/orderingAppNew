package com.tjcg.menuo.fragment

import android.annotation.SuppressLint
import com.tjcg.menuo.activity.LoginActivity
import com.tjcg.menuo.data.local.AppDatabase
import android.content.Context
import android.os.Bundle
import android.content.Intent
import android.util.Log
import androidx.fragment.app.Fragment
import com.tjcg.menuo.databinding.FragmentAdminLoginBinding

import android.os.Build
import android.provider.Settings
import com.google.firebase.messaging.FirebaseMessaging
import androidx.annotation.RequiresApi
import com.tjcg.menuo.Expandablectivity
import com.tjcg.menuo.ForgetPasswordActivity
import com.tjcg.menuo.data.remote.ServiceGenerator
import com.tjcg.menuo.data.response.LoginNew.LoginRs
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.text.method.PasswordTransformationMethod

import android.text.method.HideReturnsTransformationMethod
import cn.pedant.SweetAlert.SweetAlertDialog

import com.google.firebase.messaging.FirebaseMessagingService
import com.tjcg.menuo.R
import com.tjcg.menuo.data.response.IntermediatorServerAPI.IntermediatorLogin
import java.util.ArrayList
//import android.R
import android.app.Dialog
import android.content.BroadcastReceiver
import android.content.IntentFilter
import android.graphics.Color
import android.view.*
import android.widget.*
import androidx.core.content.ContextCompat
import com.tjcg.menuo.OrderPreviewActivity
import com.tjcg.menuo.adapter.chooseBusinessKt
//import com.tjcg.menuo.adapter.ChooseBusinessListAdapter
import com.tjcg.menuo.data.response.BusinessList.BusinessItem
import com.tjcg.menuo.data.response.GETAPIKEY.getAPIKEY
import com.tjcg.menuo.utils.*
import java.lang.Exception


class AdminLoginFragment : Fragment() {

    var binding: FragmentAdminLoginBinding? = null
    private lateinit var loginActivity: LoginActivity
    var db: AppDatabase? = null
    var prefManager: PrefManager? = null
    private var lottieProgressDialog: LottieProgressDialog? = null
    var email : String = ""
    lateinit var dialog : Dialog
    var businessId : String = ""
    var mIntentFilter: IntentFilter? = null

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
        dialog = Dialog(requireActivity().applicationContext)
        mIntentFilter = IntentFilter()
        mIntentFilter!!.addAction("businessChoosed")
        requireContext().registerReceiver(reMyreceive, mIntentFilter)
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
                    var owner_name =response.body()!!.result.name
                    Constants.bearrToken=accessToken
                    prefManager!!.setString("auth_token", accessToken)
                    prefManager!!.setString("owner_id", owner_id.toString())
                    prefManager!!.setString(SharedPreferencesKeys.businessOwner, owner_name.toString())

                    getAPIKEY(owner_id.toString())
//                    getUsers(Constants.apiKey)
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
    fun getAPIKEY(owner_id: String) {

        ServiceGenerator.nentoApi2.getAPIKEY(owner_id).enqueue(object :
            Callback<getAPIKEY?> {
            @SuppressLint("NewApi", "ResourceAsColor")
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onResponse(call: Call<getAPIKEY?>, response: Response<getAPIKEY?>) {
                if (response.isSuccessful) {
//                    Toast.makeText(activity,"siccess",Toast.LENGTH_SHORT).show()
                    var SubUSERAPIKEy=response.body()!!.result.key
                    prefManager!!.setString("SubUSERAPIKEy", SubUSERAPIKEy)
                    getUsers(SubUSERAPIKEy)
                } else {
                    lottieProgressDialog!!.cancelDialog()
                    Toast.makeText(activity,"Wrong detail",Toast.LENGTH_SHORT).show()
                    Log.e("tag", " =  = = =error = ==  " + response.message())
                }
            }

            override fun onFailure(call: Call<getAPIKEY?>, t: Throwable) {
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
                    activity!!.finish()

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
//        val arrId = ArrayList<String>()
//        val arrName = ArrayList<String>()
//        val arrEmail = ArrayList<String>()
//        val arrOwner_id = ArrayList<String>()
        val arrBusinessItem = ArrayList<BusinessItem>()

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
                        var id =  jsonUser.getString("id")
                        var emailNew =  jsonUser.getString("email")
//                        var owner_id_api =  jsonUser.getString("owner_id")
                        var businessName =  jsonUser.getString("name")
                        var bItem = BusinessItem(id,businessName,email)
                        arrBusinessItem.add(bItem)
//                        if(emailNew.equals(email) && businessName.equals("Menuo Demo") && owner_id_pref.equals(owner_id_api)){
//                        if(emailNew.equals(email) && owner_id_pref.equals(owner_id_api)){
//                            var bItem = BusinessItem(id,businessName,owner_id_api,email)
//                            arrBusinessItem.add(bItem)
//                        }
                    }
                    if(arrBusinessItem.size>1){
                        dialog = Dialog(requireContext())
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                        dialog.setCancelable(false)
                        dialog.setContentView(com.tjcg.menuo.R.layout.choose_business_dialog)
                        val rvBusinessList = dialog.findViewById(com.tjcg.menuo.R.id.rvBusiness) as ListView
                        val imageViewCancel = dialog.findViewById(com.tjcg.menuo.R.id.imageViewCancel) as ImageView
                        val ChooseBusinessListAdapter = chooseBusinessKt(arrBusinessItem,requireContext())
                        rvBusinessList.adapter = ChooseBusinessListAdapter
                        rvBusinessList.setOnItemClickListener(){adapterView, view, position, id ->
                            rvBusinessList.isEnabled= false
                            for (i in 0 until rvBusinessList.getChildCount()) {
                                if (position === i) {
                                    rvBusinessList.getChildAt(i).setBackgroundColor(getResources().getColor(R.color.orange))
                                    var textViewBusinessName = view!!.findViewById<View>(R.id.textViewBusinessName) as TextView
                                    textViewBusinessName.setTextColor(Color.WHITE)
                                } else {
                                    rvBusinessList.getChildAt(i).setBackgroundColor(Color.TRANSPARENT)
                                    var textViewBusinessName = view!!.findViewById<View>(R.id.textViewBusinessName) as TextView
//                                    textViewBusinessName.setTextColor(ContextCompat.getColor(requireContext(), R.color.orange))
                                }
                            }
                            prefManager!!.setString(SharedPreferencesKeys.businessName, arrBusinessItem.get(position).itemName)
                            prefManager!!.setBoolean("isLogin", true)
                            prefManager!!.setString("businessID", arrBusinessItem.get(position).id)
                            var businessID=prefManager!!.getString("businessID")!!
                            lottieProgressDialog!!.showDialog()

                            sendTokenAtLogin(businessID);
                        }
                        imageViewCancel.setOnClickListener {
                            lottieProgressDialog!!.cancelDialog()
                            dialog.cancel()
                        }
                        dialog.show()

                    }else if(arrBusinessItem.size==1){

//                        var businessOwnerNAme = arrBusinessItem.get(0).owner_id

                        var businessNAme = arrBusinessItem.get(0).itemName
                        businessId = arrBusinessItem.get(0).id
                        prefManager!!.setString("businessID", businessId.toString())
                        prefManager!!.setBoolean("isLogin", true)
                        prefManager!!.setString(SharedPreferencesKeys.businessName, businessNAme)
//                        prefManager!!.setString(SharedPreferencesKeys.businessOwner, businessOwnerNAme)
                        sendTokenAtLogin(prefManager!!.getString("businessID")!!);

                    }else{
                        val sweetAlertDialog = SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                        sweetAlertDialog.setCanceledOnTouchOutside(false)
                        sweetAlertDialog.setCancelable(false)
                        sweetAlertDialog.contentText = resources.getString(R.string.lbl_noBusinessFound) //sweetAlertDialog.contentTextSize = resources.getDimension(R.dimen._7ssp).roundToInt(
                        sweetAlertDialog.confirmText = resources.getString(R.string.lbl_OK)
                        sweetAlertDialog.confirmButtonBackgroundColor = resources.getColor(R.color.green)
                        sweetAlertDialog.showCancelButton(false)
                        sweetAlertDialog.setConfirmClickListener { sDialog ->
                            sDialog.cancel()
                        }.show()

                    }


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
                        activity!!.finish()

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

    private val reMyreceive: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            try {
                if (intent.action == "businessChoosed") {
                    var businessId = prefManager!!.getString("businessID")!!
                    sendTokenAtLogin(businessId);
                }
            } catch (e: Exception) {
                Log.e("Error BR", e.message.toString())
            }
        }
    }
}
