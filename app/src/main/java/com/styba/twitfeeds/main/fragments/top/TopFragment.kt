package com.styba.twitfeeds.main.fragments.top

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.airbnb.lottie.LottieAnimationView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.styba.twitfeeds.R
import com.styba.twitfeeds.common.TwitFeedManager
import com.styba.twitfeeds.common.Utils
import com.styba.twitfeeds.events.NationalEvent
import com.styba.twitfeeds.events.TwitEvent
import com.styba.twitfeeds.models.LatestNew
import com.styba.twitfeeds.models.Twit
import com.styba.twitfeeds.models.Weather
import org.greenrobot.eventbus.EventBus

class TopFragment : Fragment(), TopContract.View {

    private var frameLoading: FrameLayout? = null
    private var lottieViewLoading: LottieAnimationView? = null
    private var linearContainer: LinearLayout? = null
    private var frameTop: FrameLayout? = null
    private var imageViewBanner: ImageView? = null
    private var textViewTitle: TextView? = null
    private var textViewProfile: TextView? = null
    private var linearLatestNew: LinearLayout? = null
    private var textViewNews: TextView? = null
    private var textViewSource: TextView? = null
    private var imageViewProfile: ImageView? = null
    private var textViewWeatherFail: TextView? = null
    private var linearLocation: LinearLayout? = null
    private var textViewLocation: TextView? = null
    private var imageViewLocation: ImageView? = null
    private var relativeWeather: RelativeLayout? = null
    private var imageViewWeather: ImageView? = null
    private var textViewDegrees: TextView? = null
    private var textViewTime: TextView? = null
    private val topPresenter = TopPresenter()
    private var twitFeedManager: TwitFeedManager? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_top, container, false)
        setUpMVP()
        initViews(view)
        topPresenter.getTopNews()
        return view
    }

    override fun onDestroy() {
        super.onDestroy()
        topPresenter.detachView()
    }

    companion object {
        @JvmStatic
        fun newInstance() = TopFragment()
    }

    override fun initBanner(twit: Twit) {
        if (context != null) {
            if (twitFeedManager?.isOnlyWifi()!!) {
                if (Utils.isWifiConnection(context!!))
                    loadImages(twit)
            } else
                loadImages(twit)
        }
        textViewTitle?.text = twit.title
        textViewProfile?.text = context?.getString(R.string.main_banner_source, twit.screenName, twit.hours)
        frameTop?.setOnClickListener { EventBus.getDefault().post(NationalEvent(1)) }
    }

    override fun initLatestNews(latestNew: LatestNew) {
        textViewNews?.text = latestNew.fullText
        textViewSource?.text = context?.getString(R.string.main_latest_news_source, latestNew.screenName, latestNew.time)
        if (context != null) {
            Glide.with(context!!)
                    .load(latestNew.profileImage)
                    .into(imageViewProfile!!)
        }
        linearLatestNew?.setOnClickListener { EventBus.getDefault().post(TwitEvent(latestNew.twitId, latestNew.url, latestNew.realUrl, false, true)) }
    }

    override fun latestNewsNotAvailable() {
        textViewNews?.text = ""
    }

    override fun initWeather(weather: Weather) {
        textViewLocation?.text = if (twitFeedManager?.isExplore()!!)
            twitFeedManager?.getExploreLocationName()
        else
            twitFeedManager?.getLocationName()

        val flag =  if (twitFeedManager?.isExplore()!!)
            twitFeedManager?.getExploreFlag()
        else
            twitFeedManager?.getFlag()
        //var timezone = "GMT−06:00"
        when (flag) {
            "sv" -> imageViewLocation?.setImageResource(R.mipmap.ic_sv)
            "gt" -> imageViewLocation?.setImageResource(R.mipmap.ic_gt)
            "hn" -> imageViewLocation?.setImageResource(R.mipmap.ic_hn)
            "ni" -> imageViewLocation?.setImageResource(R.mipmap.ic_ni)
            "cr" -> imageViewLocation?.setImageResource(R.mipmap.ic_cr)
            "pa" -> imageViewLocation?.setImageResource(R.mipmap.ic_pa)
            "do" -> imageViewLocation?.setImageResource(R.mipmap.ic_do)
            "mx" -> imageViewLocation?.setImageResource(R.mipmap.ic_mx)
            "ar" -> imageViewLocation?.setImageResource(R.mipmap.ic_ar)
            "cl" -> imageViewLocation?.setImageResource(R.mipmap.ic_cl)
            "cu" -> imageViewLocation?.setImageResource(R.mipmap.ic_cu)
            "pe" -> imageViewLocation?.setImageResource(R.mipmap.ic_pe)
            "uy" -> imageViewLocation?.setImageResource(R.mipmap.ic_uy)
            "bo" -> imageViewLocation?.setImageResource(R.mipmap.ic_bo)
            "co" -> imageViewLocation?.setImageResource(R.mipmap.ic_co)
            "ec" -> imageViewLocation?.setImageResource(R.mipmap.ic_ec)
            "pr" -> imageViewLocation?.setImageResource(R.mipmap.ic_pr)
            "ve" -> imageViewLocation?.setImageResource(R.mipmap.ic_ve)
            else -> imageViewLocation?.setImageResource(R.mipmap.ic_sv)
        }
        //textViewTime?.text = context?.getString(R.string.main_top_time, getTime(timezone))
        textViewTime?.text = weather.localTime
        when (weather.icon) {
            "clear-day" -> imageViewWeather?.setImageResource(R.drawable.ic_clear_day)
            "clear-night" -> imageViewWeather?.setImageResource(R.drawable.ic_clear_night)
            "cloudy" -> imageViewWeather?.setImageResource(R.drawable.ic_cloudy)
            "partly-cloudy-day" -> imageViewWeather?.setImageResource(R.drawable.ic_partly_cloudy_day)
            "partly-cloudy-night" -> imageViewWeather?.setImageResource(R.drawable.ic_partly_cloudy_night)
            "rain" -> imageViewWeather?.setImageResource(R.drawable.ic_rain)
            else -> imageViewWeather?.setImageResource(R.drawable.ic_clear_day)
        }
        textViewDegrees?.text = context?.getString(R.string.main_weather_degrees, weather.temperature)
        linearContainer?.visibility = View.VISIBLE
    }

    override fun weatherNotAvailable() {
        linearContainer?.visibility = View.VISIBLE
        textViewWeatherFail?.visibility = View.VISIBLE
        linearLocation?.visibility = View.INVISIBLE
        relativeWeather?.visibility = View.INVISIBLE
    }

    override fun showLoading() {
        frameLoading?.visibility = View.VISIBLE
        lottieViewLoading?.playAnimation()
    }

    override fun hideLoading() {
        lottieViewLoading?.cancelAnimation()
        frameLoading?.visibility = View.GONE
    }

    fun onRefreshed() {
        topPresenter.getTopNews()
    }

    private fun setUpMVP() {
        topPresenter.attachView(this)
        val topModel = TopModel()
        twitFeedManager = TwitFeedManager(context)
        twitFeedManager?.setActivity(activity)
        topModel.initManager(twitFeedManager)
        topModel.attachPresenter(topPresenter)
        topPresenter.initModel(topModel)
    }

    private fun initViews(view: View) {
        frameLoading = view.findViewById(R.id.frameLoading)
        lottieViewLoading = view.findViewById(R.id.lottieViewLoading)
        frameTop = view.findViewById(R.id.frameTop)
        linearContainer = view.findViewById(R.id.linearContainer)
        imageViewBanner = view.findViewById(R.id.imageViewBanner)
        textViewTitle = view.findViewById(R.id.textViewTitle)
        textViewProfile = view.findViewById(R.id.textViewProfile)
        linearLatestNew = view.findViewById(R.id.linearLatestNew)
        textViewNews = view.findViewById(R.id.textViewNews)
        textViewSource = view.findViewById(R.id.textViewSource)
        imageViewProfile = view.findViewById(R.id.imageViewProfile)
        textViewWeatherFail = view.findViewById(R.id.textViewWeatherFail)
        linearLocation = view.findViewById(R.id.linearLocation)
        textViewLocation = view.findViewById(R.id.textViewLocation)
        imageViewLocation = view.findViewById(R.id.imageViewLocation)
        relativeWeather = view.findViewById(R.id.relativeWeather)
        imageViewWeather = view.findViewById(R.id.imageViewWeather)
        textViewDegrees = view.findViewById(R.id.textViewDegrees)
        textViewTime = view.findViewById(R.id.textViewTime)
        lottieViewLoading?.imageAssetsFolder = "images/"
    }

    @SuppressLint("CheckResult")
    private fun loadImages(twit: Twit) {
        val url = if (!TextUtils.isEmpty(twit.altMedia))
            twit.altMedia
        else
            twit.mediaUrl
        val requestOptions = RequestOptions()
        requestOptions.placeholder(R.drawable.default_news)
        requestOptions.error(R.drawable.default_news)
        Glide.with(context!!)
                .load(url)
                .apply(requestOptions)
                .into(imageViewBanner!!)
    }

    /*private fun getTime(timezone: String): String {
        val tz = TimeZone.getTimeZone(timezone)
        val df1 = SimpleDateFormat("hh:mm", Locale.getDefault())
        return df1.format(Calendar.getInstance(tz).time)
    }*/
}
