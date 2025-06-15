package dev.aaa1115910.bv.tv.util

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import dev.aaa1115910.bv.util.Prefs

/**
 * TV动画优化工具类
 * 提供针对TV设备优化的动画配置
 */
object TvAnimationUtils {
    
    /**
     * 获取优化后的动画持续时间
     */
    fun getOptimizedDuration(baseDuration: Int): Int {
        return if (Prefs.enableTvAnimations) {
            (baseDuration * Prefs.tvAnimationDurationScale).toInt()
        } else {
            0 // 禁用动画时直接跳过
        }
    }
    
    /**
     * 获取优化后的动画规格
     */
    fun <T> getOptimizedAnimationSpec(baseDuration: Int = 300): AnimationSpec<T> {
        val duration = getOptimizedDuration(baseDuration)
        return if (duration > 0) {
            tween(duration, easing = FastOutSlowInEasing)
        } else {
            tween(0) // 立即完成
        }
    }
    
    /**
     * 获取优化后的弹簧动画规格
     */
    fun <T> getOptimizedSpringSpec(): AnimationSpec<T> {
        return if (Prefs.enableTvAnimations) {
            spring(dampingRatio = 0.8f, stiffness = 400f)
        } else {
            tween(0)
        }
    }
    
    /**
     * 优化后的淡入淡出动画
     */
    fun optimizedFadeTransition(duration: Int = 200): ContentTransform {
        val optimizedDuration = getOptimizedDuration(duration)
        return if (optimizedDuration > 0) {
            fadeIn(tween(optimizedDuration)) togetherWith fadeOut(tween(optimizedDuration))
        } else {
            fadeIn(tween(0)) togetherWith fadeOut(tween(0))
        }
    }
    
    /**
     * 优化后的水平滑动动画
     */
    fun optimizedHorizontalSlideTransition(
        coefficient: Int = 10,
        duration: Int = 300
    ): AnimatedContentTransitionScope<*>.() -> ContentTransform = {
        val optimizedDuration = getOptimizedDuration(duration)
        if (optimizedDuration > 0) {
            if (targetState.hashCode() < initialState.hashCode()) {
                fadeIn(tween(optimizedDuration)) + slideInHorizontally(tween(optimizedDuration)) { -it / coefficient } togetherWith
                        fadeOut(tween(optimizedDuration)) + slideOutHorizontally(tween(optimizedDuration)) { it / coefficient }
            } else {
                fadeIn(tween(optimizedDuration)) + slideInHorizontally(tween(optimizedDuration)) { it / coefficient } togetherWith
                        fadeOut(tween(optimizedDuration)) + slideOutHorizontally(tween(optimizedDuration)) { -it / coefficient }
            }
        } else {
            fadeIn(tween(0)) + slideInHorizontally(tween(0)) { 0 } togetherWith
                    fadeOut(tween(0)) + slideOutHorizontally(tween(0)) { 0 }
        }
    }
    
    /**
     * 优化后的垂直滑动动画
     */
    fun optimizedVerticalSlideTransition(
        coefficient: Int = 20,
        duration: Int = 300
    ): AnimatedContentTransitionScope<*>.() -> ContentTransform = {
        val optimizedDuration = getOptimizedDuration(duration)
        if (optimizedDuration > 0) {
            if (targetState.hashCode() < initialState.hashCode()) {
                fadeIn(tween(optimizedDuration)) + slideInVertically(tween(optimizedDuration)) { -it / coefficient } togetherWith
                        fadeOut(tween(optimizedDuration)) + slideOutVertically(tween(optimizedDuration)) { it / coefficient }
            } else {
                fadeIn(tween(optimizedDuration)) + slideInVertically(tween(optimizedDuration)) { it / coefficient } togetherWith
                        fadeOut(tween(optimizedDuration)) + slideOutVertically(tween(optimizedDuration)) { -it / coefficient }
            }
        } else {
            fadeIn(tween(0)) + slideInVertically(tween(0)) { 0 } togetherWith
                    fadeOut(tween(0)) + slideOutVertically(tween(0)) { 0 }
        }
    }
    
    /**
     * 简化的进入动画（仅淡入）
     */
    fun simplifiedEnterTransition(duration: Int = 200): EnterTransition {
        val optimizedDuration = getOptimizedDuration(duration)
        return fadeIn(tween(optimizedDuration))
    }
    
    /**
     * 简化的退出动画（仅淡出）
     */
    fun simplifiedExitTransition(duration: Int = 200): ExitTransition {
        val optimizedDuration = getOptimizedDuration(duration)
        return fadeOut(tween(optimizedDuration))
    }
}

/**
 * Composable扩展函数，用于记住优化后的动画规格
 */
@Composable
fun <T> rememberOptimizedAnimationSpec(baseDuration: Int = 300): AnimationSpec<T> {
    return remember(Prefs.enableTvAnimations, Prefs.tvAnimationDurationScale) {
        TvAnimationUtils.getOptimizedAnimationSpec<T>(baseDuration)
    }
}

/**
 * Composable扩展函数，用于记住优化后的弹簧动画规格
 */
@Composable
fun <T> rememberOptimizedSpringSpec(): AnimationSpec<T> {
    return remember(Prefs.enableTvAnimations) {
        TvAnimationUtils.getOptimizedSpringSpec<T>()
    }
}
