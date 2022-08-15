package com.styba.twitfeeds.welcome

import android.text.TextUtils
import android.util.Log
import com.google.gson.reflect.TypeToken
import com.styba.twitfeeds.TwitFeedApplication
import com.styba.twitfeeds.common.BaseModel
import com.styba.twitfeeds.common.Routes
import com.styba.twitfeeds.common.TwitFeedManager
import com.styba.twitfeeds.common.TypeAdapter
import com.styba.twitfeeds.models.Source
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.json.JSONArray
import org.json.JSONObject

class WelcomeModel : BaseModel<WelcomeContract.Presenter>(), WelcomeContract.Model {
    private val tag = "LocationModel"
    private var disposable = CompositeDisposable()
    private val sourceListOrigin : MutableList<Source> = arrayListOf()

    override fun initManager(twitFeedManager: TwitFeedManager?) {
        this.twitFeedManager = twitFeedManager
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
        TwitFeedApplication.database?.sourceDao()
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
    }

}