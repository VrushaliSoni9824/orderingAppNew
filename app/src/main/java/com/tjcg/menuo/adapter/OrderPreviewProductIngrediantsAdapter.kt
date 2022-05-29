package com.tjcg.menuo.adapter

import android.annotation.SuppressLint
import android.content.*
import android.os.IBinder
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
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
import com.tjcg.menuo.databinding.ListIngrediantsBinding
import com.tjcg.menuo.dialog.*
import com.tjcg.menuo.utils.LottieProgressDialog
import net.posprinter.posprinterface.IMyBinder
import net.posprinter.service.PosprinterService
import org.json.JSONArray
import java.util.*


class OrderPreviewProductIngrediantsAdapter(var productIngredianstDataList: List<String>, var context: Context) : RecyclerView.Adapter<OrderPreviewProductIngrediantsAdapter.ViewHolder>() {


    public var order_id:String="";
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = ListIngrediantsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val product = productIngredianstDataList[holder.absoluteAdapterPosition]
        holder.binding.ingrediantsName.text="Ta bort: "+product.toString()

    }

    override fun getItemCount(): Int {
        return productIngredianstDataList.size
    }

    class ViewHolder(var binding: ListIngrediantsBinding) : RecyclerView.ViewHolder(binding.root)

}