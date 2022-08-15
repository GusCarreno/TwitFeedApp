package com.styba.twitfeeds.sources

import android.content.Context
import android.view.ViewGroup
import com.styba.twitfeeds.common.BaseViewHolder
import com.styba.twitfeeds.models.Source

interface SourceContract {
    interface View {
        fun getContext(): Context
        fun showLoading()
        fun hideLoading()
        fun initSources()
        fun initEmptyView()
        fun notifyAdapter(position: Int)
        fun gotoHome()
    }

    interface Presenter {
        fun initModel(model: SourceModel)
        fun getContext(): Context?
        fun getSources()
        fun onSourceSuccess()
        fun onSourcesFail()
        fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder
        fun onBindViewHolder(holder: BaseViewHolder, position: Int)
        fun getSourceRow(position: Int): Any
        fun getSourceCount(): Int?
        fun updateSource(position: Int, isEnabled: Boolean, source: Source)
        fun onFinishUpdate(position: Int)
        fun saveSourceSelected()
        fun onSaveFinished()
        fun onSaveFail()
        fun selectAll()
        fun desSelectAll()
        fun countSelected(): Int?
    }

    interface Model {
        fun getSources()
        fun getSourceRow(position: Int): Source?
        fun getSourceCount(): Int?
        fun updateSource(position: Int, isEnabled: Boolean, source: Source)
        fun saveSourceSelected()
        fun selectAll()
        fun desSelectAll()
        fun countSelected(): Int
    }
}