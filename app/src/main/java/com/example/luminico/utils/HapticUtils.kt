package com.example.luminico.utils

import android.view.HapticFeedbackConstants
import android.view.View

fun View.triggerHaptic() {
    try {
        this.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK)
    } catch (_: Exception) { }
}
