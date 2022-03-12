package com.tjcg.menuo.adapter

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import cn.pedant.SweetAlert.SweetAlertDialog
import com.tjcg.menuo.Expandablectivity
import com.tjcg.menuo.R
import com.tjcg.menuo.data.local.AppDatabase
import com.tjcg.menuo.data.local.OrderDao
import com.tjcg.menuo.data.remote.ServiceGenerator.nentoApi
import com.tjcg.menuo.utils.Constants
import com.tjcg.menuo.utils.Constants.readyForPickUp
import com.tjcg.menuo.utils.LottieProgressDialog
import com.tjcg.menuo.utils.PrefManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OrderComepleteAdapter(private val context: Context, private val onClickListener: orderClickListener, var onlineOrderDataList: List<String>) : RecyclerView.Adapter<OrderComepleteAdapter.ViewHolder>() {
    private var orderDao: OrderDao? = null
    var index = -1
    var businessID: String = ""
    var lottieProgressDialog: LottieProgressDialog? = null

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var expandedListTextView = itemView.findViewById<View>(R.id.txt_onum) as TextView
        var tvStatus = itemView.findViewById<View>(R.id.o_status) as TextView
        var tvPreparedIn = itemView.findViewById<View>(R.id.tvPreparedIn) as TextView
        var tvOrderTime = itemView.findViewById<View>(R.id.o_time) as TextView
        var tvCustomer = itemView.findViewById<View>(R.id.o_customer) as TextView
        var OrderTotal = itemView.findViewById<View>(R.id.OrderTotal) as TextView
        var ivDot = itemView.findViewById<View>(R.id.imageViewDot) as ImageView
        var ivNewOrder = itemView.findViewById<View>(R.id.imageViewNewOrder) as ImageView
        var ivOrderIcon = itemView.findViewById<View>(R.id.imageViewOrderIcon123) as ImageView
        var btnPickUp = itemView.findViewById<View>(R.id.btnPickUp) as Button
        var expandedListItem = itemView.findViewById<View>(R.id.expandedListItem) as LinearLayout
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        orderDao = AppDatabase.getDatabase(context)!!.orderDao()
        var itemDetailList: String = onlineOrderDataList[position]
        var orderID: String = itemDetailList
        var deliveryType: String = orderDao!!.getDeliveryType(itemDetailList.toString())
        var orderTotal: Int = orderDao!!.getOrderTotal(itemDetailList.toString())
        holder.OrderTotal.setText(orderTotal.toString() + " KR")
        businessID = PrefManager(context).getString("businessID")!!

        holder.btnPickUp.setOnClickListener(View.OnClickListener {
            val pDialog = SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
            pDialog.progressHelper.barColor = Color.parseColor("#A5DC86")
            pDialog.titleText = "Orders App"
            pDialog.contentText = "Vill du verkligen slutföra ordern?"
            //                pDialog.setCancelText("Cancel");
//                pDialog.setConfirmText("Ok");
            pDialog.setCancelButton("Cancel") { pDialog.cancel() }
            pDialog.setConfirmButton("Ok") { readyForPickUp(orderID) }
            pDialog.show()
        })


        var DeliveryText = ""


        var time = if(orderDao!!.getPreparedIn(orderID)>0 && orderDao!!.getPreparedIn(orderID)!= null) orderDao!!.getPreparedIn(orderID).toString()+" min" else "";
        holder.tvPreparedIn.setText(time.toString())
        holder.tvPreparedIn.visibility=View.GONE
        holder.expandedListTextView.setText(orderID)

        val statusType = orderDao!!.getStatus(itemDetailList.toString())

        if (statusType == "7") {
            holder.btnPickUp.setVisibility(View.VISIBLE)
        } else {
            holder.btnPickUp.setVisibility(View.GONE)
        }

        when (statusType) {
            "1", "2", "5", "6", "11", "12", "16", "17", "10" -> {
                holder.ivDot.setColorFilter(context.resources.getColor(R.color.green))
                holder.ivNewOrder.setColorFilter(context.resources.getColor(R.color.green))
                holder.ivOrderIcon.setImageDrawable(context.resources.getDrawable(R.drawable.ic_item_logo))
            }
            "3", "21", "20", "19", "18", "15", "14", "9", "8", "7", "4" -> {
                holder.ivDot.setColorFilter(context.resources.getColor(R.color.yellow))
                holder.ivNewOrder.setColorFilter(context.resources.getColor(R.color.yellow))
                holder.ivOrderIcon.setImageDrawable(context.resources.getDrawable(R.drawable.ic_inprogress_1))
            }
            "0", "13" -> {
                holder.ivDot.setColorFilter(context.resources.getColor(R.color.brown))
                holder.ivNewOrder.setColorFilter(context.resources.getColor(R.color.brown))
                holder.ivOrderIcon.setImageDrawable(context.resources.getDrawable(R.drawable.ic_pending))
            }
        }

        when (deliveryType) {
            "1" -> {
                DeliveryText = context.getString(R.string.Delivery)
            }
            "2" -> {
                DeliveryText = context.getString(R.string.Pick_Up)
                holder.ivDot.setColorFilter(context.resources.getColor(R.color.yellow))
                holder.ivNewOrder.setColorFilter(context.resources.getColor(R.color.yellow))
                holder.ivOrderIcon.setImageDrawable(context.resources.getDrawable(R.drawable.ic_inprogress_1))
            }
            "3" -> {
                DeliveryText = context.getString(R.string.Eat_In)
            }
            "4" -> {
                DeliveryText = context.getString(R.string.Curbside)
            }
            "5" -> {
                DeliveryText = context.getString(R.string.Driver_thru)
            }
        }
        when (deliveryType) {
            "2" -> {
                holder.ivOrderIcon.setImageDrawable(context.resources.getDrawable(R.drawable.ic_item_logo))
            }
            "1" -> {
                holder.ivOrderIcon.setImageDrawable(context.resources.getDrawable(R.drawable.ic_inprogress_1))
            }
        }
        holder.tvStatus.setText(DeliveryText)



        holder.tvOrderTime.setText(orderDao!!.getOrderDateTime(itemDetailList.toString()))
        holder.tvCustomer.setText(orderDao!!.getCustomerName(itemDetailList.toString()))

       /* holder.convertView.setOnClickListener(View.OnClickListener {
            val i = Intent(context, OrderPreviewActivity::class.java)
            i.putExtra("orderId", itemDetailList)
            i.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(i)
        })*/

        holder.expandedListItem.setOnClickListener {
            index = position
            notifyDataSetChanged()
            onClickListener.onItemClick(itemDetailList, position)
        }




    }


