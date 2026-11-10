package com.quicklock.app

import android.app.Activity
import android.content.Intent
import android.os.Bundle

/** Invisible activity for the launcher shortcut: lock and close immediately. */
class LockActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!QuickLock.lock()) {
            startActivity(Intent(this, MainActivity::class.java))
        }
        finish()
    }
}
