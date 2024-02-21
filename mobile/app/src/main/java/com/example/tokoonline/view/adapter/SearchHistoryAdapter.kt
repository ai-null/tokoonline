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
import com.example.tokoonline.databinding.ItemSearchHistoryBinding
import timber.log.Timber

// Create a new Kotlin file, e.g., SearchHistoryAdapter.kt

class SearchHistoryAdapter(private val onItemClick: OnItemClick) :
    ListAdapter<String, SearchHistoryAdapter.ViewHolder>(DiffCallback()) {

    class ViewHolder(val binding: ItemSearchHistoryBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(ItemSearchHistoryBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val searchHistoryItem = getItem(position)
        holder.binding.historyItemText.text = searchHistoryItem
        holder.binding.rootSearch.setOnClickListener {
            Timber.tag("SearchHistoryAdapter").d("Item clicked: %s", searchHistoryItem)
            onItemClick.onClick(
                searchHistoryItem,
                position
            )  // Pass the selectedSearch to the lambda
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