override fun getItemCount(): Int {

        return onlineOrderDataList.size
    }


    interface orderClickListener {
        fun onItemClick(orderId: String, position: Int)

    }


    fun readyForPickUp(orderId: String?) {
        lottieProgressDialog!!.showDialog()
        nentoApi.RejectORder(Constants.apiKey, orderId!!, "4").enqueue(object : Callback<String?> {
            override fun onResponse(call: Call<String?>, response: Response<String?>) {
                if (response.isSuccessful) {
                    orderDao!!.changeOrderStatus(orderId, readyForPickUp)
                    //lottieProgressDialog.cancelDialog()
                    val sweetAlertDialog = SweetAlertDialog(context, SweetAlertDialog.SUCCESS_TYPE)
                    sweetAlertDialog.contentText = context.resources.getString(R.string.lbl_pickUpReady)
                    sweetAlertDialog.confirmText = context.resources.getString(R.string.lbl_OK)
                    sweetAlertDialog.setCancelable(false)
                    sweetAlertDialog.setConfirmClickListener {
                        val i = Intent(context, Expandablectivity::class.java)
                        i.putExtra("businessID", businessID)
                        context.startActivity(i)
                    }
                    sweetAlertDialog.show()
                    //                    sweetAlertDialog.setContentText(context.getResources().getString(R.string.lbl_pickUpReady));
                }
            }

            override fun onFailure(call: Call<String?>, t: Throwable) {
                lottieProgressDialog!!.cancelDialog()
                val sweetAlertDialog = SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE)
                sweetAlertDialog.contentText = context.resources.getString(R.string.lbl_error)
                sweetAlertDialog.confirmText = context.resources.getString(R.string.lbl_OK)
                sweetAlertDialog.setCancelable(false)
                sweetAlertDialog.setConfirmClickListener { sweetAlertDialog -> sweetAlertDialog.cancel() }
                sweetAlertDialog.show()
            }
        })
    }
}