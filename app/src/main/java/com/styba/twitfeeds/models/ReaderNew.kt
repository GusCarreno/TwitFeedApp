package com.styba.twitfeeds.models

import com.google.gson.annotations.SerializedName

class ReaderNew(
        @SerializedName("title") val title: String,
        @SerializedName("author") val author: String,
        @SerializedName("date_published") val date_published: String,
        @SerializedName("lead_image_url") val lead_image_url: String,
        @SerializedName("content") val content: String,
        @SerializedName("url") val url: String,
        @SerializedName("domain") val domain: String
)