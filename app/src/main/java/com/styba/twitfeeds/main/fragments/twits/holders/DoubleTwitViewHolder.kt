package com.styba.twitfeeds.main.fragments.twits.holders

import android.content.Context
import android.view.View
import com.styba.twitfeeds.common.BaseViewHolder
import com.styba.twitfeeds.models.TwitRow
import kotlinx.android.synthetic.main.list_twit_row.view.*
import android.support.v7.widget.GridLayoutManager
import com.styba.twitfeeds.common.TwitFeedManager
import com.styba.twitfeeds.main.fragments.twits.TwitListAdapter

class DoubleTwitViewHolder(itemView: View?, val context: Context, val twitFeedManager: TwitFeedManager) : BaseViewHolder(itemView) {

    override fun bindItem(item: Any, position: Int) {
        val twitRow = item as? TwitRow
        itemView.recyclerViewTwits.layoutManager = GridLayoutManager(context, 2)
        if (twitRow?.twitList != null)
            itemView.recyclerViewTwits.adapter = TwitListAdapter(context, twitFeedManager, twitRow.twitList)
    }

}