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
import android.view.ViewGroup
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.RecyclerView
import com.tjcg.menuo.data.local.AppDatabase
import com.tjcg.menuo.data.local.OrderDao
import com.tjcg.menuo.data.local.PosDao
import com.tjcg.menuo.data.response.EntitiesModel.ProductEntity
import com.tjcg.menuo.databinding.ItemPrintPreviewAddon2Binding
//import com.tjcg.menuo.databinding.ItemPrintPreviewAddonBinding
import com.tjcg.menuo.databinding.ItemPrintPreviewBinding
import com.tjcg.menuo.dialog.*
import com.tjcg.menuo.utils.LottieProgressDialog
import net.posprinter.posprinterface.IMyBinder
import net.posprinter.service.PosprinterService
import org.json.JSONArray
import java.util.*
import kotlin.collections.ArrayList


class OrderPreviewProductAddonAdapter(var AddonDataList: ArrayList<String>,var AddonDataListPrice: ArrayList<String>, var context: Context) : RecyclerView.Adapter<OrderPreviewProductAddonAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = ItemPrintPreviewAddon2Binding.inflate(LayoutInflater.from(parent.context), parent, false)
        val intent = Intent(context, PosprinterService::class.java)
        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val product = AddonDataList[holder.absoluteAdapterPosition]
        val price = AddonDataListPrice[holder.absoluteAdapterPosition]
        holder.binding.textviewProductAddons.text=product
        holder.binding.textViewItemAddonPrice.text=if(price.equals("0")) "" else price
    }

    override fun getItemCount(): Int {
        return AddonDataList.size
    }

    class ViewHolder(var binding: ItemPrintPreviewAddon2Binding) : RecyclerView.ViewHolder(binding.root)

}