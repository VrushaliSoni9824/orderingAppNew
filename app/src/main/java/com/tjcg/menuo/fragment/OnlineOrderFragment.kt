package com.tjcg.menuo.fragment

import android.animation.ArgbEvaluator
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.tjcg.menuo.MainActivity
import com.tjcg.menuo.R
import com.tjcg.menuo.activity.LoginActivity
import com.tjcg.menuo.adapter.OnlineOrderAdapter
import com.tjcg.menuo.data.ResponseListener
import com.tjcg.menuo.data.local.AppDatabase
import com.tjcg.menuo.data.local.OrderDao
import com.tjcg.menuo.data.remote.ServiceGenerator
import com.tjcg.menuo.data.response.order.OnlineOrderData
import com.tjcg.menuo.data.response.order.OnlineOrderRS
import com.tjcg.menuo.databinding.FragmentOnlineOrderBinding
import com.tjcg.menuo.viewmodel.OnlineOrderViewModel
import retrofit2.Call
import java.util.*
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import com.tjcg.menuo.data.response.Login.OutletsRS
import com.tjcg.menuo.dialog.LocationDialog
import com.tjcg.menuo.utils.*
import com.tjcg.menuo.viewmodel.SyncViewModel
import kotlin.collections.ArrayList
import android.view.animation.Animation

import android.animation.ObjectAnimator
import android.graphics.Color
//import io.sentry.Sentry


class OnlineOrderFragment : BaseFragment(), View.OnClickListener, ResponseListener , LocationDialog.ClickListener{
    var onlineOrderViewModel: OnlineOrderViewModel? = null
    private var binding: FragmentOnlineOrderBinding? = null
    private var mainActivity: MainActivity? = null
    var lottieProgressDialog: LottieProgressDialog? = null
    var onlineOrderAdapter: OnlineOrderAdapter? = null
    var onlineOrderDataList: MutableList<OnlineOrderData>? = null
    var orderDao: OrderDao? = null;
    lateinit var mplayer: MediaPlayer
    var selectedOutletId : String =""
    private var prefManager: PrefManager? = null
    var sharedPreferences : SharedPreferences? = null
    var db: AppDatabase? = null
    var posViewModel: SyncViewModel? = null
    var isOnlineOrder:String="0"

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
        prefManager = PrefManager(context)
    }

    @SuppressLint("ResourceAsColor")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentOnlineOrderBinding.inflate(inflater)
        orderDao = AppDatabase.getDatabase(mainActivity!!.applicationContext)!!.orderDao()
        db = AppDatabase.getDatabase(mainActivity!!)
        mplayer = MediaPlayer.create(mainActivity!!.applicationContext, R.raw.alarmtone);

        sharedPreferences = mainActivity!!.getSharedPreferences("com.tjcg.nentopos", Context.MODE_PRIVATE)
        var change_location_button: ImageView =mainActivity!!.findViewById(R.id.change_location_btn)
        binding!!.textViewOutlet.text=sharedPreferences!!.getString("outLetName","No OutletSelected").toString()
        selectedOutletId=sharedPreferences!!.getInt("current_outlets",outletId.toInt()).toString()
        change_location_button.setOnClickListener {

            val outlet : List<OutletsRS> = getOutlets()
            val outletName= ArrayList<String>()
            val outletId = ArrayList<String>()
            val outletuniqueId = ArrayList<String>()
            for(outletItem in outlet)
            {
                outletName!!.add(outletItem.name.toString())
                outletId!!.add(outletItem.outlet_id)
                outletuniqueId!!.add(outletItem.unique_id.toString())
            }
            val locationDialog = LocationDialog(this,outlet)
            locationDialog.show(mainActivity!!.supportFragmentManager, "")
        }

        val online_count= mainActivity!!.getOnlineOrderCountMain()
        online_count

        if(online_count.toInt()>0)
        {
            if(!mplayer.isPlaying)
            {
                mplayer.isLooping=true
                mplayer.start()
                var tvnavigationOnlineOrder: TextView =mainActivity!!.findViewById(R.id.navigation_online_order)
                tvnavigationOnlineOrder.setBackgroundColor(R.drawable.order_item_bg_complete)
                manageBlinkEffect(tvnavigationOnlineOrder)
            }

        }
        else
        {
            if(mplayer.isPlaying)
            {

                mplayer.stop()
                var tvnavigationOnlineOrder: TextView =mainActivity!!.findViewById(R.id.navigation_online_order)
                tvnavigationOnlineOrder.setBackgroundColor(R.color.TextViewBg)
            }
        }
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        try {
            throw java.lang.Exception("This is a test.")
        } catch (e: java.lang.Exception) {
//            Sentry.captureException(e)
        }

        isOnlineOrder = sharedPreferences!!.getString(SharedPreferencesKeys.all_order,"0").toString()

        if(isOnlineOrder!!.equals(getResources().getString(R.string.True))){
            binding!!.noaccess.visibility=View.GONE
        }else{
            binding!!.noaccess.visibility=View.VISIBLE
        }

        lottieProgressDialog = LottieProgressDialog(mainActivity as Context)
        lottieProgressDialog!!.showDialog()
        onlineOrderViewModel = ViewModelProviders.of(this).get(OnlineOrderViewModel::class.java)
        posViewModel = ViewModelProviders.of(this).get(SyncViewModel::class.java)
        onlineOrders
        initData()
        lottieProgressDialog!!.cancelDialog()
        binding!!.refreshOrder.setOnClickListener {
            lottieProgressDialog = LottieProgressDialog(mainActivity as Context)
            lottieProgressDialog!!.showDialog()
            onlineOrders
        }


