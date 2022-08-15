package com.styba.twitfeeds.main.fragments.twits

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.styba.twitfeeds.common.BaseViewHolder
import com.styba.twitfeeds.models.TwitRow

class NewsAdapter(private val presenter: TwitPresenter) : RecyclerView.Adapter<BaseViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return presenter.onCreateViewHolder(parent, viewType)
    }

    override fun getItemCount(): Int = presenter.getTwitsCount()!!

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        presenter.onBindViewHolder(holder, position)
    }

    override fun getItemViewType(position: Int): Int {
        val twitRow = presenter.getTwitRow(position) as TwitRow
        return twitRow.viewType
    }

}