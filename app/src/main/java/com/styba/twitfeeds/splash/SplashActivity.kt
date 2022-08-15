package com.styba.twitfeeds.splash

import android.animation.Animator
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.styba.twitfeeds.R
import com.styba.twitfeeds.common.TwitFeedManager
import com.styba.twitfeeds.main.MainActivity
import com.styba.twitfeeds.terms.TermsAndConditionsActivity
import kotlinx.android.synthetic.main.activity_splash.*

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val twitFeedManager = TwitFeedManager(this)
        lottieViewSplash.imageAssetsFolder = "images/"
        lottieViewSplash.playAnimation()
        lottieViewSplash.addAnimatorListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {

            }

            override fun onAnimationEnd(animation: Animator) {
                lottieViewSplash.cancelAnimation()
                if (twitFeedManager.isSettingsUp()!!)
                    startActivity(Intent(this@SplashActivity, MainActivity::class.java).putExtra("isExplore", false))
                else
                    startActivity(Intent(this@SplashActivity, TermsAndConditionsActivity::class.java))
                finish()
            }

            override fun onAnimationCancel(animation: Animator) {

            }

            override fun onAnimationRepeat(animation: Animator) {

            }
        })
    }
}
