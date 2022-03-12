package com.nerdachse.controlnerd.features

import android.view.View
import com.google.android.material.snackbar.Snackbar
import com.nerdachse.controlnerd.ObsState
import com.nerdachse.controlnerd.obswebsocket.ObsWebsocket
import com.nerdachse.controlnerd.obswebsocket.protocol.setInputMute
import com.nerdachse.controlnerd.obswebsocket.protocol.switchToScene

fun togglePause(state: ObsState, ws: ObsWebsocket, v: View) {
    if (state.isPaused) {
        val res: Result<Unit> = ws.sendCommand(switchToScene(sceneName = state.currentPreviewScene))
        ws.sendCommand(setInputMute(inputName = "Mic/Aux", mute = false))
        if (res.isFailure) {
            Snackbar.make(v, "Failed to switch from Pause :(", Snackbar.LENGTH_LONG).show()
        } else {
            Snackbar.make(v, "Pause ended, work you lazy Peon!", Snackbar.LENGTH_LONG).show()
            state.isPaused = false
        }
    } else {
        val res: Result<Unit> = ws.sendCommand(switchToScene(sceneName = "Noir"))
        ws.sendCommand(setInputMute(inputName = "Mic/Aux", mute = true))
        if (res.isFailure) {
            Snackbar.make(v, "Failed to switch to Pause :(", Snackbar.LENGTH_LONG).show()
        } else {
            Snackbar.make(v, "Pausing!", Snackbar.LENGTH_LONG).show()
            state.isPaused = true
        }
    }
}
