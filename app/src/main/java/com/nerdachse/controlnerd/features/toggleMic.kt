package com.nerdachse.controlnerd.features

import android.view.View
import com.google.android.material.snackbar.Snackbar
import com.nerdachse.controlnerd.obswebsocket.ObsWebsocket
import com.nerdachse.controlnerd.obswebsocket.protocol.toggleInputMute

fun toggleMic(ws: ObsWebsocket, v: View) {
    Snackbar.make(v, "Toggle Mic", Snackbar.LENGTH_LONG).show()
    val res: Result<Unit> = ws.sendCommand(toggleInputMute(inputName = "Mic/Aux"))
    if (res.isFailure) {
        Snackbar.make(v, "Failed to toggle mic", Snackbar.LENGTH_LONG).show()
    }
}
