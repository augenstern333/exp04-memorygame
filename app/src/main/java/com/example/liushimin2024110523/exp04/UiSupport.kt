package com.example.liushimin2024110523.exp04

import android.view.MotionEvent
import android.view.View
import android.view.animation.DecelerateInterpolator
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

/** Apply the same safe content area to all three screens, including the keyboard. */
@Suppress("DEPRECATION")
fun AppCompatActivity.configureSystemBars(root: View) {
    WindowCompat.setDecorFitsSystemWindows(window, false)
    val background = getColor(R.color.app_background)
    window.decorView.setBackgroundColor(background)
    window.statusBarColor = background
    window.navigationBarColor = background
    WindowInsetsControllerCompat(window, root).apply {
        isAppearanceLightStatusBars = true
        isAppearanceLightNavigationBars = true
    }
    val initialLeft = root.paddingLeft
    val initialTop = root.paddingTop
    val initialRight = root.paddingRight
    val initialBottom = root.paddingBottom
    ViewCompat.setOnApplyWindowInsetsListener(root) { view, insets ->
        val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout())
        val keyboard = insets.getInsets(WindowInsetsCompat.Type.ime())
        view.setPadding(
            initialLeft + bars.left,
            initialTop + bars.top,
            initialRight + bars.right,
            initialBottom + maxOf(bars.bottom, keyboard.bottom)
        )
        WindowInsetsCompat.CONSUMED
    }
    ViewCompat.requestApplyInsets(root)
}

/** Keep normal click, keyboard, and accessibility handling while adding touch feedback. */
fun View.pressFeedback() {
    setOnTouchListener { view, event ->
        val scale = when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> 0.98f
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> 1f
            else -> return@setOnTouchListener false
        }
        view.animate().cancel()
        view.animate().scaleX(scale).scaleY(scale)
            .setDuration(if (scale < 1f) 90L else 180L)
            .setInterpolator(DecelerateInterpolator())
            .start()
        false
    }
}
