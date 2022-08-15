package com.styba.twitfeeds.common

import android.app.Activity
import android.content.Context
import okhttp3.*
import java.io.IOException
import java.util.concurrent.TimeUnit

class TwitFeedManager(val context: Context?) {

    private var activity: Activity? = null
    private var okHttpClient: OkHttpClient? = null
    private var twitFeedPreference = TwitFeedPreference.newInstance(context, Constants.TWIT_MANAGER)

    init {
        okHttpClient = OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build()
    }

    fun setActivity(activity: Activity?) {
        this.activity = activity
    }

    fun get(urlString: String, onSuccess: ((result: Any?) -> Unit)?, onError: ((result: Any?) -> Unit)?) {

        val request = Request.Builder()
                .url(urlString)
                .get()
                .addHeader("x-api-key", "IelPdbjUppjjotRQvaapZQ0svT4HJS4jEDUj1KeQ")
                .build()

        okHttpClient
                ?.newCall(request)
                ?.enqueue(object : Callback {

                    override fun onFailure(call: Call, e: IOException) {
                        activity?.runOnUiThread {
                            onError?.invoke(e.message)
                        }
                    }

                    @Throws(IOException::class)
                    override fun onResponse(call: Call, response: Response) {
                        val xmlResponse = response.body()?.string()

                        activity?.runOnUiThread {
                            if (response.code() == 200)
                                onSuccess?.invoke(xmlResponse)
                            else
                                onError?.invoke("Lo sentimos no hay datos disponibles")
                        }
                    }
                })
    }

    fun post(urlString: String, parameters: String?, onSuccess: ((result: Any?) -> Unit)?, onError: ((result: Any?) -> Unit)?) {

        var params = parameters ?: ""
        params += "&TokenId=" + getTokenId() + "&key=" + Constants.TWIT_FEED_KEY
        val body = RequestBody.create(Constants.mediaTypeUrlEncoded, params)

        val request = Request.Builder()
                .url(urlString)
                .post(body)
                .tag(urlString)
                .build()

        okHttpClient
                ?.newCall(request)
                ?.enqueue(object : Callback {

                    override fun onFailure(call: Call, e: IOException) {
                        activity?.runOnUiThread {
                            onError?.invoke(e.message)
                        }
                    }

                    @Throws(IOException::class)
                    override fun onResponse(call: Call, response: Response) {
                        val xmlResponse = response.body()?.string()

                        activity?.runOnUiThread {
                            if (response.code() == 200)
                                onSuccess?.invoke(xmlResponse)
                            else
                                onError?.invoke("Lo sentimos no hay datos disponibles")
                        }
                    }
                })
    }

    fun getTokenId(): String? {
        return twitFeedPreference.getString(Constants.TOKEN_ID, "")
    }

    fun setTokenId(token: String) {
        twitFeedPreference.put(Constants.TOKEN_ID, token)
    }

    fun getFireBaseToken(): String? {
        return twitFeedPreference.getString(Constants.FIRE_BASE_TOKEN, "")
    }

    fun setFireBaseToken(token: String) {
        twitFeedPreference.put(Constants.FIRE_BASE_TOKEN, token)
    }

    fun isFireBaseTokenChange(): Boolean? {
        return twitFeedPreference.getBoolean(Constants.FIRE_BASE_TOKEN_CHANGE, false)
    }

    fun setIsFireBaseTokenChange(isChange: Boolean) {
        twitFeedPreference.put(Constants.FIRE_BASE_TOKEN_CHANGE, isChange)
    }

    fun getLanguage(): String? {
        return twitFeedPreference.getString(Constants.LANGUAGE, "es")
    }

    fun setLanguage(language: String) {
        twitFeedPreference.put(Constants.LANGUAGE, language)
    }

    fun getLanguageApp(): String? {
        return twitFeedPreference.getString(Constants.LANGUAGE_APP, "es")
    }

    fun setLanguageApp(language: String) {
        twitFeedPreference.put(Constants.LANGUAGE_APP, language)
    }

    fun getLocationId(): Int? {
        return twitFeedPreference.getInt(Constants.LOCATION_ID, 0)
    }

    fun setLocationId(locationId: Int) {
        twitFeedPreference.put(Constants.LOCATION_ID, locationId)
    }

    fun getLocationName(): String? {
        return twitFeedPreference.getString(Constants.LOCATION_NAME, "")
    }

    fun setLocationName(locationName: String) {
        twitFeedPreference.put(Constants.LOCATION_NAME, locationName)
    }

    fun getLocationCountry(): String? {
        return twitFeedPreference.getString(Constants.LOCATION_COUNTRY, "")
    }

