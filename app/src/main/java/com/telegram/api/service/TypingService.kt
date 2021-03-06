package com.telegram.api.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.telegram.api.R
import com.telegram.api.utils.Constants.NOTIFICATION_ID
import inc.brody.tapi.requests.TGetChat
import inc.brody.tapi.requests.TGetChats
import inc.brody.tapi.requests.TSendChatAction
import inc.brody.tapi.requests.TSetOption
import org.drinkless.td.libcore.telegram.TdApi


class TypingService : Service() {
    override fun onBind(p0: Intent?): IBinder? = null


    override fun onCreate() {
        super.onCreate()

        TSetOption("online", TdApi.OptionValueBoolean(true)) {
            Log.d("test3", "ok")
        }
        typing()
    }

    private fun typing() {
        TGetChats { chats ->
            if (chats is TdApi.Chats) {
                chats.chatIds.forEach {
                    TGetChat(it) {
                        Log.d("test12", it?.javaClass?.name.toString())
                        if (it is TdApi.UpdateChatAction)
                            if (it.action is TdApi.ChatActionTyping)
                                TSendChatAction(it.chatId, TdApi.ChatActionTyping()) {}

                    }
                }
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) startMyOwnForeground() else startForeground(
            1,
            Notification()
        )

        return START_STICKY
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun startMyOwnForeground() {
        val NOTIFICATION_CHANNEL_ID = applicationContext.packageName
        val channelName = "My Background Service"
        val chan = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            channelName,
            NotificationManager.IMPORTANCE_NONE
        )

        chan.apply {
            lightColor = Color.BLUE
            lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        }

        val manager = (getSystemService(NOTIFICATION_SERVICE) as NotificationManager)
        manager.createNotificationChannel(chan)
        val notificationBuilder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
        val notification: Notification = notificationBuilder.setOngoing(true)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Typing on")
            .setPriority(NotificationManager.IMPORTANCE_HIGH)
            .setCategory(Notification.CATEGORY_SERVICE)
            .build()

        startForeground(NOTIFICATION_ID, notification)
    }
}