package com.tjcg.menuo

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.Toolbar
import com.tjcg.menuo.data.remote.ServiceGenerator
import com.tjcg.menuo.databinding.ActivityForgetPasswordBinding
import com.tjcg.menuo.databinding.FragmentAdminLoginBinding
import com.tjcg.menuo.utils.Constants
import com.tjcg.menuo.utils.LottieProgressDialog
import com.tjcg.menuo.utils.SharedPreferencesKeys
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ForgetPasswordActivity : AppCompatActivity() {
    var binding: ActivityForgetPasswordBinding? = null
    private var lottieProgressDialog: LottieProgressDialog? = null
    lateinit var reset : Button
    lateinit var email_et : EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forget_password)
        binding = ActivityForgetPasswordBinding.inflate(layoutInflater)
        lottieProgressDialog = LottieProgressDialog(this)

        reset = findViewById<Button>(R.id.reset)
        email_et = findViewById<EditText>(R.id.email_et)

        reset.setOnClickListener {
            var url : String = "https://apiv4.ordering.co/v400/en/menuo/users/forgot?email="+email_et.text.toString()
            forgetPassword(Constants.apiKey,url)
        }
    }

    fun forgetPassword(key: String, url : String) {
        lottieProgressDialog!!.showDialog()
        ServiceGenerator.nentoApi.forgetPassword(url)!!.enqueue(object :
            Callback<String?> {
            @SuppressLint("NewApi", "ResourceAsColor")
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onResponse(call: Call<String?>, response: Response<String?>) {
                if (response.isSuccessful) {
                    lottieProgressDialog!!.cancelDialog()
                    Toast.makeText(applicationContext,"Check your email",Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(applicationContext,"error",Toast.LENGTH_LONG).show()
                    lottieProgressDialog!!.cancelDialog()
                }
            }

            override fun onFailure(call: Call<String?>, t: Throwable) {
                lottieProgressDialog!!.cancelDialog()
            }
        })
    }

}