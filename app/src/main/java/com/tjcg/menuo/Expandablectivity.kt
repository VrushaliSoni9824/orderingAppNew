
package com.tjcg.menuo
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.*
import android.database.Cursor
import android.media.MediaPlayer
import android.net.ConnectivityManager
import android.net.Uri
import android.os.*
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import cn.pedant.SweetAlert.SweetAlertDialog
import com.google.firebase.messaging.FirebaseMessagingService
import com.tjcg.MainApp
import com.tjcg.menuo.ExpandableList.CustomizedExpandableListAdapter2
import com.tjcg.menuo.ExpandableList.ExpandableListDataItems
import com.tjcg.menuo.activity.LoginActivity
import com.tjcg.menuo.data.local.AppDatabase
import com.tjcg.menuo.data.local.OrderDao
import com.tjcg.menuo.data.remote.ServiceGenerator
import com.tjcg.menuo.data.response.EntitiesModel.*
import com.tjcg.menuo.data.response.IntermediatorServerAPI.IntermediatorLogin
import com.tjcg.menuo.data.response.IntermediatorServerAPI.IntermediatorLogout
import com.tjcg.menuo.data.response.newOrder.DialogQueue
import com.tjcg.menuo.data.response.newOrder.Result
import com.tjcg.menuo.dialog.NewOrderDialog
import com.tjcg.menuo.service.SocketIOController
import com.tjcg.menuo.utils.*
import io.sentry.Sentry
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import woyou.aidlservice.jiuiv5.IWoyouService
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.system.exitProcess
import android.content.BroadcastReceiver
import com.tjcg.menuo.service.NetworkChangeReceiver
import android.content.IntentFilter

import android.os.Build
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import java.lang.IllegalArgumentException

class Expandablectivity : AppCompatActivity(),NewOrderDialog.ClickListener {
    lateinit var dialog : Dialog
    lateinit var dialogOrderPreview : Dialog
    var expandableListViewExample: ExpandableListView? = null
    var expandableListAdapter: ExpandableListAdapter? = null
    var expandableTitleList: List<String> = emptyList()
    var expandableDetailList: HashMap<String, List<String>>? = null
    private var mNetworkReceiver: BroadcastReceiver? = null
    private var woyouService: IWoyouService? = null
    private var lottieProgressDialog: LottieProgressDialog? = null
    private var prefManager: PrefManager? = null
    lateinit var orderResult: String
    var orderDao: OrderDao? = null
    var count: Int = 0
    private var mDrawerToggle: ActionBarDrawerToggle? = null
    private var toolbar_title: Toolbar? = null
    var drawerLayout_Dashboard: DrawerLayout? = null
    var lnavigation_online_order: LinearLayout? = null
    var lnavigation_done_order: LinearLayout? = null
    val paginationLink = ArrayList<String>()
    val paginationLinkNewOrder = ArrayList<String>()
    val arrNewOrderId = ArrayList<Int>()
    var businessId: String = ""
    var isDBLoadRequired: Boolean = false
    var linkCount: Int = 0
    var linkCountNewORder: Int = 0
    lateinit var textViewOwnerName : TextView
    lateinit var textViewBusiness : Switch
    lateinit var imgRefresh : ImageView
    lateinit var imgSync : ImageView
    var NewOrderId : String? = null
    var arrlength : Int = 0
    lateinit var mplayer: MediaPlayer
    var mIntentFilter: IntentFilter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            System.gc();
            setContentView(R.layout.activity_expandablectivity)
        } catch (e: Exception) {
            Toast.makeText(applicationContext,"OM testing error",Toast.LENGTH_LONG).show()
        }
        mNetworkReceiver = NetworkChangeReceiver()
        registerNetworkBroadcastForNougat()
        NewOrderId="null"
        mIntentFilter = IntentFilter()
        mIntentFilter!!.addAction("new_order")
        mIntentFilter!!.addAction("status_changed")
        mIntentFilter!!.addAction("refreshLocal")
        registerReceiver(reMyreceive, mIntentFilter)
        dialog = Dialog(this@Expandablectivity)
        dialogOrderPreview  = Dialog(this@Expandablectivity)
        lottieProgressDialog = LottieProgressDialog(this)
        prefManager = PrefManager(this)
        toolbar_title = findViewById<Toolbar>(R.id.toolbar_title)
        drawerLayout_Dashboard = findViewById(R.id.drawerLayout_Dashboard)
        setSupportActionBar(toolbar_title)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        textViewOwnerName = findViewById<TextView>(R.id.textViewOwnerName)
        textViewBusiness = findViewById<Switch>(R.id.switchBusiness)
        imgRefresh = findViewById<ImageView>(R.id.imageViewRefresh)
        imgSync = findViewById<ImageView>(R.id.imageViewSync)
        mplayer = MediaPlayer.create(applicationContext, R.raw.alarmtone);
        textViewOwnerName.text = if (!prefManager!!.getString(SharedPreferencesKeys.businessOwner).isNullOrEmpty()) prefManager!!.getString(SharedPreferencesKeys.businessOwner) else ""
        textViewBusiness.text = if (!prefManager!!.getString(SharedPreferencesKeys.businessName).isNullOrEmpty()) prefManager!!.getString(SharedPreferencesKeys.businessName) else ""
        orderDao = AppDatabase.getDatabase(this)!!.orderDao()
        lnavigation_online_order = findViewById(R.id.navigation_logout)
        lnavigation_done_order = findViewById(R.id.navigation_online_order)
        manageQue()
        var i: Intent = intent
        businessId = i.getStringExtra("businessID").toString()
        isDBLoadRequired = i.getBooleanExtra(SharedPreferencesKeys.isDBLoadRequired,false)
        imgSync.setOnClickListener {
            syncDatabase()
        }
        imgRefresh!!.setOnClickListener {
            prefManager!!.setBoolean(SharedPreferencesKeys.isFromLogin,false)
            getDataBase()
        }

        mDrawerToggle = object : ActionBarDrawerToggle(this, drawerLayout_Dashboard, toolbar_title, R.string.drawer_open, R.string.drawer_close) {
            override fun onDrawerOpened(drawerView: View) {
                super.onDrawerOpened(drawerView)
            }

            override fun onDrawerClosed(drawerView: View) {
                super.onDrawerClosed(drawerView)
            }
        }
        drawerLayout_Dashboard!!.setDrawerListener(mDrawerToggle)

        drawerLayout_Dashboard!!.post(Runnable { (mDrawerToggle as ActionBarDrawerToggle).syncState() })

        lnavigation_online_order!!.setOnClickListener {
            logout()
        }
        lnavigation_done_order!!.setOnClickListener {
            startActivity(Intent(this, OrderCompleteActivity::class.java))
            finish()
        }
        expandableListViewExample =
                findViewById<View>(R.id.expandableListViewSample) as ExpandableListView

        if(isDBLoadRequired) {
            lottieProgressDialog!!.showDialog()
            Sentry.captureMessage("GetDatabase() load")
            getDataBase()
        }else{
            Sentry.captureMessage("refreshFromLocalrefreshFromLocal() load")
            refreshFromLocal()
        }

        // This method is called when the group is expanded
        expandableListViewExample!!.setOnGroupExpandListener { groupPosition ->

        }

