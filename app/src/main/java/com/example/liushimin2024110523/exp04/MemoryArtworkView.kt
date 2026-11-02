package com.example.liushimin2024110523.exp04

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat

/** Small vector illustration, kept local so the home screen never needs image loading. */
class MemoryArtworkView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) : View(context, attrs) {
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val blue = ContextCompat.getColor(context, R.color.accent)
    private val red = ContextCompat.getColor(context, R.color.card_red)

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.save()
        canvas.scale(width / 112f, height / 150f)
        paint.color = 0xFFE8EFFD.toInt()
        canvas.drawCircle(60f, 79f, 48f, paint)
        canvas.save()
        canvas.rotate(-17f, 45f, 77f)
        paint.color = 0xFFBDD0F5.toInt()
        canvas.drawRoundRect(15f, 28f, 78f, 126f, 11f, 11f, paint)
        paint.color = blue
        canvas.drawRoundRect(19f, 32f, 74f, 122f, 8f, 8f, paint)
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 1f
        paint.color = 0x77FFFFFF
        canvas.drawRoundRect(24f, 37f, 69f, 117f, 6f, 6f, paint)
        paint.style = Paint.Style.FILL
        canvas.restore()
        canvas.save()
        canvas.rotate(14f, 66f, 82f)
        paint.color = 0x183868CE
        canvas.drawRoundRect(37f, 39f, 103f, 138f, 11f, 11f, paint)
        paint.color = 0xFFFFFFFF.toInt()
        canvas.drawRoundRect(33f, 32f, 99f, 131f, 11f, 11f, paint)
        SuitArtwork.draw(canvas, 66f, 81f, 37f, 0, red, paint)
        SuitArtwork.draw(canvas, 44f, 46f, 9f, 0, red, paint)
        SuitArtwork.draw(canvas, 88f, 117f, 9f, 0, red, paint)
        canvas.restore()
        paint.color = blue
        canvas.drawCircle(96f, 22f, 3f, paint)
        paint.strokeWidth = 1.6f
        paint.strokeCap = Paint.Cap.ROUND
        canvas.drawLine(9f, 112f, 9f, 120f, paint)
        canvas.drawLine(5f, 116f, 13f, 116f, paint)
        canvas.restore()
    }
}
