package com.styba.twitfeeds.location

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import com.styba.twitfeeds.common.BaseViewHolder
import com.styba.twitfeeds.main.fragments.settings.SettingsPresenter
import com.styba.twitfeeds.models.Location
import com.styba.twitfeeds.profile.ProfilePresenter

class LocationAdapter(private val presenter: Any) : RecyclerView.Adapter<BaseViewHolder>(), Filterable {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return when (presenter) {
            is LocationPresenter -> presenter.onCreateViewHolder(parent, viewType)
            is SettingsPresenter -> presenter.onCreateViewHolder(parent, viewType)
            else -> (presenter as ProfilePresenter).onCreateViewHolder(parent, viewType)
        }
    }

    override fun getItemCount(): Int {
        return when (presenter) {
            is LocationPresenter -> presenter.getLocationsCount()!!
            is SettingsPresenter -> presenter .getLocationsCount()!!
            else -> (presenter as ProfilePresenter).getLocationsCount()!!
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        return when (presenter) {
            is LocationPresenter -> presenter.onBindViewHolder(holder, position)
            is SettingsPresenter -> presenter.onBindLocationHolder(holder, position)
            else -> (presenter as ProfilePresenter).onBindViewHolder(holder, position)
        }
    }

    override fun getItemViewType(position: Int): Int {
        val location = when (presenter) {
            is LocationPresenter -> presenter.getLocationRow(position) as Location
            is SettingsPresenter -> presenter.getLocationRow(position) as Location
            else -> (presenter as ProfilePresenter).getLocationRow(position) as Location
        }
        return location.viewType
    }

    override fun getFilter(): Filter {
        return when (presenter) {
            is LocationPresenter -> presenter.getFilter()
            is SettingsPresenter -> presenter.getFilter()
            else -> (presenter as ProfilePresenter).getFilter()
        }
    }

}