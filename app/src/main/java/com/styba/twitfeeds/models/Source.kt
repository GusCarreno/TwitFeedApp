package com.styba.twitfeeds.models

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity
data class Source(
        @PrimaryKey(autoGenerate = true) val id: Long,
        @SerializedName("SourceId") val sourceId: Long,
        @SerializedName("Section") val section: String,
        @SerializedName("screen_name") val screenName: String,
        @SerializedName("profile_image_url") val profileImage: String,
        @SerializedName("profile_image_url_bigger") val profileImageUrlBigger: String,
        @SerializedName("lang") val lang: String,
        var locationId: Int,
        var viewType: Int,
        var enabled: Boolean
)
