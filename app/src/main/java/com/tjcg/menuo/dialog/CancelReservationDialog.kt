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
import com.tjcg.menuo.data.ResponseListener
import com.tjcg.menuo.databinding.DialogCancelReservationBinding
import com.tjcg.menuo.utils.LottieProgressDialog

class CancelReservationDialog(var text: String, var id: String, var isWaiting: Boolean) : DialogFragment(), ResponseListener {
    var binding: DialogCancelReservationBinding? = null
    var lottieProgressDialog: LottieProgressDialog? = null
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return dialog
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DialogCancelReservationBinding.inflate(layoutInflater)
        lottieProgressDialog = LottieProgressDialog(requireContext())
        binding!!.closeButton.setOnClickListener { dismiss() }
        binding!!.headerText.text = text
        binding!!.cancelReservationBtn.setOnClickListener {
            if (isWaiting) {
//                cancelWaiting(this)
            } else {
//                cancelReservation(this)
            }
        }
        return binding!!.root
    }

    override fun onResponseReceived(responseObject: Any, requestType: Int) {}
}