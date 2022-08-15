package com.styba.twitfeeds.profile

import android.text.TextUtils
import android.util.Log
import android.widget.Filter
import com.google.gson.reflect.TypeToken
import com.styba.twitfeeds.TwitFeedApplication
import com.styba.twitfeeds.common.*
import com.styba.twitfeeds.models.Location
import com.styba.twitfeeds.models.Source
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import org.json.JSONArray
import org.json.JSONObject

class ProfileModel : BaseModel<ProfileContract.Presenter>(), ProfileContract.Model {

    private val tag = "LocationModel"
    private val locationListOrigin : MutableList<Location> = arrayListOf()
    private val locationListOriginCopy : MutableList<Location> = arrayListOf()
    private var searchFilter: SearchFilter? = null
    private var disposable = CompositeDisposable()
    private val sourceListOrigin : MutableList<Source> = arrayListOf()

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

    override fun getSources() {
        getSourceFromServer()
    }

    override fun saveSourceSelected() {
        getSelectedSourcesFromData().subscribe {
            val jsonSource = JSONObject()
            jsonSource.put("LocationId", twitFeedManager?.getLocationId())
            jsonSource.put("tokenId", twitFeedManager?.getTokenId())
            val sourceArray = JSONArray()
            for (source: Source in it) {
                sourceArray.put(source.sourceId)
            }
            jsonSource.put("sources", sourceArray)
            val params = "&json=" + jsonSource.toString()
            twitFeedManager?.post(
                    Routes.SAVE_SOURCES,
                    params,
                    onSuccess = { result ->
                        val jsonResponse = parseResponse(result as String)
                        if (!TextUtils.isEmpty(jsonResponse)) {
                            val jsonStatus = JSONObject(jsonResponse)
                            if (jsonStatus.optBoolean("Status")) {
                                twitFeedManager?.setType(1)
                                presenter?.onSaveFinished()
                            } else
                                presenter?.onSaveFail()
                        } else
                            presenter?.onSaveFail()
                    },
                    onError = { result ->
                        Log.e(tag, "$result")
                        presenter?.onSaveFail()
                    })
        }
    }

    override fun register() {
        val jsonSource = JSONObject()
        jsonSource.put("LocationId", twitFeedManager?.getLocationId())
        jsonSource.put("registrationId", twitFeedManager?.getFireBaseToken())
        jsonSource.put("type", 2)
        jsonSource.put("lang", twitFeedManager?.getLanguage())
        jsonSource.put("oldlocation", twitFeedManager?.getOldLocationId())
        jsonSource.put("DeviceType", "ANDROID")
        jsonSource.put("tokenId", twitFeedManager?.getTokenId())
        val params = "&json=" + jsonSource.toString()
        twitFeedManager?.post(
                Routes.REGISTRATION,
                params,
                onSuccess = { result ->
                    Log.e(tag, "$result")
                    presenter?.onRegisterSuccess()
                },
                onError = { result ->
                    Log.e(tag, "$result")
                })
    }

    override fun detachPresenter() {
        super.detachPresenter()
        if (!disposable.isDisposed)
            disposable.dispose()
    }

    private fun cancelTask() {
        if (!disposable.isDisposed)
            disposable.dispose()
    }

    private fun getSourceFromServer() {
        val params = "&LocationId=" + twitFeedManager?.getLocationId() +
                "&lang=" + twitFeedManager?.getLanguage()
        twitFeedManager?.post(
                Routes.SOURCES,
                params,
                onSuccess = { result ->
                    val jsonResponse = parseResponse(result as String)
                    if (!TextUtils.isEmpty(jsonResponse)) {
                        sourceListOrigin.clear()
                        val sourceList: List<Source> = gSon.fromJson(jsonResponse, object : TypeToken<List<Source>>() {}.type)
                        //sourceListOrigin.addAll(sourceList)
                        for (source: Source in sourceList) {
                            source.enabled = true
                            sourceListOrigin.add(source)
                        }
                        deleteSources(sourceListOrigin)
                    } else
                        presenter?.onSourcesFail()
                },
                onError = { result ->
                    Log.e(tag, "$result")
                    presenter?.onSourcesFail()
                })
    }

    private fun deleteSources(sourceList : MutableList<Source>) {
        Single
                .fromCallable { TwitFeedApplication.database?.sourceDao()?.deleteAll(twitFeedManager?.getLocationId()) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        {
                            insertSources(sourceList)
                        },
                        {
                            cancelTask()
                        })
    }

    private fun insertSources(sourceList : MutableList<Source>) {
        if (!sourceList.isEmpty()) {
            for (source: Source in sourceList) {
                source.locationId = twitFeedManager?.getLocationId()!!
                source.viewType = TypeAdapter.SOURCE_HOLDER
            }
            val sourceListFinal : MutableList<Source> = arrayListOf()
            for (source: Source in sourceList) {
                if (!TextUtils.isEmpty(source.screenName))
                    sourceListFinal.add(source)
            }
            Single
                    .fromCallable { TwitFeedApplication.database?.sourceDao()?.insertAll(sourceListFinal) }
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            {
                                presenter?.onSourceSuccess()
                            },
                            {
                                cancelTask()
                                presenter?.onSourcesFail()
                            })
        } else
            presenter?.onSourcesFail()
    }

    private fun getSelectedSourcesFromData(): Observable<List<Source>> = Observable.create { subscribe ->
        val subscriber: Disposable? = TwitFeedApplication.database?.sourceDao()
                ?.getSourceSelected(twitFeedManager?.getLocationId())
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe(
                        {
                            subscribe.onNext(it)
                        },
                        {
                            subscribe.onNext(ArrayList())
                        }
                )
        disposable.add(subscriber!!)
    }

}