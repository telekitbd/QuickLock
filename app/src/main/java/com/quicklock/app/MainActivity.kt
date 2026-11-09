package com.quicklock.app

import android.app.Activity
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView

class MainActivity : Activity() {
    private lateinit var dpm: DevicePolicyManager
    private lateinit var adminComponent: ComponentName
    private lateinit var statusText: TextView
    private lateinit var enableButton: Button
    private lateinit var lockButton: Button

    private val bg = Color.rgb(16, 24, 39)
    private val muted = Color.rgb(169, 185, 207)
    private val blue = Color.rgb(52, 120, 246)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dpm = getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
        adminComponent = ComponentName(this, QuickLockAdminReceiver::class.java)
        buildScreen()
    }

    override fun onResume() {
        super.onResume()
        if (::statusText.isInitialized) refreshState()
    }

    private fun buildScreen() {
        val page = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_HORIZONTAL
            setPadding(dp(24), dp(28), dp(24), dp(24))
            setBackgroundColor(bg)
        }

        val header = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }
        val logo = TextView(this).apply {
            text = "🔒"
            textSize = 25f
            gravity = Gravity.CENTER
            background = rounded(Color.rgb(37, 59, 91), 18)
            setPadding(dp(13), dp(10), dp(13), dp(10))
        }
        val name = TextView(this).apply {
            text = "  Quick Lock"
            textSize = 21f
            setTextColor(Color.WHITE)
            setTypeface(null, Typeface.BOLD)
        }
        header.addView(logo)
        header.addView(name)
        page.addView(header, matchWrap())

        val spacer = View(this)
        page.addView(spacer, LinearLayout.LayoutParams(1, 0, 1f))

        val lockIcon = TextView(this).apply {
            text = "🔐"
            textSize = 54f
            gravity = Gravity.CENTER
            background = rounded(Color.rgb(29, 49, 80), 100)
            setPadding(dp(28), dp(25), dp(28), dp(25))
        }
        page.addView(lockIcon)

        val title = TextView(this).apply {
            text = "Quick Lock"
            textSize = 29f
            setTextColor(Color.WHITE)
            setTypeface(null, Typeface.BOLD)
            gravity = Gravity.CENTER
            setPadding(0, dp(20), 0, dp(5))
        }
        page.addView(title)

        val subtitle = TextView(this).apply {
            text = "One tap. Instant lock."
            textSize = 15f
            setTextColor(muted)
            gravity = Gravity.CENTER
        }
        page.addView(subtitle)

        lockButton = Button(this).apply {
            text = "🔒   LOCK SCREEN"
            textSize = 16f
            isAllCaps = false
            setTextColor(Color.WHITE)
            background = rounded(blue, 18)
            setOnClickListener {
                if (dpm.isAdminActive(adminComponent)) {
                    dpm.lockNow()
                } else {
                    requestAdmin()
                }
            }
        }
        val lockParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, dp(58)
        ).apply { topMargin = dp(34) }
        page.addView(lockButton, lockParams)

        val hint = TextView(this).apply {
            text = "Secure your phone with one tap"
            textSize = 12f
            setTextColor(muted)
            gravity = Gravity.CENTER
            setPadding(0, dp(12), 0, dp(28))
        }
        page.addView(hint)

        val card = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            background = rounded(Color.rgb(28, 40, 59), 18)
            setPadding(dp(18), dp(16), dp(18), dp(16))
        }
        val cardTitle = TextView(this).apply {
            text = "DEVICE SECURITY"
            textSize = 11f
            setTextColor(muted)
            setTypeface(null, Typeface.BOLD)
        }
        card.addView(cardTitle)
        statusText = TextView(this).apply {
            textSize = 14f
            setTextColor(Color.WHITE)
            setPadding(0, dp(14), 0, dp(12))
        }
        card.addView(statusText)

        enableButton = Button(this).apply {
            text = "Enable Device Admin"
            isAllCaps = false
            textSize = 14f
            setTextColor(Color.WHITE)
            background = rounded(Color.rgb(54, 75, 103), 14)
            setOnClickListener { requestAdmin() }
        }
        card.addView(enableButton, LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, dp(48)
        ))
        page.addView(card, LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
        ))

        val footer = TextView(this).apply {
            text = "Lightweight  •  Private  •  No ads"
            textSize = 12f
            setTextColor(Color.rgb(120, 139, 165))
            gravity = Gravity.CENTER
            setPadding(0, dp(22), 0, 0)
        }
        page.addView(footer)
        page.addView(View(this), LinearLayout.LayoutParams(1, 0, 0.7f))

        setContentView(page)
        refreshState()
    }

    private fun refreshState() {
        val active = dpm.isAdminActive(adminComponent)
        statusText.text = if (active) "●  Device Admin is enabled" else "○  Device Admin is not enabled"
        statusText.setTextColor(if (active) Color.rgb(76, 220, 157) else Color.rgb(255, 195, 95))
        enableButton.text = if (active) "Device Admin Enabled ✓" else "Enable Device Admin"
        enableButton.isEnabled = !active
        lockButton.text = if (active) "🔒   LOCK SCREEN" else "🔒   SET UP TO LOCK"
    }

    private fun requestAdmin() {
        val intent = Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN).apply {
            putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, adminComponent)
            putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                "Quick Lock needs permission to lock the screen when you tap the Lock Screen button.")
        }
        startActivity(intent)
    }

    private fun rounded(color: Int, radius: Int) = GradientDrawable().apply {
        setColor(color)
        cornerRadius = dp(radius).toFloat()
    }

    private fun dp(value: Int): Int = (value * resources.displayMetrics.density).toInt()

    private fun matchWrap() = LinearLayout.LayoutParams(
        LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
    )
}
