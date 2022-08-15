package com.styba.twitfeeds.models

import java.io.Serializable

class TwitRow : Serializable {
    var viewType: Int = 0
    var twitList : MutableList<Twit> = arrayListOf()
    var twit: Twit? = null
    var adUrlImage: String = ""
    var adUrl: String = ""
    var adType: Int = 0
    var facebookAd: String = ""
}