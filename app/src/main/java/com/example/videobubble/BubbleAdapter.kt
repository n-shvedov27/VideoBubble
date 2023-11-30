package com.example.videobubble

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class BubbleAdapter(
    private val items: List<BubbleModel>
) : RecyclerView.Adapter<BubbleViewHolder>() {

    private var viewHolderPool: ViewHolderPool? = null

    // при присоединении adapter'а к recycler'у инициализируем ViewHolderPool
    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        viewHolderPool = viewHolderPool ?: ViewHolderPool(recyclerView)
    }

    // при отсоединении adapter'а от recycler'у зануляем ViewHolderPool
    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        viewHolderPool = null
    }

    // при создании viewHolder'а либо используем либо созданную асинхронно view,
    // либо создаем новую в методе inflateNewView()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BubbleViewHolder {
        val cachedView = viewHolderPool?.getOrNull()
        val inflatedView = cachedView ?: inflateNewView(parent)
        return BubbleViewHolder(inflatedView)
    }

    private fun inflateNewView(parentView: ViewGroup): View {
        val inflater = LayoutInflater.from(parentView.context)
        return inflater.inflate(R.layout.li_bubble, parentView, false)
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: BubbleViewHolder, position: Int) {
        holder.bind(items[position])
    }
}