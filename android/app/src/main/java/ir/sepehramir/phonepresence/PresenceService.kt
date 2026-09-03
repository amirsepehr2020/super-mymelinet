package ir.sepehramir.phonepresence

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import java.net.HttpURLConnection
import java.net.URL
import org.json.JSONObject
import kotlin.concurrent.thread

class PresenceService : Service() {
    companion object {
        const val ACTION_START = "ir.sepehramir.phonepresence.START"
        const val ACTION_STOP = "ir.sepehramir.phonepresence.STOP"
        private const val CHANNEL_ID = "presence"
        private const val API_URL = "https://YOUR-WORKER.workers.dev/api/presence"
    }

    @Volatile private var running = false

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == ACTION_STOP) {
            running = false
            stopForeground(STOP_FOREGROUND_REMOVE)
            stopSelf()
            return START_NOT_STICKY
        }
        createChannel()
        startForeground(1001, notification())
        if (!running) {
            running = true
            thread(name = "presence-heartbeat") {
                while (running) {
                    sendPresence()
                    Thread.sleep(15_000)
                }
            }
        }
        return START_STICKY
    }

    private fun sendPresence() {
        try {
            val power = getSystemService(PowerManager::class.java)
            val cm = getSystemService(ConnectivityManager::class.java)
            val online = cm?.activeNetwork?.let {
                cm.getNetworkCapabilities(it)?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            } == true
            val owner = if (Build.MANUFACTURER.lowercase().contains("samsung")) "Sepehr" else "Amir"
            val body = JSONObject().apply {
                put("userId", owner.lowercase())
                put("internetOnline", online)
                put("screenOn", power?.isInteractive == true)
                put("timestamp", System.currentTimeMillis())
            }.toString()

            val connection = URL(API_URL).openConnection() as HttpURLConnection
            connection.requestMethod = "POST"
            connection.connectTimeout = 5000
            connection.readTimeout = 5000
            connection.doOutput = true
            connection.setRequestProperty("Content-Type", "application/json")
            connection.outputStream.use { it.write(body.toByteArray()) }
            connection.responseCode
            connection.disconnect()
        } catch (_: Exception) { }
    }

    private fun createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(NotificationChannel(CHANNEL_ID, "Phone Presence", NotificationManager.IMPORTANCE_LOW))
        }
    }

    private fun notification(): Notification {
        val builder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            Notification.Builder(this, CHANNEL_ID) else Notification.Builder(this)
        return builder.setContentTitle("Phone Presence")
            .setContentText("Presence tracking is active")
            .setSmallIcon(android.R.drawable.ic_menu_info_details)
            .setOngoing(true)
            .build()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        running = false
        super.onDestroy()
    }
}