    fun setLocationCountry(locationCountry: String) {
        twitFeedPreference.put(Constants.LOCATION_COUNTRY, locationCountry)
    }

    fun isExplore(): Boolean? {
        return twitFeedPreference.getBoolean(Constants.IS_EXPLORE, false)
    }

    fun setExplore(isExplore: Boolean) {
        twitFeedPreference.put(Constants.IS_EXPLORE, isExplore)
    }

    fun getExploreLocationId(): Int? {
        return twitFeedPreference.getInt(Constants.EXPLORE_LOCATION_ID, 0)
    }

    fun setExploreLocationId(locationId: Int) {
        twitFeedPreference.put(Constants.EXPLORE_LOCATION_ID, locationId)
    }

    fun getExploreLocationCountry(): String? {
        return twitFeedPreference.getString(Constants.EXPLORE_LOCATION_COUNTRY, "")
    }

    fun setExploreLocationCountry(locationCountry: String) {
        twitFeedPreference.put(Constants.EXPLORE_LOCATION_COUNTRY, locationCountry)
    }

    fun getExploreLocationName(): String? {
        return twitFeedPreference.getString(Constants.EXPLORE_LOCATION_NAME, "")
    }

    fun setExploreLocationName(locationName: String) {
        twitFeedPreference.put(Constants.EXPLORE_LOCATION_NAME, locationName)
    }

    fun isSettingsUp(): Boolean? {
        return twitFeedPreference.getBoolean(Constants.SETTINGS_UP, false)
    }

    fun setSettingsUp(isSettingsUp: Boolean) {
        twitFeedPreference.put(Constants.SETTINGS_UP, isSettingsUp)
    }

    fun isOnlyWifi(): Boolean? {
        return twitFeedPreference.getBoolean(Constants.IS_ONLY_WIFI, false)
    }

    fun setIsOnlyWifi(isOnlyWifi: Boolean) {
        twitFeedPreference.put(Constants.IS_ONLY_WIFI, isOnlyWifi)
    }

    fun isReaderMode(): Boolean? {
        return twitFeedPreference.getBoolean(Constants.IS_READER_MODE, true)
    }

    fun setIsReaderMode(isReaderMode: Boolean) {
        twitFeedPreference.put(Constants.IS_READER_MODE, isReaderMode)
    }

    fun isSelectAll(): Boolean? {
        return twitFeedPreference.getBoolean(Constants.IS_SELECT_ALL, false)
    }

    fun setIsSelectAll(isSelectAll: Boolean) {
        twitFeedPreference.put(Constants.IS_SELECT_ALL, isSelectAll)
    }

    fun getType(): Int? {
        return twitFeedPreference.getInt(Constants.TYPE, 0)
    }

    fun setType(type: Int) {
        twitFeedPreference.put(Constants.TYPE, type)
    }

    fun getJsonAds(): String? {
        return twitFeedPreference.getString(Constants.JSON_ADS, "")
    }

    fun setJsonAds(jsonAds: String) {
        twitFeedPreference.put(Constants.JSON_ADS, jsonAds)
    }

    fun getJsonFacebookAds(): String? {
        return twitFeedPreference.getString(Constants.JSON_FACEBOOK_ADS, "")
    }

    fun setJsonFacebookAds(jsonAds: String) {
        twitFeedPreference.put(Constants.JSON_FACEBOOK_ADS, jsonAds)
    }

    fun getOldLocationId(): Int? {
        return twitFeedPreference.getInt(Constants.OLD_LOCATION_ID, 0)
    }

    fun setOldLocationId(oldLocationId: Int) {
        twitFeedPreference.put(Constants.OLD_LOCATION_ID, oldLocationId)
    }

    fun hasMenu(): Boolean? {
        return twitFeedPreference.getBoolean(Constants.HAS_MENU, false)
    }

    fun setHasMenu(hasMenu: Boolean) {
        twitFeedPreference.put(Constants.HAS_MENU, hasMenu)
    }

    fun hasExploreMenu(): Boolean? {
        return twitFeedPreference.getBoolean(Constants.HAS_EXPLORE_MENU, false)
    }

    fun setHasExploreMenu(hasMenu: Boolean) {
        twitFeedPreference.put(Constants.HAS_EXPLORE_MENU, hasMenu)
    }

    fun getFlag(): String? {
        return twitFeedPreference.getString(Constants.FLAG, "")
    }

    fun setFlag(flag: String) {
        twitFeedPreference.put(Constants.FLAG, flag)
    }

    fun getExploreFlag(): String? {
        return twitFeedPreference.getString(Constants.EXPLORE_FLAG, "")
    }

    fun setExploreFlag(flag: String) {
        twitFeedPreference.put(Constants.EXPLORE_FLAG, flag)
    }
}