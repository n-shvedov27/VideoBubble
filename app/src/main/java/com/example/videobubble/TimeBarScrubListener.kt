package com.example.videobubble

import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.ui.TimeBar

class TimeBarScrubListener(
    private val playerView: PlayerView
) : TimeBar.OnScrubListener {

    override fun onScrubStart(timeBar: TimeBar, position: Long) {
        // Останавливаем видео вначале перемотки
        playerView.player?.pause()
        // Перемотка видео
        playerView.player?.seekTo(position)
    }

    override fun onScrubMove(timeBar: TimeBar, position: Long) {
        // Перемотка видео
        playerView.player?.seekTo(position)
    }

    override fun onScrubStop(timeBar: TimeBar, position: Long, canceled: Boolean) {
        // Воспроизводим видео после перемотки
        playerView.player?.play()
    }
}