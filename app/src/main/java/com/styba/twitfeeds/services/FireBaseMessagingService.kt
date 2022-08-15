package com.styba.twitfeeds.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.RingtoneManager
import android.os.Build
import android.support.v4.app.NotificationCompat
import android.text.TextUtils
import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.styba.twitfeeds.R
import com.styba.twitfeeds.common.Constants
import com.styba.twitfeeds.common.TwitFeedManager

class FireBaseMessagingService: FirebaseMessagingService() {

    companion object {
        const val TAG = "FBMessagingService"
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        val channelId = getString(R.string.default_notification_channel_id)
        createNotificationChannel(this, channelId)
        createNotification(this, remoteMessage, channelId)
    }

    override fun onNewToken(token: String) {
        val twitFeedManager = TwitFeedManager(applicationContext)
        Log.e(TAG, "Refreshed token: $token")
        if (!TextUtils.equals(twitFeedManager.getFireBaseToken(), token)) {
            twitFeedManager.setFireBaseToken(token)
            twitFeedManager.setIsFireBaseTokenChange(true)
        }
    }

    private fun createNotificationChannel(context: Context, channelId: String) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = context.getString(R.string.channel_name)
            val description = context.getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT

            val channel = NotificationChannel(channelId, name, importance)
            channel.description = description
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            val notificationManager = context.getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun createNotification(context: Context, remoteMessage: RemoteMessage, channelId: String) {
        val clickAction = remoteMessage.notification?.clickAction
        val builder = NotificationCompat.Builder(this, channelId)
        builder.setSmallIcon(R.mipmap.ic_tf_notification)
        builder.setContentTitle(remoteMessage.notification?.title)
        builder.setStyle(NotificationCompat.BigTextStyle().bigText(remoteMessage.notification?.body))
        builder.setContentText(remoteMessage.notification?.body)
        builder.setLights(Color.BLUE, 1000, 1000)
        builder.setAutoCancel(true)
        builder.setVibrate(longArrayOf(0, 400, 250, 400))
        builder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
        builder.priority = NotificationCompat.PRIORITY_MAX
        builder.setContentIntent(PendingIntent.getActivity(this, 0,
                Intent(clickAction)
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                        .putExtra(Constants.NOTIFICATION_TWIT_ID, remoteMessage.data[Constants.NOTIFICATION_TWIT_ID])
                        .putExtra(Constants.NOTIFICATION_URL, remoteMessage.data[Constants.NOTIFICATION_URL])
                        .putExtra(Constants.NOTIFICATION_REAL_URL, remoteMessage.data[Constants.NOTIFICATION_REAL_URL])
                        .putExtra(Constants.NOTIFICATION_IS_FROM, remoteMessage.data[Constants.NOTIFICATION_IS_FROM])
                        .putExtra(Constants.NOTIFICATION_IS_AD, false),
                PendingIntent.FLAG_ONE_SHOT))

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(1, builder.build())
    }
}