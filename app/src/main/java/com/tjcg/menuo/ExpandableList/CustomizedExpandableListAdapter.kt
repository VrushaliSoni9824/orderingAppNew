package com.tjcg.menuo.ExpandableList

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import cn.pedant.SweetAlert.SweetAlertDialog
import com.tjcg.menuo.Expandablectivity
import com.tjcg.menuo.OrderPreviewActivity
import com.tjcg.menuo.R
import com.tjcg.menuo.data.local.AppDatabase.Companion.getDatabase
import com.tjcg.menuo.data.local.OrderDao
import com.tjcg.menuo.data.remote.ServiceGenerator
import com.tjcg.menuo.utils.Constants
import com.tjcg.menuo.utils.PrefManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.HashMap

class CustomizedExpandableListAdapter(
    private val context: Context, private val expandableTitleList: List<String>,
    private val expandableDetailList: HashMap<String, List<String>>
) : BaseExpandableListAdapter() {
    private val orderDao: OrderDao
    var prefManager: PrefManager? = null
    var businessID: String? = null


    // Gets the data associated with the given child within the given group.
    override fun getChild(lstPosn: Int, expanded_ListPosition: Int): Any {
        return expandableDetailList[expandableTitleList[lstPosn]]!![expanded_ListPosition]
    }

    // Gets the ID for the given child within the given group.
    // This ID must be unique across all children within the group. Hence we can pick the child uniquely
    override fun getChildId(listPosition: Int, expanded_ListPosition: Int): Long {
        return expanded_ListPosition.toLong()
    }

    @SuppressLint("UseCompatLoadingForDrawables")  // Gets a View that displays the data for the given child within the given group.
    override fun getChildView(
        lstPosn: Int, expanded_ListPosition: Int,
        isLastChild: Boolean, convertView: View, parent: ViewGroup
    ): View {
        var convertView = convertView
        val expandedListText = getChild(lstPosn, expanded_ListPosition) as String
        if (convertView == null) {
            val layoutInflater =
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            convertView = layoutInflater.inflate(R.layout.list_item, null)
        }
        val expandedListTextView = convertView.findViewById<View>(R.id.txt_onum) as TextView
        val tvStatus = convertView.findViewById<View>(R.id.o_status) as TextView
        val tvOrderTime = convertView.findViewById<View>(R.id.o_time) as TextView
        val tvCustomer = convertView.findViewById<View>(R.id.o_customer) as TextView
        val ivDot = convertView.findViewById<View>(R.id.imageViewDot) as ImageView
        val ivNewOrder = convertView.findViewById<View>(R.id.imageViewNewOrder) as ImageView
        val ivOrderIcon = convertView.findViewById<View>(R.id.imageViewOrderIcon) as ImageView
        val btnPickUp = convertView.findViewById<View>(R.id.btnPickUp) as Button
        expandedListTextView.text = expandedListText
        val deliveryType = orderDao.getDeliveryType(expandedListText)
        val orderId : String = expandedListText
        btnPickUp.setOnClickListener {
            val pDialog = SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE)
            pDialog.progressHelper.barColor = Color.parseColor("#A5DC86")
            pDialog.titleText = "Orders App"
            pDialog.contentText = "Do you confirm that this order is ready to pickup?"
            pDialog.cancelText = "Cancel"
            pDialog.confirmText = "Ok"
            pDialog.setCancelButton(
                "Cancel"
            ) { pDialog.cancel() }
            pDialog.setConfirmButton(
                "Ok"
            ) {
                readyForPickUp(orderId)
            }
            pDialog.show()
        }
        var DeliveryText = ""
        when (deliveryType) {
            "1" -> DeliveryText = context.getString(R.string.Delivery)
            "2" -> DeliveryText = context.getString(R.string.Pick_Up)
            "3" -> DeliveryText = context.getString(R.string.Eat_In)
            "4" -> DeliveryText = context.getString(R.string.Curbside)
            "5" -> DeliveryText = context.getString(R.string.Driver_thru)
        }
        tvStatus.text = DeliveryText
        val statusType = orderDao.getStatus(expandedListText)
        if (statusType == "7") {
            btnPickUp.visibility = View.VISIBLE
        }
        when (statusType) {
            "1", "2", "5", "6", "11", "12", "16", "17", "10" -> {
                ivDot.setColorFilter(context.resources.getColor(R.color.green))
                ivNewOrder.setColorFilter(context.resources.getColor(R.color.green))
                ivOrderIcon.setImageDrawable(context.resources.getDrawable(R.drawable.ic_item_logo))
            }
            "3", "21", "20", "19", "18", "15", "14", "9", "8", "7", "4" -> {
                ivDot.setColorFilter(context.resources.getColor(R.color.yellow))
                ivNewOrder.setColorFilter(context.resources.getColor(R.color.yellow))
                ivOrderIcon.setImageDrawable(context.resources.getDrawable(R.drawable.ic_inprogress_1))
            }
            "0", "13" -> {
                ivDot.setColorFilter(context.resources.getColor(R.color.brown))
                ivNewOrder.setColorFilter(context.resources.getColor(R.color.brown))
                ivOrderIcon.setImageDrawable(context.resources.getDrawable(R.drawable.ic_pending))
            }
        }
        tvOrderTime.text = orderDao.getOrderDateTime(expandedListText)
        tvCustomer.text = orderDao.getCustomerName(expandedListText)
        convertView.setOnClickListener {
            val i = Intent(context, OrderPreviewActivity::class.java)
            i.putExtra("orderId", expandedListText)
            i.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(i)
        }
        return convertView
    }

    // Gets the number of children in a specified group.
    override fun getChildrenCount(listPosition: Int): Int {
        return expandableDetailList[expandableTitleList[listPosition]]!!.size
    }

    // Gets the data associated with the given group.
    override fun getGroup(listPosition: Int): Any {
        return expandableTitleList[listPosition]
    }

    // Gets the number of groups.
    override fun getGroupCount(): Int {
        return expandableTitleList.size
    }

    // Gets the ID for the group at the given position. This group ID must be unique across groups.
    override fun getGroupId(listPosition: Int): Long {
        return listPosition.toLong()
    }

    // Gets a View that displays the given group.
    // This View is only for the group--the Views for the group's children
    // will be fetched using getChildView()
    override fun getGroupView(
        listPosition: Int,
        isExpanded: Boolean,
        convertView: View,
        parent: ViewGroup
    ): View {
        var convertView = convertView
        val listTitle = getGroup(listPosition) as String
        prefManager = PrefManager(context)
        businessID=prefManager!!.getString("businessID")

        if (convertView == null) {
            val layoutInflater =
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            convertView = layoutInflater.inflate(R.layout.list_group, null)
        }
        val listTitleTextView = convertView.findViewById<View>(R.id.listTitle) as TextView
        listTitleTextView.setTypeface(null, Typeface.BOLD)
        listTitleTextView.text = listTitle
        if (!isExpanded) listTitleTextView.setCompoundDrawablesWithIntrinsicBounds(
            R.drawable.ic_baseline_add_circle_24,
            0,
            0,
            0
        ) else listTitleTextView.setCompoundDrawablesWithIntrinsicBounds(
            R.drawable.ic_new_minus,
            0,
            0,
            0
        )
        return convertView
    }

    // Indicates whether the child and group IDs are stable across changes to the underlying data.
    override fun hasStableIds(): Boolean {
        return false
    }

    // Whether the child at the specified position is selectable.
    override fun isChildSelectable(listPosition: Int, expandedListPosition: Int): Boolean {
        return true
    }

    // constructor
    init {
        orderDao = getDatabase(context)!!.orderDao()
    }

    fun readyForPickUp(orderId : String){
        ServiceGenerator.nentoApi.RejectORder(Constants.apiKey,orderId,"1")!!.enqueue(object :
            Callback<String?> {
            @SuppressLint("NewApi", "ResourceAsColor")
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onResponse(call: Call<String?>, response: Response<String?>) {
                if (response.isSuccessful) {

                    orderDao.changeOrderStatus(orderId, Constants.readyForPickUp)
                    val sweetAlertDialog = SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                    sweetAlertDialog.setCanceledOnTouchOutside(false)
                    sweetAlertDialog.setCancelable(false)
                    sweetAlertDialog.contentText = context.resources.getString(R.string.lbl_pickUpReady) //sweetAlertDialog.contentTextSize = resources.getDimension(R.dimen._7ssp).roundToInt(
                    sweetAlertDialog.confirmText = context.resources.getString(R.string.lbl_OK)
                    sweetAlertDialog.confirmButtonBackgroundColor = context.resources.getColor(R.color.green)
                    sweetAlertDialog.showCancelButton(true)
                    sweetAlertDialog.setCancelClickListener { sDialog ->
                        sDialog.cancel()
                    }
                    sweetAlertDialog.setConfirmClickListener { sDialog ->
                        context.startActivity(Intent(context, Expandablectivity::class.java).putExtra("businessID",businessID))
                    }.show()

//                    Toast.makeText(applicationContext,"order accepted", Toast.LENGTH_SHORT).show()

                } else {
                    val sweetAlertDialog = SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                    sweetAlertDialog.setCanceledOnTouchOutside(false)
                    sweetAlertDialog.setCancelable(false)
                    sweetAlertDialog.contentText = context.resources.getString(R.string.lbl_error) //sweetAlertDialog.contentTextSize = resources.getDimension(R.dimen._7ssp).roundToInt(
                    sweetAlertDialog.confirmText = context.resources.getString(R.string.lbl_OK)
                    sweetAlertDialog.confirmButtonBackgroundColor = context.resources.getColor(R.color.green)
                    sweetAlertDialog.showCancelButton(true)
                    sweetAlertDialog.setCancelClickListener { sDialog ->

                    }
                    sweetAlertDialog.setConfirmClickListener { sDialog ->
                        sDialog.cancel()
                    }.show()
                    Log.e("tag", " =  = = =error = ==  " + response.message())
                }
            }

            override fun onFailure(call: Call<String?>, t: Throwable) {
                val sweetAlertDialog = SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                sweetAlertDialog.setCanceledOnTouchOutside(false)
                sweetAlertDialog.setCancelable(false)
                sweetAlertDialog.contentText = context.resources.getString(R.string.lbl_error) //sweetAlertDialog.contentTextSize = resources.getDimension(R.dimen._7ssp).roundToInt(
                sweetAlertDialog.confirmText = context.resources.getString(R.string.lbl_OK)
                sweetAlertDialog.confirmButtonBackgroundColor = context.resources.getColor(R.color.green)
                sweetAlertDialog.showCancelButton(true)
                sweetAlertDialog.setCancelClickListener { sDialog ->
                    sDialog.cancel()
                }
                sweetAlertDialog.setConfirmClickListener { sDialog ->
                    sDialog.cancel()
                }.show()
                Log.e("tag", " =  = = =error = ==  " + t.message)
            }
        })
    }
}