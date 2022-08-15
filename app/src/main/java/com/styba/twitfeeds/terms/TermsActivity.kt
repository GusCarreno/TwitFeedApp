package com.styba.twitfeeds.terms

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import com.styba.twitfeeds.R
import kotlinx.android.synthetic.main.activity_terms.*
import kotlinx.android.synthetic.main.loading_view.*

class TermsActivity : AppCompatActivity() {

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_terms)
        lottieViewLoading?.imageAssetsFolder = "images/"

        webViewTerms.setLayerType(View.LAYER_TYPE_HARDWARE, null)
        webViewTerms.webViewClient = TwitWebViewClient()
        webViewTerms.settings.javaScriptEnabled = true
        webViewTerms.scrollBarStyle = View.SCROLLBARS_INSIDE_OVERLAY
        webViewTerms.settings.cacheMode = WebSettings.LOAD_NO_CACHE
        webViewTerms.loadUrl(intent.getStringExtra("URL"))
    }

    private fun showLoading() {
        frameLoading.visibility = View.VISIBLE
        lottieViewLoading.playAnimation()
    }

    private fun hideLoading() {
        lottieViewLoading.cancelAnimation()
        frameLoading.visibility = View.GONE
    }

    private inner class TwitWebViewClient: WebViewClient() {

        override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
            view.loadUrl(request.url.toString())
            return true
        }

        override fun onPageFinished(view: WebView, url: String) {
            super.onPageFinished(view, url)
            hideLoading()
        }

        override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)
            showLoading()
        }
    }
}
