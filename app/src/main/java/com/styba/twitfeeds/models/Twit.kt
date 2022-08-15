package com.styba.twitfeeds.models

import com.google.gson.annotations.SerializedName
import com.styba.twitfeeds.common.TypeAdapter
import java.io.Serializable

data class Twit(
        @SerializedName("TwitId") val twitId: Long,
        @SerializedName("horas") val hours: Int,
        @SerializedName("full_text") val fullText: String,
        @SerializedName("Url") val url: String,
        @SerializedName("screen_name") val screenName: String,
        @SerializedName("media_url") val mediaUrl: String,
        @SerializedName("real_url") val realUrl: String,
        @SerializedName("alternative_media") val altMedia: String,
        @SerializedName("Title") val title: String,
        @SerializedName("profile_image_url") val profileImage: String,
        @SerializedName("TwitterImage") val twitterImage: String,
        @SerializedName("TwitterText") val twitterText: String
): Serializable {
    var viewType: Int = TypeAdapter.TWIT_HOLDER
    var adUrlImage: String = ""
    var adUrl: String = ""
    var adType: Int = 0
    var facebookAd: String = ""
}
