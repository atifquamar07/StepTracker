package com.example.steptracker

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View

class CanvasView(context: Context, attrs: AttributeSet?) : View(context, attrs) {

    private var paint: Paint = Paint()
    private var xPos: Double = 0.0
    private var yPos: Double = 0.0
    private var lastXPos: Double = xPos
    private var lastYPos: Double = yPos
    private var scaleFactor = 1.0f
    private var centered: Boolean = false
    private val mLines = mutableListOf<Pair<Pair<Double, Double>, Pair<Double, Double>>>()

    init {
        paint.color = Color.BLACK
        paint.strokeWidth = 5f
        paint.style = Paint.Style.STROKE
    }


    fun updateData(dx: Double, dy: Double, reset: Boolean) {
        if(reset){
            xPos = 0.0
            yPos = 0.0
            lastXPos = xPos
            lastYPos = yPos
            centered = false
            mLines.clear()
        }
        else {
            xPos += dx
            yPos += dy
        }
        val p1 = Pair(lastXPos, lastYPos)
        val p2 = Pair(xPos, yPos)
        mLines.add(Pair(p1, p2))
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.save()
        canvas.scale(scaleFactor, scaleFactor)
        if(!centered){
            mLines.clear()
            xPos += width/2
            yPos += height/2
            lastXPos += width/2
            lastYPos += height/2
            val p1 = Pair(lastXPos, lastYPos)
            val p2 = Pair(xPos, yPos)
            mLines.add(Pair(p1, p2))
            centered = true
        }
        Log.i("Canvas values", "xPos: $xPos, yPos: $yPos, lastXPos: $lastXPos, lastYPos: $lastYPos")
        for (line in mLines) {
            canvas.drawCircle(line.second.first.toFloat(), line.second.second.toFloat(), 7f, paint)
            canvas.drawLine(line.first.first.toFloat(), line.first.second.toFloat(), line.second.first.toFloat(), line.second.second.toFloat(), paint)
        }
        canvas.save()
        lastXPos = xPos
        lastYPos = yPos
    }


}
