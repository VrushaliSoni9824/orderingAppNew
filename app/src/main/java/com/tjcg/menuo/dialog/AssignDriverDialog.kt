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
import androidx.lifecycle.Observer
import com.tjcg.menuo.MainActivity
import com.tjcg.menuo.data.local.AppDatabase
import com.tjcg.menuo.data.local.OrderDao
import com.tjcg.menuo.data.response.DriverData
import com.tjcg.menuo.databinding.DialogAsignDriverBinding
import com.tjcg.menuo.utils.LottieProgressDialog
import com.tjcg.menuo.viewmodel.OnlineOrderViewModel

class AssignDriverDialog(var onlineOrderViewModel: OnlineOrderViewModel, var mainActivity: MainActivity, var order_id: String) : DialogFragment() {
    var binding: DialogAsignDriverBinding? = null
    val strings = ArrayList<String>()
    val driverIdList = ArrayList<String>()
    var orderDao: OrderDao? = null
    var lottieProgressDialog: LottieProgressDialog? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = Dialog(requireActivity())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return dialog
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DialogAsignDriverBinding.inflate(layoutInflater)
        orderDao = AppDatabase.getDatabase(requireContext())!!.orderDao()
        lottieProgressDialog = LottieProgressDialog(requireContext())
        binding!!.closeButton.setOnClickListener { dismiss() }
        binding!!.closeButton2.setOnClickListener { dismiss() }
        binding!!.assignButton.setOnClickListener {
            lottieProgressDialog!!.showDialog()

            orderDao!!.updateDriverID(order_id,driverIdList.get(binding!!.driverSpinner.selectedIndex))
//            AsignDeliveryBoy(order_id,driverIdList.get(binding!!.driverSpinner.selectedIndex))

        }

        onlineOrderViewModel.getDriverList().observe(viewLifecycleOwner,
                Observer<List<DriverData>> { driverData: List<DriverData> ->

                    for (driverData1 in driverData) {
                        strings.add(driverData1.firstname!! + " " + driverData1.lastname!!)
                        driverIdList.add((driverData1.id))
                    }
                    if (strings.size > 0) {
                        binding!!.driverSpinner.setItems(strings)
                        binding!!.driverSpinner.selectItemByIndex(0)
                    }
                })

        return binding!!.root
    }


}