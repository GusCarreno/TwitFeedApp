package com.styba.twitfeeds

import android.app.Application
import android.arch.persistence.room.Room
import android.text.TextUtils
import com.crashlytics.android.Crashlytics
import com.crashlytics.android.answers.Answers
import com.crashlytics.android.core.CrashlyticsCore
import com.styba.twitfeeds.calligraphy.CalligraphyConfig
import com.styba.twitfeeds.calligraphy.CalligraphyInterceptor
import com.styba.twitfeeds.common.TwitFeedManager
import com.styba.twitfeeds.common.Utils
import com.styba.twitfeeds.models.database.TwitDatabase
import com.styba.twitfeeds.viewpump.ViewPump
import com.twitter.sdk.android.core.Twitter
import com.twitter.sdk.android.core.TwitterAuthConfig
import com.twitter.sdk.android.core.TwitterConfig
import io.fabric.sdk.android.Fabric


class TwitFeedApplication : Application() {

    companion object {
        var database: TwitDatabase? = null
    }

    override fun onCreate() {
        super.onCreate()

        /* ************* FABRIC ************* */
        val core = CrashlyticsCore.Builder().disabled(BuildConfig.DEBUG).build()
        Fabric.with(this, Crashlytics.Builder().core(core).build(), Answers())

        /* ************* CALLIGRAPHY ************* */
        ViewPump.init(ViewPump.builder()
                .addInterceptor(CalligraphyInterceptor(
                         CalligraphyConfig.Builder()
                                .setDefaultFontPath("fonts/Lato_Regular.ttf")
                                .setFontAttrId(R.attr.fontPath)
                                .build()))
                .build())

        /* ************* ROOM ************* */
        TwitFeedApplication.database = Room
                .databaseBuilder(this,
                        TwitDatabase::class.java,
                        "twit-feed-db")
                .fallbackToDestructiveMigration()
                .build()

        /* ************* TWITTER ************* */
        val config = TwitterConfig.Builder(this)
                .debug(true)
                .twitterAuthConfig(TwitterAuthConfig(getString(R.string.twitter_key), getString(R.string.twitter_secret)))
                .build()
        Twitter.initialize(config)

        val twitFeedManager = TwitFeedManager(this)
        if (TextUtils.isEmpty(twitFeedManager.getTokenId()))
            twitFeedManager.setTokenId(Utils.getDeviceId())
    }
}