package com.example.mygymapp.ui.motion

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween

object MotionSpec {
    const val VeryFast = 120
    const val Fast = 180
    const val Medium = 240
    const val Slow = 320

    val easeOut: Easing = LinearOutSlowInEasing
    val fastOutSlowIn: Easing = FastOutSlowInEasing
    val bookEase: Easing = CubicBezierEasing(0.2f, 0.0f, 0.0f, 1.0f)

    fun <T> tweenVeryFast(easing: Easing = easeOut): TweenSpec<T> =
        tween(durationMillis = VeryFast, easing = easing)

    fun <T> tweenFast(easing: Easing = easeOut): TweenSpec<T> =
        tween(durationMillis = Fast, easing = easing)

    fun <T> tweenMedium(easing: Easing = easeOut): TweenSpec<T> =
        tween(durationMillis = Medium, easing = easing)

    fun <T> tweenSlow(easing: Easing = easeOut): TweenSpec<T> =
        tween(durationMillis = Slow, easing = easing)

    fun <T> springSoft() =
        spring<T>(dampingRatio = 0.75f, stiffness = Spring.StiffnessMediumLow)

    fun <T> springSnappy() =
        spring<T>(dampingRatio = 0.8f, stiffness = Spring.StiffnessMedium)
}
