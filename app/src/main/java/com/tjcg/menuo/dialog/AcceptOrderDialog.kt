package com.tjcg.menuo.dialog

import android.app.Dialog
import android.content.Context
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
import com.tjcg.menuo.R
import com.tjcg.menuo.data.ResponseListener
import com.tjcg.menuo.data.local.AppDatabase.Companion.getDatabase
import com.tjcg.menuo.data.local.OrderDao
import com.tjcg.menuo.databinding.DialogAcceptOrderBinding
import com.tjcg.menuo.utils.LottieProgressDialog

class AcceptOrderDialog(var order_id: String, var order_status: String,var mainActivity: MainActivity) : DialogFragment(), ResponseListener {
    var binding: DialogAcceptOrderBinding? = null
    var orderDao: OrderDao? = null
    var lottieProgressDialog: LottieProgressDialog? = null
    lateinit var mplayer: MediaPlayer
    val sharedPreferences: SharedPreferences = mainActivity!!.getSharedPreferences("com.tjcg.nentopos", Context.MODE_PRIVATE)
    var selectedOutletId : String =""

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = Dialog(requireActivity())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return dialog
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DialogAcceptOrderBinding.inflate(layoutInflater)
        orderDao = getDatabase(requireContext())!!.orderDao()
        lottieProgressDialog = LottieProgressDialog(requireContext())
        mplayer = MediaPlayer.create(mainActivity!!.applicationContext, R.raw.alarmtone);
        selectedOutletId=sharedPreferences.getInt("current_outlets",1).toString()
        init()
        return binding!!.root
    }

    private fun init() {

        binding!!.btnclose.setOnClickListener {
            dialog!!.dismiss()
        }
        binding!!.seekBar1?.setOnSeekBarChangeListener(object :
                SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seek: SeekBar,
                                           progress: Int, fromUser: Boolean) {
                // write custom code for progress is changed
                binding!!.cookingTime.text = progress.toString() + ""
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }
        });

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
        binding!!.closeButton.setOnClickListener { dismiss() }
    }


    override fun onResponseReceived(responseObject: Any, requestType: Int) {

        val sharedPreferences: SharedPreferences = mainActivity.getSharedPreferences("com.tjcg.nentopos", Context.MODE_PRIVATE)
        val outletId=sharedPreferences.getInt("current_outlets",1)
        val online_count=orderDao!!.getOnlineOrdersCount(outletId.toString());
        online_count

        if(online_count.toInt()>0)
        {
            if(!mplayer.isPlaying)
            {
                mplayer.start()
            }

        }
        else
        {
            if(mplayer.isPlaying)
            {
                mplayer.stop()
            }
        }

        Toast.makeText(context, "" + responseObject.toString(), Toast.LENGTH_SHORT).show()
//        mainActivity.setFragment(OnlineOrderFragment.newInstance())
//        setTargetFragment(OnlineOrderFragment,0)

    }
}