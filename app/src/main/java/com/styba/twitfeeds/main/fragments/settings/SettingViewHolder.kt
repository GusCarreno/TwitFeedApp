package com.styba.twitfeeds.main.fragments.settings

import android.view.View
import com.styba.twitfeeds.common.BaseViewHolder
import com.styba.twitfeeds.events.SettingEvent
import com.styba.twitfeeds.models.Setting
import kotlinx.android.synthetic.main.setting_row.view.*
import org.greenrobot.eventbus.EventBus

class SettingViewHolder(itemView: View?) : BaseViewHolder(itemView) {

    override fun bindItem(item: Any, position: Int) {
        val location = item as? Setting
        itemView.textViewSetting.text = location?.name
        itemView.setOnClickListener { EventBus.getDefault().post(SettingEvent(position)) }
    }

}