package com.styba.twitfeeds.models

import com.google.gson.annotations.SerializedName

data class Weather(
        @SerializedName("summary") val summary: String,
        @SerializedName("icon") val icon: String,
        @SerializedName("temperature") val temperature: Int,
        @SerializedName("LocalTime") val localTime: String
)