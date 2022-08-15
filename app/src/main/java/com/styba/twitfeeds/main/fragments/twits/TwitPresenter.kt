package com.styba.twitfeeds.main.fragments.twits

import android.content.Context
import android.view.ViewGroup
import com.styba.twitfeeds.common.BasePresenter
import com.styba.twitfeeds.common.BaseViewHolder
import com.styba.twitfeeds.models.TwitRow

class TwitPresenter : BasePresenter<TwitContract.View>(), TwitContract.Presenter {

    private var model: TwitModel? = null

    override fun initModel(twitModel: TwitModel) {
        model = twitModel
    }

    override fun provideContext(): Context? {
        return view?.provideContext()
    }

    override fun getTwits(section: String?) {
        model?.getTwits(section)
    }

    override fun onLoadTwitsSuccess() {
        view?.hideLoading()
        view?.initTwitsList()
    }

    override fun onLoadTwitsFail() {
        view?.hideLoading()
        view?.initEmptyView()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return BaseViewHolder.createViewHolder(parent, viewType)
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.bindItem(model?.getTwitRow(position) as Any, position)
    }

    override fun getTwitRow(position: Int): Any {
        return model?.getTwitRow(position) as Any
    }

    override fun getTwitsCount(): Int? {
        return model?.getTwitsCount()
    }

    override fun initList(twitListOrigin: MutableList<TwitRow>) {
        model?.initList(twitListOrigin)
    }

    override fun detachView() {
        super.detachView()
        model?.detachPresenter()
    }

}