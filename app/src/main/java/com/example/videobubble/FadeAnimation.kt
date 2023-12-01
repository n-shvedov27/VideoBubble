package com.example.videobubble

import android.animation.ObjectAnimator
import android.view.View

fun View.fadeIn() = createFadeAnimator(0F, 1F)

fun View.fadeOut() = createFadeAnimator(1F, 0F)

private fun View.createFadeAnimator(from: Float, to: Float): ObjectAnimator {
    return ObjectAnimator.ofFloat(this, View.ALPHA, from, to).apply {
        this.duration = 500L
    }
}