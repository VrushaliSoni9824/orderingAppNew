package com.tjcg.menuo.dialog

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.MediaPlayer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.SeekBar
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.tjcg.menuo.MainActivity
import com.tjcg.menuo.OrderPreviewActivity
import com.tjcg.menuo.R
import com.tjcg.menuo.data.ResponseListener
import com.tjcg.menuo.data.local.AppDatabase.Companion.getDatabase
import com.tjcg.menuo.data.local.OrderDao
import com.tjcg.menuo.databinding.DialogAcceptOrderBinding
import com.tjcg.menuo.databinding.DialogNewOrderBinding
import com.tjcg.menuo.utils.LottieProgressDialog

class NewOrderDialog(var clickListener: ClickListener, var orderId : String, var date: String, var amt : String) : DialogFragment() {
    var binding: DialogNewOrderBinding? = null
    var orderDao: OrderDao? = null
    var lottieProgressDialog: LottieProgressDialog? = null
    lateinit var mplayer: MediaPlayer


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = Dialog(requireActivity())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return dialog
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DialogNewOrderBinding.inflate(layoutInflater)
        orderDao = getDatabase(requireContext())!!.orderDao()
        lottieProgressDialog = LottieProgressDialog(requireContext())
        init()
        return binding!!.root
    }

    private fun init() {

        binding!!.btnclose.setOnClickListener {
            dialog!!.dismiss()
            clickListener.onClose();
        }
        binding!!.confirmOrderBtn.setOnClickListener {
            dialog!!.dismiss()
            clickListener.onOKClick(orderId);
        }
        binding!!.textView9.setText("Order ID :"+orderId.toString())
        binding!!.textView6.setText("Total Amount :"+amt.toString())
//        binding!!.tvDate.setText(date.toString())
//        binding!!.textView9.setText("Order ID :"+id.toString())

//        binding!!.cookingSeekbar.onProgressChangedListener = object : OnProgressChangedListener {
//            @SuppressLint("SetTextI18n")
//            override fun onProgressChanged(bubbleSeekBar: BubbleSeekBar, progress: Int, progressFloat: Float, fromUser: Boolean) {
//                binding!!.cookingTime.text = progress.toString() + ""
//            }
//
//            override fun getProgressOnActionUp(bubbleSeekBar: BubbleSeekBar, progress: Int, progressFloat: Float) {}
//            override fun getProgressOnFinally(bubbleSeekBar: BubbleSeekBar, progress: Int, progressFloat: Float, fromUser: Boolean) {}
//        }
//        binding!!.confirmOrderBtn.setOnClickListener { acceptOrder(this) }
    }

    interface ClickListener {
        fun onClose()
        fun onOKClick(orderId : String)
    }



}