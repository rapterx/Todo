package com.example.todo

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat


class NotificationService(name: String?) : Service() {
    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        onHandleIntent(intent)
        return super.onStartCommand(intent, flags, startId)
    }

     private fun onHandleIntent(intent: Intent?) {
        val mNotificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "YOUR_CHANNEL_ID",
                "YOUR_CHANNEL_NAME",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            channel.description = "YOUR_NOTIFICATION_CHANNEL_DESCRIPTION"
            mNotificationManager.createNotificationChannel(channel)
        }
        val mBuilder: NotificationCompat.Builder =
            NotificationCompat.Builder(applicationContext, "YOUR_CHANNEL_ID")
                .setSmallIcon(R.drawable.edit_text_background) // notification icon
                .setContentTitle("title") // title for notification
                .setContentText("Message") // message for notification
                .setAutoCancel(true) // clear notification after click
        val i = Intent(applicationContext, MainActivity::class.java)
        val pi = PendingIntent.getActivity(this, 0, i, PendingIntent.FLAG_UPDATE_CURRENT)
        mBuilder.setContentIntent(pi)
        mNotificationManager.notify(0, mBuilder.build())
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    companion object {
        private const val NOTIFICATION_ID = 3
    }
}