package com.styba.twitfeeds.sources

import android.os.Handler
import android.text.TextUtils
import android.util.Log
import com.google.gson.reflect.TypeToken
import com.styba.twitfeeds.R
import com.styba.twitfeeds.TwitFeedApplication
import com.styba.twitfeeds.common.*
import com.styba.twitfeeds.models.Source
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import org.json.JSONArray
import org.json.JSONObject

class SourceModel : BaseModel<SourceContract.Presenter>(), SourceContract.Model {

    private val tag = "LocationModel"
    private var disposable = CompositeDisposable()
    private val sourceListOrigin : MutableList<Source> = arrayListOf()

    override fun initManager(twitFeedManager: TwitFeedManager?) {
        this.twitFeedManager = twitFeedManager
    }

    override fun getSources() {
        Single
                .fromCallable { TwitFeedApplication.database?.sourceDao()?.getCount(twitFeedManager?.getLocationId()) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        {
                            if (it > 0)
                                getSourceLocal()
                            else
                                getSourceFromServer()
                        },
                        {
                            getSourceFromServer()
                        })
    }

    override fun getSourceRow(position: Int): Source? {
        return sourceListOrigin[position]
    }

    override fun getSourceCount(): Int? {
        return if (sourceListOrigin.isEmpty())
            0
        else
            sourceListOrigin.size
    }

    override fun updateSource(position: Int, isEnabled: Boolean, source: Source) {
        if (!isEnabled)
            twitFeedManager?.setIsSelectAll(false)
        val handler = Handler()
        handler.postDelayed({
            source.enabled = isEnabled
            Single
                    .fromCallable { TwitFeedApplication.database?.sourceDao()?.updateSource(source) }
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            {
                                sourceListOrigin[position].enabled = isEnabled
                                presenter?.onFinishUpdate(position)
                            },
                            {
                                presenter?.onFinishUpdate(position)
                            })
        }, 500)
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

    override fun selectAll() {
        val sourceList : MutableList<Source> = arrayListOf()
        for (source: Source in sourceListOrigin) {
            source.enabled = true
            sourceList.add(source)
        }
        deleteSources(sourceList)
    }

    override fun desSelectAll() {
        val sourceList : MutableList<Source> = arrayListOf()
        for (source: Source in sourceListOrigin) {
            source.enabled = false
            sourceList.add(source)
        }
        deleteSources(sourceList)
    }

    override fun countSelected(): Int {
        var counter = 0
        for (source: Source in sourceListOrigin) {
            if (source.enabled)
                counter++
        }
        return counter
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
        //val sourceList: List<Source> = gSon.fromJson(jsonResponse, object : TypeToken<List<Source>>() {}.type)
        if (!sourceList.isEmpty()) {
            for (source: Source in sourceList) {
                source.locationId = twitFeedManager?.getLocationId()!!
                source.viewType = TypeAdapter.SOURCE_HOLDER
                /*if (!isDesSelect)
                    source.enabled = true*/
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
                                getSourceLocal()
                            },
                            {
                                cancelTask()
                                presenter?.onSourcesFail()
                            })
        } else
            presenter?.onSourcesFail()
    }

    private fun getSourcesFromData(): Observable<List<Source>> = Observable.create { subscribe ->
        TwitFeedApplication.database?.sourceDao()
                ?.getAll(twitFeedManager?.getLocationId())
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

    private fun getSourceLocal() {
        getSourcesFromData().subscribe {
            var section = ""
            sourceListOrigin.clear()
            for (source: Source in it) {
                if (!TextUtils.equals(source.section, section)) {
                    section = source.section
                    val sectionName = if (getSectionName(section) != null)
                        getSectionName(section)
                    else
                        ""
                    sourceListOrigin.add(Source(1, 1, sectionName!!, "",
                            "", "", "", 1, TypeAdapter.SOURCE_SECTION_HOLDER, false))
                }
                source.viewType = TypeAdapter.SOURCE_HOLDER
                sourceListOrigin.add(source)
            }
            presenter?.onSourceSuccess()
        }
    }

    private fun getSectionName(section: String): String? {
        return when (section) {
            "ECONOMY_BUSINESS" -> presenter?.getContext()?.getString(R.string.source_section_economy)
            "INTERNATIONAL" -> presenter?.getContext()?.getString(R.string.source_section_international)
            "NATIONAL" -> presenter?.getContext()?.getString(R.string.source_section_national)
            "SCI_TECH" -> presenter?.getContext()?.getString(R.string.source_section_tech)
            "SPORTS" -> presenter?.getContext()?.getString(R.string.source_section_sports)
            "LOCAL" -> presenter?.getContext()?.getString(R.string.source_section_local)
            else -> ""
        }
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