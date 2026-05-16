package com.goosebumps.rider.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.BatteryManager
import android.os.IBinder
import android.os.Looper
import androidx.core.app.NotificationCompat
import com.goosebumps.rider.MainActivity
import com.goosebumps.rider.R
import com.goosebumps.rider.data.local.prefs.SessionManager
import com.goosebumps.rider.data.socket.SocketManager
import com.goosebumps.rider.domain.model.RiderLocation
import com.goosebumps.rider.domain.repository.RiderRepository
import com.google.android.gms.location.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class RiderLocationService : Service() {

    @Inject lateinit var fusedLocationClient: FusedLocationProviderClient
    @Inject lateinit var riderRepository: RiderRepository
    @Inject lateinit var socketManager: SocketManager
    @Inject lateinit var sessionManager: SessionManager

    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var locationCallback: LocationCallback? = null

    companion object {
        const val CHANNEL_ID = "rider_location_channel"
        const val NOTIFICATION_ID = 1001
        const val ACTION_START = "ACTION_START_LOCATION"
        const val ACTION_STOP = "ACTION_STOP_LOCATION"
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> startLocationTracking()
            ACTION_STOP -> stopSelf()
        }
        return START_STICKY
    }

    private fun startLocationTracking() {
        startForeground(NOTIFICATION_ID, buildNotification())

        serviceScope.launch {
            val batterySaver = sessionManager.batterySaverFlow.first()
            val intervalMs = if (batterySaver) 10_000L else 5_000L

            val request = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, intervalMs)
                .setMinUpdateIntervalMillis(intervalMs / 2)
                .setMaxUpdateDelayMillis(intervalMs * 2)
                .build()

            locationCallback = object : LocationCallback() {
                override fun onLocationResult(result: LocationResult) {
                    result.lastLocation?.let { location ->
                        val battery = getBatteryLevel()
                        val riderLocation = RiderLocation(
                            lat = location.latitude,
                            lng = location.longitude,
                            speed = location.speed,
                            battery = battery,
                            accuracy = location.accuracy
                        )
                        serviceScope.launch {
                            riderRepository.updateLocation(riderLocation)
                            socketManager.emitLocation(
                                location.latitude, location.longitude,
                                location.speed, battery
                            )
                        }
                    }
                }
            }

            try {
                fusedLocationClient.requestLocationUpdates(
                    request,
                    locationCallback!!,
                    Looper.getMainLooper()
                )
            } catch (e: SecurityException) {
                Timber.e(e, "Location permission denied")
                stopSelf()
            }
        }
    }

    private fun getBatteryLevel(): Int {
        val bm = getSystemService(BATTERY_SERVICE) as BatteryManager
        return bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
    }

    private fun buildNotification(): Notification {
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Goosebumps Rider")
            .setContentText("You are online and tracking is active")
            .setSmallIcon(android.R.drawable.ic_menu_mylocation)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Rider Location Tracking",
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = "Active while you are online"
        }
        val nm = getSystemService(NotificationManager::class.java)
        nm.createNotificationChannel(channel)
    }

    override fun onDestroy() {
        locationCallback?.let { fusedLocationClient.removeLocationUpdates(it) }
        serviceScope.cancel()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
