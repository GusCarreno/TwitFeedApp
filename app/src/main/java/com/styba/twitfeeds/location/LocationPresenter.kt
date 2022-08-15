package com.styba.twitfeeds.location

import android.view.ViewGroup
import android.widget.Filter
import com.styba.twitfeeds.common.BasePresenter
import com.styba.twitfeeds.common.BaseViewHolder

class LocationPresenter : BasePresenter<LocationContract.View>(), LocationContract.Presenter {

    private var model: LocationModel? = null

    override fun initModel(model: LocationModel) {
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

    override fun detachView() {
        super.detachView()
        model?.detachPresenter()
    }

}