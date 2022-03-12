package com.nerdachse.controlnerd.obswebsocket.eventhandlers

import com.nerdachse.controlnerd.ObsState
import com.nerdachse.controlnerd.obswebsocket.EventData
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

fun currentPreviewSceneChanged(d: EventData, state: ObsState) {
    if (d.eventData != null) {
        val sceneName = d.eventData.jsonObject["sceneName"]
        println("Setting currentPreviewScene to $sceneName")
        if (sceneName != null) {
            state.currentPreviewScene = sceneName.jsonPrimitive.content
        }
    }
}

fun currentProgramSceneChanged(d: EventData, state: ObsState) {
    if (d.eventData != null) {
        val sceneName = d.eventData.jsonObject["sceneName"]
        println("Setting currentProgramScene to $sceneName")
        if (sceneName != null) {
            state.currentProgramScene = sceneName.jsonPrimitive.content
        }
    }
}
