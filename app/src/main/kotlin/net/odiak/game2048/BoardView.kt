package net.odiak.game2048

import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Point
import android.view.MotionEvent
import android.view.View
import org.jetbrains.anko.dip

class BoardView(context: Context, val game: Game) : View(context) {

    companion object {
        val COLOR_BACKGROUND = Color.parseColor("#EEEEEE")
        val COLOR_CELL = Color.parseColor("#666666")
        val COLOR_TEXT = Color.parseColor("#FFFFFF")
    }

    val nRows = game.nRows
    val nColumns = game.nColumns

    private val paint = Paint().apply {
        color = COLOR_CELL
    }

    private val textPaint = Paint().apply {
        color = COLOR_TEXT
        textSize = context.dip(30).toFloat()
        textAlign = Paint.Align.CENTER
    }

    private var startPos: Pair<Float, Float>? = null

    init {
        setWillNotDraw(false)
        game.addOnMoveListener {
            invalidate()
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(measuredWidth, measuredWidth)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas ?: return

        canvas.drawColor(COLOR_BACKGROUND)

        val cellWidth = measuredWidth / nColumns * 1f
        val cellHeight = measuredHeight / nRows * 1f

        for (i in 0..nColumns - 1) {
            for (j in 0..nRows - 1) {
                val v = game.rawBoard[i, j]
                if (v == 0) continue
                val x = cellWidth * i
                val y = cellHeight * j
                canvas.drawRect(x, y, x + cellWidth, y + cellHeight, paint)
                canvas.drawText(
                        v.toString(),
                        x + cellWidth / 2,
                        y + cellHeight / 2 + 30,
                        textPaint)
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                if (event != null) startPos = Pair(event.x, event.y)
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                startPos?.let { sp ->
                    event ?: return@let
                    val (sx, sy) = sp
                    val dx = sx - event.x
                    val dy = sy - event.y
                    when {
                        dx > 100 -> {
                            onSwipe(Game.Direction.LEFT)
                            startPos = null
                        }
                        dx < -100 -> {
                            onSwipe(Game.Direction.RIGHT)
                            startPos = null
                        }
                        dy > 100 -> {
                            onSwipe(Game.Direction.UPPER)
                            startPos = null
                        }
                        dy < -100 -> {
                            onSwipe(Game.Direction.LOWER)
                            startPos = null
                        }
                    }
                    return true
                }
            }
            MotionEvent.ACTION_UP -> {
                startPos = null
            }
        }
        return super.onTouchEvent(event)
    }

    private fun onSwipe(direction: Game.Direction) {
        game.move(direction)
    }
}