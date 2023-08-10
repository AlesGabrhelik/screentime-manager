package com.example.screentime_manager

import android.content.pm.PackageManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class SelectedAppAdapter(private val selectedApps: MutableList<String>, private val mainActivity: MainActivity) :

    RecyclerView.Adapter<SelectedAppAdapter.SelectedAppViewHolder>() {

    inner class SelectedAppViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val appNameTextView: TextView = itemView.findViewById(R.id.appNameTextView)
        val deleteButton: Button = itemView.findViewById(R.id.deleteButton)


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectedAppViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_selected_app, parent, false)
        return SelectedAppViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: SelectedAppViewHolder, position: Int) {
        val packageName = selectedApps[position]
        val packageManager = holder.itemView.context.packageManager
        val appName = packageManager.getApplicationLabel(
            packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA)
        ).toString()

        holder.appNameTextView.text = appName

        holder.deleteButton.setOnClickListener {
            selectedApps.removeAt(position)
            notifyItemRemoved(position)
            mainActivity.deleteSelectedApp(packageName)
        }
    }

    override fun getItemCount(): Int = selectedApps.size
}