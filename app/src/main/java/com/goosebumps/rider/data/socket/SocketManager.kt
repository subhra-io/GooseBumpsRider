package com.goosebumps.rider.data.socket

import com.goosebumps.rider.BuildConfig
import com.goosebumps.rider.data.local.prefs.SessionManager
import io.socket.client.IO
import io.socket.client.Socket
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import org.json.JSONObject
import timber.log.Timber
import java.net.URI
import javax.inject.Inject
import javax.inject.Singleton

sealed class SocketEvent {
    data class NewOrder(val orderId: String, val orderJson: String) : SocketEvent()
    data class OrderCancelled(val orderId: String) : SocketEvent()
    data class OrderStatusUpdate(val orderId: String, val status: String) : SocketEvent()
    data object Connected : SocketEvent()
    data object Disconnected : SocketEvent()
}

@Singleton
class SocketManager @Inject constructor(
    private val sessionManager: SessionManager
) {
    private var socket: Socket? = null

    private val _events = MutableSharedFlow<SocketEvent>(extraBufferCapacity = 10)
    val events: SharedFlow<SocketEvent> = _events

    fun connect() {
        try {
            val token = sessionManager.getToken() ?: return
            val options = IO.Options.builder()
                .setAuth(mapOf("token" to token))
                .setReconnection(true)
                .setReconnectionAttempts(5)
                .setReconnectionDelay(2000)
                .build()

            socket = IO.socket(URI.create(BuildConfig.SOCKET_URL), options)

            socket?.apply {
                on(Socket.EVENT_CONNECT) {
                    Timber.d("Socket connected")
                    _events.tryEmit(SocketEvent.Connected)
                }

                on(Socket.EVENT_DISCONNECT) {
                    Timber.d("Socket disconnected")
                    _events.tryEmit(SocketEvent.Disconnected)
                }

                on("new_order") { args ->
                    try {
                        val data = args[0] as JSONObject
                        val orderId = data.getString("order_id")
                        _events.tryEmit(SocketEvent.NewOrder(orderId, data.toString()))
                    } catch (e: Exception) {
                        Timber.e(e, "Error parsing new_order event")
                    }
                }

                on("order_cancelled") { args ->
                    try {
                        val data = args[0] as JSONObject
                        val orderId = data.getString("order_id")
                        _events.tryEmit(SocketEvent.OrderCancelled(orderId))
                    } catch (e: Exception) {
                        Timber.e(e, "Error parsing order_cancelled event")
                    }
                }

                on("order_status_update") { args ->
                    try {
                        val data = args[0] as JSONObject
                        val orderId = data.getString("order_id")
                        val status = data.getString("status")
                        _events.tryEmit(SocketEvent.OrderStatusUpdate(orderId, status))
                    } catch (e: Exception) {
                        Timber.e(e, "Error parsing order_status_update event")
                    }
                }

                connect()
            }
        } catch (e: Exception) {
            Timber.e(e, "Socket connection error")
        }
    }

    fun emitLocation(lat: Double, lng: Double, speed: Float, battery: Int) {
        if (socket?.connected() == true) {
            val data = JSONObject().apply {
                put("lat", lat)
                put("lng", lng)
                put("speed", speed)
                put("battery", battery)
            }
            socket?.emit("rider_location", data)
        }
    }

    fun emitOnlineStatus(isOnline: Boolean) {
        if (socket?.connected() == true) {
            val data = JSONObject().apply { put("is_online", isOnline) }
            socket?.emit("rider_status", data)
        }
    }

    fun disconnect() {
        socket?.disconnect()
        socket = null
    }

    fun isConnected(): Boolean = socket?.connected() == true
}
