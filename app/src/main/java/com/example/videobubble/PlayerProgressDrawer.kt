package com.example.videobubble

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PathMeasure
import androidx.core.content.ContextCompat

class PlayerProgressDrawer(
    private val bufferPathStroke: Float,
    context: Context
) {

    // Path для отрисовки линии прогресса
    private val playProgressPath = Path()
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = ContextCompat.getColor(context, R.color.progress_color)
        strokeWidth = bufferPathStroke
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
    }

    fun drawProgress(canvas: Canvas, progress: Float, bufferPathMeasure: PathMeasure) {
        playProgressPath.reset()
        // получаем длину линии относительно прогресса просмотра
        val segmentLength = bufferPathMeasure.length * progress
        // Получаем часть круга буффера, равную длине линии прогресса
        bufferPathMeasure.getSegment(0F, segmentLength, playProgressPath, true)
        // Отрисовываем линию прогресса
        canvas.drawPath(playProgressPath, paint)
    }
}