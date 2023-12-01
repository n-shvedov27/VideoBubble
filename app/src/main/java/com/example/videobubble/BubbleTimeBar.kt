package com.example.videobubble

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.widget.FrameLayout
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.TimeBar

class BubbleTimeBar
@JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr), TimeBar, Player.Listener {

    private var currentPlayPosition: Float = 0F
    // отступ линии буффера от края view
    private val padding = resources.getDimensionPixelSize(R.dimen.bubble_timebar_buffer_path_padding).toFloat()
    // ширина линии буффера
    private val bufferPathStroke = resources.getDimensionPixelSize(R.dimen.bubble_timebar_buffer_path_stroke).toFloat()
    // сущность для отрисовки линии буффера
    private val playerBufferDrawer = PlayerBufferDrawer(
        padding = padding,
        bufferPathStroke = bufferPathStroke,
        context = context
    )
    private val playerProgressDrawer = PlayerProgressDrawer(
        bufferPathStroke = bufferPathStroke,
        context = context
    )
    private val badgeDrawer = TimeBarBadgeDrawer(context)
    var isVideoPlaying: Boolean = false
    var listeners = mutableListOf<TimeBar.OnScrubListener>()
    var videoDuration: Long = 0

    init {
        // тк BubbleTimeBar-это viewGroup, то для вызова onDraw необходимо прописать это
        setWillNotDraw(false)
        setOnTouchListener(TimeBarTouchListener(padding, bufferPathStroke))
    }

    // уведомляем PlayerBufferDrawer об изменениях размера view
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        playerBufferDrawer.onViewSizeChanged(w, h)
    }

    // просим PlayerBufferDrawer отрисовать линию буффера
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        playerBufferDrawer.drawBuffer(canvas)
        val bufferPathMeasure = playerBufferDrawer.getBufferPathMeasure()
        val playProgress = runCatching { currentPlayPosition / videoDuration }.getOrDefault(0F)
        playerProgressDrawer.drawProgress(
            canvas = canvas,
            progress = playProgress,
            bufferPathMeasure = bufferPathMeasure
        )
        if (!isVideoPlaying) {
            badgeDrawer.drawBadge(
                canvas = canvas,
                progress = playProgress,
                bufferPathMeasure = bufferPathMeasure
            )
        }
    }

    override fun setPosition(position: Long) {
        currentPlayPosition = position.toFloat()
        invalidate()
    }

    override fun setDuration(duration: Long) {
        videoDuration = duration
    }

    override fun onIsPlayingChanged(isPlaying: Boolean) {
        super.onIsPlayingChanged(isPlaying)
        isVideoPlaying = isPlaying
    }

    override fun addListener(listener: TimeBar.OnScrubListener) {
        listeners.add(listener)
    }

    override fun removeListener(listener: TimeBar.OnScrubListener) {
        listeners.remove(listener)
    }
    
    override fun setKeyTimeIncrement(time: Long) = Unit
    override fun setKeyCountIncrement(count: Int) = Unit
    override fun setAdGroupTimesMs(adGroupTimesMs: LongArray?, playedAdGroups: BooleanArray?, adGroupCount: Int) = Unit
    override fun setBufferedPosition(bufferedPosition: Long) = Unit
    override fun getPreferredUpdateDelay() = 0L
}