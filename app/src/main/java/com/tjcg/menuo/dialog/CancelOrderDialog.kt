package com.tjcg.menuo.dialog

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.tjcg.menuo.data.ResponseListener
import com.tjcg.menuo.data.local.AppDatabase.Companion.getDatabase
import com.tjcg.menuo.data.local.OrderDao
import com.tjcg.menuo.data.response.order.OngoingOrderData
import com.tjcg.menuo.data.response.order.OnlineOrderData
import com.tjcg.menuo.databinding.DialogCancelOrderBinding
import com.tjcg.menuo.utils.LottieProgressDialog

class CancelOrderDialog : DialogFragment, ResponseListener {
    var binding: DialogCancelOrderBinding? = null
    var ongoingOrder: OngoingOrderData? = null
    var onlineOrderData: OnlineOrderData? = null
    var orderDao: OrderDao? = null
    var lottieProgressDialog: LottieProgressDialog? = null

    constructor(ongoingOrder: OngoingOrderData?) {
        this.ongoingOrder = ongoingOrder
    }

    constructor(onlineOrderData: OnlineOrderData?) {
        this.onlineOrderData = onlineOrderData
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return dialog
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DialogCancelOrderBinding.inflate(layoutInflater)
        lottieProgressDialog = LottieProgressDialog(requireContext())
        orderDao = getDatabase(requireContext())!!.orderDao()
        binding!!.closeButton.setOnClickListener { dismiss() }
        binding!!.closeButton2.setOnClickListener { dismiss() }
        if (ongoingOrder != null) {
            binding!!.orderId.text = ongoingOrder!!.order_id
        } else {
            binding!!.orderId.text = onlineOrderData!!.order_id.toString()
        }
        return binding!!.root
    }

    override fun onResponseReceived(responseObject: Any, requestType: Int) {
        Toast.makeText(context, "" + responseObject.toString(), Toast.LENGTH_SHORT).show()
    }
}