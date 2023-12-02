package com.example.videobubble

import android.view.MotionEvent
import android.view.View
import kotlin.math.acos
import kotlin.math.pow
import kotlin.math.sqrt

class TimeBarTouchListener(
    padding: Float,
    bufferPathStroke: Float
): View.OnTouchListener {

    private val calculator = TimeBarTouchCalculator(padding * 2 + bufferPathStroke)

    override fun onTouch(v: View, e: MotionEvent): Boolean {
        if (v !is BubbleTimeBar) return false
        when (e.action) {
            // начало touch-события
            MotionEvent.ACTION_DOWN -> {
                if (!calculator.isEventInRewindArea(v, e.x, e.y) || v.isVideoPlaying) return false
                v.parent.requestDisallowInterceptTouchEvent(true)
                v.listeners.forEach { it.onScrubStart(v, calculator.getNewScrubPosition(v, e.x, e.y)) }
            }
            // touch-событие движения пальца по экрану
            MotionEvent.ACTION_MOVE -> v.listeners.forEach {
                it.onScrubMove(v, calculator.getNewScrubPosition(v, e.x, e.y))
            }
            // конец touch-событие по системным причинам
            MotionEvent.ACTION_CANCEL -> v.listeners.forEach {
                it.onScrubStop(v, calculator.getNewScrubPosition(v, e.x, e.y), true)
            }
            // конец touch-события
            MotionEvent.ACTION_UP -> v.listeners.forEach {
                it.onScrubStop(v, calculator.getNewScrubPosition(v, e.x, e.y), false)
            }
        }
        return true
    }

    internal class TimeBarTouchCalculator(
        private val rewindAreaSize: Float
    ) {

        /**
         * Вычисляем позицию перемотки
         * Шаги вычисления:
         * 1. Вычслили угол между векторами A и B, где
         *      A - вектор из центра вью ровно вверх (0,-1)
         *      B - вектор из центра вью до точки [MotionEvent]
         * 2. По формуле пропорции полученного угла к 360 градусам получаем новую позицию
         */
        fun getNewScrubPosition(view: BubbleTimeBar, touchX: Float, touchY: Float) : Long {
            val x1 = 0F
            val y1 = -1F

            val x2 = touchX - view.width.toFloat() / 2
            val y2 = touchY - view.height.toFloat() / 2

            val cosAngle = (x1 * x2 + y1 * y2) / (sqrt(x1 * x1 + y1 * y1) * sqrt(x2 * x2 + y2 * y2))
            val angle = when {
                x2 < 0 -> 360F - Math.toDegrees(acos(cosAngle).toDouble())
                else -> Math.toDegrees(acos(cosAngle).toDouble())
            }
            return (view.videoDuration * angle / 360F).toLong()
        }

        /**
         * Проверяем находится ли [MotionEvent] в зоне перемотки
         * Шаги проверки:
         * 1. Вычисляем расстоляние от центра вью до точки касания
         * 2. Сравниваем полученное расстояние с радиусом вью за вычетом зоны перемотки
         */
        fun isEventInRewindArea(view: BubbleTimeBar, touchX: Float, touchY: Float): Boolean {
            val centerX = view.width / 2F
            val centerY = view.height / 2F
            val distance = sqrt((centerX - touchX).pow(2) + (centerY - touchY).pow(2))
            return distance >= centerY - rewindAreaSize
        }
    }
}