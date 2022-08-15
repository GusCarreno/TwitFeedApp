package com.styba.twitfeeds.common

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import java.lang.reflect.Type

class TwitFeedPreference(context: Context?, prefName: String) {
    private var mPref: SharedPreferences? = context?.getSharedPreferences(prefName, Context.MODE_PRIVATE)
    private var mEditor: SharedPreferences.Editor? = null
    private var mBulkUpdate = false

    companion object {
        fun newInstance(context: Context?, prefName: String): TwitFeedPreference {
            return TwitFeedPreference(context?.applicationContext, prefName)
        }
    }

    fun put(key: String, `val`: String) {
        doEdit()
        mEditor!!.putString(key, `val`)
        doCommit()
    }

    fun put(key: String, `val`: Int) {
        doEdit()
        mEditor!!.putInt(key, `val`)
        doCommit()
    }

    fun put(key: String, `val`: Boolean) {
        doEdit()
        mEditor!!.putBoolean(key, `val`)
        doCommit()
    }

    fun put(key: String, `val`: Float) {
        doEdit()
        mEditor!!.putFloat(key, `val`)
        doCommit()
    }

    /**
     * Convenience method for storing doubles.
     *
     *
     * There may be instances where the accuracy of a double is desired.
     * SharedPreferences does not handle doubles so they have to
     * cast to and from String.
     *
     * @param key The name of the preference to store.
     * @param val The new value for the preference.
     */
    fun put(key: String, `val`: Double) {
        doEdit()
        mEditor!!.putString(key, `val`.toString())
        doCommit()
    }

    fun put(key: String, `val`: Long) {
        doEdit()
        mEditor!!.putLong(key, `val`)
        doCommit()
    }

    fun <T> setList(key: String, list: List<T>) {
        val gson = Gson()
        val json = gson.toJson(list)
        put(key, json)
    }

    fun <T> getList(key: String, type: Type): List<T>? {
        val json = getString(key)
        val gson = Gson()
        return gson.fromJson<List<T>>(json, type)
    }

    fun getString(key: String, defaultValue: String): String? {
        return mPref?.getString(key, defaultValue)
    }

    fun getString(key: String): String? {
        return mPref?.getString(key, null)
    }

    fun getInt(key: String): Int? {
        return mPref?.getInt(key, 0)
    }

    fun getInt(key: String, defaultValue: Int): Int? {
        return mPref?.getInt(key, defaultValue)
    }

    fun getLong(key: String): Long? {
        return mPref?.getLong(key, 0)
    }

    fun getLong(key: String, defaultValue: Long): Long? {
        return mPref?.getLong(key, defaultValue)
    }

    fun getFloat(key: String): Float? {
        return mPref?.getFloat(key, 0f)
    }

    fun getFloat(key: String, defaultValue: Float): Float? {
        return mPref?.getFloat(key, defaultValue)
    }

    /**
     * Convenience method for retrieving doubles.
     *
     *
     * There may be instances where the accuracy of a double is desired.
     * SharedPreferences does not handle doubles so they have to
     * cast to and from String.
     *
     * @param key The name of the preference to fetch.
     */
    fun getDouble(key: String): Double {
        return getDouble(key, 0.0)
    }

    /**
     * Convenience method for retrieving doubles.
     *
     *
     * There may be instances where the accuracy of a double is desired.
     * SharedPreferences does not handle doubles so they have to
     * cast to and from String.
     *
     * @param key The name of the preference to fetch.
     */
    fun getDouble(key: String, defaultValue: Double): Double {
        try {
            return java.lang.Double.valueOf(mPref?.getString(key, defaultValue.toString())!!)
        } catch (nfe: NumberFormatException) {
            return defaultValue
        }

    }

    fun getBoolean(key: String, defaultValue: Boolean): Boolean? {
        return mPref?.getBoolean(key, defaultValue)
    }

    fun getBoolean(key: String): Boolean? {
        return mPref?.getBoolean(key, false)
    }

    /**
     * Remove keys from SharedPreferences.
     *
     * @param keys The name of the key(s) to be removed.
     */
    fun remove(vararg keys: String) {
        doEdit()
        for (key in keys) {
            mEditor!!.remove(key)
        }
        doCommit()
    }

    /**
     * Remove all keys from SharedPreferences.
     */
    fun clear() {
        doEdit()
        mEditor!!.clear()
        doCommit()
    }

    fun edit() {
        mBulkUpdate = true
        mEditor = mPref?.edit()
    }

    fun commit() {
        mBulkUpdate = false
        mEditor!!.commit()
        mEditor = null
    }

    private fun doEdit() {
        if (!mBulkUpdate && mEditor == null) {
            mEditor = mPref?.edit()
        }
    }

    private fun doCommit() {
        if (!mBulkUpdate && mEditor != null) {
            mEditor!!.commit()
            mEditor = null
        }
    }
}