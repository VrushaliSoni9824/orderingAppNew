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
import com.tjcg.menuo.databinding.DialogOptionReceiptBinding

class OptionReceiptDialog(var clickListener: ClickListener, var order_status: String) : DialogFragment() {
    var binding: DialogOptionReceiptBinding? = null
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return dialog
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DialogOptionReceiptBinding.inflate(layoutInflater)
        binding!!.closeButton.setOnClickListener { view: View? -> dismiss()

        }
        binding!!.tvEmail.setOnClickListener {
            clickListener.onSendMain(binding!!.edEmail.text.toString())
        }
        binding!!.tvPrint.setOnClickListener {
            clickListener.onPrint(order_status)
        }
        binding!!.btnSkip.setOnClickListener {
//            dismiss()
//            setTargetFragment(PosTabFragment.newInstance(),1)
//            clickListener.onSkip(order_status)
//            startActivity(Intent(context, MainActivity::class.java))

            clickListener.onSkip(order_status)
        }
        binding!!.yes.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked) {
                binding!!.no.isChecked = false
                binding!!.lEmail.visibility=View.VISIBLE
                binding!!.tvEmail.visibility=View.VISIBLE

            }
//            binding!!.chkComplete.isChecked=true
        }
        binding!!.no.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked) {
                binding!!.yes.isChecked = false
                binding!!.lEmail.visibility=View.GONE
                binding!!.tvEmail.visibility=View.GONE
            }
//            binding!!.chkComplete.isChecked=true
        }
        return binding!!.root
    }

    interface ClickListener {
        fun onPrint(order_status: String)
        fun onSkip(order_status: String)
        fun onSendMain(order_status: String)

    }

}