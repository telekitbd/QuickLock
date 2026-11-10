package com.quicklock.app

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.provider.Settings

object QuickLock {

    /** True if the user has switched the accessibility service on in Android settings. */
    fun isServiceEnabled(context: Context): Boolean {
        val expected = ComponentName(context, QuickLockAccessibilityService::class.java)
        val enabled = Settings.Secure.getString(
            context.contentResolver, Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
        ) ?: return false
        return enabled.split(':').any { ComponentName.unflattenFromString(it) == expected }
    }

    /** Locks the screen. Returns false if the service is not connected yet. */
    fun lock(): Boolean = QuickLockAccessibilityService.instance?.lockScreen() ?: false

    fun openAccessibilitySettings(context: Context) {
        context.startActivity(
            Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        )
    }
}
