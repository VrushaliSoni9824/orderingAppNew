package com.tjcg.menuo.dialog

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import com.tjcg.menuo.databinding.DialogOrderStatusBinding
import com.tjcg.menuo.utils.Constants

class OrderStatusDialog(var clickListener: ClickListener) : DialogFragment() {
    var binding: DialogOrderStatusBinding? = null
    var order_status="4";
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return dialog
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DialogOrderStatusBinding.inflate(layoutInflater)
        binding!!.closeButton.setOnClickListener { view: View? -> dismiss() }
        binding!!.next.setOnClickListener { view: View? ->
            clickListener.onNext(order_status)
        }
        binding!!.chkComplete.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked) {
                binding!!.chkOngoig.isChecked = false
                order_status=Constants.ORDER_STATUS_SERVED
            }
//            binding!!.chkComplete.isChecked=true
        }
        binding!!.chkOngoig.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked) {
                binding!!.chkComplete.isChecked = false
                order_status=Constants.ORDER_STATUS_PROCESSING
            }
//            binding!!.chkComplete.isChecked=false
//            binding!!.chkOngoig.isChecked=true
        }
        return binding!!.root
    }

    interface ClickListener {
        fun onNext(order_status:String)
    }
}