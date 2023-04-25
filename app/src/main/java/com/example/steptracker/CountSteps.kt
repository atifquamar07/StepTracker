package com.example.steptracker

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import kotlin.math.sqrt

class CountSteps : AppCompatActivity(), SensorEventListener {
    private lateinit var sensorManager: SensorManager
    private lateinit var accelerometerSensor: Sensor
    private lateinit var btnStopSensor: Button
    private var maxDelay: Float = 20000.0f
    private var stepCount = 0
    private var currEWMA = 0.0

    private lateinit var tvStepCount: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_count_steps)

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        tvStepCount = findViewById(R.id.tv_step_count)
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        Log.i("sampling rate", "$maxDelay")

        btnStopSensor = findViewById(R.id.btn_stop_sensor)
        onPause()
        var isStarted = false
        btnStopSensor.setOnClickListener {
            if(!isStarted){
                isStarted = true
                btnStopSensor.text = "Stop Walking"
                stepCount = 0
                tvStepCount.text = "Steps Count: $stepCount"
                btnStopSensor.setBackgroundColor(ContextCompat.getColor(this, R.color.red))
                onResume()
            }
            else {
                isStarted = false
                stepCount/=10
                tvStepCount.text = "Steps Count: $stepCount"
                btnStopSensor.text = "Start Walking"
                btnStopSensor.setBackgroundColor(ContextCompat.getColor(this, R.color.green))
                onPause()
            }
        }

    }

    override fun onResume() {
        super.onResume()
        sensorManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_GAME)
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent) {
        val xAcceleration = event.values[0]
        val yAcceleration = event.values[1]
        val zAcceleration = event.values[2]

        val magnitudeAcceleration = sqrt(xAcceleration*xAcceleration + yAcceleration*yAcceleration + zAcceleration*zAcceleration)
        currEWMA = calculateEWMA(magnitudeAcceleration, currEWMA, 0.1f)
        Log.i("Values", "magnitudeAcceleration = $magnitudeAcceleration, currEWMA = $currEWMA")
        if(magnitudeAcceleration > currEWMA){
            stepCount+=1
        }
    }

    private fun calculateEWMA(currentValue: Float, previousEWMA: Double, alpha: Float): Double {
        return alpha * currentValue + (1 - alpha) * previousEWMA
    }



    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Do nothing
    }
}