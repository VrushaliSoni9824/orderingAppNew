package com.tjcg.menuo.dialog

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.CompoundButton
import androidx.fragment.app.DialogFragment
import com.tjcg.menuo.databinding.DialogBookingStatusBinding

class BookingStatusDialog : DialogFragment() {
    var binding: DialogBookingStatusBinding? = null
    var confirm = false
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return dialog
    }

    override fun onStart() {
        super.onStart()
        val dialog = dialog
        if (dialog != null) {
            val width = ViewGroup.LayoutParams.WRAP_CONTENT
            val height = ViewGroup.LayoutParams.WRAP_CONTENT
            dialog.window!!.setLayout(width, height)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DialogBookingStatusBinding.inflate(layoutInflater)
        binding!!.closeButton.setOnClickListener { dismiss() }
        binding!!.closeButton2.setOnClickListener { dismiss() }
        binding!!.confirmRadioButton.setOnCheckedChangeListener { _: CompoundButton?, b: Boolean ->
            if (b) {
                confirm = true
                binding!!.cancelRadioButton.isChecked = false
            }
        }
        binding!!.cancelRadioButton.setOnCheckedChangeListener { _: CompoundButton?, b: Boolean ->
            if (b) {
                confirm = false
                binding!!.confirmRadioButton.isChecked = false
            }
        }
        return binding!!.root
    }
}