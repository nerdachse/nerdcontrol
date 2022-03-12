package com.nerdachse.controlnerd.ui.transform

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nerdachse.controlnerd.R
import com.nerdachse.controlnerd.features.toggleMic
import com.nerdachse.controlnerd.features.togglePause
import com.nerdachse.controlnerd.getMainActivity
import com.nerdachse.controlnerd.ui.ActionButton

class TransformViewModel : ViewModel() {

    private val _buttons = MutableLiveData<List<ActionButton>>().apply {
        value = listOf(
            ActionButton(
                name = "Toggle Pause",
                action = { v: View -> v.getMainActivity()?.let { togglePause(it.state, it.ws, v) } },
                drawable = R.drawable.ic_dachs
            ),
            ActionButton(
                name = "Toggle Mic",
                action = { v: View -> v.getMainActivity()?.let { toggleMic(it.ws, v) } },
                drawable = R.drawable.ic_dachs_kopf
            ),
        )
    }

    val buttons: LiveData<List<ActionButton>> = this._buttons
}
