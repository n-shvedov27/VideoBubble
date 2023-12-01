package com.example.videobubble

import android.content.Context
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PathMeasure
import androidx.core.content.ContextCompat

class PlayerBufferDrawer(
    private val padding: Float,
    private val bufferPathStroke: Float,
    context: Context
) {

    // path по которому будет отрисовываться круг таймлайна
    private val bufferPath = Path()
    private val paint = Paint().apply {
        // цвет круга
        color = ContextCompat.getColor(context, R.color.buffer_color)
        // отрисовываем только контур от bufferPath (иначе отрисовывается закрашенный круг)
        style = Paint.Style.STROKE
        // ширина круга
        strokeWidth = bufferPathStroke
    }
    private var pathMeasure = PathMeasure(bufferPath, false)

    fun getBufferPathMeasure(): PathMeasure {
        return pathMeasure
    }

    // обновляем Path при изменении размеров view
    fun onViewSizeChanged(width: Int, height: Int) {
        bufferPath.reset()
        val centerX = width.toFloat() / 2
        val centerY = height.toFloat() / 2
        val radius = centerX - padding
        bufferPath.addCircle(centerX, centerY, radius, Path.Direction.CW)
        val matrix = Matrix()
        matrix.postRotate(-90F, centerY, centerY)
        bufferPath.transform(matrix)
        pathMeasure = PathMeasure(bufferPath, false)
    }

    // отрисовываем Path
    fun drawBuffer(canvas: Canvas) {
        canvas.drawPath(bufferPath, paint)
    }
}