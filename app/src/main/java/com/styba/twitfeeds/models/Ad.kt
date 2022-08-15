package com.styba.twitfeeds.models

import com.google.gson.annotations.SerializedName

data class Ad(
        @SerializedName("id") val id: Int,
        @SerializedName("AdUrl") val adUrl: String,
        @SerializedName("AdSize") val adSize: String,
        @SerializedName("Url") val url: String,
        @SerializedName("Active") val active: Boolean,
        @SerializedName("Publicidad") val advertising: Int
)