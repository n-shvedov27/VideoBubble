package com.example.videobubble

import android.animation.ValueAnimator
import android.graphics.Outline
import android.view.View
import android.view.ViewOutlineProvider
import android.widget.ImageView
import androidx.core.animation.doOnStart
import androidx.core.view.animation.PathInterpolatorCompat
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
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
import com.google.android.exoplayer2.ui.R as exoPlayerR

private const val LOAD_VIDEO_DEBOUNCE_MS = 700L

class BubbleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), CoroutineScope {

    private val playerViewTimeBar = itemView.findViewById<BubbleTimeBar>(exoPlayerR.id.exo_progress)
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
        addListener(playerViewTimeBar)
    }
    private var changeSizeAnimator: ValueAnimator? = null
    var isActive = false

    init {
        playerView.player = player
        playerViewTimeBar.addListener(TimeBarScrubListener(playerView))
        itemView.outlineProvider = object : ViewOutlineProvider() {
            override fun getOutline(view: View, outline: Outline) {
                outline.setOval(0, 0, view.width, view.height)
            }
        }
        itemView.clipToOutline = true
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

    // сжимает видео когда оно пропадает с экрана
    fun makeInactiveImmediately() {
        isActive = false
        val initialSize = itemView.context.resources.getDimensionPixelSize(R.dimen.bubble_initial_size)
        playerView.updateLayoutParams {
            height = initialSize
            width = initialSize
        }
        changeControllerVisibility(false)
    }

    // АНИМИРОВАННО сжимает видео при раскрытии другого видео
    fun makeInactiveAnimated() {
        changeControllerVisibility(false)
        isActive = false
        val initialSize = itemView.context.resources.getDimensionPixelSize(R.dimen.bubble_initial_size)
        // отменяем предыдущую анимацию если она еще не закончилась
        changeSizeAnimator?.cancel()
        changeSizeAnimator = ValueAnimator.ofInt(playerView.height, initialSize).apply {
            duration = 500L
            interpolator = PathInterpolatorCompat.create(0.33F, 0F, 0F, 1F)
            addUpdateListener {
                playerView.updateLayoutParams {
                    height = it.animatedValue as Int
                    width = it.animatedValue as Int
                }
            }
        }
        changeSizeAnimator?.start()
        playerViewTimeBar.fadeOut().start()
    }

    // АНИМИРОВАННО раскрывает видео при тапе по нему
    fun makeActive() {
        isActive = true
        val expandedSize = itemView.context.resources.getDimensionPixelSize(R.dimen.bubble_expanded_size)
        // отменяем предыдущую анимацию если она еще не закончилась
        changeSizeAnimator?.cancel()
        changeSizeAnimator = ValueAnimator.ofInt(playerView.height, expandedSize).apply {
            duration = 500L
            interpolator = PathInterpolatorCompat.create(0.33F, 0F, 0F, 1F)
            addUpdateListener {
                playerView.updateLayoutParams {
                    height = it.animatedValue as Int
                    width = it.animatedValue as Int
                }
            }
        }
        changeSizeAnimator?.start()
        playerViewTimeBar.fadeIn().apply {
            doOnStart { changeControllerVisibility(true) }
        }.start()
    }

    private fun onStartLoadVideo(videoUrl: String) {
        thumbnail.isVisible = true
        Glide.with(itemView.context).asBitmap()
            .load(videoUrl)
            .apply(RequestOptions().frame(0))
            .into(thumbnail)
    }

    private fun changeControllerVisibility(isVisible: Boolean) {
        when (isVisible) {
            true -> {
                playerView.useController = true
                playerView.showController()
            }
            false -> {
                playerView.useController = false
                playerView.hideController()
            }
        }
    }
}