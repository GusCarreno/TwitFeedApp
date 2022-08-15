package com.styba.twitfeeds.models

import com.google.gson.annotations.SerializedName
import com.styba.twitfeeds.common.TypeAdapter

data class Location(
        @SerializedName("LocationId") val locationId: Int,
        @SerializedName("LocationName") val locationName: String,
        @SerializedName("LocationCountry") val locationCountry: String,
        @SerializedName("Menu") val menu: Int,
        @SerializedName("flag") val flag: String
) {
    var viewType: Int = TypeAdapter.LOCATION_HOLDER
}