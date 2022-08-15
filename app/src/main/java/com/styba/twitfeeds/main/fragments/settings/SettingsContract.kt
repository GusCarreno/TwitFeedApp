package com.styba.twitfeeds.main.fragments.settings

import android.content.Context
import android.view.ViewGroup
import android.widget.Filter
import com.styba.twitfeeds.common.BaseViewHolder
import com.styba.twitfeeds.models.Location
import com.styba.twitfeeds.models.Setting

interface SettingsContract {
    interface View {
        fun showLoading()
        fun hideLoading()
        fun initSettings()
        fun initEmptyView()
        fun initLocations()
        fun onFilterList()
    }

    interface Presenter {
        fun initModel(model: SettingsModel)
        fun onStart()
        fun onStop()
        fun getSettings(context: Context)
        fun onSettingsSuccess()
        fun onSettingsFail()
        fun getLocations()
        fun onLocationSuccess()
        fun onLocationsFail()
        fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder
        fun onBindSettingsHolder(holder: BaseViewHolder, position: Int)
        fun onBindLocationHolder(holder: BaseViewHolder, position: Int)
        fun getSettingRow(position: Int): Any
        fun getSettingsCount(): Int?
        fun getLocationRow(position: Int): Any
        fun getLocationsCount(): Int?
        fun getFilter(): Filter
        fun onFilterList()
    }

    interface Model {
        fun getSettings(context: Context)
        fun getLocations()
        fun getSettingRow(position: Int): Setting?
        fun getSettingsCount(): Int?
        fun getLocationRow(position: Int): Location?
        fun getLocationsCount(): Int?
        fun getFilter(): Filter
    }
}