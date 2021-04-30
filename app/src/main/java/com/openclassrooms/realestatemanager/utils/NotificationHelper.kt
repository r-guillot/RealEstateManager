package com.openclassrooms.realestatemanager.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat.getSystemService
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.propertylist.PropertyListActivity

class NotificationHelper {
    lateinit var context: Context
    private lateinit var pendingIntent: PendingIntent

    //creation on notificationChannel, called in newPropertyActivity
    fun createNotificationChannel(context: Context) {
        this.context = context
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = context.getString(R.string.notificationChannel)
            val name = context.getString(R.string.channel_name)
            val descriptionText = context.getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager = getSystemService(
                    context,
                    NotificationManager::class.java) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    //show notification after the creation of a new property
    fun showNotification() {
        configureNotificationIntent()
        val builder = NotificationCompat.Builder(context, context.getString(R.string.notificationChannel))
                .setSmallIcon(R.drawable.ic_house)
                .setContentTitle(context.getString(R.string.notification_title))
                .setStyle(NotificationCompat.BigTextStyle().bigText(context.getString(R.string.notification_body)))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
        val notificationManagerCompat: NotificationManagerCompat = NotificationManagerCompat.from(context)
        val notificationID = 123
        notificationManagerCompat.notify(notificationID, builder.build())
    }

    private fun configureNotificationIntent() {
        val intent = Intent(context, PropertyListActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        pendingIntent = PendingIntent.getActivity(context, 0, intent, 0)
    }
}