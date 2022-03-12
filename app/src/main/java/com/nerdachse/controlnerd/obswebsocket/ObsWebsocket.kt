package com.nerdachse.controlnerd.obswebsocket

import android.os.Build
import androidx.annotation.RequiresApi
import com.neovisionaries.ws.client.WebSocket
import com.neovisionaries.ws.client.WebSocketAdapter
import com.neovisionaries.ws.client.WebSocketFactory
import com.neovisionaries.ws.client.WebSocketFrame
import com.nerdachse.controlnerd.ObsState
import com.nerdachse.controlnerd.obswebsocket.eventhandlers.currentPreviewSceneChanged
import com.nerdachse.controlnerd.obswebsocket.eventhandlers.currentProgramSceneChanged
import com.nerdachse.controlnerd.obswebsocket.protocol.Authenticate
import com.nerdachse.controlnerd.obswebsocket.protocol.Message
import com.nerdachse.controlnerd.obswebsocket.protocol.getCurrentProgramScene
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.io.IOException

enum class Protocol(val prefix: String) {
    SECURED("wss"),
    UNSECURED("ws")
}

@JvmInline
value class EventType(val eventType: String)

@JvmInline
value class EventData(val eventData: JsonElement?)

class ObsWebsocket(private val protocol: Protocol = Protocol.UNSECURED,
                   private val ipAddress: String,
                   private val port: String,
                   private val password: String,
                   val state: ObsState,
                   ) {
    private lateinit var ws: WebSocket

    private var eventHandlers: Map<EventType, (EventData) -> Unit> = hashMapOf(
        EventType("CurrentPreviewSceneChanged") to { d: EventData -> currentPreviewSceneChanged(d, state) },
        EventType("CurrentProgramSceneChanged") to { d: EventData -> currentProgramSceneChanged(d, state) },
    )

    fun sendCommand(msg: Message): Result<Unit> {
        if (ws.isOpen) {
            val json = Json.encodeToString(Message.serializer(), msg)
            println("Sending msg: $json")
            ws.sendText(json)
            return Result.success(Unit)
        }
        return Result.failure(RuntimeException("Failed to send"))
    }

    fun disconnect() {
        ws.disconnect()
    }

    fun connect() {
        val wsf = WebSocketFactory().setConnectionTimeout(5000)
        try {
            println("Attempting connection")
            // 10.0.2.2 is loopback to localhost of dev machine
            val uri = "${protocol.prefix}://$ipAddress:$port"
            println("Connecting to uri: $uri")
            ws = wsf.createSocket(uri)
            ws.addListener(object : WebSocketAdapter() {
                @Throws(Exception::class)
                override fun onConnected(websocket: WebSocket?, headers: MutableMap<String, MutableList<String>>?) {
                    println("Connected!")
                    super.onConnected(websocket, headers)
                }

                @RequiresApi(Build.VERSION_CODES.O)
                @Throws(Exception::class)
                override fun onTextMessage(websocket: WebSocket, text: String) {
                    val message = Json.parseToJsonElement(text)
                    try {
                        val op: String? = message.jsonObject["op"]?.jsonPrimitive?.content
                        val d: JsonElement? = message.jsonObject["d"]
                        // Initial
                        if (op.equals("0")) {
                            println("Attempting authentication")
                            val authentication = d?.jsonObject?.get("authentication")
                            if (authentication != null) {
                                val challenge = authentication.jsonObject["challenge"]?.jsonPrimitive?.content
                                val salt = authentication.jsonObject["salt"]?.jsonPrimitive?.content
                                if (salt != null && challenge != null) {
                                    val authenticationString = buildAuthenticationString(password, challenge, salt)
                                    sendCommand(
                                        Authenticate(
                                            authentication = authenticationString,
                                            eventSubscriptions = 33
                                        )
                                    )
                                } else {
                                    // TODO show something in the view
                                    println("Failed to authenticate, no salt or challenge given")
                                }
                            } else {
                                sendCommand(Authenticate(authentication = "", eventSubscriptions = 33))
                            }
                            return
                        }
                        if (op.equals("2")) {
                            println("Hurray, we are identified")
                            sendCommand(getCurrentProgramScene())
                            return
                        }
                        // Request Responses
                        if (op.equals("7")) {
                            println("Hurray, we are identified")
                            val requestType = d?.jsonObject?.get("requestType")?.jsonPrimitive?.content
                            val responseData = d?.jsonObject?.get("responseData")
                            if (requestType.equals("GetCurrentProgramScene") && responseData != null) {
                                val currentProgramScene = responseData.jsonObject["currentProgramSceneName"]
                                if (currentProgramScene != null) {
                                    state.currentProgramScene = currentProgramScene.jsonPrimitive.content
                                }
                            }
                            return
                        }
                        // Events
                        if (op.equals("5")) {
                            val eventType = d?.jsonObject?.get("eventType")?.jsonPrimitive?.content
                            val eventData = d?.jsonObject?.get("eventData")
                            val function: ((EventData) -> Unit)? = eventHandlers[eventType?.let { EventType(it) }]
                            if (function != null) {
                                function(EventData(eventData))
                            } else {
                                println("Unhandled eventType: $eventType")
                            }
                        }
                        println("Got text msg: $message")
                    } catch (e: Exception) {
                        println("Failed to decode")
                    }
                }

                @Throws
                override fun onBinaryMessage(websocket: WebSocket?, binary: ByteArray?) {
                    println("Got binary message")
                    super.onBinaryMessage(websocket, binary)
                }

                override fun onDisconnected(
                    websocket: WebSocket?,
                    serverCloseFrame: WebSocketFrame?,
                    clientCloseFrame: WebSocketFrame?,
                    closedByServer: Boolean
                ) {
                    super.onDisconnected(websocket, serverCloseFrame, clientCloseFrame, closedByServer)
                    println("Disconnected")
                    connect()
                }
            })
            ws.connectAsynchronously()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}
