package com.styba.twitfeeds.welcome

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import com.google.firebase.iid.FirebaseInstanceId
import com.styba.twitfeeds.R
import com.styba.twitfeeds.common.BaseActivity
import com.styba.twitfeeds.common.TwitFeedManager
import com.styba.twitfeeds.common.Utils
import com.styba.twitfeeds.main.MainActivity
import kotlinx.android.synthetic.main.activity_welcome.*
import kotlinx.android.synthetic.main.loading_view.*

class WelcomeActivity : BaseActivity(), WelcomeContract.View  {

    private val welcomePresenter = WelcomePresenter()
    private var twitFeedManager: TwitFeedManager? = null

    override fun getLayout(): Int {
        return R.layout.activity_welcome
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lottieViewLoading.imageAssetsFolder = "images/"
        setUpMVP()

        FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener(this@WelcomeActivity) { instanceIdResult ->
            Log.e("TOKEN", "Refreshed token: $instanceIdResult.token")
            if (!TextUtils.equals(twitFeedManager?.getFireBaseToken(), instanceIdResult.token)) {
                twitFeedManager?.setFireBaseToken(instanceIdResult.token)
                twitFeedManager?.setIsFireBaseTokenChange(true)
            }
        }

        buttonFinish.setOnClickListener{
            twitFeedManager?.setIsReaderMode(true)
            buttonFinish.isEnabled = false
            welcomePresenter.getSources()
        }
    }

    override fun showLoading() {
        frameLoading.visibility = View.VISIBLE
        lottieViewLoading.playAnimation()
    }

    override fun hideLoading() {
        lottieViewLoading.cancelAnimation()
        frameLoading.visibility = View.GONE
    }

    override fun gotoMain() {
        twitFeedManager?.setSettingsUp(true)
        startActivity(Intent(this, MainActivity::class.java).putExtra("isExplore", false))
        finish()
    }

    override fun showError() {
        Utils.showSnackBar(this, activityWelcome, resources.getString(R.string.welcome_error))
    }

    private fun setUpMVP() {
        welcomePresenter.attachView(this)
        val welcomeModel = WelcomeModel()
        twitFeedManager = TwitFeedManager(this)
        twitFeedManager?.setActivity(this)
        welcomeModel.initManager(twitFeedManager)
        welcomeModel.attachPresenter(welcomePresenter)
        welcomePresenter.initModel(welcomeModel)
    }
}
