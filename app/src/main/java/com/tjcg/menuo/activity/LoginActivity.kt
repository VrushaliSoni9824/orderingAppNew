package com.tjcg.menuo.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.tjcg.menuo.R
import com.tjcg.menuo.databinding.ActivityLoginBinding
import com.tjcg.menuo.fragment.AdminLoginFragment.Companion.newInstance

class LoginActivity : AppCompatActivity() {
    var fragment: Fragment? = null
    var transaction: FragmentTransaction? = null
    var binding: ActivityLoginBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        fragment = newInstance()
        setFragment(fragment)
    }

    @JvmName("setFragment1")
    fun setFragment(fragment: Fragment?) {
        transaction = supportFragmentManager.beginTransaction()
        transaction!!.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
        transaction!!.replace(R.id.frame_layout, fragment!!)
        transaction!!.commitAllowingStateLoss()
    }
}