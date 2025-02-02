package com.example.orchestrion


import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.HorizontalScrollView
import android.widget.SeekBar
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider

class PianoActivity : AppCompatActivity() {

    private val whiteKeyIds = arrayOf(
        R.id.keyA1, R.id.keyB1, R.id.keyC1, R.id.keyD1, R.id.keyE1, R.id.keyF1, R.id.keyG1,
        R.id.keyA2, R.id.keyB2, R.id.keyC2, R.id.keyD2, R.id.keyE2, R.id.keyF2, R.id.keyG2,

        )

    private val blackKeyIds = arrayOf(
        R.id.keyA1Sharp, R.id.keyC1Sharp, R.id.keyD1Sharp, R.id.keyF1Sharp, R.id.keyG1Sharp,
        R.id.keyA2Sharp, R.id.keyC2Sharp, R.id.keyD2Sharp, R.id.keyF2Sharp, R.id.keyG2Sharp,

        )


    private lateinit var viewModel: ViewModel

    private lateinit var keys: Array<Button>

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        setContentView(R.layout.activity_piano)

        viewModel= ViewModelProvider(this)[ViewModel::class.java]

//        val bleManager = viewModel.BLEManager

        val seekBar = findViewById<SeekBar>(R.id.seekBar)
        val scrollView = findViewById<HorizontalScrollView>(R.id.scrollView)
        scrollView.post { scrollView.scrollTo((scrollView.getChildAt(0).width * 0.55).toInt(), 0) }

        scrollView.setOnTouchListener { _, _ -> true }

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val scrollX = (scrollView.getChildAt(0).width - scrollView.width) * progress / 100
                scrollView.scrollTo(scrollX, 0)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                // Not needed for this example
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                // Not needed for this example
            }
        })

        val whiteKeys = whiteKeyIds.mapIndexed { index, keyId ->
            findViewById<Button>(keyId).apply {
                setOnTouchListener { _, event ->
                    when (event.action) {
                        MotionEvent.ACTION_DOWN -> {
//                            if(bleManager.isOrchestrionConnected())
//                                bleManager.sendMidiMessage(0x91, index+100, 0xFF)
                            setBackgroundColor(Color.parseColor("#80ffe5"))
                            Log.d("index", "$index")
                            Handler().postDelayed({
                                setBackgroundColor(Color.WHITE)
                            }, 100)
                            true
                        }
                        MotionEvent.ACTION_UP -> {
//                            if(bleManager.isOrchestrionConnected())
//                                bleManager.sendMidiMessage(0x91 , index+100, 0x00)
                            true
                        }
                        else -> false // Pour les autres événements, retourne false
                    }
                }
            }
        }

        val blackKeys = blackKeyIds.mapIndexed { index, keyId ->
            findViewById<Button>(keyId).apply {
                setOnTouchListener { _, event ->
                    when (event.action) {
                        MotionEvent.ACTION_DOWN -> {
//                            if(bleManager.isOrchestrionConnected())
//                                bleManager.sendMidiMessage(0x91, index+100, 0xFF)
                            setBackgroundColor(Color.parseColor("#80ffe5"))
                            Log.d("index", "$index")
                            Handler().postDelayed({
                                setBackgroundColor(Color.BLACK)
                            }, 100)
                            true
                        }
                        MotionEvent.ACTION_UP -> {
//                            if(bleManager.isOrchestrionConnected())
//                                bleManager.sendMidiMessage(0x91, index+100, 0x00)
                            true
                        }
                        else -> false // Pour les autres événements, retourne false
                    }
                }
            }
        }

        keys = whiteKeys.toTypedArray() + blackKeys.toTypedArray()
    }

}
