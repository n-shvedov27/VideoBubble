package com.example.videobubble

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class BubbleAdapter(
    private val items: List<BubbleModel>
) : RecyclerView.Adapter<BubbleViewHolder>() {

    private var viewHolderPool: ViewHolderPool? = null
    private var recyclerView: RecyclerView? = null

    // при присоединении adapter'а к recycler'у инициализируем ViewHolderPool
    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        viewHolderPool = viewHolderPool ?: ViewHolderPool(recyclerView)
        this.recyclerView = recyclerView
    }

    // при отсоединении adapter'а от recycler'у зануляем ViewHolderPool
    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        viewHolderPool = null
        this.recyclerView = null
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
        holder.itemView.setOnClickListener {
            // если раскрыто другое сообщение, то сжимаем его
            findActiveViewHolder()?.makeInactiveAnimated()
            holder.makeActive()
        }
    }

    // поиск уже раскрытого сообщения
    private fun findActiveViewHolder(): BubbleViewHolder? {
        recyclerView?.let { rv ->
            val layoutManager = rv.layoutManager as LinearLayoutManager
            val firstVisiblePosition = layoutManager.findFirstVisibleItemPosition()
            val lastVisiblePosition = layoutManager.findLastVisibleItemPosition()

            for (i in firstVisiblePosition ..  lastVisiblePosition) {
                val viewHolder = rv.findViewHolderForAdapterPosition(i) as? BubbleViewHolder
                if (viewHolder?.isActive == true) {
                    return viewHolder
                }
            }
        }
        return null
    }

    // сжимаем сообщение, когда оно исчезает с экрана
    override fun onViewDetachedFromWindow(holder: BubbleViewHolder) {
        super.onViewDetachedFromWindow(holder)
        holder.makeInactiveImmediately()
    }
}