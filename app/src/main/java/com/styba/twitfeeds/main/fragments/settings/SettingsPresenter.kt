package com.styba.twitfeeds.main.fragments.settings

import android.content.Context
import android.view.ViewGroup
import android.widget.Filter
import com.styba.twitfeeds.common.BasePresenter
import com.styba.twitfeeds.common.BaseViewHolder
import org.greenrobot.eventbus.EventBus

class SettingsPresenter : BasePresenter<SettingsContract.View>(), SettingsContract.Presenter {

    private var model: SettingsModel? = null

    override fun initModel(model: SettingsModel) {
        this.model = model
    }

    override fun onStart() {
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        EventBus.getDefault().unregister(this)
    }

    override fun getSettings(context: Context) {
        model?.getSettings(context)
    }

    override fun onSettingsSuccess() {
        view?.initSettings()
    }

    override fun onSettingsFail() {
        view?.initEmptyView()
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
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return BaseViewHolder.createViewHolder(parent, viewType)
    }

    override fun onBindSettingsHolder(holder: BaseViewHolder, position: Int) {
        holder.bindItem(model?.getSettingRow(position) as Any, position)
    }

    override fun onBindLocationHolder(holder: BaseViewHolder, position: Int) {
        holder.bindItem(model?.getLocationRow(position) as Any, position)
    }

    override fun getSettingRow(position: Int): Any {
        return model?.getSettingRow(position) as Any
    }

    override fun getSettingsCount(): Int? {
        return model?.getSettingsCount()
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