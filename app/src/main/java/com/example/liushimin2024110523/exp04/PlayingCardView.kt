package com.example.liushimin2024110523.exp04

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import android.view.animation.DecelerateInterpolator
import androidx.core.content.ContextCompat

/** A native card surface. All four suits are drawn as paths, with no font or emoji dependency. */
class PlayingCardView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : View(context, attrs) {
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val bounds = RectF()
    private var shownFace = false
    private var targetFace = false
    private var matched = false
    private var suit = 0
    private val density = resources.displayMetrics.density
    private val blue = ContextCompat.getColor(context, R.color.accent)
    private val white = ContextCompat.getColor(context, R.color.surface)
    private val soft = ContextCompat.getColor(context, R.color.accent_soft)
    private val ink = ContextCompat.getColor(context, R.color.ink)
    private val red = ContextCompat.getColor(context, R.color.card_red)
    private val outline = ContextCompat.getColor(context, R.color.outline)

    init {
        isClickable = true
        isFocusable = true
        importantForAccessibility = IMPORTANT_FOR_ACCESSIBILITY_YES
    }

    override fun getAccessibilityClassName(): CharSequence = android.widget.Button::class.java.name

    fun bind(card: Card, position: Int, animateChange: Boolean = true) {
        suit = Card.validSuits.indexOf(card.suit).coerceAtLeast(0)
        val show = card.isChosen || card.isMatched
        val wasMatched = matched
        matched = card.isMatched
        isEnabled = !matched
        val suitName = context.getString(
            intArrayOf(R.string.suit_hearts, R.string.suit_diamonds, R.string.suit_spades, R.string.suit_clubs)[suit]
        )
        contentDescription = when {
            matched -> context.getString(R.string.card_matched_description, position + 1, suitName)
            show -> context.getString(R.string.card_open_description, position + 1, suitName)
            else -> context.getString(R.string.card_hidden_description, position + 1)
        }
        if (show != targetFace) {
            targetFace = show
            animate().cancel()
            if (animateChange && isLaidOut) {
                animate().scaleX(0f).setDuration(65).withEndAction {
                    shownFace = show
                    invalidate()
                    animate().scaleX(1f).setDuration(105)
                        .setInterpolator(DecelerateInterpolator()).withEndAction(null).start()
                }.start()
            } else {
                shownFace = show
                scaleX = 1f
                invalidate()
            }
        } else if (wasMatched != matched) {
            invalidate()
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val inset = density
        bounds.set(inset, inset, width - inset, height - inset)
        val radius = 13f * density
        paint.style = Paint.Style.FILL
        paint.color = if (!shownFace) blue else if (matched) soft else white
        canvas.drawRoundRect(bounds, radius, radius, paint)
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = density
        paint.color = if (!shownFace) 0x55FFFFFF else if (matched) 0xFFC4D5F6.toInt() else outline
        canvas.drawRoundRect(bounds, radius, radius, paint)
        paint.style = Paint.Style.FILL
        if (shownFace) {
            val color = if (suit < 2) red else ink
            SuitArtwork.draw(canvas, width / 2f, height / 2f, minOf(width * .49f, height * .37f), suit, color, paint)
            SuitArtwork.draw(canvas, 14f * density, 17f * density, 9f * density, suit, color, paint)
            canvas.save()
            canvas.rotate(180f, width / 2f, height / 2f)
            SuitArtwork.draw(canvas, 14f * density, 17f * density, 9f * density, suit, color, paint)
            canvas.restore()
            if (matched) {
                paint.color = blue
                paint.style = Paint.Style.STROKE
                paint.strokeWidth = 1.5f * density
                paint.strokeCap = Paint.Cap.ROUND
                val x = width - 17f * density
                val y = 16f * density
                canvas.drawLine(x - 4 * density, y, x - density, y + 3 * density, paint)
                canvas.drawLine(x - density, y + 3 * density, x + 4 * density, y - 3 * density, paint)
                paint.style = Paint.Style.FILL
            }
        } else {
            paint.color = 0x20FFFFFF
            val step = 13f * density
            var y = 12f * density
            var row = 0
            while (y < height - 9f * density) {
                var x = (if (row % 2 == 0) 11f else 17.5f) * density
                while (x < width - 8f * density) {
                    canvas.drawCircle(x, y, .8f * density, paint)
                    x += step
                }
                row++
                y += step
            }
            canvas.save()
            canvas.rotate(45f, width / 2f, height / 2f)
            val size = minOf(width * .24f, 18f * density)
            paint.color = 0xFFF3F7FF.toInt()
            canvas.drawRoundRect(width / 2f - size, height / 2f - size, width / 2f + size, height / 2f + size, 5f * density, 5f * density, paint)
            paint.color = blue
            canvas.drawRoundRect(width / 2f - size * .37f, height / 2f - size * .37f, width / 2f + size * .37f, height / 2f + size * .37f, 2f * density, 2f * density, paint)
            canvas.restore()
        }
        if (isFocused) {
            paint.style = Paint.Style.STROKE
            paint.strokeWidth = 3f * density
            paint.color = ink
            canvas.drawRoundRect(bounds, radius, radius, paint)
            paint.style = Paint.Style.FILL
        }
    }

    override fun onDetachedFromWindow() {
        animate().cancel()
        super.onDetachedFromWindow()
    }
}

internal object SuitArtwork {
    private val heart = Path().apply {
        moveTo(0f, .47f)
        cubicTo(-.13f, .32f, -.55f, .03f, -.48f, -.25f)
        cubicTo(-.42f, -.52f, -.12f, -.52f, 0f, -.27f)
        cubicTo(.12f, -.52f, .42f, -.52f, .48f, -.25f)
        cubicTo(.55f, .03f, .13f, .32f, 0f, .47f)
        close()
    }
    private val diamond = Path().apply {
        moveTo(0f, -.5f); lineTo(.38f, 0f); lineTo(0f, .5f); lineTo(-.38f, 0f); close()
    }
    private val stem = Path().apply {
        moveTo(-.07f, .1f); lineTo(.07f, .1f); cubicTo(.06f, .27f, .1f, .4f, .22f, .48f)
        lineTo(-.22f, .48f); cubicTo(-.1f, .4f, -.06f, .27f, -.07f, .1f); close()
    }

    fun draw(canvas: Canvas, cx: Float, cy: Float, size: Float, suit: Int, color: Int, paint: Paint) {
        canvas.save()
        canvas.translate(cx, cy)
        canvas.scale(size, size)
        paint.style = Paint.Style.FILL
        paint.color = color
        when (suit) {
            0 -> canvas.drawPath(heart, paint)
            1 -> canvas.drawPath(diamond, paint)
            2 -> {
                canvas.save(); canvas.rotate(180f); canvas.drawPath(heart, paint); canvas.restore()
                canvas.drawPath(stem, paint)
            }
            else -> {
                canvas.drawCircle(0f, -.24f, .23f, paint)
                canvas.drawCircle(-.23f, .06f, .23f, paint)
                canvas.drawCircle(.23f, .06f, .23f, paint)
                canvas.drawPath(stem, paint)
            }
        }
        canvas.restore()
    }
}
