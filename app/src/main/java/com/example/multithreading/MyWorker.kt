package com.example.multithreading

import android.app.Notification.DEFAULT_ALL
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_HIGH
import android.app.PendingIntent.getActivity
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.O
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters

class MyWorker(context: Context, params: WorkerParameters) : Worker(context, params) {

    override fun doWork(): Result {

        showNotification(inputData.getLong("TimerValue", 10L))
        return Result.success()
    }

    private fun showNotification(time: Long) {
        val intent = Intent(applicationContext, MainActivity::class.java)
        intent.flags = FLAG_ACTIVITY_NEW_TASK or FLAG_ACTIVITY_CLEAR_TASK
        intent.putExtra("multithreading_notify", id)

        val notificationManager =
            applicationContext.getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        val pendingIntent = getActivity(applicationContext, 0, intent, 0)

        val notification = NotificationCompat.Builder(applicationContext, "multithreading_channel")
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setContentTitle("Timer").setContentText(
                "${time / 60000L} : " +
                        "${time / 1000L % 60L} : " +
                        "${time % 60L}"
            )
            .setDefaults(DEFAULT_ALL).setContentIntent(pendingIntent).setAutoCancel(true)

        if (SDK_INT >= O) {
            notification.setChannelId("multithreading_channel")

            val channel =
                NotificationChannel("multithreading_channel", "multithreading", IMPORTANCE_HIGH)

            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(1, notification.build())
    }
}