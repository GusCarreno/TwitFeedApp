package com.styba.twitfeeds.models

import com.google.gson.annotations.SerializedName

data class LatestNew(
        @SerializedName("Horas") val time: Int,
        @SerializedName("TwitId") val twitId: Long,
        @SerializedName("full_text") val fullText: String,
        @SerializedName("screen_name") val screenName: String,
        @SerializedName("profile_image_url") val profileImage: String,
        @SerializedName("Url") val url: String,
        @SerializedName("real_url") val realUrl: String
)