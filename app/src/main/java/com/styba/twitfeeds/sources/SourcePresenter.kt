package com.styba.twitfeeds.sources

import android.content.Context
import android.view.ViewGroup
import com.styba.twitfeeds.common.BasePresenter
import com.styba.twitfeeds.common.BaseViewHolder
import com.styba.twitfeeds.models.Source

class SourcePresenter : BasePresenter<SourceContract.View>(), SourceContract.Presenter {

    private var model: SourceModel? = null

    override fun initModel(model: SourceModel) {
        this.model = model
    }

    override fun getContext(): Context? {
        return view?.getContext()
    }

    override fun getSources() {
        view?.showLoading()
        model?.getSources()
    }

    override fun onSourceSuccess() {
        view?.hideLoading()
        view?.initSources()
    }

    override fun onSourcesFail() {
        view?.hideLoading()
        view?.initEmptyView()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return BaseViewHolder.createViewHolder(parent, viewType)
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.bindItem(model?.getSourceRow(position) as Any, position)
    }

    override fun getSourceRow(position: Int): Any {
        return model?.getSourceRow(position) as Any
    }

    override fun getSourceCount(): Int? {
        return model?.getSourceCount()
    }

    override fun updateSource(position: Int, isEnabled: Boolean, source: Source) {
        model?.updateSource(position, isEnabled, source)
    }

    override fun onFinishUpdate(position: Int) {
        view?.notifyAdapter(position)
    }

    override fun saveSourceSelected() {
        model?.saveSourceSelected()
    }

    override fun onSaveFinished() {
        view?.gotoHome()
    }

    override fun onSaveFail() {

    }

    override fun selectAll() {
        model?.selectAll()
    }

    override fun desSelectAll() {
        model?.desSelectAll()
    }

    override fun countSelected(): Int? {
        return model?.countSelected()
    }

    override fun detachView() {
        super.detachView()
        model?.detachPresenter()
    }

}