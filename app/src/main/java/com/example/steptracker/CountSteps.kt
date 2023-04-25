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
    private lateinit var magnetometerSensor: Sensor
    private lateinit var rotationVectorSensor: Sensor
    private lateinit var btnStopSensor: Button
    private lateinit var tvDirection: TextView
    private var maxDelay: Float = 20000.0f
    private var stepCount = 0
    private var currEWMA = 0.0
    private val filterFactor = 0.9f // Filter factor
    private var isSensorsActive = false
    private var isButtonClicked = false
    private val accelerometerValues = FloatArray(3)
    private val magneticSensorValues = FloatArray(3)
    private var smoothedRotationVector = FloatArray(4)

    private lateinit var tvStepCount: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_count_steps)

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        tvStepCount = findViewById(R.id.tv_step_count)
        tvDirection = findViewById(R.id.tv_direction)
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        magnetometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
        rotationVectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
        Log.i("sampling rate", "$maxDelay")
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
            sensorManager.registerListener(this, magnetometerSensor, SensorManager.SENSOR_DELAY_GAME)
            sensorManager.registerListener(this, rotationVectorSensor, SensorManager.SENSOR_DELAY_GAME)
        }
        isSensorsActive = !isSensorsActive
    }


    override fun onResume() {
        super.onResume()
        if (isButtonClicked && isSensorsActive) {
            sensorManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_GAME)
            sensorManager.registerListener(this, magnetometerSensor, SensorManager.SENSOR_DELAY_GAME)
            sensorManager.registerListener(this, rotationVectorSensor, SensorManager.SENSOR_DELAY_GAME)
        }

    }

    override fun onPause() {
        super.onPause()
        if (isButtonClicked) {
            sensorManager.unregisterListener(this)
        }
    }

    override fun onSensorChanged(event: SensorEvent) {
        when (event.sensor?.type) {
            Sensor.TYPE_ACCELEROMETER -> {
                accelerometerValues[0] = event.values[0]
                accelerometerValues[1] = event.values[1]
                accelerometerValues[2] = event.values[2] - 9.81f
//                Log.i("Acc", "x: $xAcceleration, y: $yAcceleration, z: $zAcceleration")
                val magnitudeAcceleration = sqrt(accelerometerValues[0]*accelerometerValues[0] + accelerometerValues[1]*accelerometerValues[1] + accelerometerValues[2]*accelerometerValues[2])
                currEWMA = calculateEWMA(magnitudeAcceleration, currEWMA, 0.08f)
//                Log.i("Values", "magnitudeAcceleration = $magnitudeAcceleration, currEWMA = $currEWMA")
                if(magnitudeAcceleration > currEWMA+1.1){
                    stepCount+=1
                }
            }
            Sensor.TYPE_MAGNETIC_FIELD -> {
                magneticSensorValues[0] = event.values[0]
                magneticSensorValues[1] = event.values[1]
                magneticSensorValues[2] = event.values[2]
                //Log.i("Magnetometer", "x: ${magneticSensorValues[0]}, y: ${magneticSensorValues[1]}, z: ${magneticSensorValues[2]}")
            }
            Sensor.TYPE_ROTATION_VECTOR -> {
                val rotationMatrix = FloatArray(9)
                SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values)
                val orientation = FloatArray(3)
                SensorManager.getOrientation(rotationMatrix, orientation)
                val x = orientation[0] * 180 / Math.PI
                val y = orientation[1] * 180 / Math.PI
                val z = orientation[2] * 180 / Math.PI
                Log.i("Rotation Vector", "x: $x, y: $y, z: $z")
                if(x <= 22.5 && x >= -22.5){
                    tvDirection.text = "Direction: North"
                }
                else if(x < -22.5 && x >= -67.5){
                    tvDirection.text = "Direction: North-West"
                }
                else if(x < -67.5 && x >= -112.5){
                    tvDirection.text = "Direction: West"
                }
                else if(x < -112.5 && x >= -157.5){
                    tvDirection.text = "Direction: South-West"
                }
                else if(x < -157.5 || x >= 157.5){
                    tvDirection.text = "Direction: South"
                }
                else if(x < 157.5 && x >= 112.5){
                    tvDirection.text = "Direction: South-East"
                }
                else if(x < 112.5 && x >= 67.5){
                    tvDirection.text = "Direction: East"
                }
                else if(x < 67.5 && x > 22.5){
                    tvDirection.text = "Direction: North-East"
                }
            }
        }
    }

    private fun calculateEWMA(currentValue: Float, previousEWMA: Double, alpha: Float): Double {
        return alpha * currentValue + (1 - alpha) * previousEWMA
    }



    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Do nothing
    }
}