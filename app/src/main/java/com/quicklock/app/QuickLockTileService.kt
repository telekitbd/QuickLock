package com.quicklock.app

import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Icon
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService

class QuickLockTileService : TileService() {
    private lateinit var dpm: DevicePolicyManager
    private lateinit var adminComponent: ComponentName

    override fun onCreate() {
        super.onCreate()
        dpm = getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
        adminComponent = ComponentName(this, QuickLockAdminReceiver::class.java)
    }

    override fun onStartListening() {
        super.onStartListening()
        updateTile()
    }

    override fun onClick() {
        super.onClick()
        if (dpm.isAdminActive(adminComponent)) {
            dpm.lockNow()
        } else {
            val launch = packageManager.getLaunchIntentForPackage(packageName)
            launch?.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            if (launch != null) startActivityAndCollapse(launch)
        }
    }

    private fun updateTile() {
        val tile = qsTile ?: return
        tile.label = "Quick Lock"
        tile.contentDescription = "Tap to lock your phone screen"
        tile.icon = Icon.createWithResource(this, R.drawable.ic_quick_lock)
        tile.state = if (dpm.isAdminActive(adminComponent)) Tile.STATE_ACTIVE else Tile.STATE_INACTIVE
        tile.updateTile()
    }
}
