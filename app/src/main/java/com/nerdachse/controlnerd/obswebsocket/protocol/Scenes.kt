package com.nerdachse.controlnerd.obswebsocket.protocol

import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

fun switchToScene(sceneName: String): Command = Command(
    requestType = "SetCurrentProgramScene",
    requestData = JsonObject(
        mapOf(
            "sceneName" to JsonPrimitive(sceneName)
        )
    )
)

fun getCurrentPreviewScene(): Command = Command (
    requestType = "GetCurrentPreviewScene",
    requestData = JsonObject(mapOf())
)

fun getCurrentProgramScene(): Command = Command (
    requestType = "GetCurrentProgramScene",
    requestData = JsonObject(mapOf())
)
