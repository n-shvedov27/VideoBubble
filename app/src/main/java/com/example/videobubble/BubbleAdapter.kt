package com.example.videobubble

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class BubbleAdapter(
    private val items: List<BubbleModel>
) : RecyclerView.Adapter<BubbleViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BubbleViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.li_bubble, parent, false)
        return BubbleViewHolder(view)
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: BubbleViewHolder, position: Int) {
        holder.bind(items[position])
    }
}