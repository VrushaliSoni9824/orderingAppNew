package com.tjcg.menuo.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.tjcg.menuo.Expandablectivity
import com.tjcg.menuo.R
import com.tjcg.menuo.utils.Constants.Authorization
import com.tjcg.menuo.utils.PrefManager
import com.tjcg.menuo.utils.SharedPreferencesKeys

class SplashActivity : AppCompatActivity() {
    var prefManager: PrefManager? = null
    var SPLASH_TIMEOUT: Long = 3000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        prefManager = PrefManager(this)



        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        Handler().postDelayed({
            if (prefManager!!.getBoolean("isLogin")) {
                var businessID = prefManager!!.getString("businessID")
                Authorization = prefManager!!.getString("auth_token").toString()
                startActivity(Intent(this@SplashActivity, Expandablectivity::class.java).putExtra(SharedPreferencesKeys.isDBLoadRequired,true).putExtra("businessID",businessID))
                finish()
            } else {
                startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
                finish()
            }
        }, 3000)

    }
}