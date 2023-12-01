package com.example.videobubble

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PathMeasure

class TimeBarBadgeDrawer(context: Context) {

    private val badgeCoordinates = floatArrayOf(0f, 0f)
    private val badgeRadius = context.resources.getDimensionPixelSize(R.dimen.bubble_timebar_badge_radius)
    private val paint = Paint().apply { color = Color.WHITE }

    fun drawBadge(canvas: Canvas, progress: Float, bufferPathMeasure: PathMeasure) {
        // Записываем координаты края линии прогресса в badgeCoordinates
        bufferPathMeasure.getPosTan(bufferPathMeasure.length * progress, badgeCoordinates, null)
        val circleX = badgeCoordinates[0]
        val circleY = badgeCoordinates[1]
        // Отрисовываем badge на конце линии прогресса
        canvas.drawCircle(circleX, circleY, badgeRadius.toFloat(), paint)
    }
}