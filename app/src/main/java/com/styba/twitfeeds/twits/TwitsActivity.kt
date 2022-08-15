package com.styba.twitfeeds.twits

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import com.styba.twitfeeds.R
import com.styba.twitfeeds.common.BaseActivity
import com.styba.twitfeeds.common.Constants
import com.styba.twitfeeds.common.TwitFeedManager
import com.styba.twitfeeds.main.MainActivity
import com.styba.twitfeeds.models.ReaderNew
import kotlinx.android.synthetic.main.activity_twits.*
import kotlinx.android.synthetic.main.loading_view.*

class TwitsActivity : BaseActivity(), TwitsContract.View {

    private val twitsPresenter = TwitsPresenter()
    private var twitFeedManager: TwitFeedManager? = null
    private var twitId = ""
    private var url = ""
    private var realUrl = ""
    private var isReaderMode = false
    private var readerNew: ReaderNew? = null
    private var menu: Menu? = null
    private var menuItemType: MenuItem? = null
    private var isFromNotification = false

    override fun getLayout(): Int {
        return R.layout.activity_twits
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lottieViewLoading.imageAssetsFolder = "images/"
        lottieViewLoading2.imageAssetsFolder = "images/"

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(true)
        supportActionBar?.title = getString(R.string.app_name)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_arrow_back)

        val isAd = intent.getBooleanExtra(Constants.NOTIFICATION_IS_AD, false)
        val isLatestNew = intent.getBooleanExtra(Constants.IS_LATEST_NEW, false)
        val fromNotification = intent.getStringExtra(Constants.NOTIFICATION_IS_FROM)
        isFromNotification = TextUtils.equals(fromNotification, "true")
        twitId = intent.getStringExtra(Constants.NOTIFICATION_TWIT_ID)
        url = if (intent.hasExtra(Constants.NOTIFICATION_URL))
            intent.getStringExtra(Constants.NOTIFICATION_URL)
        else ""
        realUrl = if (intent.hasExtra(Constants.NOTIFICATION_REAL_URL))
            intent.getStringExtra(Constants.NOTIFICATION_REAL_URL)
        else ""

        setUpMVP()
        /*if (isLatestNew) {
            setUpWebView()
            webViewTwits.loadUrl(url)
        } else if (twitFeedManager?.isReaderMode()!! && !isAd) {
            isReaderMode = true
            twitsPresenter.getReaderMode(realUrl)
        //} else {*/
            setUpWebView()
            webViewTwits.loadUrl(url)
        //}

        if (isAd)
            relativeBottom.visibility = View.GONE
        else
            relativeBottom.visibility = View.VISIBLE

        slidingLayoutTwit.isTouchEnabled = false

        imageViewComments.setOnClickListener {
            slidingLayoutTwit.panelState = SlidingUpPanelLayout.PanelState.EXPANDED
            lottieViewLoading2.playAnimation()
            initWebViewComments()
        }

        imageButtonBack.setOnClickListener {
            slidingLayoutTwit.panelState = SlidingUpPanelLayout.PanelState.COLLAPSED
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        this.menu = menu
        menuInflater.inflate(R.menu.menu_detail, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        menuItemType = menu.findItem(R.id.actionChange)
        menuItemType?.icon = if (isReaderMode)
            ContextCompat.getDrawable(this, R.drawable.ic_www)
        else
            ContextCompat.getDrawable(this, R.drawable.ic_chrome_reader)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finishActivity()
                true
            }
            R.id.actionShare -> {
                val shareIntent = Intent(Intent.ACTION_SEND)
                shareIntent.type = "text/plain"
                shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.main_share_label, url))
                startActivity(Intent.createChooser(shareIntent, getString(R.string.share_label)))
                true
            }
            R.id.actionChange -> {
                showLoading()
               //if (isReaderMode) {
                    menuItemType?.icon = ContextCompat.getDrawable(this, R.drawable.ic_www)
                    isReaderMode = false
                    setUpWebView()
                    webViewTwits.loadUrl(url)
                /*} else {
                    isReaderMode = true
                    menuItemType?.icon = ContextCompat.getDrawable(this, R.drawable.ic_chrome_reader)
                    twitsPresenter.getReaderMode(realUrl)
                }*/
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onNewIntent(intent: Intent) {
        this.intent = intent
    }

    override fun showLoading() {
        frameLoading.visibility = View.VISIBLE
        lottieViewLoading.playAnimation()
    }

    override fun hideLoading() {
        lottieViewLoading.cancelAnimation()
        lottieViewLoading2.cancelAnimation()
        frameLoading.visibility = View.GONE
        lottieViewLoading2.visibility = View.GONE
    }

    override fun initWebView(readerNew: ReaderNew?, isRealUrl: Boolean) {
        this.readerNew = readerNew
        setUpWebView()
        if (isRealUrl)
            webViewTwits.loadUrl("file:///android_asset/reader_template/readerTemplate.html")
        else
            webViewTwits.loadUrl(url)
    }

    override fun onBackPressed() {
        finishActivity()
    }

    private fun setUpMVP() {
        twitsPresenter.attachView(this)
        val twitsModel = TwitsModel()
        twitFeedManager = TwitFeedManager(this)
        twitFeedManager?.setActivity(this)
        twitsModel.initManager(twitFeedManager)
        twitsModel.attachPresenter(twitsPresenter)
        twitsPresenter.initModel(twitsModel)
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setUpWebView() {
        webViewTwits.setLayerType(View.LAYER_TYPE_HARDWARE, null)
        webViewTwits.webViewClient = TwitWebViewClient(false)
        webViewTwits.settings.javaScriptEnabled = true
        webViewTwits.scrollBarStyle = View.SCROLLBARS_INSIDE_OVERLAY
        webViewTwits.settings.cacheMode = WebSettings.LOAD_NO_CACHE
    }

    private fun injectJs() {
        val title = if (readerNew?.title != null)
            readerNew?.title
        else ""
        val author = if (readerNew?.author != null)
            readerNew?.author
        else ""
        val content = if (readerNew?.content != null)
            readerNew?.content
        else ""
        val domain = if (readerNew?.domain != null)
            readerNew?.domain
        else ""
        val publishedDate = if (readerNew?.date_published != null)
            readerNew?.date_published
        else ""

        val js = "javascript:(function() { " +
                "setHeadline('"+ title +"');" +
                "setAuthor('"+ author +"');" +
                "setContent('"+ content +"');" +
                "setDomain('"+ domain +"');" +
                "setPublicationDate('"+ publishedDate +"');" +
                "hideLoader()" +
                "})()"
        webViewTwits.loadUrl(js)
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initWebViewComments() {
        webViewComments.setLayerType(View.LAYER_TYPE_HARDWARE, null)
        webViewComments.webViewClient = TwitWebViewClient(true)
        webViewComments.settings.javaScriptEnabled = true
        webViewComments.scrollBarStyle = View.SCROLLBARS_INSIDE_OVERLAY
        webViewComments.settings.cacheMode = WebSettings.LOAD_NO_CACHE
        webViewComments.loadUrl(resources.getString(R.string.twit_comments, twitId))
    }

    private fun finishActivity() {
        if (isFromNotification) {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
        finish()
    }

    private inner class TwitWebViewClient(val isFromComments: Boolean) : WebViewClient() {

        override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
            view.loadUrl(request.url.toString())
            return true
        }

        override fun onPageFinished(view: WebView, url: String) {
            super.onPageFinished(view, url)
            hideLoading()
            if (!isFromComments)
                injectJs()
        }

        override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)
            showLoading()
        }
    }
}
