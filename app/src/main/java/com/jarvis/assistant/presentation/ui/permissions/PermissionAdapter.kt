package com.jarvis.assistant.presentation.ui.permissions

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.jarvis.assistant.R
import com.google.android.material.button.MaterialButton

data class PermissionInfo(
    val name: String,
    val description: String,
    val permission: String,
    val requestCode: Int
)

class PermissionAdapter(
    private val permissions: List<PermissionInfo>,
    private val onGrant: (PermissionInfo) -> Unit
) : RecyclerView.Adapter<PermissionAdapter.PermissionViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PermissionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_permission, parent, false)
        return PermissionViewHolder(view)
    }

    override fun onBindViewHolder(holder: PermissionViewHolder, position: Int) {
        holder.bind(permissions[position])
    }

    override fun getItemCount(): Int = permissions.size

    inner class PermissionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvName = itemView.findViewById<TextView>(R.id.tvPermissionName)
        private val tvDesc = itemView.findViewById<TextView>(R.id.tvPermissionDesc)
        private val btnGrant = itemView.findViewById<MaterialButton>(R.id.btnGrant)

        fun bind(permission: PermissionInfo) {
            tvName.text = permission.name
            tvDesc.text = permission.description
            btnGrant.text = if (hasPermission(permission.permission)) "Granted" else "Grant"
            btnGrant.isEnabled = !hasPermission(permission.permission)
            btnGrant.setOnClickListener { onGrant(permission) }
        }

        private fun hasPermission(permission: String): Boolean {
            return itemView.context.checkSelfPermission(permission) ==
                android.content.pm.PackageManager.PERMISSION_GRANTED
        }
    }
}
