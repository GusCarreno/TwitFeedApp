package com.styba.twitfeeds.common

import android.widget.Filter
import com.styba.twitfeeds.location.LocationModel
import com.styba.twitfeeds.main.fragments.settings.SettingsModel
import com.styba.twitfeeds.models.Location
import com.styba.twitfeeds.profile.ProfileModel

class SearchFilter(private val model: Any, private val searchList: MutableList<Location>) : Filter() {

    override fun performFiltering(constraint: CharSequence?): FilterResults {
        val constraintLow = constraint.toString().toLowerCase()
        val result = Filter.FilterResults()
        if (constraintLow.isNotEmpty()) {
            val filteredItems : MutableList<Location> = arrayListOf()
            var i = 0
            val l = searchList.size
            while (i < l) {
                val searchItem = searchList[i]
                if (searchItem.locationName.toLowerCase().contains(constraintLow) or
                        searchItem.locationCountry.toLowerCase().contains(constraintLow))
                    filteredItems.add(searchItem)
                i++
            }
            result.count = filteredItems.size
            result.values = filteredItems
        } else {
            synchronized(this) {
                result.values = searchList
                result.count = searchList.size
            }
        }
        return result
    }

    @Suppress("UNCHECKED_CAST")
    override fun publishResults(constraint: CharSequence?, result: FilterResults?) {
        when (model) {
            is LocationModel -> model.updateList(result?.values as List<Location>)
            is SettingsModel -> model.updateList(result?.values as List<Location>)
            else -> (model as ProfileModel).updateList(result?.values as List<Location>)
        }
    }
}