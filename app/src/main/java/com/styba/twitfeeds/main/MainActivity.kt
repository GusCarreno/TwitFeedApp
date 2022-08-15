package com.styba.twitfeeds.main

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.styba.twitfeeds.R
import com.styba.twitfeeds.common.BaseActivity
import com.styba.twitfeeds.common.Constants
import com.styba.twitfeeds.common.TwitFeedManager
import com.styba.twitfeeds.events.NationalEvent
import com.styba.twitfeeds.events.ReloadEvent
import com.styba.twitfeeds.events.TwitEvent
import com.styba.twitfeeds.main.fragments.top.TopFragment
import com.styba.twitfeeds.twits.TwitsActivity
import com.vorlonsoft.android.rate.AppRate
import com.vorlonsoft.android.rate.StoreType
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.loading_view.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.json.JSONArray


class MainActivity : BaseActivity(), MainContract.View {

    private val mainPresenter = MainPresenter()
    private var twitFeedManager: TwitFeedManager? = null
    private var mainPagerAdapter: MainPagerAdapter? = null

    override fun getLayout(): Int {
        return R.layout.activity_main
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lottieViewLoading.imageAssetsFolder = "images/"
        setUpMVP()
        frameLoading?.visibility = View.VISIBLE
        lottieViewLoading?.playAnimation()
        twitFeedManager?.setExplore(intent.getBooleanExtra("isExplore", false))
        mainPresenter.getAds()

        if (twitFeedManager?.isFireBaseTokenChange()!!)
            mainPresenter.register()
        initRateDialog()
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        EventBus.getDefault().unregister(this)
        super.onStop()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onTopClicked(event: NationalEvent) {
        viewPagerMain.currentItem = event.category
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onTwitClicked(event: TwitEvent) {
        startActivity(Intent(this, TwitsActivity::class.java)
                .putExtra(Constants.NOTIFICATION_TWIT_ID, event.twitId.toString())
                .putExtra(Constants.NOTIFICATION_URL, event.url)
                .putExtra(Constants.NOTIFICATION_REAL_URL, event.realUrl)
                .putExtra(Constants.NOTIFICATION_IS_AD, event.isAd)
                .putExtra(Constants.NOTIFICATION_IS_FROM, "false")
                .putExtra(Constants.IS_LATEST_NEW, event.isLatestNew))
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onRefreshed(event: ReloadEvent) {
        val topFragment = mainPagerAdapter?.getFragment(0) as TopFragment
        topFragment.onRefreshed()
    }

    private fun setUpMVP() {
        mainPresenter.attachView(this)
        val mainModel = MainModel()
        twitFeedManager = TwitFeedManager(this)
        twitFeedManager?.setActivity(this)
        mainModel.initManager(twitFeedManager)
        mainModel.attachPresenter(mainPresenter)
        mainPresenter.initModel(mainModel)
        val ids = resources.getStringArray(R.array.facebook_ads)
        val idsJson = JSONArray()
        for (id: String in ids) {
            idsJson.put(id)
        }
        twitFeedManager?.setJsonFacebookAds(idsJson.toString())
    }

    override fun initPager() {
        lottieViewLoading?.cancelAnimation()
        frameLoading?.visibility = View.GONE
        smartTabMain.visibility = View.VISIBLE
        viewPagerMain.visibility = View.VISIBLE
        val hasMenu =  if (twitFeedManager?.isExplore()!!)
            twitFeedManager?.hasExploreMenu()
        else
            twitFeedManager?.hasMenu()
        val sectionsNames = if (hasMenu!!)
            resources.getStringArray(R.array.main_sections_names_with_local)
        else
            resources.getStringArray(R.array.main_sections_names)
        val sections = if (hasMenu)
            resources.getStringArray(R.array.main_sections_with_local)
        else
            resources.getStringArray(R.array.main_sections)
        val pageLimit = if (hasMenu) 8 else 7
        mainPagerAdapter = MainPagerAdapter(supportFragmentManager, hasMenu)
        //val sectionsNames = resources.getStringArray(R.array.main_sections_names)
        //val sections = resources.getStringArray(R.array.main_sections)
        val sectionNamesList : MutableList<String> = arrayListOf()
        val sectionList : MutableList<String> = arrayListOf()
        sectionNamesList.addAll(sectionsNames)
        sectionList.addAll(sections)
        mainPagerAdapter?.initSections(sectionNamesList, sectionList)
        viewPagerMain.adapter = mainPagerAdapter
        viewPagerMain.offscreenPageLimit = pageLimit
        smartTabMain.setViewPager(viewPagerMain)
    }

    private fun initRateDialog() {
        AppRate.with(this)
                .setStoreType(StoreType.GOOGLEPLAY)
                .setInstallDays(15.toByte())
                .setLaunchTimes(30.toByte())
                .setRemindInterval(15.toByte())
                .setSelectedAppLaunches(30.toByte())
                .setShowLaterButton(true)
                .setShowNeverButton(false)
                //.setDebug(true)
                .setCancelable(false)
                .setOnClickButtonListener {
                    val appPackageName = packageName
                    try {
                        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$appPackageName")))
                    } catch (e: android.content.ActivityNotFoundException) {
                        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")))
                    }
                }
                .setTitle(R.string.main_rate_title)
                .setTextLater(R.string.main_rate_later)
                .setMessage(R.string.main_rate_message)
                .setTextRateNow(R.string.main_rate_now)
                .monitor()

        if (AppRate.with(this).storeType == StoreType.GOOGLEPLAY) {
            //Check that Google Play is available
            if (GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this) != ConnectionResult.SERVICE_MISSING)
                AppRate.showRateDialogIfMeetsConditions(this)

        } else
            AppRate.showRateDialogIfMeetsConditions(this)
    }
}
