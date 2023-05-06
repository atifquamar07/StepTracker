package com.example.steptracker

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.view.View
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class CanvasView(context: Context, attrs: AttributeSet?) : View(context, attrs) {

    private var paint: Paint = Paint()
    private var xPos: Double = (width/2).toDouble()
    private var yPos: Double = (height/2).toDouble()
    private var lastXPos: Double = xPos
    private var lastYPos: Double = yPos

    init {
        paint.color = Color.BLACK
        paint.strokeWidth = 5f
        paint.style = Paint.Style.STROKE
    }

    fun updateData(dx: Double, dy: Double) {
        xPos += dx
        yPos += dy
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        Log.i("Canvas values", "xPos: $xPos, yPos: $yPos, lastXPos: $lastXPos, lastYPos: $lastYPos")
        canvas.drawCircle(xPos.toFloat(), yPos.toFloat(), 5f, paint)
        canvas.drawLine(lastXPos.toFloat(), lastYPos.toFloat(), xPos.toFloat(), yPos.toFloat(), paint)
        lastXPos = xPos
        lastYPos = yPos
    }

}
