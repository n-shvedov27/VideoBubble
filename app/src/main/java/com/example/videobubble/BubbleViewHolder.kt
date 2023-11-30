package com.example.videobubble

import android.view.View
import android.widget.ImageView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.ui.PlayerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

private const val LOAD_VIDEO_DEBOUNCE_MS = 700L

class BubbleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), CoroutineScope {

    private var loadVideoJob: Job? = null
    private val mediaSourceFactory = MediaSourceFactory(itemView.context)
    private val playerView = itemView.findViewById<PlayerView>(R.id.li_bubble_player_view)
    private val listener = BubblePlayerListener(this)
    private val thumbnail = itemView.findViewById<ImageView>(R.id.li_bubble_thumbnail)
    // плеер, отвечающий за взаимодействие с видео
    private val player = ExoPlayer.Builder(itemView.context).build().apply {
        //настройка повтора видео. В нашем случае воспроизводим одно видео по кругу
        repeatMode = ExoPlayer.REPEAT_MODE_ONE
        addListener(listener)
    }

    init {
        playerView.player = player
    }

    override val coroutineContext: CoroutineContext
        get() = Job() + Dispatchers.Main

    fun bind(model: BubbleModel) {
        onStartLoadVideo(model.videoUrl)
        // отменяем предыдущую загрузку предыдущего видео
        loadVideoJob?.cancel()
        player.clearMediaItems()
        loadVideoJob = launch {
            delay(LOAD_VIDEO_DEBOUNCE_MS)
            val mediaSource = mediaSourceFactory.createMediaSource(model.videoUrl)
            // начинаем загрузку нового видео после 700мс задержки
            player.setMediaSource(mediaSource)
            player.prepare()
            player.play()
        }
    }

    fun onFinishLoadVideo() {
        thumbnail.isVisible = false
    }

    private fun onStartLoadVideo(videoUrl: String) {
        thumbnail.isVisible = true
        Glide.with(itemView.context).asBitmap()
            .load(videoUrl)
            .apply(RequestOptions().frame(0))
            .into(thumbnail)
    }
}