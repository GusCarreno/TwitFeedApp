package com.styba.twitfeeds.location

import android.text.TextUtils
import android.util.Log
import android.widget.Filter
import com.google.gson.reflect.TypeToken
import com.styba.twitfeeds.common.*
import com.styba.twitfeeds.models.Location

class LocationModel : BaseModel<LocationContract.Presenter>(), LocationContract.Model {

    private val tag = "LocationModel"
    private val locationListOrigin : MutableList<Location> = arrayListOf()
    private val locationListOriginCopy : MutableList<Location> = arrayListOf()
    private var searchFilter: SearchFilter? = null

    override fun initManager(twitFeedManager: TwitFeedManager?) {
        this.twitFeedManager = twitFeedManager
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