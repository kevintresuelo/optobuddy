package com.kevintresuelo.clinicus.models.services

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.RingtoneManager
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.kevintresuelo.clinicus.MainActivity
import com.kevintresuelo.clinicus.R
import com.kevintresuelo.clinicus.models.Device
import com.kevintresuelo.clinicus.utils.*
import com.kevintresuelo.clinicus.utils.notifications.NotificationChannels
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.net.URL
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class PushNotificationService: FirebaseMessagingService() {

    @Inject
    lateinit var storageService: StorageService

    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.IO + job)

    override fun onNewToken(token: String) {
        sendRegistrationToServer(token)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        val notificationManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (notificationManager.areNotificationsEnabled()) {
            message.notification?.let { notification ->
                if (!message.data["title"].isNullOrBlank() || notification.channelId == NotificationChannels.CHANNEL_UPDATES) {
                    sendUpdateNotification(notification, message.data)
                } else {
                    sendRemoteNotification(notification)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

    private fun sendRegistrationToServer(token: String) {
        scope.launch {
            readString(DataStore.PreferenceKeys.DEVICE_UID).collect {
                val device = if (it.isBlank()) null else storageService.getDevice(it)

                if (device == null) {
                    val id = autoId()
                    storageService.saveDevice(
                        Device(
                            documentId = id,
                            fcm_token = token,
                            device = getDeviceDetails(),
                            last_accessed = System.currentTimeMillis()
                        )
                    )
                    writeString(DataStore.PreferenceKeys.DEVICE_UID, id)
                } else {
                    storageService.saveDevice(
                        device.copy(
                            fcm_token = token,
                            last_accessed = System.currentTimeMillis()
                        )
                    )
                }
            }
        }
    }

    private fun sendRemoteNotification(notification: RemoteMessage.Notification) {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
            PendingIntent.FLAG_IMMUTABLE)

        val channelId = NotificationChannels.CHANNEL_DEFAULT
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(android.R.drawable.stat_sys_data_bluetooth)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)

        notification.title?.let { notificationBuilder.setContentTitle(it) }
        notification.body?.let { notificationBuilder.setContentText(it) }
        notification.imageUrl?.let {
            var bitmap: Bitmap? = null
            val largeIcon: Bitmap? = null

            try {
                val stream = URL(it.toString()).openStream()
                bitmap = BitmapFactory.decodeStream(stream)
            } catch (e: Exception) {
                e.printStackTrace()
            }

            notificationBuilder.setLargeIcon(bitmap)
                .setStyle(
                    NotificationCompat.BigPictureStyle()
                        .bigPicture(bitmap)
                        .bigLargeIcon(largeIcon)
                )

        }

        val notificationManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        notificationManager.notify(Random().nextInt(1000), notificationBuilder.build())
    }

    private fun sendUpdateNotification(notification: RemoteMessage.Notification, data: Map<String, String>) {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
            PendingIntent.FLAG_IMMUTABLE)

        val channelId = NotificationChannels.CHANNEL_UPDATES
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(android.R.drawable.stat_sys_data_bluetooth)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)

        notification.title?.let { notificationBuilder.setContentTitle(it) }
        notification.body?.let { notificationBuilder.setContentText(it) }
        notification.imageUrl?.let {
            var bitmap: Bitmap? = null
            val largeIcon: Bitmap? = null

            try {
                val stream = URL(it.toString()).openStream()
                bitmap = BitmapFactory.decodeStream(stream)
            } catch (e: Exception) {
                e.printStackTrace()
            }

            notificationBuilder.setLargeIcon(bitmap)
                .setStyle(
                    NotificationCompat.BigPictureStyle()
                        .bigPicture(bitmap)
                        .bigLargeIcon(largeIcon)
                )

        }

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(Random().nextInt(1000), notificationBuilder.build())
    }

}