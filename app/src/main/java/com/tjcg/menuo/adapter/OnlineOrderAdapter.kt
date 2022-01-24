package com.tjcg.menuo.adapter

//import retrofit2.Response

//import org.apache.http.HttpResponse
//import org.apache.http.client.methods.HttpPost
//import org.apache.http.conn.scheme.Scheme
//import org.apache.http.conn.scheme.SchemeRegistry
//import org.apache.http.HttpResponse
//import org.apache.http.NameValuePair
//import org.apache.http.client.methods.HttpPost
//import org.apache.http.conn.scheme.Scheme
//import org.apache.http.conn.scheme.SchemeRegistry
//import org.apache.http.impl.client.DefaultHttpClient
//import org.apache.http.impl.conn.SingleClientConnManager
//import org.apache.http.impl.client.DefaultHttpClient
//import org.apache.http.impl.conn.SingleClientConnManager
import android.annotation.SuppressLint
import android.content.*
import android.os.IBinder
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.RecyclerView
import com.tjcg.menuo.MainActivity
import com.tjcg.menuo.data.ResponseListener
import com.tjcg.menuo.data.local.AppDatabase
import com.tjcg.menuo.data.local.OrderDao
import com.tjcg.menuo.data.local.PosDao
import com.tjcg.menuo.data.response.order.OnlineOrderData
import com.tjcg.menuo.databinding.ItemOnlineOrderBinding
import com.tjcg.menuo.dialog.*
import com.tjcg.menuo.utils.Constants
import com.tjcg.menuo.utils.LottieProgressDialog
import com.tjcg.menuo.viewmodel.OnlineOrderViewModel
import net.posprinter.posprinterface.IMyBinder
import net.posprinter.service.PosprinterService
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern


class OnlineOrderAdapter(var mainActivity: MainActivity,
                         var outlet_id: String,
                         var onlineOrderDataList: List<OnlineOrderData>,
                         var onlineOrderViewModel: OnlineOrderViewModel) : RecyclerView.Adapter<OnlineOrderAdapter.ViewHolder>(), ResponseListener, CalculatorDialog.ClickListener, OrderStatusDialog.ClickListener, OptionReceiptDialog.ClickListener, CardTerminalDialog.ClickListener {
    private var posDao: PosDao = AppDatabase.getDatabase(mainActivity)!!.posDao()
    var orderDao: OrderDao? = null
    var myBinder: IMyBinder? = null
    var ISCONNECT = false
    var transaction: FragmentTransaction? = null
    var lottieProgressDialog: LottieProgressDialog? = null
    public var paymentMethodType: String = ""
    public var order_id:String="";
    public var pos:String="";
    public var customerPaidAmount: String = ""

//    var param: List<NameValuePair>? = nullg


    var mSerconnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            myBinder = service as IMyBinder
            Log.e("myBinder", "connect")
        }

        override fun onServiceDisconnected(name: ComponentName) {
            Log.e("myBinder", "disconnect")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = ItemOnlineOrderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val intent = Intent(mainActivity.applicationContext, PosprinterService::class.java)
        lottieProgressDialog = LottieProgressDialog(mainActivity as Context)
        orderDao = AppDatabase.getDatabase(mainActivity)!!.orderDao()
        mainActivity.bindService(intent, mSerconnection, Context.BIND_AUTO_CREATE)



        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val onlineOrderData = onlineOrderDataList[holder.absoluteAdapterPosition]
//        holder.binding.viewDetails.visibility=View.GONE
        if(onlineOrderData.order_status.equals("4") || onlineOrderData.order_status.equals("5") ){
            holder.binding.acceptOrder.visibility=View.GONE
            holder.binding.cancelOrder.visibility=View.GONE
//            holder.binding.updateOrder.visibility=View.GONE
            holder.binding.assignDriver.visibility=View.GONE
        }
        else if(onlineOrderData.order_status.equals("2")){
            holder.binding.acceptOrder.visibility=View.GONE
            holder.binding.completeOrder.visibility=View.VISIBLE

        }
        else{ holder.binding.acceptOrder.visibility=View.VISIBLE
        holder.binding.cancelOrder.visibility=View.VISIBLE
//            holder.binding.updateOrder.visibility=View.VISIBLE
            holder.binding.assignDriver.visibility=View.VISIBLE
        }



        holder.binding.orderId.text = onlineOrderData.order_id.toString()
        holder.binding.orderTime.text = onlineOrderData.order_date + " " + onlineOrderData.order_time
        holder.binding.tableName.text = "N/A"
        try{

            holder.binding.pickupTime.text =if(onlineOrderData.order_pickup_at.equals("") || onlineOrderData.order_pickup_at.equals(null)) "N/A" else onlineOrderData.order_pickup_at
        }catch (ex: Exception){
            holder.binding.pickupTime.text = "N/A"
        }

        holder.binding.totalAmount.text = Constants.CUREENCY + onlineOrderData.totalamount

        holder.binding.paymentStatus.text = if(onlineOrderData.pis_payment_received.equals("0")) "Pending" else "Paid"
        try{

            holder.binding.futureOrderDate.text =if(onlineOrderData.future_order_date.equals(null)) "N/A" else onlineOrderData.future_order_date+" "+ if(onlineOrderData.future_order_time.equals(null)) "N/A" else onlineOrderData.future_order_time.toString()
        }catch (ex: Exception){
    holder.binding.assignedDriver.text="N/A"
}
//        holder.binding.futureOrderDate.text =if(holder.binding.futureOrderDate.text.equals(null) || onlineOrderData.future_order_time!!.equals(null))
//      "N/A"  else onlineOrderData.future_order_date + "s " + onlineOrderData.future_order_time

        try {
            val driverName = posDao.getDriverName(onlineOrderData.driver_user_id)+" "+posDao.getDriverLastNAme(onlineOrderData.driver_user_id)
            holder.binding.assignedDriver.text = if(driverName.isEmpty()) "N/A" else driverName
        }catch (ex: Exception){
            holder.binding.assignedDriver.text="N/A"
        }


        holder.binding.viewDetails.setOnClickListener {
//            showlist(outlet_id, onlineOrderData.order_id)

            //onlineOrderViewModel.getInvoice(outlet_id, onlineOrderData.order_id, this)
        }
        holder.binding.viewDetails.setOnClickListener {

            val sharedPreferences: SharedPreferences = mainActivity.getSharedPreferences("com.tjcg.nentopos", Context.MODE_PRIVATE)
            val editor:SharedPreferences.Editor =  sharedPreferences.edit()
            editor.putString("online_order_id", onlineOrderData.order_id.toString())
            editor.putString("online_outlet_id", outlet_id)
            editor.putInt("online_position", position.toInt())
            editor.apply()
            editor.commit()


//            mainActivity.startActivity(Intent(mainActivity.getApplicationContext(), InvoiceActivity::class.java).putExtra("outlet_id",onlineOrderData.outlet_id).putExtra("order_id",onlineOrderData.order_id).putExtra("position",position.toString()))
        }
        holder.binding.acceptOrder.setOnClickListener {
            lottieProgressDialog!!.showDialog()
            val acceptOrderDialog = AcceptOrderDialog(onlineOrderData.order_id.toString(), onlineOrderData.order_status!!, mainActivity)
            acceptOrderDialog.show(mainActivity.supportFragmentManager, "")
            lottieProgressDialog!!.cancelDialog()

        }
        holder.binding.cancelOrder.setOnClickListener {

//            lottieProgressDialog!!.showDialog()
            val acceptOrderDialog = CancelOrderDialog(onlineOrderData)
            acceptOrderDialog.show(mainActivity.supportFragmentManager, "")


        }
        holder.binding.assignDriver.setOnClickListener {
            val acceptOrderDialog = AssignDriverDialog(onlineOrderViewModel, mainActivity, onlineOrderData.order_id.toString())
            acceptOrderDialog.show(mainActivity.supportFragmentManager, "")
        }


    }

