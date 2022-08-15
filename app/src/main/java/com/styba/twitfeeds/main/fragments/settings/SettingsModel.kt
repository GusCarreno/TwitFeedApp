package com.styba.twitfeeds.main.fragments.settings

import android.content.Context
import android.text.TextUtils
import android.util.Log
import android.widget.Filter
import com.google.gson.reflect.TypeToken
import com.styba.twitfeeds.R
import com.styba.twitfeeds.common.*
import com.styba.twitfeeds.models.Location
import com.styba.twitfeeds.models.Setting

class SettingsModel : BaseModel<SettingsContract.Presenter>(), SettingsContract.Model {

    private val tag = "SettingsModel"
    private val settingsList: MutableList<Setting> = arrayListOf()
    private val locationListOrigin : MutableList<Location> = arrayListOf()
    private val locationListOriginCopy : MutableList<Location> = arrayListOf()
    private var searchFilter: SearchFilter? = null

    override fun initManager(twitFeedManager: TwitFeedManager?) {
        this.twitFeedManager = twitFeedManager
    }

    override fun getSettings(context: Context) {
        val settings = context.resources.getStringArray(R.array.main_settings_names)
        settingsList.clear()
        for (setting: String in settings) {
            settingsList.add(Setting(TypeAdapter.SETTING_HOLDER, setting))
        }
        if (!settingsList.isEmpty())
            presenter?.onSettingsSuccess()
        else
            presenter?.onSettingsFail()
    }

    override fun getLocations() {
        twitFeedManager?.post(
                Routes.COUNTRY_LIST,
                null,
                onSuccess = { result ->
                    val jsonResponse = parseResponse(result as String)
                    if (!TextUtils.isEmpty(jsonResponse)) {
                        locationListOrigin.clear()
                        locationListOriginCopy.clear()
                        val locationList: List<Location> = gSon.fromJson(jsonResponse, object : TypeToken<List<Location>>() {}.type)
                        if (!locationList.isEmpty()) {
                            /*for (i in 0..6) {
                                val location = locationList[i]
                                location.viewType = TypeAdapter.LOCATION_HOLDER
                                locationListOrigin.add(location)
                            }*/
                            for (location: Location in locationList) {
                                location.viewType = TypeAdapter.LOCATION_HOLDER
                            }
                            locationListOrigin.addAll(locationList)
                            locationListOriginCopy.addAll(locationList)
                            presenter?.onLocationSuccess()
                        } else
                            presenter?.onLocationsFail()
                    } else
                        presenter?.onLocationsFail()
                },
                onError = { result ->
                    Log.e(tag, "$result")
                    presenter?.onLocationsFail()
                })
    }

    override fun getSettingRow(position: Int): Setting? {
        return settingsList[position]
    }

    override fun getSettingsCount(): Int? {
        return if (settingsList.isEmpty())
            0
        else
            settingsList.size
    }

    override fun getLocationRow(position: Int): Location? {
        return locationListOrigin[position]
    }

    override fun getLocationsCount(): Int? {
        return if (locationListOrigin.isEmpty())
            0
        else
            locationListOrigin.size
    }

    override fun getFilter(): Filter {
        if (searchFilter == null)
            searchFilter = SearchFilter(this, locationListOriginCopy)
        return searchFilter as SearchFilter
    }

    fun updateList(searchListFilter: List<Location>) {
        locationListOrigin.clear()
        locationListOrigin.addAll(searchListFilter)
        presenter?.onFilterList()
    }

}