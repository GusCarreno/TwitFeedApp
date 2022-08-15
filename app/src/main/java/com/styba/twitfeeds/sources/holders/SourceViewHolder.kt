package com.styba.twitfeeds.sources.holders

import android.content.Context
import android.view.View
import com.bumptech.glide.Glide
import com.styba.twitfeeds.R
import com.styba.twitfeeds.common.BaseViewHolder
import com.styba.twitfeeds.events.SourceEvent
import com.styba.twitfeeds.models.Source
import kotlinx.android.synthetic.main.source_row.view.*
import org.greenrobot.eventbus.EventBus

class SourceViewHolder(itemView: View?, val context: Context) : BaseViewHolder(itemView) {

    override fun bindItem(item: Any, position: Int) {
        val source = item as? Source

        Glide.with(context)
                .load(source?.profileImage)
                .into(itemView.imageViewProfile!!)

        itemView.textViewSource?.text = context.getString(R.string.source_label_value, source?.screenName, source?.lang)
        itemView.switchSource.setOnCheckedChangeListener(null)
        itemView.switchSource.isChecked = source?.enabled!!
        itemView.switchSource.setOnCheckedChangeListener { _, isChecked -> EventBus.getDefault().post(SourceEvent(position, isChecked, source)) }
    }

}