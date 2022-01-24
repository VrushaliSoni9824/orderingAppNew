package com.tjcg.menuo

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import cn.pedant.SweetAlert.SweetAlertDialog
import com.tjcg.menuo.data.local.AppDatabase
import com.tjcg.menuo.data.remote.ServiceGenerator
import com.tjcg.menuo.utils.Constants
import com.tjcg.menuo.utils.LottieProgressDialog
import com.tjcg.menuo.utils.PrefManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class OrderRejectActivity : AppCompatActivity() {

    private lateinit var textViewPhoneNumber: TextView
    private lateinit var editTextRejectionInfo: EditText
    private lateinit var linearLayoutBack: LinearLayout
    lateinit var buttonReject: TextView
    var phoneNumber: String = ""
    var orderId: String = ""
    var db: AppDatabase? = null
    var prefManager: PrefManager? = null
    var businessID: String? = null
    var lottieProgressDialog: LottieProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_reject)

        lottieProgressDialog = LottieProgressDialog(this)
        textViewPhoneNumber = findViewById(R.id.textViewPhoneNumber)
        editTextRejectionInfo = findViewById(R.id.editTextRejectionInfo)
        buttonReject = findViewById(R.id.buttonReject)
        linearLayoutBack = findViewById(R.id.linearLayoutBack)
        db = AppDatabase.getDatabase(applicationContext)
        prefManager = PrefManager(applicationContext)
        businessID=prefManager!!.getString("businessID")

        val intent = intent
        phoneNumber = intent.getStringExtra("phoneNumber")!!
        orderId = intent.getStringExtra("orderId")!!
        textViewPhoneNumber.text = phoneNumber

        linearLayoutBack.setOnClickListener {
            startActivity(Intent(this, Expandablectivity::class.java).putExtra("businessID",businessID))

            finish()
        }
        buttonReject.setOnClickListener {

            val sweetAlertDialog = SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
            sweetAlertDialog.setCanceledOnTouchOutside(false)
            sweetAlertDialog.setCancelable(false)
            sweetAlertDialog.contentText = resources.getString(R.string.lbl_reject) //sweetAlertDialog.contentTextSize = resources.getDimension(R.dimen._7ssp).roundToInt()
            sweetAlertDialog.cancelText = resources.getString(R.string.lbl_NO)
            sweetAlertDialog.confirmText = resources.getString(R.string.lbl_YES)
            sweetAlertDialog.confirmButtonBackgroundColor = resources.getColor(R.color.green)
            sweetAlertDialog.cancelButtonBackgroundColor = resources.getColor(R.color.red)
            sweetAlertDialog.showCancelButton(true)
            sweetAlertDialog.setCancelClickListener { sDialog ->
            sDialog.cancel()
            }
            sweetAlertDialog.setConfirmClickListener { sDialog ->
                setRejectOrder()
            }.show()
        }
    }

    fun setRejectOrder(){
        lottieProgressDialog!!.showDialog()
        ServiceGenerator.nentoApi.RejectORder(Constants.apiKey,orderId,"6")!!.enqueue(object :
            Callback<String?> {
            @SuppressLint("NewApi", "ResourceAsColor")
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onResponse(call: Call<String?>, response: Response<String?>) {
                if (response.isSuccessful) {
                    db!!.orderDao().changeOrderStatus(orderId,Constants.rejectStatus)
                    lottieProgressDialog!!.cancelDialog()
                    val sweetAlertDialog = SweetAlertDialog(this@OrderRejectActivity, SweetAlertDialog.WARNING_TYPE)
                    sweetAlertDialog.setCanceledOnTouchOutside(false)
                    sweetAlertDialog.setCancelable(false)
                    sweetAlertDialog.contentText = resources.getString(R.string.lbl_reject_done) //sweetAlertDialog.contentTextSize = resources.getDimension(R.dimen._7ssp).roundToInt(
                    sweetAlertDialog.confirmText = resources.getString(R.string.lbl_OK)
                    sweetAlertDialog.confirmButtonBackgroundColor = resources.getColor(R.color.green)
                    sweetAlertDialog.showCancelButton(true)
                    sweetAlertDialog.setCancelClickListener { sDialog ->
                        sDialog.cancel()
                    }
                    sweetAlertDialog.setConfirmClickListener { sDialog ->
                        startActivity(Intent(applicationContext, Expandablectivity::class.java).putExtra("businessID",businessID))

                        finish()
                    }.show()

//                    Toast.makeText(applicationContext,"order accepted", Toast.LENGTH_SHORT).show()

                } else {
                    lottieProgressDialog!!.cancelDialog()
                    val sweetAlertDialog = SweetAlertDialog(applicationContext, SweetAlertDialog.WARNING_TYPE)
                    sweetAlertDialog.setCanceledOnTouchOutside(false)
                    sweetAlertDialog.setCancelable(false)
                    sweetAlertDialog.contentText = resources.getString(R.string.lbl_error) //sweetAlertDialog.contentTextSize = resources.getDimension(R.dimen._7ssp).roundToInt(
                    sweetAlertDialog.confirmText = resources.getString(R.string.lbl_OK)
                    sweetAlertDialog.confirmButtonBackgroundColor = resources.getColor(R.color.green)
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
                lottieProgressDialog!!.cancelDialog()
                val sweetAlertDialog = SweetAlertDialog(applicationContext, SweetAlertDialog.WARNING_TYPE)
                sweetAlertDialog.setCanceledOnTouchOutside(false)
                sweetAlertDialog.setCancelable(false)
                sweetAlertDialog.contentText = resources.getString(R.string.lbl_error) //sweetAlertDialog.contentTextSize = resources.getDimension(R.dimen._7ssp).roundToInt(
                sweetAlertDialog.confirmText = resources.getString(R.string.lbl_OK)
                sweetAlertDialog.confirmButtonBackgroundColor = resources.getColor(R.color.green)
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