package com.example.videobubble

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.ui.PlayerView

class BubbleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val mediaSourceFactory = MediaSourceFactory(itemView.context)
    private val playerView = itemView.findViewById<PlayerView>(R.id.li_bubble_player_view)
    // плеер, отвечающий за взаимодействие с видео
    private val player = ExoPlayer.Builder(itemView.context).build().apply {
        //настройка повтора видео. В нашем случае воспроизводим одно видео по кругу
        repeatMode = ExoPlayer.REPEAT_MODE_ONE
    }

    init {
        playerView.player = player
    }

    fun bind(model: BubbleModel) {
        val mediaSource = mediaSourceFactory.createMediaSource(model.videoUrl)
        player.setMediaSource(mediaSource)
        //начинает загрузку видео
        player.prepare()
        //начинаем воспроизведение как только видео загрузится
        player.play()
    }
}