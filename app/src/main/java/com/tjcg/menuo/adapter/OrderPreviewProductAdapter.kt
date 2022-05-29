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
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tjcg.menuo.data.local.AppDatabase
import com.tjcg.menuo.data.local.OrderDao
import com.tjcg.menuo.data.local.PosDao
import com.tjcg.menuo.data.response.EntitiesModel.ProductEntity
import com.tjcg.menuo.data.response.order.KitchenOrderInfo
import com.tjcg.menuo.databinding.ItemPrintPreviewBinding
import com.tjcg.menuo.dialog.*
import com.tjcg.menuo.utils.LottieProgressDialog
import net.posprinter.posprinterface.IMyBinder
import net.posprinter.service.PosprinterService
import org.json.JSONArray
import java.util.*


class OrderPreviewProductAdapter(var productDataList: List<ProductEntity>, var context: Context) : RecyclerView.Adapter<OrderPreviewProductAdapter.ViewHolder>() {
    private var posDao: PosDao = AppDatabase.getDatabase(context)!!.posDao()
    var orderDao: OrderDao? = null
    var myBinder: IMyBinder? = null
    var ISCONNECT = false
    var transaction: FragmentTransaction? = null
    var lottieProgressDialog: LottieProgressDialog? = null
    public var paymentMethodType: String = ""
    public var order_id:String="";
    public var pos:String="";
    public var customerPaidAmount: String = ""
    var productaddonAdapter: OrderPreviewProductAddonAdapter? = null
    var productIngAdapter: OrderPreviewProductIngrediantsAdapter? = null

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
        val view = ItemPrintPreviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val intent = Intent(context, PosprinterService::class.java)
        lottieProgressDialog = LottieProgressDialog(context)
        orderDao = AppDatabase.getDatabase(context)!!.orderDao()
        context.bindService(intent, mSerconnection, Context.BIND_AUTO_CREATE)
        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val product = productDataList[holder.absoluteAdapterPosition]
//        holder.binding.viewDetails.visibility=View.GONE
        holder.binding.textviewProductName.text=product.quantity.toString()+" x "+product.name
        var addonText : String =""
        val addonsArray = JSONArray(product.options)
        val ingrediants = JSONArray(product.ingredients)
        val addonName = ArrayList<String>()
        val addonPrice = ArrayList<String>()
        val arrIngre = ArrayList<String>()

        for (i in 0..addonsArray.length() - 1) {
            val addon = addonsArray.getJSONObject(i)
            addonText= addonText+"<b>"+addon.getString("name")+"</b> <br/>"
            val suboptionsArray = addon.getJSONArray("suboptions")
            for(j in 0..suboptionsArray.length()-1){
                val suboption = suboptionsArray.getJSONObject(j)
                var name =  suboption.getInt("quantity").toString()+" x "+suboption.getString("name");
                var price = suboption.getString("price");
                addonName!!.add(name)
                addonPrice!!.add(price)
                addonText=addonText+suboption.getInt("quantity")+" x "+suboption.getString("name")+" "+suboption.getString("price")+" <br/>"
            }
            addonText=addonText+"<br/>"
        }

        for(i in 0..ingrediants.length()-1){
            var ingreItem = ingrediants.getJSONObject(i)
            arrIngre.add(ingreItem.getString("name"))
        }

        if(!product.comment.equals("null")){
            holder.binding!!.textViewComments.visibility=View.VISIBLE
            holder.binding!!.textViewComments.text=product.comment.toString()
        }else{
            holder.binding!!.textViewComments.visibility=View.GONE
        }


        productaddonAdapter = OrderPreviewProductAddonAdapter(addonName!!,addonPrice!!,context)
        holder.binding!!.rvAddon.layoutManager = LinearLayoutManager(context)
        holder.binding!!.rvAddon.adapter = productaddonAdapter

        productIngAdapter = OrderPreviewProductIngrediantsAdapter(arrIngre,context)
        holder.binding!!.rvIngrediants.layoutManager = LinearLayoutManager(context)
        holder.binding!!.rvIngrediants.adapter = productIngAdapter

        holder.binding.textviewProductAddons.text=Html.fromHtml(addonText)
        holder.binding.textViewItemPrice.text=product.price.toString()

    }

//    val onlineOrders: Unit
//        get() {
//            onlineOrderViewModel!!.getOnlineOrder(outlet_id, onlineOrderDataList[posi], this)
//            onlineOrderViewModel!!.onlineOrderDataObserver.observe(mainActivity, Observer {  })
//        }
    override fun getItemCount(): Int {
        return productDataList.size
    }

    class ViewHolder(var binding: ItemPrintPreviewBinding) : RecyclerView.ViewHolder(binding.root)

}