package com.nerdachse.controlnerd.obswebsocket.protocol

import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

fun toggleInputMute(inputName: String): Command = Command(
    requestType = "ToggleInputMute",
    requestData = JsonObject(
        mapOf(
            "inputName" to JsonPrimitive(inputName)
        )
    )
)

fun setInputMute(inputName: String, mute: Boolean): Command = Command(
    requestType = "SetInputMute",
    requestData = JsonObject(
        mapOf(
            "inputName" to JsonPrimitive(inputName),
            "inputMuted" to JsonPrimitive(mute)
        )
    )
)
