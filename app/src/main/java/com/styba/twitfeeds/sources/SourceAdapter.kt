package com.styba.twitfeeds.sources

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.styba.twitfeeds.common.BaseViewHolder
import com.styba.twitfeeds.models.Source

class SourceAdapter(private val presenter: SourcePresenter) : RecyclerView.Adapter<BaseViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return presenter.onCreateViewHolder(parent, viewType)
    }

    override fun getItemCount(): Int {
        return presenter.getSourceCount()!!
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        return presenter.onBindViewHolder(holder, position)
    }

    override fun getItemViewType(position: Int): Int {
        val source = presenter.getSourceRow(position) as Source
        return source.viewType
    }

}