package com.tjcg.menuo

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.telephony.TelephonyManager
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.gsd.dynamicMenu.menus.VerticalListPopupMenu
import com.tjcg.menuo.activity.LoginActivity
import com.tjcg.menuo.data.local.AppDatabase
import com.tjcg.menuo.data.local.AppDatabase.Companion.getDatabase
import com.tjcg.menuo.data.local.OrderDao
import com.tjcg.menuo.data.local.PosDao
import com.tjcg.menuo.data.response.Login.OutletsRS
import com.tjcg.menuo.databinding.ActivityMainBinding
import com.tjcg.menuo.fragment.*
import com.tjcg.menuo.utils.Constants
import com.tjcg.menuo.utils.PrefManager
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity(), View.OnClickListener {

    lateinit var fragment: Fragment
    var transaction: FragmentTransaction? = null
    var binding: ActivityMainBinding? = null
    var prefManager: PrefManager? = null
    var pixelPopupMenu: VerticalListPopupMenu? = null
    var posDao: PosDao? = null
    var orderDao: OrderDao? = null
    var onlineOrderCount:Int=0;
    var ongoingOrderCount:Int=0;
    var outlet_id:String ="";
    var sharedPreferences: SharedPreferences? =null
    var db: AppDatabase? = null

    @SuppressLint("NonConstantResourceId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view: View = binding!!.root
        setContentView(view)
        posDao = getDatabase(this)!!.posDao()
        orderDao = getDatabase(this)!!.orderDao()
        db = getDatabase(this)
        sharedPreferences = getSharedPreferences("com.tjcg.nentopos", Context.MODE_PRIVATE)
        val userName=sharedPreferences!!.getString("userName","No Profile")
        val outletname=sharedPreferences!!.getString("outLetName","No outLetName")
        outlet_id= sharedPreferences!!.getInt("current_outlets",1)!!.toString()
        binding!!.userName.setText(userName)
        binding!!.outletname.setText(outletname)
        prefManager = PrefManager(this)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val manager = applicationContext.getSystemService(TELEPHONY_SERVICE) as TelephonyManager
            prefManager!!.setBoolean("is_phone", manager.phoneType != TelephonyManager.PHONE_TYPE_NONE)
        }
        fragment = PosTabFragment.newInstance()
//        setSelectedPOS()
        setFragment(fragment)
        binding!!.mainLayout.drawerBtn.setOnClickListener(this)
        binding!!.llPos.setOnClickListener(this)
        binding!!.llAllOrders.setOnClickListener(this)
        binding!!.llCounterDisplay.setOnClickListener(this)
        binding!!.llKitchenDisplay.setOnClickListener(this)
        binding!!.llReservation.setOnClickListener(this)
        binding!!.llWaitingList.setOnClickListener(this)
        binding!!.mainLayout.navigationPos.setOnClickListener(this)
        var orderDao: OrderDao = AppDatabase.getDatabase(applicationContext)!!.orderDao()
        onlineOrderCount=orderDao.getOnlineOrdersCount(outlet_id);
        ongoingOrderCount=orderDao.getOngoingOrdersCount();
        var tvnavigationOnlineOrder: TextView=findViewById(R.id.navigation_online_order)
        tvnavigationOnlineOrder.text="Online Order ( "+getOnlineOrderCountMain().toString()+" )"
//        binding!!.navigationOnlineOrder.text="Online Order ( "+onlineOrderCount.toString()+" )"


        binding!!.mainLayout.navigationOnlineOrder.setOnClickListener(this)
        binding!!.mainLayout.navigationOngoingOrder.setOnClickListener(this)

        binding!!.mainLayout.logoutBtn.setOnClickListener {
            prefManager!!.setString(Constants.authorization_key, "")
            Constants.Authorization = ""
            prefManager!!.setBoolean("isLogin", false)
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_setting, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_setting -> {


            }
            else -> super.onOptionsItemSelected(item)
        }
        return true;
    }

    @JvmName("setFragment1")
    fun setFragment(fragment: Fragment?) {
        transaction = supportFragmentManager.beginTransaction()
        transaction!!.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
        transaction!!.replace(R.id.frame_layout, fragment!!)
        transaction!!.addToBackStack(null)
        transaction!!.commitAllowingStateLoss()
        if (fragment is PosTabFragment) {
            binding!!.mainLayout.bottomLayout.visibility = View.GONE
        } else {
            binding!!.mainLayout.bottomLayout.visibility = View.VISIBLE
        }
    }

   override fun onClick(view: View) {
        when (view.id) {
            R.id.drawer_btn -> {
                binding!!.drawerLayout.openDrawer(binding!!.drawerLayoutMain)
            }
            R.id.ll_pos -> {
                setSelectedPOS()
                setFragment(PosTabFragment.newInstance())
                binding!!.drawerLayout.closeDrawers()
            }
            R.id.navigation_pos -> {
                setFragment(PosTabFragment.newInstance())
                binding!!.drawerLayout.closeDrawers()
            }


        }
    }

    private fun setSelectedPOS() {
        binding!!.llPos.isSelected = true
        binding!!.llAllOrders.isSelected = false
        binding!!.llKitchenDisplay.isSelected = false
        binding!!.llCounterDisplay.isSelected = false
        binding!!.llReservation.isSelected = false
        binding!!.llWaitingList.isSelected = false
        binding!!.llMenu.isSelected = false
    }

    private fun setSelectedAllOrder() {
        binding!!.llPos.isSelected = false
        binding!!.llAllOrders.isSelected = true
        binding!!.llKitchenDisplay.isSelected = false
        binding!!.llCounterDisplay.isSelected = false
        binding!!.llReservation.isSelected = false
        binding!!.llWaitingList.isSelected = false
        binding!!.llMenu.isSelected = false
    }

    private fun setSelectedKitchen() {
        binding!!.llPos.isSelected = false
        binding!!.llAllOrders.isSelected = false
        binding!!.llKitchenDisplay.isSelected = true
        binding!!.llCounterDisplay.isSelected = false
        binding!!.llReservation.isSelected = false
        binding!!.llWaitingList.isSelected = false
        binding!!.llMenu.isSelected = false
    }

    private fun setSelectedWaiting() {
        binding!!.llPos.isSelected = false
        binding!!.llAllOrders.isSelected = false
        binding!!.llKitchenDisplay.isSelected = false
        binding!!.llCounterDisplay.isSelected = false
        binding!!.llReservation.isSelected = false
        binding!!.llWaitingList.isSelected = true
        binding!!.llMenu.isSelected = false
    }

    private fun setSelectedCOunter() {
        binding!!.llPos.isSelected = false
        binding!!.llAllOrders.isSelected = false
        binding!!.llKitchenDisplay.isSelected = false
        binding!!.llCounterDisplay.isSelected = true
        binding!!.llReservation.isSelected = false
        binding!!.llWaitingList.isSelected = false
        binding!!.llMenu.isSelected = false
    }

    private fun setSelectedReservation() {
        binding!!.llPos.isSelected = false
        binding!!.llAllOrders.isSelected = false
        binding!!.llKitchenDisplay.isSelected = false
        binding!!.llCounterDisplay.isSelected = false
        binding!!.llReservation.isSelected = true
        binding!!.llWaitingList.isSelected = false
        binding!!.llMenu.isSelected = false
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val fragment = supportFragmentManager.findFragmentById(R.id.frame_layout)
        if (fragment is PosTabFragment) {
            binding!!.mainLayout.bottomLayout.visibility = View.GONE
        } else {
            binding!!.mainLayout.bottomLayout.visibility = View.VISIBLE
        }
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

    fun getOnlineOrderCountMain(): Int{

        outlet_id= sharedPreferences!!.getInt("current_outlets",1)!!.toString()
        var count : Int=0
        var onlineOrders=orderDao!!.getOnlineOrdersCountMain(outlet_id)
        for (onlineOrder in onlineOrders){
            if(onlineOrder.future_order_date!=null)
            {
                var minute: Long = getDifMinute(onlineOrder.future_order_date.toString(), onlineOrder.future_order_time.toString())
                if(minute <= 60){
                    count=count+1
                }
            }else{
                count=count+1
            }
        }
        return count
    }

    public fun getOutlets() : List<OutletsRS>{
        val outletsRS = db!!.adminItemDao().outlets
        return outletsRS;
    }

}