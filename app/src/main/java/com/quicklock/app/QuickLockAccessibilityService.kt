package com.quicklock.app

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.view.accessibility.AccessibilityEvent

/**
 * Locks the screen with Android's own GLOBAL_ACTION_LOCK_SCREEN (same as the power button).
 *
 * Unlike DevicePolicyManager.lockNow(), this does NOT force "PIN/pattern required",
 * so fingerprint and face unlock keep working.
 *
 * The service never reads screen content (no events, canRetrieveWindowContent = false).
 */
class QuickLockAccessibilityService : AccessibilityService() {

    override fun onServiceConnected() {
        super.onServiceConnected()
        instance = this
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) { /* intentionally empty */ }

    override fun onInterrupt() { /* intentionally empty */ }

    override fun onUnbind(intent: Intent?): Boolean {
        instance = null
        return super.onUnbind(intent)
    }

    override fun onDestroy() {
        instance = null
        super.onDestroy()
    }

    fun lockScreen(): Boolean = performGlobalAction(GLOBAL_ACTION_LOCK_SCREEN)

    companion object {
        @Volatile
        var instance: QuickLockAccessibilityService? = null
    }
}
