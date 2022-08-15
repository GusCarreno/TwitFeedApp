package com.styba.twitfeeds.main.fragments.twits.holders

import android.content.Context
import android.text.TextUtils
import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.styba.twitfeeds.R
import com.styba.twitfeeds.common.BaseViewHolder
import com.styba.twitfeeds.common.TwitFeedManager
import com.styba.twitfeeds.common.Utils
import com.styba.twitfeeds.events.TwitEvent
import com.styba.twitfeeds.models.TwitRow
import kotlinx.android.synthetic.main.top_twit_row.view.*
import org.greenrobot.eventbus.EventBus

class TopTwitViewHolder(itemView: View?, val context: Context, val twitFeedManager: TwitFeedManager) : BaseViewHolder(itemView) {

    override fun bindItem(item: Any, position: Int) {
        val twitRow = item as? TwitRow
        val url = if (!TextUtils.isEmpty(twitRow?.twit?.altMedia))
            twitRow?.twit?.altMedia
        else
            twitRow?.twit?.mediaUrl

        if (twitFeedManager.isOnlyWifi()!!) {
            if (Utils.isWifiConnection(context))
                loadImages(url)
        } else
            loadImages(url)

        Glide.with(context)
                .load(twitRow?.twit?.profileImage)
                .into(itemView.imageViewProfile)

        itemView.textViewTitle?.text = twitRow?.twit?.title
        itemView.textViewSource.text = context.getString(R.string.main_banner_source, twitRow?.twit?.screenName, twitRow?.twit?.hours)
        itemView.setOnClickListener { EventBus.getDefault().post(TwitEvent(twitRow?.twit?.twitId, twitRow?.twit?.url, twitRow?.twit?.realUrl, false, false)) }
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