package com.tjcg.menuo.fragment

import android.annotation.SuppressLint
import android.content.*
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.lifecycle.ViewModelProviders
import com.tjcg.menuo.MainActivity
import com.tjcg.menuo.activity.LoginActivity
import com.tjcg.menuo.data.local.AppDatabase
import com.tjcg.menuo.data.local.OrderDao
import com.tjcg.menuo.data.local.PosDao
import com.tjcg.menuo.databinding.FragmentPosBinding
import com.tjcg.menuo.dialog.*

import com.tjcg.menuo.utils.Constants
import com.tjcg.menuo.utils.Constants.Authorization
import com.tjcg.menuo.utils.LottieProgressDialog
import com.tjcg.menuo.utils.PrefManager
import com.tjcg.menuo.viewmodel.OnlineOrderViewModel
import net.posprinter.posprinterface.IMyBinder
import java.lang.Float.*
import java.util.*
import kotlin.collections.ArrayList

class PosTabFragment : BaseFragment() {

    @JvmField
    var onlineOrderViewModel: OnlineOrderViewModel? = null
    private var selectedPosition = 0
    private var selectedPositionName = ""
    private var binding: FragmentPosBinding? = null
    private lateinit var mainActivity: MainActivity
    private var lottieProgressDialog: LottieProgressDialog? = null

    private var posDao: PosDao? = null
    private var prefManager: PrefManager? = null
    public var orderStatus: String = ""
    var myBinder: IMyBinder? = null
    val tableList = ArrayList<String>()
    var selected_tbl_pos: Int=0;
    var onlineOrderCount:Int=0;
    var ongoingOrderCount:Int=0;
    var isPos:String="0"

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPosBinding.inflate(inflater)
        var orderDao: OrderDao = AppDatabase.getDatabase(mainActivity!!)!!.orderDao()
        onlineOrderCount=orderDao.getOnlineOrdersCount(outletId);
        binding!!.navigationOnlineOrder.text="Online Order ( "+mainActivity!!.getOnlineOrderCountMain().toString()+" )"
        return binding!!.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        val sharedPreferences: SharedPreferences = mainActivity.getSharedPreferences("com.tjcg.nentopos", Context.MODE_PRIVATE)
//        isPos = sharedPreferences.getString(SharedPreferencesKeys.Is_Pos,"0").toString()
//        if(isPos!!.equals(getResources().getString(R.string.True))){
//            disableEnableControls(true,binding!!.llPos)
//            binding!!.smartTab.visibility=View.VISIBLE
//            binding!!.productRecyclerView.visibility=View.VISIBLE
//            binding!!.noaccess.visibility=View.GONE
//        }else{
//           disableEnableControls(false,binding!!.llPos)
//           disableEnableControls(false,binding!!.rvmain)
//            binding!!.logoutBtn.isEnabled=true
//            binding!!.smartTab.visibility=View.GONE
//            binding!!.productRecyclerView.visibility=View.GONE
//            binding!!.noaccess.visibility=View.VISIBLE
//            disableEnableControls(true,binding!!.bottomLayoutMain)

//        }

        lottieProgressDialog = LottieProgressDialog(mainActivity as Context)
        onlineOrderViewModel = ViewModelProviders.of(this).get(OnlineOrderViewModel::class.java)

        var orderDao: OrderDao = AppDatabase.getDatabase(mainActivity!!)!!.orderDao()
        onlineOrderCount=orderDao.getOnlineOrdersCount(outletId);
        binding!!.navigationOnlineOrder.text="Online Order ( "+mainActivity!!.getOnlineOrderCountMain().toString()+" )"
        prefManager = PrefManager(mainActivity)
        lottieProgressDialog!!.showDialog()
        initData()

        binding!!.logoutBtn.setOnClickListener {
            prefManager!!.setString(Constants.authorization_key, "")
            Authorization = ""
            prefManager!!.setBoolean("isLogin", false)
            startActivity(Intent(mainActivity, LoginActivity::class.java))
            mainActivity.finish()
        }
        binding!!.smartTab.setOnClickListener {
            binding!!.menuSpinner.clearFocus()
        }


    }

    @SuppressLint("SetTextI18n")
    private fun initData() {
        val sharedPreferences: SharedPreferences = mainActivity.getSharedPreferences("com.tjcg.nentopos", Context.MODE_PRIVATE)
        binding!!.navigationOnlineOrder.setOnClickListener {
        }
//        val sharedPreferences: SharedPreferences = mainActivity.getSharedPreferences("com.tjcg.nentopos", Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor =  sharedPreferences.edit()
        editor.putBoolean("fisrtSyncDone", true)
        editor.apply()
        editor.commit()
        Log.e("vvv","Dataloaded")
    }

    companion object {
        @JvmStatic
        fun newInstance(): PosTabFragment {
            return PosTabFragment()
        }

        fun formatHoursAndMinutes(totalMinutes: Int): String {
            var minutes = (totalMinutes % 60).toString()
            minutes = if (minutes.length == 1) "0$minutes" else minutes
            return (totalMinutes / 60).toString() + ":" + minutes
        }
    }
    /**
     * @author Vrushali Prajapoti
     * @since 24 November 2021
     * @suppress disable controlls when permission is not given
     */
    private fun disableEnableControls(enable: Boolean, vg: ViewGroup) {
        for (i in 0 until vg.childCount) {
            val child = vg.getChildAt(i)
            child.isEnabled = enable
            if (child is ViewGroup) {
                disableEnableControls(enable, child)
            }
        }
    }



}