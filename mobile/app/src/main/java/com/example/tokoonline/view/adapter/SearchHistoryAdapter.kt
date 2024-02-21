package com.example.tokoonline.view.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.tokoonline.R
import com.example.tokoonline.core.util.OnItemClick

// Create a new Kotlin file, e.g., SearchHistoryAdapter.kt

class SearchHistoryAdapter(private val onItemClick: (String) -> Unit) :
    ListAdapter<String, SearchHistoryAdapter.ViewHolder>(DiffCallback()) {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val historyItemText: TextView = itemView.findViewById(R.id.historyItemText)
        val rootSearch: LinearLayout = itemView.findViewById(R.id.rootSearch)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_search_history, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val searchHistoryItem = getItem(position)
        holder.historyItemText.text = searchHistoryItem


        holder.rootSearch.setOnClickListener {
            Log.d("SearchHistoryAdapter", "Item clicked: $searchHistoryItem")
            onItemClick(searchHistoryItem)  // Pass the selectedSearch to the lambda
        }
    }

    private class DiffCallback : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }
    }
}
