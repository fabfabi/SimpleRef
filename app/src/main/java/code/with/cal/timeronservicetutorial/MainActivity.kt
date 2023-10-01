package code.with.cal.timeronservicetutorial

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.appcompat.app.AppCompatActivity
import code.with.cal.timeronservicetutorial.databinding.ActivityMainBinding
import kotlin.math.roundToInt


class MainActivity : AppCompatActivity()
{
    private lateinit var binding: ActivityMainBinding
    private var timerStarted = false
    private lateinit var serviceIntent: Intent
    private var time = 0.0

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.startStopButton.setOnClickListener { startStopTimer() }
        binding.resetButton.setOnClickListener { resetTimer() }

        serviceIntent = Intent(applicationContext, TimerService::class.java)
        registerReceiver(updateTime, IntentFilter(TimerService.TIMER_UPDATED))
    }

    private fun resetTimer()
    {
        stopTimer()
        time = 0.0
        binding.timeTV.text = getTimeStringFromDouble(time)
        vibeReset()
    }

    private fun startStopTimer()
    {
        if(timerStarted)
            stopTimer()
        else
            startTimer()
        vibeReset()
    }

    private fun startTimer()
    {
        serviceIntent.putExtra(TimerService.TIME_EXTRA, time)
        startService(serviceIntent)
        binding.startStopButton.text = "Stop"
        binding.startStopButton.icon = getDrawable(R.drawable.ic_baseline_pause_24)
        timerStarted = true
    }

    private fun stopTimer()
    {
        stopService(serviceIntent)
        binding.startStopButton.text = "Start"
        binding.startStopButton.icon = getDrawable(R.drawable.ic_baseline_play_arrow_24)
        timerStarted = false
    }

    private val updateTime: BroadcastReceiver = object : BroadcastReceiver()
    {
        override fun onReceive(context: Context, intent: Intent)
        {
            time = intent.getDoubleExtra(TimerService.TIME_EXTRA, 0.0)
            binding.timeTV.text = getTimeStringFromDouble(time)
        }

    }

    private fun checkForAlarm(time: Int)
    {
        when(time){
            80 -> vibeHint()
            90 -> vibeHint()
            100 -> vibeAlarm()
            130 -> vibeHint()
            140 -> vibeHint()
            150 -> vibeAlarm()
            250 -> vibeHint()
            300 -> vibeAlarm()
            850 -> vibeHint()
            900 -> vibeAlarm()
        }
    }
    private fun getTimeStringFromDouble(time: Double): String
    {
        val resultInt = time.roundToInt()

        val minutes = resultInt  / 600
        val seconds = resultInt  % 600
        val tenthseconds = resultInt  % 10

        checkForAlarm(resultInt)

        return makeTimeString(minutes, seconds, tenthseconds )
    }

    private fun vibrate(time: Int)
    {
        val v = getSystemService(VIBRATOR_SERVICE) as Vibrator
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(time.toLong(), VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            //deprecated in API 26
            v.vibrate(time.toLong())
        }
    }

    private fun vibeReset()
    {
        vibrate(50)
    }

    private fun vibeHint()
    {
        vibrate(50)
    }

    private fun vibeAlarm()
    {
        vibrate(500)
    }

    private fun makeTimeString(min: Int, sec: Int, tSec: Int): String = String.format("%02d:%02d.%01d", min, sec, tSec)
}