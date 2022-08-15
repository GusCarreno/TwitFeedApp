package com.styba.twitfeeds.profile

import android.view.ViewGroup
import android.widget.Filter
import com.styba.twitfeeds.common.BasePresenter
import com.styba.twitfeeds.common.BaseViewHolder

class ProfilePresenter : BasePresenter<ProfileContract.View>(), ProfileContract.Presenter {

    private var model: ProfileModel? = null

    override fun initModel(model: ProfileModel) {
        this.model = model
    }

    override fun getLocations() {
        view?.showLoading()
        model?.getLocations()
    }

    override fun onLocationSuccess() {
        view?.hideLoading()
        view?.initLocations()
    }

    override fun onLocationsFail() {
        view?.hideLoading()
        view?.initEmptyView()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return BaseViewHolder.createViewHolder(parent, viewType)
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.bindItem(model?.getLocationRow(position) as Any, position)
    }

    override fun getLocationRow(position: Int): Any {
        return model?.getLocationRow(position) as Any
    }

    override fun getLocationsCount(): Int? {
        return model?.getLocationsCount()
    }

    override fun getFilter(): Filter {
        return model?.getFilter()!!
    }

    override fun onFilterList() {
        view?.onFilterList()
    }

    override fun getSources() {
        view?.showLoading()
        model?.getSources()
    }

    override fun onSourceSuccess() {
        model?.saveSourceSelected()
    }

    override fun onSourcesFail() {
        view?.hideLoading()
        view?.showError()
    }

    override fun onSaveFinished() {
        model?.register()
    }

    override fun onSaveFail() {
        view?.hideLoading()
        view?.showError()
    }

    override fun onRegisterSuccess() {
        view?.hideLoading()
        view?.gotoMain()
    }

    override fun detachView() {
        super.detachView()
        model?.detachPresenter()
    }

}