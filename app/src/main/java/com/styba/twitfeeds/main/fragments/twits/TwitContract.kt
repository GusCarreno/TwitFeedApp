package com.styba.twitfeeds.main.fragments.twits

import android.content.Context
import android.view.ViewGroup
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.styba.twitfeeds.common.BaseViewHolder
import com.styba.twitfeeds.models.TwitRow

interface TwitContract {
    interface View {
        fun provideContext(): Context?
        fun initTwitsList()
        fun initEmptyView()
        fun showLoading()
        fun hideLoading()
    }

    interface Presenter {
        fun initModel(twitModel: TwitModel)
        fun provideContext(): Context?
        fun getTwits(section: String?)
        fun onLoadTwitsSuccess()
        fun onLoadTwitsFail()
        fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder
        fun onBindViewHolder(holder: BaseViewHolder, position: Int)
        fun getTwitRow(position: Int): Any
        fun getTwitsCount(): Int?
        fun initList(twitListOrigin : MutableList<TwitRow>)
    }

    interface Model {
        fun initList(twitListOrigin : MutableList<TwitRow>)
        fun getTwits(section: String?)
        fun getTwitRow(position: Int): TwitRow?
        fun getTwitsCount(): Int
    }
}