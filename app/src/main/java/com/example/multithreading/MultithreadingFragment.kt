package com.example.multithreading

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager

class MultithreadingFragment : Fragment() {

    private lateinit var startTimerBtn: Button
    private lateinit var timerText: TextView
    private var isStart = false
    private var time = 0L

    companion object {

        fun newInstance() = MultithreadingFragment()
        const val delay = 1L
    }

    private val thread = Thread {
        var sTime: String
        val handler = Handler(Looper.getMainLooper())
        while (true) {
            Thread.sleep(delay)
            handler.post {
                sTime = "${time / 60000L} : " +
                        "${time / 1000L % 60L} : " +
                        "${time % 60L}"
                time++
                timerText.text = sTime

            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putBoolean("isStart", isStart)
        outState.putLong("timerValue", time)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)

        time = savedInstanceState?.getLong("timerValue") ?: 0L
        isStart = savedInstanceState?.getBoolean("isStart") ?: false
        if (isStart) {
            thread.start()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val mView = inflater.inflate(R.layout.fragment_multithreading, container, false)

        timerText = mView.findViewById(R.id.timer_text)
        startTimerBtn = mView.findViewById(R.id.start_timer_btn)
        startTimerBtn.setOnClickListener {
            //start timer
            isStart = !isStart
            if (isStart) {
                thread.start()
            }
        }

        return mView
    }

    private fun startWork() {
        val data = Data.Builder()
        data.putLong("TimerValue", time)

        val request = OneTimeWorkRequestBuilder<MyWorker>()
            .setInputData(data.build())
            .build()

        WorkManager.getInstance(requireContext()).enqueue(request)
    }

    override fun onPause() {
        super.onPause()
        startWork()
    }
}