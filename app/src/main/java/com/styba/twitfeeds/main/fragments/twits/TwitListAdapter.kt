package com.styba.twitfeeds.main.fragments.twits

import android.annotation.SuppressLint
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.facebook.ads.*
import com.styba.twitfeeds.R
import com.styba.twitfeeds.common.TwitFeedManager
import com.styba.twitfeeds.common.TypeAdapter
import com.styba.twitfeeds.common.Utils
import com.styba.twitfeeds.events.TwitEvent
import com.styba.twitfeeds.models.Twit
import com.styba.twitfeeds.views.AspectRatioImageView
import org.greenrobot.eventbus.EventBus

class TwitListAdapter(private val context: Context, val twitFeedManager: TwitFeedManager, private val twitList: MutableList<Twit>) : RecyclerView.Adapter<TwitListAdapter.TwitViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TwitViewHolder {
        val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.twit_item, parent, false)

        return TwitViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: TwitViewHolder, position: Int) {
        val twit = twitList[position]
        when (getItemViewType(position)) {
            TypeAdapter.TWIT_HOLDER -> {
                holder.linearTwit?.visibility = View.VISIBLE
                holder.nativeBannerAdContainer?.visibility = View.GONE
                initNew(holder, twit)
            }
            TypeAdapter.AD_HOLDER -> {
                holder.linearTwit?.visibility = View.GONE
                when {
                    twit.adType == 0 -> {
                        holder.nativeBannerAdContainer?.visibility = View.GONE
                        holder.imageViewAd?.visibility = View.VISIBLE
                        Glide.with(context)
                                .load(twit.adUrlImage)
                                .into(holder.imageViewAd!!)
                        holder.imageViewAd?.setOnClickListener { EventBus.getDefault().post(TwitEvent(0, twit.adUrl, "", true, false)) }
                    }
                    else -> {
                        holder.nativeBannerAdContainer?.visibility = View.VISIBLE
                        holder.imageViewAd?.visibility = View.GONE
                        val nativeBannerAd = NativeBannerAd(context, twit.facebookAd)
                        nativeBannerAd.setAdListener(object : NativeAdListener {
                            override fun onAdClicked(p0: Ad?) {

                            }

                            override fun onMediaDownloaded(p0: Ad?) {

                            }

                            override fun onError(p0: Ad?, p1: AdError?) {

                            }

                            override fun onLoggingImpression(p0: Ad?) {

                            }

                            override fun onAdLoaded(ad: Ad) {
                                // Race condition, load() called again before last ad was displayed
                                if (nativeBannerAd == null || nativeBannerAd !== ad) {
                                    return
                                }
                                // Inflate Native Banner Ad into Container
                                inflateAd(holder, nativeBannerAd)
                            }

                        })
                        // load the ad
                        nativeBannerAd.loadAd()
                    }
                }
            }
        }
    }

    private fun initNew(holder: TwitViewHolder, twit: Twit) {
        val url = if (!TextUtils.isEmpty(twit.altMedia))
            twit.altMedia
        else
            twit.mediaUrl
        if (twitFeedManager.isOnlyWifi()!!) {
            if (Utils.isWifiConnection(context))
                loadImages(holder, url)
        } else
            loadImages(holder, url)

        Glide.with(context)
                .load(twit.profileImage)
                .into(holder.imageViewProfile!!)

        holder.textViewTitle?.text = twit.title
        holder.textViewProfile?.text = twit.screenName
        holder.textViewTime?.text = context.getString(R.string.main_twit_time, twit.hours)
        holder.linearTwit?.setOnClickListener { EventBus.getDefault().post(TwitEvent(twit.twitId, twit.url, twit.realUrl, false, false)) }
    }

    private fun inflateAd(holder: TwitViewHolder, nativeBannerAd: NativeBannerAd) {
        // Unregister last ad
        nativeBannerAd.unregisterView()
        holder.nativeBannerAdContainer?.removeAllViews()

        val inflater = LayoutInflater.from(context)
        // Inflate the Ad view.  The layout referenced is the one you created in the last step.
        val adView = inflater.inflate(R.layout.facebook_ad_item, holder.nativeBannerAdContainer, false) as LinearLayout
        holder.nativeBannerAdContainer?.addView(adView)

        // Add the AdChoices icon
        val adChoicesContainer = adView.findViewById(R.id.ad_choices_container) as RelativeLayout
        val adChoicesView = AdChoicesView(context, nativeBannerAd, true)
        adChoicesContainer.addView(adChoicesView, 0)

        // Create native UI using the ad metadata.
        val nativeAdTitle = adView.findViewById(R.id.native_ad_title) as TextView
        val nativeAdSocialContext = adView.findViewById(R.id.native_ad_social_context) as TextView
        val sponsoredLabel = adView.findViewById(R.id.native_ad_sponsored_label) as TextView
        val nativeAdIconView = adView.findViewById(R.id.native_icon_view) as AdIconView
        val nativeAdCallToAction = adView.findViewById(R.id.native_ad_call_to_action) as Button

        // Set the Text.
        nativeAdCallToAction.text = nativeBannerAd.adCallToAction
        nativeAdCallToAction.visibility = if (nativeBannerAd.hasCallToAction()) View.VISIBLE else View.INVISIBLE
        nativeAdTitle.text = nativeBannerAd.advertiserName
        nativeAdSocialContext.text = nativeBannerAd.adSocialContext
        sponsoredLabel.text = nativeBannerAd.sponsoredTranslation

        // Register the Title and CTA button to listen for clicks.
        val clickableViews: MutableList<View>? = arrayListOf()
        clickableViews?.add(nativeAdTitle)
        clickableViews?.add(nativeAdCallToAction)
        nativeBannerAd.registerViewForInteraction(adView, nativeAdIconView, clickableViews)
    }

    override fun getItemCount(): Int {
        return twitList.size
    }

    override fun getItemViewType(position: Int): Int {
        return twitList[position].viewType
    }

    @SuppressLint("CheckResult")
    private fun loadImages(holder: TwitViewHolder, url: String?) {
        val requestOptions = RequestOptions()
        requestOptions.placeholder(R.drawable.default_news)
        requestOptions.error(R.drawable.default_news)

        Glide.with(context)
                .load(url)
                .apply(requestOptions)
                .into(holder.imageViewNews!!)
    }

    inner class TwitViewHolder internal constructor(view: View) : RecyclerView.ViewHolder(view) {

        var linearTwit: LinearLayout? = null
        var imageViewNews: ImageView? = null
        var textViewTitle: TextView? = null
        var imageViewProfile: ImageView? = null
        var textViewProfile: TextView? = null
        var textViewTime: TextView? = null
        var imageViewAd: AspectRatioImageView? = null
        var nativeBannerAdContainer: RelativeLayout? = null

        init {
            linearTwit = view.findViewById(R.id.linearTwit)
            imageViewNews = view.findViewById(R.id.imageViewNews)
            textViewTitle = view.findViewById(R.id.textViewTitle)
            imageViewProfile = view.findViewById(R.id.imageViewProfile)
            textViewProfile = view.findViewById(R.id.textViewProfile)
            textViewTime = view.findViewById(R.id.textViewTime)
            nativeBannerAdContainer = view.findViewById(R.id.native_banner_ad_container)
            imageViewAd = view.findViewById(R.id.imageViewAd)
        }
    }
}