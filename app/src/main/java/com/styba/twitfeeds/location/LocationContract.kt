package com.styba.twitfeeds.location

import android.view.ViewGroup
import android.widget.Filter
import com.styba.twitfeeds.common.BaseViewHolder
import com.styba.twitfeeds.models.Location

interface LocationContract {
    interface View {
        fun showLoading()
        fun hideLoading()
        fun initLocations()
        fun initEmptyView()
        fun onFilterList()
    }

    interface Presenter {
        fun initModel(model: LocationModel)
        fun getLocations()
        fun onLocationSuccess()
        fun onLocationsFail()
        fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder
        fun onBindViewHolder(holder: BaseViewHolder, position: Int)
        fun getLocationRow(position: Int): Any
        fun getLocationsCount(): Int?
        fun getFilter(): Filter
        fun onFilterList()
    }

    interface Model {
        fun getLocations()
        fun getLocationRow(position: Int): Location?
        fun getLocationsCount(): Int?
        fun getFilter(): Filter
    }
}