package com.styba.twitfeeds.location

import android.content.Context
import android.view.View
import com.styba.twitfeeds.R
import com.styba.twitfeeds.common.BaseViewHolder
import com.styba.twitfeeds.events.LocationEvent
import com.styba.twitfeeds.models.Location
import kotlinx.android.synthetic.main.location_row.view.*
import org.greenrobot.eventbus.EventBus

class LocationViewHolder(itemView: View?, val context: Context) : BaseViewHolder(itemView) {

    override fun bindItem(item: Any, position: Int) {
        val location = item as? Location

        itemView.textViewLocation.text = context.resources.getString(R.string.location_item, location?.locationName, location?.locationCountry)
        itemView.setOnClickListener { EventBus.getDefault().post(LocationEvent(location?.locationId!!,
                location.locationName, location.locationCountry, location.menu, location.flag)) }
    }

}