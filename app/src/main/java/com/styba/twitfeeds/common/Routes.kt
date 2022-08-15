package com.styba.twitfeeds.common

object Routes {
    private const val DOMAIN = "http://52.3.89.139/WS_TwitFeed.asmx/"
    //private const val DOMAIN = "http://ec2-54-89-247-51.compute-1.amazonaws.com/WS_TwitFeed.asmx/"

    const val COUNTRY_LIST = DOMAIN + "CountriesList"
    const val LATEST_NEWS = DOMAIN + "ReturnLastBreaking"
    const val WEATHER = DOMAIN + "locationWeather"
    const val TWITS = DOMAIN + "ReturnTwits"
    const val SOURCES = DOMAIN + "ReturnSources"
    const val SAVE_SOURCES = DOMAIN + "UserSelectedSources"
    const val REGISTRATION = DOMAIN + "UpdateRegistrationId"
    const val ADVERTISING = DOMAIN + "Advertising"
    const val READER_MODE = "https://mercury.postlight.com/parser?url="
}