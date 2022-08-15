package com.styba.twitfeeds.main.fragments.twits.holders

import android.content.Context
import android.text.TextUtils
import android.view.View
import com.bumptech.glide.Glide
import com.styba.twitfeeds.R
import com.styba.twitfeeds.common.BaseViewHolder
import com.styba.twitfeeds.events.TwitEvent
import com.styba.twitfeeds.models.TwitRow
import kotlinx.android.synthetic.main.single_twit_row.view.*
import org.greenrobot.eventbus.EventBus
import com.bumptech.glide.request.RequestOptions
import com.styba.twitfeeds.common.TwitFeedManager
import com.styba.twitfeeds.common.Utils

class SingleTwitViewHolder(itemView: View?, val context: Context, val twitFeedManager: TwitFeedManager) : BaseViewHolder(itemView) {

    override fun bindItem(item: Any, position: Int) {
        val twitRow = item as? TwitRow
        val twit = twitRow?.twit
        val url = if (!TextUtils.isEmpty(twit?.altMedia))
            twit?.altMedia
        else
            twit?.mediaUrl

        if (twitFeedManager.isOnlyWifi()!!) {
            if (Utils.isWifiConnection(context))
                loadImages(url)
        } else
            loadImages(url)

        Glide.with(context)
                .load(twit?.profileImage)
                .into(itemView.imageViewProfile!!)

        itemView.textViewTitle?.text = twit?.title
        itemView.textViewProfile?.text = twit?.screenName
        itemView.textViewTime?.text = context.getString(R.string.main_twit_time, twit?.hours)
        itemView.setOnClickListener { EventBus.getDefault().post(TwitEvent(twit?.twitId, twit?.url, twit?.realUrl, false, false)) }
    }

    private fun loadImages(url: String?) {
        val requestOptions = RequestOptions()
        requestOptions.placeholder(R.drawable.default_news)
        requestOptions.error(R.drawable.default_news)

        Glide.with(context)
                .load(url)
                .apply(requestOptions)
                .into(itemView.imageViewNews)
    }

}