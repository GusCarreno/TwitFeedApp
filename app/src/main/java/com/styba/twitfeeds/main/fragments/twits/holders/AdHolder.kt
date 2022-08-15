package com.styba.twitfeeds.main.fragments.twits.holders

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import com.bumptech.glide.Glide
import com.facebook.ads.*
import com.styba.twitfeeds.R
import com.styba.twitfeeds.common.BaseViewHolder
import com.styba.twitfeeds.events.TwitEvent
import com.styba.twitfeeds.models.TwitRow
import kotlinx.android.synthetic.main.admob_row.view.*
import org.greenrobot.eventbus.EventBus

class AdHolder(itemView: View?, val context: Context) : BaseViewHolder(itemView) {

    override fun bindItem(item: Any, position: Int) {
        val twitRow = item as? TwitRow
        when {
            twitRow?.adType == 0 -> {
                itemView.imageViewAd.visibility = View.VISIBLE
                itemView.native_banner_ad_container.visibility = View.GONE
                Glide.with(context)
                        .load(twitRow.adUrlImage)
                        .into(itemView.imageViewAd)
                itemView.setOnClickListener { EventBus.getDefault().post(TwitEvent(0, twitRow.adUrl, "", true, false)) }
            }
            else -> {
                itemView.imageViewAd.visibility = View.GONE
                itemView.native_banner_ad_container.visibility = View.VISIBLE
                //val nativeBannerAd = NativeBannerAd(context, "1756265091136938_1798248770271903") debug
                val nativeBannerAd = NativeBannerAd(context, twitRow?.facebookAd)
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
                        inflateAd(nativeBannerAd)
                    }

                })
                // load the ad
                nativeBannerAd.loadAd()
            }
        }
    }

    private fun inflateAd(nativeBannerAd: NativeBannerAd) {
        // Unregister last ad
        nativeBannerAd.unregisterView()
        itemView.native_banner_ad_container.removeAllViews()

        val inflater = LayoutInflater.from(context)
        // Inflate the Ad view.  The layout referenced is the one you created in the last step.
        val adView = inflater.inflate(R.layout.facebook_ad, itemView.native_banner_ad_container, false) as LinearLayout
        itemView.native_banner_ad_container.addView(adView)

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

}