package com.styba.twitfeeds.services

import android.app.IntentService
import android.content.Intent
import android.text.TextUtils
import android.util.Log
import com.google.firebase.iid.FirebaseInstanceId
import com.styba.twitfeeds.common.TwitFeedManager

class FireBaseTokenService: IntentService("FireBaseTokenService") {

    private val tag = "FireBaseTokenService"

    override fun onHandleIntent(intent: Intent?) {
        if (intent != null) {
            val twitFeedManager = TwitFeedManager(applicationContext)
            try {
                val fireBaseToken = FirebaseInstanceId.getInstance().token
                if (fireBaseToken != null) {
                    Log.e(tag, fireBaseToken)
                    if (!TextUtils.equals(twitFeedManager.getFireBaseToken(), fireBaseToken)) {
                        twitFeedManager.setFireBaseToken(fireBaseToken)
                        twitFeedManager.setIsFireBaseTokenChange(true)
                    }
                }
            } catch (exc: Exception) {
                // Return exc to onPostExecute
                exc.printStackTrace()
                Log.d(tag, exc.message)
            }
        }
    }

}