//         This method is called when the group is collapsed
        expandableListViewExample!!.setOnGroupCollapseListener { groupPosition ->
        }

        expandableListViewExample!!.setOnChildClickListener { parent, v, groupPosition, childPosition, id ->

            var orderId: String = expandableDetailList!![expandableTitleList!!.get(groupPosition)]!![childPosition].toString()

            var singleOrderURL = "http://apiv4.ordering.co/v400/en/menuo/orders/" + orderId + "?mode=dashboard"
            getOrderDetail(singleOrderURL, Constants.apiKey, orderId)

            false
        }

    }

    override fun onResume() {
        super.onResume()
        refreshFromLocal()
        manageQue()

    }
    private val connService: ServiceConnection = object : ServiceConnection {
        override fun onServiceDisconnected(name: ComponentName) {
            woyouService = null
        }

        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            woyouService = IWoyouService.Stub.asInterface(service)
        }
    }

    //
    fun testSunmiPrint(context: Context) {
        try {
            if (woyouService != null) {
                printerConfigure(context)
                woyouService!!.printText("=======================\n\n", null)
                woyouService!!.printText("\n", null)
                woyouService!!.cutPaper(null)
            }
        } catch (ex: RemoteException) {

        }
    }

    //
    private fun printerConfigure(context: Context) {
        if (woyouService != null) {
            woyouService!!.setFontSize(30f, null)
            woyouService!!.printText("Hello", null)
        }
    }

    fun connectPrinter(context: Context) {
        val intent = Intent()
        intent.setPackage(Constants.SERVICE_PACKAGE)
        intent.action = Constants.SERVICE_ACTION
        context.applicationContext.startService(intent)
        context.applicationContext.bindService(intent, connService, Context.BIND_AUTO_CREATE)
    }



    fun getOrders(url: String, key: String, isFromNewOrder : Boolean = false) {
        var responceString: String = ""

        ServiceGenerator.nentoApi.getUsers(url, key)!!.enqueue(object :
                Callback<String?> {
            @SuppressLint("NewApi", "ResourceAsColor")
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onResponse(call: Call<String?>, response: Response<String?>) {
                if (response.isSuccessful) {
                    count = count + 1
                    responceString = response.body().toString()

                    if(isFromNewOrder)
                        parseOrderDataJson(responceString, true)
                    else
                        //demo
                        parseOrderDataJson(responceString)
                } else {
                    if(!isFromNewOrder) lottieProgressDialog!!.cancelDialog()
                    responceString = "error"
                    Toast.makeText(applicationContext, "Wrong detail", Toast.LENGTH_SHORT).show()
//                    Toast.makeText(mainActivity,"Error while fetching invoice detail..",Toast.LENGTH_SHORT).show()
                    Log.e("tag", " =  = = =error = ==  " + response.message())
                }
            }

            override fun onFailure(call: Call<String?>, t: Throwable) {
                if(!isFromNewOrder) lottieProgressDialog!!.cancelDialog()
                Log.e("tag", " =  = = =error = ==  " + url.toString())
                getOrders(url, Constants.apiKey, true)
                responceString = "error"
//                Toast.makeText(applicationContext, "Error", Toast.LENGTH_SHORT).show()

            }
        })
    }

    fun getOrdersNewOrder(url: String, key: String) {
        var responceString: String = ""
        ServiceGenerator.nentoApi.getUsers(url, key)!!.enqueue(object :
            Callback<String?> {
            @SuppressLint("NewApi", "ResourceAsColor")
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onResponse(call: Call<String?>, response: Response<String?>) {
                if (response.isSuccessful) {
                    count = count + 1
                    responceString = response.body().toString()
                    parseOrderDataJsonNewORder(responceString)
                } else {
//                    lottieProgressDialog!!.cancelDialog()
                    responceString = "error"
                    Toast.makeText(applicationContext, "Wrong detail", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<String?>, t: Throwable) {
//                lottieProgressDialog!!.cancelDialog()
                getOrdersNewOrder(url, Constants.apiKey)
                responceString = "error"
            }
        })
    }
    fun getPaginationInfor(url: String, key: String, isFromNewOrder: Boolean = false) {
        if(!isFromNewOrder)lottieProgressDialog!!.showDialog()
        var responceString: String = ""

        ServiceGenerator.nentoApi.getUsers(url, key)!!.enqueue(object :
                Callback<String?> {
            @SuppressLint("NewApi", "ResourceAsColor")
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onResponse(call: Call<String?>, response: Response<String?>) {
                if (response.isSuccessful) {
                 orderResult = response.body()!!.toString()
                    if(isFromNewOrder) parsePaginationInfoJson(orderResult, true, true) else parsePaginationInfoJson(orderResult, true)
                } else {
                    if(!isFromNewOrder) lottieProgressDialog!!.cancelDialog()
                    responceString = "error"
                    Toast.makeText(applicationContext, "Wrong detail", Toast.LENGTH_SHORT).show()
//                    Toast.makeText(mainActivity,"Error while fetching invoice detail..",Toast.LENGTH_SHORT).show()
                    Log.e("tag", " =  = = =error = ==  " + response.message())
                    getPaginationInfor(url,key,isFromNewOrder)
                }
            }

            override fun onFailure(call: Call<String?>, t: Throwable) {
                if(!isFromNewOrder) lottieProgressDialog!!.cancelDialog()
                getOrders(url, Constants.apiKey)
                responceString = "error"
                Toast.makeText(applicationContext, "Error", Toast.LENGTH_SHORT).show()
                Log.e("tag", " =  = = =error = ==  " + t.message)
            }
        })
    }


    fun getOrderDetail(url: String, key: String, orderId: String) {
        lottieProgressDialog!!.showDialog()
        var responceString: String = ""

        ServiceGenerator.nentoApi.getUsers(url, key)!!.enqueue(object :
                Callback<String?> {
            @SuppressLint("NewApi", "ResourceAsColor")
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onResponse(call: Call<String?>, response: Response<String?>) {
                if (response.isSuccessful) {
                    lottieProgressDialog!!.cancelDialog()
                    var singleOrderResponce = response.body()!!.toString()
                    startActivity(Intent(applicationContext, OrderDetailActivity::class.java).putExtra("orderId", orderId).putExtra("orderData", singleOrderResponce))

                } else {
                    lottieProgressDialog!!.cancelDialog()
                    responceString = "error"
                    Toast.makeText(applicationContext, "Wrong detail", Toast.LENGTH_SHORT).show()
//                    Toast.makeText(mainActivity,"Error while fetching invoice detail..",Toast.LENGTH_SHORT).show()
                    Log.e("tag", " =  = = =error = ==  " + response.message())
                }
            }

            override fun onFailure(call: Call<String?>, t: Throwable) {
                lottieProgressDialog!!.cancelDialog()
//                getOrders(url,Constants.apiKey)
                responceString = "error"
                Toast.makeText(applicationContext, "Error", Toast.LENGTH_SHORT).show()
                getOrderDetail(url,key,orderId)
                Log.e("tag", " =  = = =error = ==  " + t.message)
            }
        })
    }


    fun logout() {
        val sweetAlertDialog = SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
        sweetAlertDialog.setCanceledOnTouchOutside(false)
        sweetAlertDialog.setCancelable(false)
        sweetAlertDialog.contentText = resources.getString(R.string.lbl_WantToLogOut) //sweetAlertDialog.contentTextSize = resources.getDimension(R.dimen._7ssp).roundToInt()
        sweetAlertDialog.cancelText = resources.getString(R.string.lbl_NO)
        sweetAlertDialog.confirmText = resources.getString(R.string.lbl_YES)
        sweetAlertDialog.confirmButtonBackgroundColor = resources.getColor(R.color.green)
        sweetAlertDialog.cancelButtonBackgroundColor = resources.getColor(R.color.red)
        sweetAlertDialog.showCancelButton(true)
        sweetAlertDialog.setCancelClickListener { sDialog ->
            sDialog.titleText = resources.getString(R.string.lbl_Cancelled)
            sDialog.contentText = resources.getString(R.string.lbl_StillLoggedIn)
            sDialog.confirmText = resources.getString(R.string.dialog_ok)
            sDialog.showCancelButton(false).setCancelClickListener { null }.setConfirmClickListener { sweetAlertDialog.dismissWithAnimation() }.changeAlertType(SweetAlertDialog.ERROR_TYPE)

        }
        sweetAlertDialog.setConfirmClickListener { sDialog ->
            DeleteDataDbAsyncTask().execute()
            sDialog.titleText = resources.getString(R.string.lbl_LoggedOut)
            sDialog.confirmText = resources.getString(R.string.dialog_ok)
            sDialog.showCancelButton(false).setCancelClickListener { null }.setConfirmClickListener { sweetAlertDialog1 ->
                sweetAlertDialog1.dismissWithAnimation() //val intent = Intent(this, LoginActivity::class.java)
                removeTokenAtLogout(businessId)
            }.changeAlertType(SweetAlertDialog.SUCCESS_TYPE)
        }.show()
    }

    inner class DeleteDataDbAsyncTask : CoroutineAsyncTask<Void, Void, Void>() {
        override fun doInBackground(vararg params: Void?): Void? {
            try {
                if (MainApp.mainApp != null) {
                    if (MainApp.mainApp!!.getDao() != null){
                        MainApp.mainApp!!.getDao()!!.deleteTblResult()
                        MainApp.mainApp!!.getDao()!!.deleteTblBisiness()
                        MainApp.mainApp!!.getDao()!!.deleteTblCity()
                        MainApp.mainApp!!.getDao()!!.deleteTblCustomer()
                        MainApp.mainApp!!.getDao()!!.deleteTblDriver()
                        MainApp.mainApp!!.getDao()!!.deleteTblDriverData()
                        MainApp.mainApp!!.getDao()!!.deleteTblHistory()
                        MainApp.mainApp!!.getDao()!!.deleteTblKitchenItemList()
                        MainApp.mainApp!!.getDao()!!.deleteTblKitchenSelectedModifier()
                        MainApp.mainApp!!.getDao()!!.deleteTblKitchenOrderData()
                        MainApp.mainApp!!.getDao()!!.deleteTblKitchenOrderInfo()
                        MainApp.mainApp!!.getDao()!!.deleteTblLocation()
                        MainApp.mainApp!!.getDao()!!.deleteTblMetafield()
                        MainApp.mainApp!!.getDao()!!.deleteTblOngoingOrderData()
                        MainApp.mainApp!!.getDao()!!.deleteTblOnlineOrderData()
                        MainApp.mainApp!!.getDao()!!.deleteTblOutletsRS()
//                        MainApp.mainApp!!.getDao()!!.deleteTblPaymentMethod()
                        MainApp.mainApp!!.getDao()!!.deleteTblPaymethod()
                        MainApp.mainApp!!.getDao()!!.deleteTblProduct()
                        MainApp.mainApp!!.getDao()!!.deleteTblReview()
                        MainApp.mainApp!!.getDao()!!.deleteTblUserDetails()
                        MainApp.mainApp!!.getDao()!!.deleteTblUserPermissions()
                    }
                }
            }catch (ex: Exception){}
            return null
        }
    }

    fun parseOrderDataJson(jsonData: String,isFromNewOrder : Boolean = false) {

        try {
            val jobj = JSONObject(jsonData)
            val jarrResult: JSONArray = jobj.getJSONArray("result")
            //            for (order:jarrResult) {
//
//            }
            var resultList: ArrayList<Result>? = arrayListOf()
            val bisinessTblList: ArrayList<BisinessEntity> = ArrayList()
            val cityTblList: ArrayList<CityEntity> = ArrayList()
            val customerTblList: ArrayList<CustomerEntity> = ArrayList()
            val driverTblList: ArrayList<DriverEntity> = ArrayList()
            val historyTblList: ArrayList<HistoryEntity> = ArrayList()
            val locationTblList: ArrayList<LocationEntity> = ArrayList()
            val metafieldTblList: ArrayList<MetafieldEntity> = ArrayList()
            val paymethodTblList: ArrayList<PaymethodEntity> = ArrayList()
            val productTblList: ArrayList<ProductEntity> = ArrayList()
            val reviewTblList: ArrayList<ReviewEntity> = ArrayList()
            val summaryTblList: ArrayList<SummaryEntity> = ArrayList()

//            var latein resultList : List<Result>

            for (i in 0..jarrResult.length() - 1) {
                val order = jarrResult.getJSONObject(i)

                var resObj: Result = Result()
                var orderId = order.getString("id");
                //  //result
                resObj!!.id = order.getString("id").toInt()
                resObj!!.app_id = order.getString("app_id")
                resObj!!.business_id = order.getString("business_id")
                resObj!!.cash = order.getString("cash")
                resObj!!.comment = order.getString("comment")
                resObj!!.coupon = order.getString("coupon")
                resObj!!.created_at = order.getString("created_at")
                //fetch customer
                resObj!!.customer_id = order.getString("customer_id")
                resObj!!.delivered_in = order.getString("delivered_in")
                resObj!!.delivery_datetime = order.getString("delivery_datetime")
                resObj!!.delivery_datetime_utc = order.getString("delivery_datetime_utc")
                resObj!!.delivery_type = order.getString("delivery_type")
                resObj!!.delivery_zone_id = order.getString("delivery_zone_id")
                resObj!!.delivery_zone_price = order.getString("delivery_zone_price")
                resObj!!.discount = order.getString("discount")
                resObj!!.driver = order.getString("driver")
                resObj!!.driver_compString_id = "0"
                resObj!!.driver_id = order.getString("driver_id")
                resObj!!.driver_tip = order.getString("driver_tip")
                resObj!!.expired_at = order.getString("expired_at")
                resObj!!.external_driver = order.getString("external_driver")
                resObj!!.external_driver_id = order.getString("external_driver_id")
                //load histor
                resObj!!.language_id = order.getString("language_id")
                resObj!!.last_direct_message_at = order.getString("last_direct_message_at")
                resObj!!.last_driver_assigned_at = order.getString("last_driver_assigned_at")
                resObj!!.last_general_message_at = order.getString("last_general_message_at")
                resObj!!.last_logistic_attempted_at = order.getString("last_logistic_attempted_at")
                resObj!!.last_message_at = order.getString("last_message_at")
                resObj!!.locked = order.getString("locked")
                resObj!!.logistic_attemps = order.getString("logistic_attemps")
                resObj!!.logistic_status = order.getString("logistic_status")
                //load meta fiels
                resObj!!.offer = order.getString("offer")
                resObj!!.offer_id = order.getString("offer_id")
                resObj!!.offer_rate = order.getString("offer_rate")
                resObj!!.offer_type = order.getString("offer_type")
                resObj!!.order_group = order.getString("order_group")
                resObj!!.order_group_id = order.getString("order_group_id")
                resObj!!.pay_data = order.getString("pay_data")

                resObj!!.paymethod_id = order.getString("paymethod_id")
                resObj!!.place = order.getString("place")
                resObj!!.place_id = order.getString("place_id")
                resObj!!.prepared_in = order.getString("prepared_in")
                resObj!!.priority = order.getString("priority")
//                paymethod load
//                product

                resObj!!.refund_data = order.getString("refund_data")
                resObj!!.resolved_at = order.getString("resolved_at")
                resObj!!.review = order.getString("review")
                resObj!!.service_fee = order.getString("service_fee")
                resObj!!.status = order.getString("status")
                //    this.summary = orderResultSummary,
                resObj!!.summary_version = order.getString("summary_version")
                resObj!!.tax = order.getString("tax")
                resObj!!.tax_type = order.getString("tax_type")
                resObj!!.to_print = order.getString("to_print")
                resObj!!.unread_count = order.getString("unread_count")
                resObj!!.unread_direct_count = order.getString("unread_direct_count")
                resObj!!.unread_general_count = order.getString("unread_general_count")

                resObj!!.updated_at = order.getString("updated_at")
                resObj!!.user_review = order.getString("user_review")
                resObj!!.uuid = order.getString("uuid")

                resultList!!.add(resObj)


                val buisnessJson = order.getJSONObject("business")
                val batchDetailsEntity = BisinessEntity(
                    buisnessJson.getInt("id"),
                    buisnessJson.getInt("order_id"),
                    buisnessJson.getString("name"),
                    buisnessJson.getString("logo"),
                    buisnessJson.getString("email"),
                    buisnessJson.getInt("city_id"),
                    buisnessJson.getString("address"),
                    buisnessJson.getString("address_notes"),
                    buisnessJson.getString("zipcode"),
                    buisnessJson.getString("cellphone"),
                    buisnessJson.getString("phone"),
                    buisnessJson.getString("location"),
                    buisnessJson.getString("header"),
                    buisnessJson.getString("pickup_time"),
                    buisnessJson.getString("delivery_time"),
                    buisnessJson.getString("city")
                )
                bisinessTblList.add(batchDetailsEntity)

                val cityJson = buisnessJson.getJSONObject("city")
                val cityDetailsEntity = CityEntity(
                    cityJson.getInt("id"),
                    cityJson.getString("name"),
                    cityJson.getInt("country_id"),
                    cityJson.getInt("administrator_id"),
                    cityJson.getBoolean("enabled"),
                    orderId.toInt()
                )
                cityTblList.add(cityDetailsEntity)

                val customerObj = order.getJSONObject("customer")
                val customerDetailsEntity = CustomerEntity(
                    customerObj.getInt("id"),
                    customerObj.getInt("order_id"),
                    customerObj.getString("name"),
                    customerObj.getString("photo"),
                    customerObj.getString("lastname"),
                    customerObj.getString("email"),
                    customerObj.getString("dropdown_option"),
                    customerObj.getString("address"),
                    customerObj.getString("address_notes"),
                    customerObj.getString("zipcode"),
                    customerObj.getString("cellphone"),
                    customerObj.getString("phone"),
                    customerObj.getString("location"),
                    customerObj.getString("internal_number"),
                    customerObj.getString("map_data"),
                    customerObj.getString("tag"),
                    customerObj.getString("middle_name"),
                    customerObj.getString("second_lastname"),
                    customerObj.getString("country_phone_code"),
                    customerObj.getString("dropdown_option_id")
                )
                customerTblList.add(customerDetailsEntity)

                val historyArray : JSONArray = order.getJSONArray("history")
                if(historyArray.length()>0){
                    for(i in 0..historyArray!!.length()-1){
                        var historyObj : JSONObject = historyArray.getJSONObject(i)
                        val historyDetailsEntity = HistoryEntity(
                            historyObj.getInt("id"),
                            historyObj.getInt("order_id"),
                            historyObj.getInt("type"),
                            historyObj.getString("data"),
                            historyObj.getString("created_at"),
                            historyObj.getString("updated_at")
                        )
                        historyTblList.add(historyDetailsEntity)
                    }
                }
                val productsArray : JSONArray = order.getJSONArray("products")
                for(i in 0..productsArray.length()-1){
                    val productJson : JSONObject = productsArray.getJSONObject(i)
                    val productDetailsEntity = ProductEntity(
                        productJson.getInt("id"),
                        productJson.getInt("product_id"),
                        productJson.getInt("order_id"),
                        productJson.getString("name"),
                        productJson.getInt("price"),
                        productJson.getInt("quantity"),
                        productJson.getString("comment"),
                        productJson.getString("ingredients"),
                        productJson.getString("options"),
                        productJson.getBoolean("featured"),
                        productJson.getBoolean("upselling"),
                        productJson.getBoolean("in_offer"),
                        productJson.getString("offer_price"),
                        productJson.getString("images"),
                        productJson.getInt("offer_rate"),
                        productJson.getInt("offer_rate_type"),
                        productJson.getBoolean("offer_include_options"),
                        productJson.getInt("status"),
                        productJson.getInt("priority"),
                        productJson.getString("reporting_data"),
                        productJson.getString("fee_id"),
                        productJson.getString("tax_id"),
                        productJson.getString("summary"),
                        productJson.getInt("category_id"),
                        productJson.getInt("total"),
                        productJson.getString("tax"),
                        productJson.getString("fee")
                    )
                    productTblList.add(productDetailsEntity)
                }

                val summeryObj : JSONObject = order.getJSONObject("summary")
                val summaryDetailsEntity = SummaryEntity(
                        orderId.toInt(),
                    summeryObj.getInt("total"),
                    summeryObj.getInt("discount"),
                    summeryObj.getDouble("subtotal"),
                    summeryObj.getDouble("subtotal_with_discount"),
                    summeryObj.getInt("service_fee_rate"),
                    summeryObj.getInt("service_fee"),
                    summeryObj.getInt("service_fee_with_discount"),
                    summeryObj.getInt("delivery_price"),
                    summeryObj.getInt("delivery_price_with_discount"),
                    summeryObj.getInt("tax_rate"),
                    summeryObj.getDouble("tax"),
                    summeryObj.getDouble("tax_with_discount"),
                    summeryObj.getInt("driver_tip_rate"),
                    summeryObj.getInt("driver_tip")

                )
                summaryTblList.add(summaryDetailsEntity)

            }

            orderDao!!.insertOrderResult(resultList!!)
            orderDao!!.insertBisinessData(bisinessTblList!!)
            orderDao!!.insertCityData(cityTblList!!)
            orderDao!!.insertCustomerData(customerTblList!!)
            orderDao!!.insertDriverData(driverTblList!!)
            orderDao!!.insertHistoryData(historyTblList!!)
            orderDao!!.insertLocationData(locationTblList!!)
            orderDao!!.insertMetafieldData(metafieldTblList!!)
            orderDao!!.insertPaymethodData(paymethodTblList!!)
            orderDao!!.insertProductData(productTblList!!)
            orderDao!!.insertReviewData(reviewTblList!!)
            orderDao!!.insertSummaryData(summaryTblList!!)

            linkCount = linkCount + 1

            if (linkCount.equals(paginationLink.size)) {
                if(isFromNewOrder)
                {
                    if (NewOrderId != null) {
                        var orderData : Result = orderDao!!.getOrderById(NewOrderId.toString())
                        var summery : SummaryEntity = orderDao!!.getSummaryById(NewOrderId.toString())
//                        NewOrderId=null
                        if(orderData != null) {

                            var lastAcceptedOrder=prefManager!!.getString(SharedPreferencesKeys.lastAcceptedOrder);
                            if(!lastAcceptedOrder.equals(NewOrderId)){
                                val customerDetailsEntity = DialogQueue(orderData.id.toInt(),orderData.id.toString())
                                orderDao!!.insertQueueData(customerDetailsEntity)
                                NewOrderId =  null
                            }

                        }
                    }
                }else{
                    var orderListPending : List<Int> = orderDao!!.getPendingOrder()
                    var orderListInprocess : List<Int> = orderDao!!.getInProgressOrder()
                    var orderListDone : List<Int> = orderDao!!.getDoneOrder()

                    var orderListPendingString : List<String> = convertIntToString(orderListPending)
                    var orderListInprocessString : List<String> = convertIntToString(orderListInprocess)
                    var orderListDoneString : List<String> = convertIntToString(orderListDone)

                    expandableDetailList = ExpandableListDataItems.getData("", applicationContext,orderListPendingString,orderListInprocessString,orderListDoneString)
                    expandableTitleList = ArrayList(expandableDetailList!!.keys)
                    expandableListAdapter =
                        CustomizedExpandableListAdapter2(
                            this@Expandablectivity,
                            this,
                            expandableTitleList.sortedDescending(),
                            expandableDetailList
                        )
                    expandableListViewExample!!.setAdapter(expandableListAdapter)
                    expandableListViewExample!!.expandGroup(1)
                    lottieProgressDialog!!.cancelDialog()
                }
            }

        } catch (e: JSONException) {
            e.printStackTrace()
        }

    }


    fun parseOrderDataJsonNewORder(jsonData: String) {
        try {
            val jobj = JSONObject(jsonData)
            val jarrResult: JSONArray = jobj.getJSONArray("result")

            for (i in 0..jarrResult.length() - 1) {
                val order = jarrResult.getJSONObject(i)
                var orderId = order.getString("id");
                arrNewOrderId.add(orderId.toInt())
            }
            linkCountNewORder = linkCountNewORder + 1

        } catch (e: JSONException) {
            e.printStackTrace()
        }

    }


    fun parsePaginationInfoJson(jsonString: String, isFirstTime: Boolean, isFromNewOrder: Boolean = false) {

        try {
            val jobj = JSONObject(jsonString)
            val jsnPagination = jobj.getJSONObject("pagination")!!
            val totalRecord = jsnPagination.getString("total")!!
            val from = jsnPagination.getString("from")!!
            val to = jsnPagination.getString("to")!!
            val current_page = jsnPagination.getString("current_page")!!
            val page_size = jsnPagination.getString("page_size")!!
            val total_pages = jsnPagination.getString("total_pages")!!
            val fisrt_page = jsnPagination.getString("fisrt_page")!!
            val back_page = jsnPagination.getString("back_page")!!
            val next_page = jsnPagination.getString("next_page")!!
            val last_page = jsnPagination.getString("last_page")!!


            for (i in 1..total_pages.toInt()) {
                var url: String = makeUrl(i.toString(), businessId)
                paginationLink!!.add(url)
            }
            paginationLink

            for (link in paginationLink) {
                if(isFromNewOrder)
                    getOrders(link, Constants.apiKey,true)
                else
                    //demo
                    getOrders(link, Constants.apiKey)
            }

        } catch (e: JSONException) {
            e.printStackTrace()
        }

    }

    fun parsePaginationInfoJsonNewORder(jsonString: String, isFirstTime: Boolean) {

        paginationLinkNewOrder.clear()
        try {
            val jobj = JSONObject(jsonString)
            val jsnPagination = jobj.getJSONObject("pagination")!!
            val total_pages = jsnPagination.getString("total_pages")!!
            for (i in 1..total_pages.toInt()) {
                var url: String = makeUrlNewOrder(i.toString(), businessId)
                paginationLinkNewOrder!!.add(url)
            }
            arrlength = paginationLinkNewOrder.size
            for (link in paginationLinkNewOrder) {
                getOrdersNewOrder(link, Constants.apiKey)
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }

    }

    fun makeUrl(pageNo: String, businessId: String): String {
        var url: String = Constants.URL
        url = url + "page_size=" + "10" + "&mode=" + "dashboard" + "&page=" + pageNo + "&where=[{\"attribute\":\"business_id\",\"value\":[" + businessId + "]}]"
        return url
    }


    fun makeUrlNewOrder(pageNo: String, businessId: String): String {
        var url: String = Constants.URL
        url = url + "page_size=" + "10" + "&mode=" + "dashboard" + "&page=" + pageNo + "&where=[{\"attribute\":\"business_id\",\"value\":[" + businessId + "]},{\"attribute\":\"status\",\"value\":[0,13]}]"
        return url
    }

    fun syncDatabase(){
        DeleteDataDbAsyncTask().execute()
        var url = Constants.URL
        url = url + "page_size=" + "10" + "&mode=" + "dashboard" + "&page=" + "1" + "&where=[{\"attribute\":\"business_id\",\"value\":[" + businessId + "]}]"
        getPaginationInfor(url, Constants.apiKey)
    }


    fun getDataBase(isFromNewOrder: Boolean= false) {
        if (!prefManager!!.getBoolean(SharedPreferencesKeys.isFromLogin)) {
            prefManager!!.setBoolean(SharedPreferencesKeys.isFromLogin, true)
            var url = Constants.URL
            url = url + "page_size=" + "10" + "&mode=" + "dashboard" + "&page=" + "1" + "&where=[{\"attribute\":\"business_id\",\"value\":[" + businessId + "]}]"
            if(isFromNewOrder)
                getPaginationInfor(url, Constants.apiKey, true)
            else
                getPaginationInfor(url, Constants.apiKey)
        }else{
            var orderListPending : List<Int> = orderDao!!.getPendingOrder()
            var orderListInprocess : List<Int> = orderDao!!.getInProgressOrder()
            var orderListDone : List<Int> = orderDao!!.getDoneOrder()
            var orderListPendingString : List<String> = convertIntToString(orderListPending)
            var orderListInprocessString : List<String> = convertIntToString(orderListInprocess)
            var orderListDoneString : List<String> = convertIntToString(orderListDone)

            expandableDetailList = ExpandableListDataItems.getData("", applicationContext,orderListPendingString,orderListInprocessString,orderListDoneString)
            expandableTitleList = ArrayList(expandableDetailList!!.keys)
            expandableListAdapter =
                CustomizedExpandableListAdapter2(this@Expandablectivity,
                    this,
                    expandableTitleList.sortedDescending(),
                    expandableDetailList
                )
            expandableListViewExample!!.setAdapter(expandableListAdapter)
            expandableListViewExample!!.expandGroup(1)
            if(!isFromNewOrder)
                lottieProgressDialog!!.cancelDialog()
        }
    }
    fun refreshFromLocal(){
        var orderListPending : List<Int> = orderDao!!.getPendingOrder()
        var orderListInprocess : List<Int> = orderDao!!.getInProgressOrder()
        var orderListDone : List<Int> = orderDao!!.getDoneOrder()
        var orderListPendingString : List<String> = convertIntToString(orderListPending)
        var orderListInprocessString : List<String> = convertIntToString(orderListInprocess)
        var orderListDoneString : List<String> = convertIntToString(orderListDone)

        expandableDetailList = ExpandableListDataItems.getData("", applicationContext,orderListPendingString,orderListInprocessString,orderListDoneString)
        expandableTitleList = ArrayList(expandableDetailList!!.keys)
        expandableListAdapter =
            CustomizedExpandableListAdapter2(this@Expandablectivity,
                this,
                expandableTitleList.sortedDescending(),
                expandableDetailList
            )
        expandableListViewExample!!.setAdapter(expandableListAdapter)
        expandableListViewExample!!.expandGroup(1)
    }

    fun convertIntToString(objInt: List<Int>) : List<String>{
        val stringObj: MutableList<String> = ArrayList()
        for(obj in objInt){
            stringObj.add(obj.toString())
        }
        return stringObj
    }

    fun getPaginationInfoNewORder(url: String, key: String) {
        ServiceGenerator.nentoApi.getUsers(url, key)!!.enqueue(object :
            Callback<String?> {
            @SuppressLint("NewApi", "ResourceAsColor")
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onResponse(call: Call<String?>, response: Response<String?>) {
                if (response.isSuccessful) {
                    orderResult = response.body()!!.toString()
                    parsePaginationInfoJsonNewORder(orderResult, true)
                } else {
//                    lottieProgressDialog!!.cancelDialog()
                    Toast.makeText(applicationContext, "Wrong detail", Toast.LENGTH_SHORT).show()
                    Log.e("tag", " =  = = =error = ==  " + response.message())
                }
            }

            override fun onFailure(call: Call<String?>, t: Throwable) {
//                lottieProgressDialog!!.cancelDialog()
                getOrdersNewOrder(url, Constants.apiKey)
            }
        })
    }

    override fun onClose() {
        NewOrderId=null
        if(mplayer.isPlaying)
        {
            mplayer.stop()
        }

    }

//    override fun onOKClick(orderId: String) {
//        val i = Intent(applicationContext, OrderPreviewActivity::class.java)
//        i.putExtra("orderId", orderId)
//        i.flags = Intent.FLAG_ACTIVITY_NEW_TASK
//        startActivity(i)
//    }

    private fun manageQue(){
        var orderId=orderDao!!.getMaxOrderFromQueue();

        if(dialog.isShowing){
            val textView9 = dialog.findViewById(R.id.textView9) as TextView
            var dialogORderID = textView9.text.toString().substringAfter("#");
            if(dialogORderID.equals(orderId)){

            }else{
                if(orderId != null){
                    var status = orderDao!!.getStatus(orderId);
                    var btnText = "New Order"
                    if(status.equals("13")){
                        btnText="Pre Order"
                    }
                    var orderData : Result = orderDao!!.getOrderById(orderId.toString())
                    var summery : SummaryEntity = orderDao!!.getSummaryById(orderId.toString())

                    showDialog(orderData.id.toString(),orderData.delivery_datetime.toString(),summery.total.toString(),orderData.delivery_type.toString(),btnText)
                }
            }
            Log.e("aaavv","dialog open")
        }else{
            if(orderId != null){
                if(orderId.equals(" ") || orderId.equals("null") || orderId.equals("0")){

                }else{
                    var status = orderDao!!.getStatus(orderId);
                    var btnText = "New Order"
                    if(status.equals("13")){
                        btnText="Pre Order"
                    }
                    var orderData : Result = orderDao!!.getOrderById(orderId.toString())
                    var summery : SummaryEntity = orderDao!!.getSummaryById(orderId.toString())
                    showDialog(orderData.id.toString(),orderData.delivery_datetime.toString(),summery.total.toString(),orderData.delivery_type.toString(),btnText)
                }
            }
            Log.e("aaavv","dialog close")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterNetworkChanges()
    }
    private fun showDialog( orderId : String, date: String, amt : String, deliveryType : String, buttonText: String) {
//        val dialog = Dialog(application)

        runOnUiThread {
            dismissAllDialogs(supportFragmentManager)
            dialog = Dialog(this@Expandablectivity)

            if(!mplayer.isPlaying)
            {
                Log.e("newupor",NewOrderId.toString())
                mplayer.isLooping=true
                mplayer.start()
//            NewOrderId =  null
            }

            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setCancelable(false)
            dialog.setContentView(R.layout.dialog_new_order)
            val textView9 = dialog.findViewById(R.id.textView9) as TextView
            val txtNewORderlbl = dialog.findViewById(R.id.txtNewORderlbl) as TextView
            val textView6 = dialog.findViewById(R.id.textView6) as TextView
            val textView8 = dialog.findViewById(R.id.textView8) as TextView
            val tvDate = dialog.findViewById(R.id.tvDate) as TextView
            val tvTime = dialog.findViewById(R.id.tvTime) as TextView

            txtNewORderlbl.text= buttonText


            var deliveryType1 : String = ""
            when (deliveryType) {
                "1" -> deliveryType1 = applicationContext.getString(R.string.Delivery)
                "2" -> deliveryType1 = applicationContext.getString(R.string.Pick_Up)
                "3" -> deliveryType1 = applicationContext.getString(R.string.Eat_In)
                "4" -> deliveryType1 = applicationContext.getString(R.string.Curbside)
                "5" -> deliveryType1 = applicationContext.getString(R.string.Driver_thru)
            }
            Log.e("logdel",deliveryType1.toString())
            Log.e("logdel",deliveryType.toString())
            textView9.setText("#"+orderId.toString())
            textView6.setText(amt.toString()+" Kr")
            textView8.setText(deliveryType1.toString())
            val dateWithMinute = date.dropLast(3)
            val time: String? = dateWithMinute.substringAfterLast(" ")
            val date: String? = dateWithMinute.substringBefore(" ")
            tvDate.setText("    "+date.toString()+"    ")
            tvTime.setText("   "+time.toString()+"   ")

            val btnclose = dialog.findViewById(R.id.btnclose) as ImageView
            val confirmOrderBtn = dialog.findViewById(R.id.confirm_order_btn) as TextView
            btnclose.setOnClickListener {
                orderDao!!.deleteFromQueue(orderId)
                NewOrderId=null
                if(mplayer.isPlaying)
                {
                    mplayer.stop()
                }
                dialog.dismiss()
            }
            confirmOrderBtn.setOnClickListener {
                orderDao!!.deleteFromQueue(orderId)
                if(mplayer.isPlaying)
                {
                    mplayer.stop()
                }
                dialog.dismiss()
                NewOrderId=null

//                context.sendBroadcast(new Intent(Default.IS_FROM_DONE).putExtra(Default.IS_ORDER_DONE_ACTIVITY, true));
//                Intent i = new Intent(context, OrderPreviewActivity.class);
//                i.putExtra("orderId",expandedListText);
//                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                context.startActivity(i);
                val newOrderDialog = NewOrderDialog(orderId)
                newOrderDialog.show(getSupportFragmentManager(), "")

//                val i = Intent(applicationContext, OrderPreviewActivity::class.java)
//                i.putExtra("orderId", orderId)
//                i.flags = Intent.FLAG_ACTIVITY_NEW_TASK
//                startActivity(i)
//            finish()

            }

            dialog.show()
        }
    }

    fun removeTokenAtLogout(businessId: String) {

        val sharedPref = getSharedPreferences("com.tjcg.nentopos", FirebaseMessagingService.MODE_PRIVATE)
        val push_token= sharedPref.getString(SharedPreferencesKeys.device_token,null)
        lottieProgressDialog!!.showDialog()
        ServiceGenerator.nentoApiIntermediator.logoutAtIntermediateServer(businessId,push_token).enqueue(object : Callback<IntermediatorLogout?> {
            @SuppressLint("NewApi", "ResourceAsColor")
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onResponse(call: Call<IntermediatorLogout?>, response: Response<IntermediatorLogout?>) {
                if (response.isSuccessful) {
                    if (response.body() != null && response.body()!!.status) {
                        lottieProgressDialog!!.cancelDialog()
                        prefManager!!.setString(Constants.authorization_key, "")
                        Constants.Authorization = ""
                        prefManager!!.setBoolean("isLogin", false)
                        startActivity(Intent(this@Expandablectivity, LoginActivity::class.java).putExtra(SharedPreferencesKeys.isDBLoadRequired,true))
                        finish()
                        finishAffinity()
                        exitProcess(0)
                    }
                } else {
                    lottieProgressDialog!!.cancelDialog()
                    Log.e("tag", " =  = = =error = ==  " + response.message())
                }
            }

            override fun onFailure(call: Call<IntermediatorLogout?>, t: Throwable) {
                lottieProgressDialog!!.cancelDialog()
                Log.e("tag", " =  = = =error = ==  " + t.message)
            }
        })
    }

    private val reMyreceive: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            try {
                if (intent.action == "new_order") {
                    val orderId = intent.getStringExtra("id");
                    var total=orderDao!!.getOrderTotal(orderId!!)
                    var deliveryDateTime=intent.getStringExtra("deliveryDateTime");
                    var deliverytype=intent.getStringExtra("deliverytype");
                    var status=intent.getStringExtra("status");
                    var newButtonText = "New Order";
                    if(status.equals("13")){
                        newButtonText= "Pre Order";
                    }
                    Log.e("msggg", "inside broadcast rec")
//                    Toast.makeText(applicationContext,"newOrder from broad",Toast.LENGTH_LONG).show()
                    showDialog(orderId,deliveryDateTime!!,
                        total.toString()!!,deliverytype!!,newButtonText)
                }
                if(intent.action == "status_changed"){
                    val from = intent.getStringExtra("from")
                    if(from.equals("com.tjcg.menuo.Expandablectivity")){
//                        expandableListViewExample.setAdapter()
//                        startActivity(Intent(applicationContext, Expandablectivity::class.java))
//                        finish()
                        refreshFromLocal()
                    }
                }
                if(intent.action == "refreshLocal"){
                    Log.e("refreshLocal", "called")
                    refreshFromLocal()
                    manageQue()
                }
            } catch (e: Exception) {
               Log.e("Error BR", e.message.toString())
            }
        }
    }

    override fun onPause() {
        super.onPause()
//        unregisterReceiver(reMyreceive)
    }

    private fun registerNetworkBroadcastForNougat() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            registerReceiver(
                mNetworkReceiver,
                IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
            )
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            registerReceiver(
                mNetworkReceiver,
                IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
            )
        }
    }

    protected fun unregisterNetworkChanges() {
        try {
            unregisterReceiver(mNetworkReceiver)
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        }
    }

    private fun showOrderPreviewDialog( orderId : String, date: String, amt : String, deliveryType : String, buttonText: String) {

        runOnUiThread {
            dialogOrderPreview = Dialog(this@Expandablectivity)
            dialogOrderPreview.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialogOrderPreview.setCancelable(false)
            dialogOrderPreview.setContentView(R.layout.order_preview_layout)
            dialogOrderPreview.show()
        }
    }

    fun dismissAllDialogs(manager: FragmentManager?) {
        val fragments: List<Fragment> = manager!!.getFragments() ?: return
        for (fragment in fragments) {
            if (fragment is DialogFragment) {
                val dialogFragment: DialogFragment = fragment as DialogFragment
                dialogFragment.dismissAllowingStateLoss()
            }
            val childFragmentManager: FragmentManager = fragment.getChildFragmentManager()
            if (childFragmentManager != null) dismissAllDialogs(childFragmentManager)
        }
    }


}