package com.styba.twitfeeds.main.fragments.settings

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.styba.twitfeeds.common.BaseViewHolder
import com.styba.twitfeeds.models.Setting

class SettingsAdapter(private val presenter: SettingsPresenter) : RecyclerView.Adapter<BaseViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return presenter.onCreateViewHolder(parent, viewType)
    }

    override fun getItemCount(): Int = presenter.getSettingsCount()!!

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        presenter.onBindSettingsHolder(holder, position)
    }

    override fun getItemViewType(position: Int): Int {
        val setting = presenter.getSettingRow(position) as Setting
        return setting.viewType
    }

}