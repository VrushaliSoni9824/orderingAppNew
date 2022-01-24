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
import com.tjcg.menuo.databinding.DialogCardTerminalsBinding

class CardTerminalDialog(var clickListener: ClickListener) : DialogFragment() {
    var binding: DialogCardTerminalsBinding? = null
    var order_status="4";
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return dialog
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DialogCardTerminalsBinding.inflate(layoutInflater)
        binding!!.closeButton.setOnClickListener { dismiss() }
        binding!!.next.setOnClickListener {
            clickListener.onNextCard(order_status)
//            val optionReceiptDialog = OptionReceiptDialog()
//            optionReceiptDialog.show(childFragmentManager, "")
        }
        return binding!!.root
    }

    interface ClickListener {
        fun onNextCard(order_status:String)
    }
}