package com.example.pagingwiki.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.pagingwiki.R
import com.example.pagingwiki.data.NetworkState
import com.example.pagingwiki.data.Status
import kotlinx.android.synthetic.main.network_state_item.view.*

/**
 * Created by Ahmed Abd-Elmeged on 2/20/2018.
 */
class NetworkStateViewHolder(val view: View, private val retryCallback: () -> Unit) : RecyclerView.ViewHolder(view) {

    init {
        itemView.retry_button.setOnClickListener { retryCallback() }
    }

    fun bindTo(networkState: NetworkState?) {
        //error message
        itemView.error_msg.visibility = if (networkState?.message != null) View.VISIBLE else View.GONE
        if (networkState?.message != null) {
            itemView.error_msg.text = networkState.message
        }

        //loading and retry
        itemView.retry_button.visibility = if (networkState?.status == Status.FAILED) View.VISIBLE else View.GONE
        itemView.progress_bar.visibility = if (networkState?.status == Status.RUNNING) View.VISIBLE else View.GONE
    }

    companion object {
        fun create(parent: ViewGroup, retryCallback: () -> Unit): NetworkStateViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val view = layoutInflater.inflate(R.layout.network_state_item, parent, false)
            return NetworkStateViewHolder(view, retryCallback)
        }
    }

}