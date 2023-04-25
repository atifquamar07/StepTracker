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
    private var isSensorsActive = false
    private var isButtonClicked = false


    private lateinit var tvStepCount: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_count_steps)

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        tvStepCount = findViewById(R.id.tv_step_count)
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        Log.i("sampling rate", "$maxDelay")
        onPause()
        btnStopSensor = findViewById(R.id.btn_stop_sensor)
        btnStopSensor.setOnClickListener {
            toggleSensorListener()
            isButtonClicked = true
        }

    }

    private fun toggleSensorListener() {
        if (isSensorsActive) {
            stepCount/=10
            tvStepCount.text = "Steps Count: $stepCount"
            btnStopSensor.text = "Start Walking"
            btnStopSensor.setBackgroundColor(ContextCompat.getColor(this, R.color.green))
            sensorManager.unregisterListener(this)
        } else {
            btnStopSensor.text = "Stop Walking"
            stepCount = 0
            tvStepCount.text = "Steps Count: $stepCount"
            btnStopSensor.setBackgroundColor(ContextCompat.getColor(this, R.color.red))
            sensorManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_GAME)
        }
        isSensorsActive = !isSensorsActive
    }


    override fun onResume() {
        super.onResume()
        if (isButtonClicked && isSensorsActive) {
            sensorManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_GAME)
        }
    }

    override fun onPause() {
        super.onPause()
        if (isButtonClicked) {
            sensorManager.unregisterListener(this)
        }
    }


    override fun onSensorChanged(event: SensorEvent) {

        val xAcceleration = event.values[0]
        val yAcceleration = event.values[1]
        var zAcceleration = event.values[2]
        zAcceleration -= 9.81f
//        Log.i("Acc", "x: $xAcceleration, y: $yAcceleration, z: $zAcceleration")
        val magnitudeAcceleration = sqrt(xAcceleration*xAcceleration + yAcceleration*yAcceleration + zAcceleration*zAcceleration)
        currEWMA = calculateEWMA(magnitudeAcceleration, currEWMA, 0.1f)
        Log.i("Values", "magnitudeAcceleration = $magnitudeAcceleration, currEWMA = $currEWMA")
        if(magnitudeAcceleration > currEWMA+1){
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