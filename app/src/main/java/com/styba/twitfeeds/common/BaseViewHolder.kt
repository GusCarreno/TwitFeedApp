package com.styba.twitfeeds.common

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.styba.twitfeeds.R
import com.styba.twitfeeds.location.LocationViewHolder
import com.styba.twitfeeds.main.fragments.settings.SettingViewHolder
import com.styba.twitfeeds.main.fragments.twits.holders.*
import com.styba.twitfeeds.sources.holders.SectionViewHolder
import com.styba.twitfeeds.sources.holders.SourceViewHolder

abstract class BaseViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {

    companion object {
        fun createViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val root: View
            return when (viewType) {
                TypeAdapter.LOCATION_HOLDER -> {
                    root = inflater.inflate(R.layout.location_row, parent, false)
                    LocationViewHolder(root, parent.context)
                }
                TypeAdapter.TOP_TWIT_HOLDER -> {
                    root = inflater.inflate(R.layout.top_twit_row, parent, false)
                    TopTwitViewHolder(root, parent.context, TwitFeedManager(parent.context))
                }
                TypeAdapter.DOUBLE_TWIT_HOLDER -> {
                    root = inflater.inflate(R.layout.list_twit_row, parent, false)
                    DoubleTwitViewHolder(root, parent.context, TwitFeedManager(parent.context))
                }
                TypeAdapter.TRIPLE_TWIT_HOLDER -> {
                    root = inflater.inflate(R.layout.list_twit_row, parent, false)
                    TripleTwitViewHolder(root, parent.context, TwitFeedManager(parent.context))
                }
                TypeAdapter.SINGLE_TWIT_HOLDER -> {
                    root = inflater.inflate(R.layout.single_twit_row, parent, false)
                    SingleTwitViewHolder(root, parent.context, TwitFeedManager(parent.context))
                }
                TypeAdapter.SETTING_HOLDER -> {
                    root = inflater.inflate(R.layout.setting_row, parent, false)
                    SettingViewHolder(root)
                }
                TypeAdapter.SOURCE_SECTION_HOLDER -> {
                    root = inflater.inflate(R.layout.source_section_row, parent, false)
                    SectionViewHolder(root)
                }
                TypeAdapter.SOURCE_HOLDER -> {
                    root = inflater.inflate(R.layout.source_row, parent, false)
                    SourceViewHolder(root, parent.context)
                }
                TypeAdapter.AD_HOLDER -> {
                    root = inflater.inflate(R.layout.admob_row, parent, false)
                    AdHolder(root, parent.context)
                }
                else -> {
                    root = inflater.inflate(R.layout.location_row, parent, false)
                    LocationViewHolder(root, parent.context)
                }
            }
        }
    }

    abstract fun bindItem(item: Any, position: Int)

}