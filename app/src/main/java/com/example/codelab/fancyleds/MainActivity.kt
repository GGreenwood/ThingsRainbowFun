package com.example.codelab.fancyleds

import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.widget.SeekBar
import com.google.android.things.contrib.driver.apa102.Apa102
import com.google.android.things.contrib.driver.rainbowhat.RainbowHat
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch


class MainActivity : Activity() {
    lateinit var delayBar: SeekBar
    lateinit var brightnessBar: SeekBar

    lateinit var ledStrip: Apa102

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        delayBar = findViewById(R.id.delayBar)
        brightnessBar = findViewById(R.id.brightnessBar)

        var delayAmount: Int = 0

        with(delayBar) {
            setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    delayAmount = progress
                }
                override fun onStartTrackingTouch(seekBar: SeekBar?) {}
                override fun onStopTrackingTouch(seekBar: SeekBar?) {}
            })
            min = 1
            max = 100
            progress = 10
        }

        with(brightnessBar) {
            setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    if ( ::ledStrip.isInitialized) {
                        ledStrip.brightness = progress
                    }
                }
                override fun onStartTrackingTouch(seekBar: SeekBar?) {}
                override fun onStopTrackingTouch(seekBar: SeekBar?) {}
            })
            min = 0
            max = 15
            progress = 15
        }

        ledStrip = RainbowHat.openLedStrip()
        ledStrip.brightness = 10

        launch {
            var frame: Int = 0
            while (true) {
                updateLeds(frame)
                frame++
                delay(delayAmount)
            }
        }
    }

    suspend fun updateLeds(frame: Int) {
        //Log.i("led", "Rendering frame: " + frame)
        val rainbow = IntArray(RainbowHat.LEDSTRIP_LENGTH)
        for (i in rainbow.indices) {
            val hue: Float = (i*2 + frame) % 360.0f
            val saturation: Float = 1.0f
            val value: Float = 1.0f
            rainbow[i] = Color.HSVToColor(
                    255,
                    floatArrayOf(hue, saturation, value)
            )
        }
        ledStrip.write(rainbow)
    }

    override fun onDestroy() {
        if (::ledStrip.isInitialized)
            ledStrip.close()
        super.onDestroy()
    }
}