//    val onlineOrders: Unit
//        get() {
//            onlineOrderViewModel!!.getOnlineOrder(outlet_id, onlineOrderDataList[posi], this)
//            onlineOrderViewModel!!.onlineOrderDataObserver.observe(mainActivity, Observer {  })
//        }
    override fun getItemCount(): Int {
        return onlineOrderDataList.size
    }

    class ViewHolder(var binding: ItemOnlineOrderBinding) : RecyclerView.ViewHolder(binding.root)

    fun splitString(msg: String?, lineSize: Int): ArrayList<String>? {
        val res = ArrayList<String>()
        val p: Pattern = Pattern.compile("\\b.{1," + (lineSize - 1) + "}\\b\\W?")
        val m: Matcher = p.matcher(msg)
        while (m.find()) {
            res.add(m.group())
        }
        return res
    }


    fun byteArrayOfInts(vararg ints: String) = ByteArray(ints.size) { pos -> ints[pos].toByte() }


    override fun onResponseReceived(responseObject: Any, requestType: Int) {
        responseObject
    }




    override fun onCalculatorNext(customerPaidAmount: String) {
        this.customerPaidAmount = customerPaidAmount
        val orderStatusDialog = OrderStatusDialog(this)
//        checkOut(cartProductDataList!!);
        orderStatusDialog.show(mainActivity.supportFragmentManager, "Order Status")
    }

    override fun onNext(order_status: String) {
        val optionReceiptDialog = OptionReceiptDialog(this,"")
        optionReceiptDialog.show(mainActivity.supportFragmentManager, "")
    }

    override fun onPrint(order_status: String) {

    }

    override fun onSkip(order_status: String) {

    }

    fun dismissAllDialogs(manager: FragmentManager?) {
        val fragments: List<Fragment> = manager!!.getFragments()
                ?: return
        for (fragment in fragments) {
            if (fragment is DialogFragment) {
                val dialogFragment: DialogFragment = fragment as DialogFragment
                dialogFragment.dismissAllowingStateLoss()
            }
            val childFragmentManager: FragmentManager = fragment.childFragmentManager
            if (childFragmentManager != null) dismissAllDialogs(childFragmentManager)
        }
    }


    override fun onSendMain(email: String) {
    }

    override fun onNextCard(order_status: String) {
            val optionReceiptDialog = OptionReceiptDialog(this,"")
            optionReceiptDialog.show(mainActivity.supportFragmentManager, "")
    }
}