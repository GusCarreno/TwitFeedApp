package com.styba.twitfeeds.profile

import android.view.ViewGroup
import android.widget.Filter
import com.styba.twitfeeds.common.BaseViewHolder
import com.styba.twitfeeds.models.Location

interface ProfileContract {
    interface View {
        fun showLoading()
        fun hideLoading()
        fun initLocations()
        fun initEmptyView()
        fun gotoMain()
        fun showError()
        fun onFilterList()
    }

    interface Presenter {
        fun initModel(model: ProfileModel)
        fun getLocations()
        fun onLocationSuccess()
        fun onLocationsFail()
        fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder
        fun onBindViewHolder(holder: BaseViewHolder, position: Int)
        fun getLocationRow(position: Int): Any
        fun getLocationsCount(): Int?
        fun getFilter(): Filter
        fun onFilterList()
        fun getSources()
        fun onSourceSuccess()
        fun onSourcesFail()
        fun onSaveFinished()
        fun onSaveFail()
        fun onRegisterSuccess()
    }

    interface Model {
        fun getLocations()
        fun getLocationRow(position: Int): Location?
        fun getLocationsCount(): Int?
        fun getFilter(): Filter
        fun getSources()
        fun saveSourceSelected()
        fun register()
    }
}