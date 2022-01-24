package com.tjcg.menuo

import android.app.Application
import com.google.firebase.FirebaseApp

class NentoPOS : Application() {

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
    }


}