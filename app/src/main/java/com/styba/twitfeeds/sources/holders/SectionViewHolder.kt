package com.styba.twitfeeds.sources.holders

import android.view.View
import com.styba.twitfeeds.common.BaseViewHolder
import com.styba.twitfeeds.models.Source
import kotlinx.android.synthetic.main.source_section_row.view.*

class SectionViewHolder(itemView: View?) : BaseViewHolder(itemView) {

    override fun bindItem(item: Any, position: Int) {
        val source = item as? Source

        itemView.textViewSection?.text = source?.section
    }

}