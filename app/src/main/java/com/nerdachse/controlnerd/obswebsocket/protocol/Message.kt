package com.nerdachse.controlnerd.obswebsocket.protocol

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import java.util.*

@Serializable
open class Message(val op: Long, val d: JsonObject)

@Serializable
data class Command(
    val requestType: String,
    val requestId: String = UUID.randomUUID().toString(),
    val requestData: JsonElement
) : Message(
    op = 6,
    d = JsonObject(
        mapOf(
            "requestType" to JsonPrimitive(requestType),
            "requestId" to JsonPrimitive(requestId),
            "requestData" to requestData
        )
    )
)

@Serializable
data class Authenticate(val rpcVersion: Long = 1, val authentication: String, val eventSubscriptions: Long? = null) : Message(
    op = 1,
    d = JsonObject(
        mapOf(
            "rpcVersion" to JsonPrimitive(rpcVersion),
            "authentication" to JsonPrimitive(authentication),
            // TODO set a meaningful default
            //"eventSubscriptions" to JsonPrimitive(eventSubscriptions)
        )
    )
)
