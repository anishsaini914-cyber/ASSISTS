package com.jarvis.assistant.presentation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.jarvis.assistant.R

class SettingsAdapter(
    private val items: List<SettingsItem>
) : RecyclerView.Adapter<SettingsAdapter.SettingsViewHolder>() {

    data class SettingsItem(
        val title: String,
        val subtitle: String,
        val iconRes: Int,
        val action: () -> Unit
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SettingsViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_setting, parent, false)
        return SettingsViewHolder(view)
    }

    override fun onBindViewHolder(holder: SettingsViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class SettingsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val icon = itemView.findViewById<ImageView>(R.id.ivSettingIcon)
        private val title = itemView.findViewById<TextView>(R.id.tvSettingTitle)
        private val subtitle = itemView.findViewById<TextView>(R.id.tvSettingSubtitle)

        fun bind(item: SettingsItem) {
            icon.setImageResource(item.iconRes)
            title.text = item.title
            subtitle.text = item.subtitle
            itemView.setOnClickListener { item.action() }
        }
    }
}
