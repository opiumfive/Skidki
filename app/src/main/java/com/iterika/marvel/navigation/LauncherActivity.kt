package com.iterika.marvel.navigation

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.iterika.marvel.MainActivity
import com.iterika.marvel.Prefs
import com.iterika.marvel.WelcomeActivity
import org.jetbrains.anko.startActivity
import org.koin.android.ext.android.inject

class LauncherActivity : AppCompatActivity() {

    val prefs: Prefs by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (prefs.welcomePassed) {
            startActivity<MainActivity>()
        } else {
            startActivity<WelcomeActivity>()
        }
        finish()
    }
}