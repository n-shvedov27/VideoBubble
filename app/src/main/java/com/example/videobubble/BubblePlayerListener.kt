package com.example.videobubble

import com.google.android.exoplayer2.Player

class BubblePlayerListener(
    private val viewHolder: BubbleViewHolder
) : Player.Listener {

    override fun onIsPlayingChanged(isPlaying: Boolean) {
        if (isPlaying) {
            viewHolder.onFinishLoadVideo()
        }
    }
}