package com.styba.twitfeeds.terms

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.view.View
import android.webkit.WebSettings
import android.webkit.WebViewClient
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import com.styba.twitfeeds.R
import com.styba.twitfeeds.common.BaseActivity
import com.styba.twitfeeds.common.Constants
import com.styba.twitfeeds.common.Utils
import com.styba.twitfeeds.location.LocationActivity
import kotlinx.android.synthetic.main.activity_terms_and_conditions.*
import kotlinx.android.synthetic.main.panel_terms.*

class TermsAndConditionsActivity : BaseActivity() {

    private var isEnabled = false

    override fun getLayout(): Int {
        return R.layout.activity_terms_and_conditions
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initWebView()

        switchTerms.setOnCheckedChangeListener { _, isChecked ->
            isEnabled = isChecked
            if (isChecked)
                buttonContinue.setBackgroundColor(ActivityCompat.getColor(this, R.color.colorBlue2))
            else
                buttonContinue.setBackgroundColor(ActivityCompat.getColor(this, R.color.colorGray2))
        }

        textViewTerms.setOnClickListener {
            //slidingLayoutTerms.panelState = SlidingUpPanelLayout.PanelState.EXPANDED
            startActivity(Intent(this, TermsActivity::class.java).putExtra("URL", Constants.TERMS_URL))
        }

        buttonContinue.setOnClickListener {
            if (isEnabled) {
                startActivity(Intent(this, LocationActivity::class.java))
                finish()
            } else
                Utils.showSnackBar(this, slidingLayoutTerms, getString(R.string.terms_label_alert))
        }
    }

    override fun onBackPressed() {
        if (slidingLayoutTerms.panelState == SlidingUpPanelLayout.PanelState.EXPANDED)
            slidingLayoutTerms.panelState = SlidingUpPanelLayout.PanelState.COLLAPSED
        else
            super.onBackPressed()
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initWebView() {
        webViewTerms.setLayerType(View.LAYER_TYPE_HARDWARE, null)
        webViewTerms.webViewClient = object : WebViewClient() {
        }
        webViewTerms.settings.javaScriptEnabled = true
        webViewTerms.scrollBarStyle = View.SCROLLBARS_INSIDE_OVERLAY
        webViewTerms.settings.cacheMode = WebSettings.LOAD_NO_CACHE
        webViewTerms.loadUrl(Constants.TERMS_URL)
    }
}
