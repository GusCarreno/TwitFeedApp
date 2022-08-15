package com.styba.twitfeeds.common

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import com.styba.twitfeeds.BuildConfig
import com.styba.twitfeeds.R
import java.util.*

class Utils {
    companion object {
        fun getDeviceId(): String {
            val idRandom = UUID.randomUUID()
            return idRandom.toString()
        }

        fun getAppVersion(): String {
            return "Version: " + BuildConfig.VERSION_NAME
        }

        fun verifyConnection(context: Context): Boolean {
            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetwork = cm.activeNetworkInfo
            return activeNetwork != null && isConnectedToWifiOrMobileData(activeNetwork)
        }

        private fun isConnectedToWifiOrMobileData(networkInfo: NetworkInfo): Boolean {
            return networkInfo.state == NetworkInfo.State.CONNECTED && (networkInfo.type == ConnectivityManager.TYPE_WIFI || networkInfo.type == ConnectivityManager.TYPE_MOBILE)
        }

        fun isWifiConnection(context: Context): Boolean {
            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val networkInfo = cm.activeNetworkInfo
            return networkInfo.type == ConnectivityManager.TYPE_WIFI
        }

        fun showSnackBar(activity: Activity, view: View, msg: String) {
            val snackBar = Snackbar.make(view, msg, Snackbar.LENGTH_LONG)
            val snackBarView = snackBar.view
            snackBarView.setBackgroundColor(ContextCompat.getColor(activity, R.color.colorYellow1))
            val textView = snackBarView.findViewById<View>(android.support.design.R.id.snackbar_text) as TextView
            textView.setTextColor(Color.BLACK)
            textView.typeface = Typeface.createFromAsset(activity.assets, "fonts/Lato_Regular.ttf")
            snackBar.show()
        }

        fun hideSoftKeyboard(activity: Activity, editText: EditText) {
            if (activity.currentFocus != null && activity.currentFocus is EditText) {
                val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(editText.windowToken, 0)
            }
        }
    }
}