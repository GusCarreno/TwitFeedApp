package com.styba.twitfeeds.common

import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.styba.twitfeeds.viewpump.ViewPumpContextWrapper

abstract class BaseActivity: AppCompatActivity() {

    abstract fun getLayout(): Int

    override fun attachBaseContext(newBase: Context) {
        val twitFeedManager = TwitFeedManager(newBase)
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase, twitFeedManager.getLanguageApp()))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getLayout())
    }
}