package com.styba.twitfeeds.common

import okhttp3.MediaType

object Constants {
    const val TWIT_FEED_KEY = "PsgqkLkD3OmfRO4ent5mA"
    const val TERMS_URL = "http://twitfeedapps.com/terminos.html"
    const val FAQ_URL = "http://twitfeedapps.com/#faq"
    const val TWIT_MANAGER = "twit_manager"
    const val LOCATION_ID = "location_id"
    const val OLD_LOCATION_ID = "old_location_id"
    const val IS_EXPLORE = "is_explore"
    const val EXPLORE_LOCATION_ID = "explore_location_id"
    const val LOCATION_NAME = "location_name"
    const val LOCATION_COUNTRY = "location_country"
    const val EXPLORE_LOCATION_NAME = "explore_location_name"
    const val EXPLORE_LOCATION_COUNTRY = "explore_location_country"
    const val TYPE = "type"
    const val IS_ONLY_WIFI = "is_only_wifi"
    const val IS_READER_MODE = "is_reader_mode"
    const val IS_SELECT_ALL = "is_select_all"
    const val JSON_ADS = "json_ads"
    const val JSON_FACEBOOK_ADS = "json_facebook_ads"
    const val LANGUAGE = "language"
    const val LANGUAGE_APP = "language_app"
    const val SETTINGS_UP = "settings_up"
    const val TOKEN_ID = "token_id"
    const val FIRE_BASE_TOKEN = "fire_base_token"
    const val FIRE_BASE_TOKEN_CHANGE = "fire_base_token_change"
    const val HAS_MENU = "has_menu"
    const val HAS_EXPLORE_MENU = "has_explore_menu"
    const val FLAG = "flag"
    const val EXPLORE_FLAG = "explore_flag"
    const val REQUEST_SIGN_IN = 1
    const val REQUEST_SOURCES = 2
    const val REQUEST_PROFILE = 3
    const val NOTIFICATION_TWIT_ID = "TwitId"
    const val NOTIFICATION_URL = "url"
    const val NOTIFICATION_REAL_URL = "real_url"
    const val NOTIFICATION_IS_AD = "is_ad"
    const val NOTIFICATION_IS_FROM = "isFromNotification"
    const val IS_LATEST_NEW = "isLatestNew"
    val mediaTypeUrlEncoded = MediaType.parse("application/x-www-form-urlencoded")
}