//        val handler = Handler()
//        handler.postDelayed({
//            // yourMethod();
//            onlineOrdersAfterSync
//        }, 5000)
        val mHandler = Handler()
        Thread {

            while (true) {
                try {
                    Thread.sleep(10000)
                    mHandler.post(Runnable {

                        val IsNewOrder=sharedPreferences!!.getBoolean("IsNeworder",false)
                        if(IsNewOrder){
                            binding!!.newOrder.isSelected = true
                            binding!!.accepted.isSelected = false
                            binding!!.completed.isSelected = false
                            binding!!.cancelled.isSelected = false
                            binding!!.futureORder.isSelected = false
                            val editor:SharedPreferences.Editor =  sharedPreferences!!.edit()
                            editor.putBoolean("IsNeworder", false)
                            editor.apply()
                            editor.commit()
                            initData()
                        }
                        var olist: List<OnlineOrderData> = orderDao!!.getOnlineOrderNEw(selectedOutletId)
                        onlineOrderAdapter = OnlineOrderAdapter(mainActivity!!, outletId, olist, onlineOrderViewModel!!)
                        binding!!.productRecyclerView.layoutManager = LinearLayoutManager(context)
                        binding!!.productRecyclerView.adapter = onlineOrderAdapter
//                        binding!!.productRecyclerView.adapter!!.notifyDataSetChanged()
//                        onlineOrdersAfterSync
                        Log.e("vvvsss","Hi")
//                        getOnlineOrderCount(sharedPreferences!!.getInt("current_outlets",outletId.toInt())!!.toString(),sharedPreferences!!.getInt("current_outlets",outletId.toInt())!!.toString())
                    })
                } catch (e: Exception) {
                }
            }
        }.start()

    }

    private fun initData() {
        var onlineOrderCount:Int=0;
        onlineOrderCount=orderDao!!.getOnlineOrdersCount(selectedOutletId);
        var tvnavigationOnlineOrder: TextView =mainActivity!!.findViewById(R.id.navigation_online_order)
        tvnavigationOnlineOrder.text="Online Order ( "+mainActivity!!.getOnlineOrderCountMain().toString()+" )"

        var ongoingOrderCount:Int=0;
        ongoingOrderCount=orderDao!!.getOngoingOrdersCount();
        var tvnavigationOngoingOrder: TextView =mainActivity!!.findViewById(R.id.navigation_ongoing_order)
        tvnavigationOngoingOrder.text="Ongoing Order ( "+ongoingOrderCount.toString()+" )"

        binding!!.newOrder.isSelected = true
        onlineOrderDataList = ArrayList()
        onlineOrderAdapter = OnlineOrderAdapter(mainActivity!!, outletId, onlineOrderDataList as ArrayList<OnlineOrderData>, onlineOrderViewModel!!)
        binding!!.productRecyclerView.layoutManager = LinearLayoutManager(context)
        binding!!.productRecyclerView.adapter = onlineOrderAdapter
        binding!!.newOrder.setOnClickListener(this)
        binding!!.accepted.setOnClickListener(this)
        binding!!.completed.setOnClickListener(this)
        binding!!.cancelled.setOnClickListener(this)
        binding!!.futureORder.setOnClickListener(this)

        setOnlineOrderByStatus("1", true)

    }

    private fun setOnlineOrderByStatus(orderStatus: String, noData: Boolean) {
        onlineOrderViewModel!!.getOnlineOrderByStatus(orderStatus,selectedOutletId).observe(viewLifecycleOwner,
                Observer<List<OnlineOrderData>> { onlineOrderData: List<OnlineOrderData> ->
                    if (onlineOrderData.isNotEmpty()) {
                        lottieProgressDialog!!.cancelDialog()
                        binding!!.noDataFound.visibility = View.GONE
                        binding!!.productRecyclerView.visibility = View.VISIBLE
                        onlineOrderDataList!!.clear()

                        if (orderStatus == "1") {

                            for(odata in onlineOrderData)
                            {
                                if(odata.future_order_date!=null && !odata.future_order_date.equals(""))
                                {
                                    var minute: Long = getDifMinute(odata.future_order_date.toString(), odata.future_order_time.toString())
                                    if(minute <= 60){
                                        onlineOrderDataList!!.add(odata)
                                    }
                                }else{
                                    onlineOrderDataList!!.add(odata)
                                }
                            }
                            if(onlineOrderDataList!!.size>0){
                                onlineOrderDataList

                                binding!!.productRecyclerView.visibility = View.VISIBLE
                                binding!!.productRecyclerViewAccept.visibility = View.GONE
                                binding!!.productRecyclerViewCancel.visibility = View.GONE
                                binding!!.productRecyclerViewCompleted.visibility = View.GONE
                                binding!!.productRecyclerFuture.visibility = View.GONE
                                onlineOrderAdapter = OnlineOrderAdapter(mainActivity!!, outletId, onlineOrderDataList as ArrayList<OnlineOrderData>, onlineOrderViewModel!!)
                                binding!!.productRecyclerView.layoutManager = LinearLayoutManager(context)
                                binding!!.productRecyclerView.adapter = onlineOrderAdapter

                            }else{
                                binding!!.noDataFound.visibility = View.VISIBLE
                                binding!!.productRecyclerView.visibility = View.GONE
                                binding!!.productRecyclerViewAccept.visibility = View.GONE
                                binding!!.productRecyclerViewCancel.visibility = View.GONE
                                binding!!.productRecyclerViewCompleted.visibility = View.GONE
                                binding!!.productRecyclerFuture.visibility = View.GONE
                            }

                            var onlineOrderCount:Int=0;
//                            var orderDao: OrderDao = AppDatabase.getDatabase(mainActivity!!.applicationContext)!!.orderDao()
                            onlineOrderCount=orderDao!!.getOnlineOrdersCount(selectedOutletId);
                            var tvnavigationOnlineOrder: TextView =mainActivity!!.findViewById(R.id.navigation_online_order)
                            tvnavigationOnlineOrder.text="Online Order ( "+mainActivity!!.getOnlineOrderCountMain().toString()+" )"


                        } else if (orderStatus == "2") {

                            onlineOrderDataList!!.addAll(onlineOrderData)
                                binding!!.productRecyclerView.visibility = View.GONE
                                binding!!.productRecyclerViewAccept.visibility = View.VISIBLE
                                binding!!.productRecyclerViewCancel.visibility = View.GONE
                                binding!!.productRecyclerViewCompleted.visibility = View.GONE
                                onlineOrderAdapter = OnlineOrderAdapter(mainActivity!!, outletId, onlineOrderDataList as ArrayList<OnlineOrderData>, onlineOrderViewModel!!)
                                binding!!.productRecyclerViewAccept.layoutManager = LinearLayoutManager(context)
                                binding!!.productRecyclerViewAccept.adapter = onlineOrderAdapter

                        } else if (orderStatus == "4") {
                            onlineOrderDataList!!.addAll(onlineOrderData)
                                binding!!.productRecyclerView.visibility = View.GONE
                                binding!!.productRecyclerViewAccept.visibility = View.GONE
                                binding!!.productRecyclerViewCancel.visibility = View.VISIBLE
                                binding!!.productRecyclerViewCompleted.visibility = View.GONE
                                onlineOrderAdapter = OnlineOrderAdapter(mainActivity!!, outletId, onlineOrderDataList as ArrayList<OnlineOrderData>, onlineOrderViewModel!!)
                                binding!!.productRecyclerViewCancel.layoutManager = LinearLayoutManager(context)
                                binding!!.productRecyclerViewCancel.adapter = onlineOrderAdapter

                        } else if (orderStatus == "5") {
                            onlineOrderDataList!!.addAll(onlineOrderData)
                                binding!!.productRecyclerView.visibility = View.GONE
                                binding!!.productRecyclerViewAccept.visibility = View.GONE
                                binding!!.productRecyclerViewCancel.visibility = View.GONE
                                binding!!.productRecyclerViewCompleted.visibility = View.VISIBLE
                                onlineOrderAdapter = OnlineOrderAdapter(mainActivity!!, outletId, onlineOrderDataList as ArrayList<OnlineOrderData>, onlineOrderViewModel!!)
                                binding!!.productRecyclerViewCompleted.layoutManager = LinearLayoutManager(context)
                                binding!!.productRecyclerViewCompleted.adapter = onlineOrderAdapter

                        } else {
                            onlineOrderDataList!!.addAll(onlineOrderData)
                            binding!!.productRecyclerView.visibility = View.VISIBLE
                            binding!!.productRecyclerViewAccept.visibility = View.GONE
                            binding!!.productRecyclerViewCancel.visibility = View.GONE
                            binding!!.productRecyclerViewCompleted.visibility = View.GONE
                            onlineOrderAdapter = OnlineOrderAdapter(mainActivity!!, outletId, onlineOrderDataList as ArrayList<OnlineOrderData>, onlineOrderViewModel!!)
                            binding!!.productRecyclerView.layoutManager = LinearLayoutManager(context)
                            binding!!.productRecyclerView.adapter = onlineOrderAdapter
                        }
                    } else if (noData) {
                        lottieProgressDialog!!.cancelDialog()
                        binding!!.noDataFound.visibility = View.VISIBLE
                        binding!!.productRecyclerView.visibility = View.GONE
                        binding!!.productRecyclerViewAccept.visibility = View.GONE
                        binding!!.productRecyclerViewCancel.visibility = View.GONE
                        binding!!.productRecyclerViewCompleted.visibility = View.GONE
                    }


//            onlineOrderAdapter!!.notifyDataSetChanged()
                })
    }

    private fun setOnlineOrderByFutureOrder(orderStatus: String="1", noData: Boolean) {
        onlineOrderViewModel!!.getOnlineOrderByStatus(orderStatus,selectedOutletId).observe(viewLifecycleOwner,
                Observer<List<OnlineOrderData>> { onlineOrderData: List<OnlineOrderData> ->
                    if (onlineOrderData.isNotEmpty()) {
                        lottieProgressDialog!!.cancelDialog()
                        binding!!.noDataFound.visibility = View.GONE
                        binding!!.productRecyclerFuture.visibility = View.VISIBLE
                        onlineOrderDataList!!.clear()
                        if (orderStatus == "1") {

                            for(odata in onlineOrderData)
                            {
                                if(odata.future_order_date!=null && !odata.future_order_date.equals(""))
                                {
                                    if(getDifMinute(odata.future_order_date.toString(),odata.future_order_time.toString())>60){
                                        onlineOrderDataList!!.add(odata)
                                    }
                                }
                            }
                            onlineOrderDataList


                            binding!!.productRecyclerView.visibility = View.GONE
                            binding!!.productRecyclerViewAccept.visibility = View.GONE
                            binding!!.productRecyclerViewCancel.visibility = View.GONE
                            binding!!.productRecyclerViewCompleted.visibility = View.GONE
                            binding!!.productRecyclerFuture.visibility = View.VISIBLE
                            onlineOrderAdapter = OnlineOrderAdapter(mainActivity!!, outletId, onlineOrderDataList as ArrayList<OnlineOrderData>, onlineOrderViewModel!!)
                            binding!!.productRecyclerFuture.layoutManager = LinearLayoutManager(context)
                            binding!!.productRecyclerFuture.adapter = onlineOrderAdapter

                            var onlineOrderCount:Int=0;
//                            var orderDao: OrderDao = AppDatabase.getDatabase(mainActivity!!.applicationContext)!!.orderDao()
                            onlineOrderCount=orderDao!!.getOnlineOrdersCount(selectedOutletId);
                            var tvnavigationOnlineOrder: TextView =mainActivity!!.findViewById(R.id.navigation_online_order)
                            tvnavigationOnlineOrder.text="Online Order ( "+mainActivity!!.getOnlineOrderCountMain().toString()+" )"


                        }
                    } else if (noData) {
                        lottieProgressDialog!!.cancelDialog()
                        binding!!.noDataFound.visibility = View.VISIBLE
                        binding!!.productRecyclerView.visibility = View.GONE
                        binding!!.productRecyclerViewAccept.visibility = View.GONE
                        binding!!.productRecyclerViewCancel.visibility = View.GONE
                        binding!!.productRecyclerViewCompleted.visibility = View.GONE
                    }


//            onlineOrderAdapter!!.notifyDataSetChanged()
                })
    }

    private val onlineOrders: Unit
        get() {

            val outlet : List<OutletsRS> = getOutlets()
            val outletName= ArrayList<String>()
            val outletId = ArrayList<String>()
            for(outletItem in outlet)
            {
//                outletName!!.add(outletItem.name.toString())
//                outletId!!.add(outletItem.outlet_id)
                onlineOrderViewModel!!.getOnlineOrder(outletItem.outlet_id, outletItem.unique_id, getResources().getString(R.string.is_all_data_yes).toString(), this)
            }


            if ( view != null)
            {

                onlineOrderViewModel!!.onlineOrderDataObserver.observe(viewLifecycleOwner, Observer { onlineOrderData: Resource<List<OnlineOrderData?>?> ->
                    lottieProgressDialog!!.cancelDialog()
                    if (onlineOrderData.data != null) {
                        var orderDao: OrderDao = AppDatabase.getDatabase(mainActivity!!)!!.orderDao()
                        val online_count=orderDao.getOnlineOrdersCount(selectedOutletId);
                        online_count
                        val o_data: List<OnlineOrderData>
                        o_data = onlineOrderData.data as List<OnlineOrderData>
                        if (o_data.isNotEmpty()) {
                            binding!!.newOrder.isSelected = true
                            binding!!.accepted.isSelected = false
                            binding!!.completed.isSelected = false
                            binding!!.cancelled.isSelected = false
                            binding!!.futureORder.isSelected = false
                            setOnlineOrderByStatus("1", true)
                        } else {
                            lottieProgressDialog!!.cancelDialog()
                            var new_order= orderDao.getTotalNewOrder(selectedOutletId)

                            if(new_order>0 && binding!!.productRecyclerView.visibility.equals(View.VISIBLE)){

                            }else{
                                binding!!.noDataFound.visibility = View.VISIBLE
                                binding!!.productRecyclerView.visibility = View.GONE
                                binding!!.productRecyclerViewAccept.visibility = View.GONE
                                binding!!.productRecyclerViewCancel.visibility = View.GONE
                                binding!!.productRecyclerViewCompleted.visibility = View.GONE
                                binding!!.productRecyclerFuture.visibility = View.GONE
                            }


                        }
//                    Toast.makeText(mainActivity,""+a.data.toString(),Toast.LENGTH_SHORT).show()
                    }

                })
            }

        }

    private val onlineOrdersAfterSync: Unit
        get() {

            val outlet : List<OutletsRS> = getOutlets()
            val outletName= ArrayList<String>()
            val outletId = ArrayList<String>()
            for(outletItem in outlet)
            {
//                outletName!!.add(outletItem.name.toString())
//                outletId!!.add(outletItem.outlet_id)
                onlineOrderViewModel!!.getOnlineOrderSync(outletItem.outlet_id, outletItem.unique_id, getResources().getString(R.string.is_all_data_yes).toString(), this)
            }


            if ( view != null)
            {

                onlineOrderViewModel!!.onlineOrderDataObserver.observe(viewLifecycleOwner, Observer { onlineOrderData: Resource<List<OnlineOrderData?>?> ->
                    lottieProgressDialog!!.cancelDialog()
                    if (onlineOrderData.data != null) {
                        var orderDao: OrderDao = AppDatabase.getDatabase(mainActivity!!)!!.orderDao()
                        val online_count=orderDao.getOnlineOrdersCount(selectedOutletId);
                        online_count
                        val o_data: List<OnlineOrderData>
                        o_data = onlineOrderData.data as List<OnlineOrderData>
                        if (o_data.isNotEmpty()) {
//                            binding!!.newOrder.isSelected = true
//                            binding!!.accepted.isSelected = false
//                            binding!!.completed.isSelected = false
//                            binding!!.cancelled.isSelected = false
//                            binding!!.futureORder.isSelected = false
                            setOnlineOrderByStatus("1", true)
                        } else {
                            lottieProgressDialog!!.cancelDialog()
                            var new_order= orderDao.getTotalNewOrder(selectedOutletId)

                            if(new_order>0 && binding!!.productRecyclerView.visibility.equals(View.VISIBLE)){

                            }else{
                                binding!!.noDataFound.visibility = View.VISIBLE
                                binding!!.productRecyclerView.visibility = View.GONE
                                binding!!.productRecyclerViewAccept.visibility = View.GONE
                                binding!!.productRecyclerViewCancel.visibility = View.GONE
                                binding!!.productRecyclerViewCompleted.visibility = View.GONE
                                binding!!.productRecyclerFuture.visibility = View.GONE
                            }


                        }
//                    Toast.makeText(mainActivity,""+a.data.toString(),Toast.LENGTH_SHORT).show()
                    }

                })
            }

        }


    @SuppressLint("NonConstantResourceId")
    override fun onClick(view: View) {
        when (view.id) {
            R.id.new_order -> {
                binding!!.newOrder.isSelected = true
                binding!!.accepted.isSelected = false
                binding!!.completed.isSelected = false
                binding!!.cancelled.isSelected = false
                binding!!.futureORder.isSelected = false
                setOnlineOrderByStatus("1", true)
            }
            R.id.accepted -> {
                binding!!.newOrder.isSelected = false
                binding!!.accepted.isSelected = true
                binding!!.completed.isSelected = false
                binding!!.cancelled.isSelected = false
                binding!!.futureORder.isSelected = false

                setOnlineOrderByStatus("2", true)
            }
            R.id.completed -> {
                binding!!.newOrder.isSelected = false
                binding!!.accepted.isSelected = false
                binding!!.completed.isSelected = true
                binding!!.cancelled.isSelected = false
                binding!!.futureORder.isSelected = false
                setOnlineOrderByStatus("4", true)
            }
            R.id.cancelled -> {
                binding!!.newOrder.isSelected = false
                binding!!.accepted.isSelected = false
                binding!!.completed.isSelected = false
                binding!!.cancelled.isSelected = true
                binding!!.futureORder.isSelected = false
                setOnlineOrderByStatus("5", true)
            }
            R.id.futureORder->{
                binding!!.newOrder.isSelected = false
                binding!!.accepted.isSelected = false
                binding!!.completed.isSelected = false
                binding!!.cancelled.isSelected = false
                binding!!.futureORder.isSelected = true
                setOnlineOrderByFutureOrder("1",true)
            }
        }
    }

    override fun onResponseReceived(responseObject: Any, requestType: Int) {
        lottieProgressDialog!!.cancelDialog()
        if (responseObject.toString() == "Invalid Auth Token") {
            prefManager!!.setString(Constants.authorization_key, "")
            Constants.Authorization = ""
            prefManager!!.setBoolean("isLogin", false)
            startActivity(Intent(mainActivity, LoginActivity::class.java))
            mainActivity!!.finish()
        }

        if (requestType == 1) {
            var new_order= orderDao!!.getTotalNewOrder(selectedOutletId)
             if(new_order>0 && binding!!.productRecyclerView.visibility.equals(View.VISIBLE)){

             }else{
                 binding!!.noDataFound.visibility = View.VISIBLE
                 binding!!.productRecyclerView.visibility = View.GONE
                 binding!!.productRecyclerViewAccept.visibility = View.GONE
                 binding!!.productRecyclerViewCancel.visibility = View.GONE
                 binding!!.productRecyclerViewCompleted.visibility = View.GONE
             }

        }
    }

    companion object {
        @JvmStatic
        fun newInstance(): OnlineOrderFragment {
            return OnlineOrderFragment()
        }
    }

    public fun refereshCancel()
    {
        binding!!.newOrder.isSelected = false
        binding!!.accepted.isSelected = false
        binding!!.completed.isSelected = false
        binding!!.cancelled.isSelected = true
        binding!!.futureORder.isSelected = true
        setOnlineOrderByStatus("5", true)
    }

    fun getOnlineOrderCount(orderId: String, deliveryBoyID: String) {
        Log.e("tag", " = = = 1= = = $orderId")
        Log.e("tag", " = = = 3= = = $deliveryBoyID")

//        lottieProgressDialog!!.showDialog()
        ServiceGenerator.nentoApi.getOnlineOrderCount(orderId, deliveryBoyID, Constants.Authorization).enqueue(object : Callback<OnlineOrderRS?> {
            @SuppressLint("NewApi", "ResourceAsColor")
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onResponse(call: Call<OnlineOrderRS?>, response: Response<OnlineOrderRS?>) {
                if (response.isSuccessful) {
                    if (response.body() != null && response.body()!!.status.equals("true")) {
                        var new_order: ArrayList<OnlineOrderData>? = null
                        new_order=response.body()!!.data!!.new_order;
                        val a=new_order!!.size

                        var orderDao: OrderDao = AppDatabase.getDatabase(mainActivity!!)!!.orderDao()
                        val local_online_count=mainActivity!!.getOnlineOrderCountMain()
                        val local_online_futureOrder_count=orderDao.getOnlineOrdersCount(selectedOutletId);

                        var onlineOrderCountWithoutFuturOrder: Int =0
                        var isFutureOrderInNewOrderTab:Boolean=false;
                        var newOrderMovingCount: Int=0
                        for (order in new_order){
                            if(order.future_order_date!=null && !order.future_order_date.equals("")){

                                var minutes: Long= getDifMinute(order.future_order_date.toString(),order.future_order_time.toString())
                                if(minutes<=60){
                                        isFutureOrderInNewOrderTab=true
                                    onlineOrderCountWithoutFuturOrder=onlineOrderCountWithoutFuturOrder+1
                                    newOrderMovingCount++
                                }

                            }else{
                                newOrderMovingCount++
                            }
                        }
                        onlineOrderCountWithoutFuturOrder
                        newOrderMovingCount
//                        online_count
//
                        if(local_online_count.toInt()>0 || onlineOrderCountWithoutFuturOrder.toInt()>0 || isFutureOrderInNewOrderTab)
                        {
                            if(!mplayer.isPlaying)
                            {
                                var tvnavigationOnlineOrder: TextView =mainActivity!!.findViewById(R.id.navigation_online_order)
                                tvnavigationOnlineOrder.setBackgroundColor(R.drawable.order_item_bg_complete)
                                mplayer.start()
                            }

                        }
                        else
                        {
                            if(mplayer.isPlaying)
                            {
                                var tvnavigationOnlineOrder: TextView =mainActivity!!.findViewById(R.id.navigation_online_order)
                                tvnavigationOnlineOrder.setBackgroundColor(R.color.TextViewBg)
                                mplayer.stop()
                            }
                        }

                        if(newOrderMovingCount.toInt()>local_online_count.toInt())
                        {
                            var onlineOrderCount:Int=0;
//                            var orderDao: OrderDao = AppDatabase.getDatabase(mainActivity!!.applicationContext)!!.orderDao()
                            onlineOrderCount=orderDao.getOnlineOrdersCount(selectedOutletId);
                            var tvnavigationOnlineOrder: TextView =mainActivity!!.findViewById(R.id.navigation_online_order)
                            tvnavigationOnlineOrder.text="Online Order ( "+mainActivity!!.getOnlineOrderCountMain().toString()+" )"

                            lottieProgressDialog = LottieProgressDialog(mainActivity as Context)
                            lottieProgressDialog!!.showDialog()
                            onlineOrders
                            lottieProgressDialog!!.cancelDialog()
                        }
                    }
                } else {
                    lottieProgressDialog!!.cancelDialog()
//                    Toast.makeText(mainActivity,"Error while fetching invoice detail..",Toast.LENGTH_SHORT).show()
                    Log.e("tag", " =  = = =error = ==  " + response.message())
                }
            }

            override fun onFailure(call: Call<OnlineOrderRS?>, t: Throwable) {
                lottieProgressDialog!!.cancelDialog()

                Log.e("tag", " =  = = =error = ==  " + t.message)
            }
        })
    }

    public fun getDifMinute(future_order_date : String,future_order_time : String ) : Long {
        val stringDate = future_order_date+" "+future_order_time
        val toyBornTime = stringDate
        val dateFormat = SimpleDateFormat(
            "yyyy-MM-dd HH:mm:ss"
        )
            val oldDate = dateFormat.parse(toyBornTime)
            System.out.println(oldDate)
            val currentDate = Date()
            val diff = oldDate.time -currentDate.time
            val seconds = diff / 1000
            val minutes = seconds / 60
            return  minutes

    }
    override fun onPause() {
        super.onPause()
        if(mplayer.isPlaying)
        {
            var tvnavigationOnlineOrder: TextView =mainActivity!!.findViewById(R.id.navigation_online_order)
            tvnavigationOnlineOrder.setBackgroundResource(0)
            mplayer.stop()
        }

    }

    override fun onLocationChanged(outletId: String, outletname: String, uniqueId: String) {
        val sharedPreferences: SharedPreferences =requireContext().getSharedPreferences("com.tjcg.nentopos", Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor =  sharedPreferences.edit()
        editor.putInt("current_outlets",outletId.toInt())
        editor.putString("current_outlets_unique_id", uniqueId.toString())
        editor.apply()
        editor.commit()
        binding!!.textViewOutlet.text= db!!.adminItemDao().getOutletName(outletId.toInt())
        selectedOutletId= outletId.toInt().toString()

        getOnlineOrderCount(outletId,uniqueId)
        initData()
    }

    @SuppressLint("WrongConstant")
    private fun manageBlinkEffect(txt : TextView) {
        val anim = ObjectAnimator.ofInt(
            txt, "backgroundColor", Color.WHITE, getResources().getColor(R.color.colorPrimary),
            Color.WHITE
        )
        anim.duration = 1500
        anim.setEvaluator(ArgbEvaluator())
        anim.repeatMode = Animation.REVERSE
        anim.repeatCount = Animation.INFINITE
        anim.start()
    }

    public fun showToast() {
//       Toast.makeText(activity,"hello",Toast.LENGTH_LONG).show()
//        onlineOrdersAfterSync
//        onlineOrders
//        initData()
//        onlineOrdersAfterSync
//        orderDao = AppDatabase.getDatabase(mainActivity!!.applicationContext)!!.orderDao()
//        var olist = orderDao!!.getOnlineOrderNEw(selectedOutletId);
//        onlineOrderAdapter = OnlineOrderAdapter(mainActivity!!, outletId, olist, onlineOrderViewModel!!)
//        binding!!.productRecyclerView.layoutManager = LinearLayoutManager(context)
//        binding!!.productRecyclerView.adapter = onlineOrderAdapter
//        initData()

//        setOnlineOrderByStatus("1", true)

//        if(context!=null){
//            getOnlineOrderCount(sharedPreferences!!.getInt("current_outlets",outletId.toInt())!!.toString(),sharedPreferences!!.getInt("current_outlets",outletId.toInt())!!.toString())
//        }


//        onlineOrdersAfterSync
    }


}