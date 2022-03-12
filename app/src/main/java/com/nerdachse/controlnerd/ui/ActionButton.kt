package com.nerdachse.controlnerd.ui

import android.view.View

data class ActionButton(
    val name: String,
    val action: (View) -> Unit,
    val drawable: Int
)
