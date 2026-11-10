package com.quicklock.app

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.app.Activity
import android.app.StatusBarManager
import android.content.ComponentName
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.Icon
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.Gravity
import android.view.HapticFeedbackConstants
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast

class MainActivity : Activity() {

    // ---- palette ----
    private val bg = Color.parseColor("#0A0E14")
    private val surface = Color.parseColor("#131A23")
    private val surface2 = Color.parseColor("#1D2733")
    private val textMain = Color.parseColor("#EAF1F8")
    private val muted = Color.parseColor("#8A9BB0")
    private val accent = Color.parseColor("#2DD4BF")
    private val accent2 = Color.parseColor("#38BDF8")
    private val ok = Color.parseColor("#34D399")
    private val warn = Color.parseColor("#FBBF24")
    private val onAccent = Color.parseColor("#04211E")

    private lateinit var prefs: SharedPreferences
    private lateinit var chip: TextView
    private lateinit var ring: View
    private lateinit var glow: View
    private lateinit var lockButton: FrameLayout
    private lateinit var heroTitle: TextView
    private lateinit var heroSub: TextView
    private lateinit var progress: TextView
    private lateinit var badge1: TextView
    private lateinit var badge2: TextView
    private lateinit var step1Button: Button
    private lateinit var step1Help: TextView
    private lateinit var step2Button: Button
    private var pulse: ObjectAnimator? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prefs = getSharedPreferences("quicklock", MODE_PRIVATE)
        setContentView(buildScreen())
    }

    override fun onResume() {
        super.onResume()
        refresh()
    }

    override fun onPause() {
        stopPulse()
        super.onPause()
    }

    // ------------------------------------------------------------------ UI

    private fun buildScreen(): View {
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(22), dp(24), dp(22), dp(28))
        }

        // top bar
        val top = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }
        top.addView(label("Quick Lock", 20f, textMain, true),
            LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f))
        chip = label("", 12f, ok, true).apply {
            setPadding(dp(12), dp(6), dp(12), dp(6))
        }
        top.addView(chip)
        root.addView(top, matchWrap())

        // hero
        val stage = FrameLayout(this)
        glow = View(this).apply { background = oval(accent); alpha = 0.10f }
        ring = View(this).apply {
            background = GradientDrawable().apply {
                shape = GradientDrawable.OVAL
                setColor(Color.TRANSPARENT)
                setStroke(dp(2), accent)
            }
            alpha = 0f
        }
        lockButton = FrameLayout(this).apply {
            elevation = dp(10).toFloat()
            isClickable = true
            isFocusable = true
            contentDescription = "Lock screen"
            setOnTouchListener { v, e ->
                when (e.actionMasked) {
                    MotionEvent.ACTION_DOWN ->
                        v.animate().scaleX(0.94f).scaleY(0.94f).setDuration(90).start()
                    MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL ->
                        v.animate().scaleX(1f).scaleY(1f).setDuration(120).start()
                }
                false
            }
            setOnClickListener {
                it.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                onLockTapped()
            }
            addView(ImageView(context).apply { setImageResource(R.drawable.ic_quick_lock) },
                FrameLayout.LayoutParams(dp(72), dp(72), Gravity.CENTER))
        }
        stage.addView(glow, FrameLayout.LayoutParams(dp(250), dp(250), Gravity.CENTER))
        stage.addView(ring, FrameLayout.LayoutParams(dp(214), dp(214), Gravity.CENTER))
        stage.addView(lockButton, FrameLayout.LayoutParams(dp(172), dp(172), Gravity.CENTER))
        root.addView(stage, LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, dp(280)).apply { topMargin = dp(12) })

        heroTitle = label("", 26f, textMain, true).apply { gravity = Gravity.CENTER }
        heroSub = label("", 14f, muted).apply {
            gravity = Gravity.CENTER
            setPadding(dp(16), dp(6), dp(16), 0)
        }
        root.addView(heroTitle, matchWrap())
        root.addView(heroSub, matchWrap())

        // setup card
        val card = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            background = rounded(surface, 22)
            setPadding(dp(20), dp(18), dp(20), dp(8))
        }
        val cardHead = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
        cardHead.addView(label("SETUP", 11f, muted, true),
            LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f))
        progress = label("", 11f, muted, true)
        cardHead.addView(progress)
        card.addView(cardHead, matchWrap())

        badge1 = makeBadge()
        step1Button = primaryButton("Open Accessibility settings") {
            QuickLock.openAccessibilitySettings(this)
            Toast.makeText(this, "Find \"Quick Lock\" and turn it on", Toast.LENGTH_LONG).show()
        }
        step1Help = label("Switch greyed out? Open App info, tap ⋮ and choose \"Allow restricted settings\".",
            12f, warn).apply {
            setOnClickListener {
                startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.parse("package:$packageName")))
            }
        }
        card.addView(stepRow(
            badge1,
            "Allow lock access",
            "Quick Lock presses the lock button for you, so fingerprint and face unlock keep working.",
            step1Button, step1Help
        ), matchWrap())

        card.addView(View(this).apply { setBackgroundColor(surface2) },
            LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, dp(1)))

        badge2 = makeBadge()
        step2Button = primaryButton("Add to Quick Settings") { addTile() }
        val step2Desc = if (Build.VERSION.SDK_INT >= 33)
            "Lock straight from the notification shade, without opening the app."
        else
            "Swipe down twice, tap the pencil/Edit icon and drag \"Quick Lock\" into your active tiles."
        if (Build.VERSION.SDK_INT >= 33) {
            card.addView(stepRow(badge2, "Add the tile", step2Desc, step2Button), matchWrap())
        } else {
            card.addView(stepRow(badge2, "Add the tile", step2Desc), matchWrap())
        }
        root.addView(card, LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply { topMargin = dp(26) })

        val footer = label(
            "Tip: long-press the app icon for a “Lock now” shortcut.\nNo internet  •  No ads  •  Never reads your screen",
            12f, Color.parseColor("#62748A")
        ).apply {
            gravity = Gravity.CENTER
            setPadding(0, dp(20), 0, 0)
            setLineSpacing(0f, 1.3f)
        }
        root.addView(footer, matchWrap())

        return ScrollView(this).apply {
            setBackgroundColor(bg)
            isFillViewport = true
            addView(root)
        }
    }

    private fun stepRow(badge: TextView, title: String, desc: String, vararg extras: View): LinearLayout {
        val row = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(0, dp(16), 0, dp(16))
        }
        row.addView(badge, LinearLayout.LayoutParams(dp(30), dp(30)).apply {
            rightMargin = dp(14)
            topMargin = dp(1)
        })
        val col = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        col.addView(label(title, 16f, textMain, true))
        col.addView(label(desc, 13f, muted).apply {
            setPadding(0, dp(4), 0, 0)
            setLineSpacing(0f, 1.15f)
        })
        for (v in extras) {
            val h = if (v is Button) dp(46) else LinearLayout.LayoutParams.WRAP_CONTENT
            col.addView(v, LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, h).apply { topMargin = dp(12) })
        }
        row.addView(col, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f))
        return row
    }

    // --------------------------------------------------------------- state

    private fun refresh() {
        val ready = QuickLock.isServiceEnabled(this)
        val tileDone = prefs.getBoolean("tile_added", false)

        chip.text = if (ready) "●  Ready" else "●  Setup needed"
        chip.setTextColor(if (ready) ok else warn)
        chip.background = rounded(if (ready) Color.parseColor("#12332A") else Color.parseColor("#3A2E10"), 20)

        lockButton.background = if (ready)
            GradientDrawable(GradientDrawable.Orientation.TL_BR, intArrayOf(accent2, accent))
                .apply { shape = GradientDrawable.OVAL }
        else oval(surface2)

        heroTitle.text = if (ready) "Tap to lock" else "Almost there"
        heroSub.text = if (ready)
            "Fingerprint and face unlock work as normal."
        else
            "Finish step 1 below to turn on the lock button."

        setBadge(badge1, "1", ready)
        step1Button.text = if (ready) "Enabled ✓" else "Open Accessibility settings"
        step1Button.isEnabled = !ready
        step1Button.background = rounded(if (ready) surface2 else accent, 14)
        step1Button.setTextColor(if (ready) ok else onAccent)
        step1Help.visibility = if (ready) View.GONE else View.VISIBLE

        val tileShown = Build.VERSION.SDK_INT >= 33
        setBadge(badge2, "2", tileDone)
        step2Button.text = if (tileDone) "Tile added ✓" else "Add to Quick Settings"
        step2Button.isEnabled = !tileDone
        step2Button.background = rounded(if (tileDone) surface2 else accent, 14)
        step2Button.setTextColor(if (tileDone) ok else onAccent)

        val done = (if (ready) 1 else 0) + (if (tileDone && tileShown) 1 else 0)
        progress.text = if (tileShown) "$done OF 2 DONE" else if (ready) "1 OF 1 DONE" else "0 OF 1 DONE"

        if (ready) startPulse() else stopPulse()
    }

    private fun onLockTapped() {
        when {
            QuickLock.lock() -> Unit
            QuickLock.isServiceEnabled(this) ->
                Toast.makeText(this, "Service is starting… try again in a second", Toast.LENGTH_SHORT).show()
            else -> {
                Toast.makeText(this, "Turn on Quick Lock in Accessibility first", Toast.LENGTH_LONG).show()
                QuickLock.openAccessibilitySettings(this)
            }
        }
    }

    private fun addTile() {
        if (Build.VERSION.SDK_INT < 33) return
        val sbm = getSystemService(StatusBarManager::class.java) ?: return
        sbm.requestAddTileService(
            ComponentName(this, QuickLockTileService::class.java),
            getString(R.string.tile_label),
            Icon.createWithResource(this, R.drawable.ic_quick_lock),
            mainExecutor
        ) { result ->
            if (result == StatusBarManager.TILE_ADD_REQUEST_RESULT_TILE_ADDED ||
                result == StatusBarManager.TILE_ADD_REQUEST_RESULT_TILE_ALREADY_ADDED
            ) {
                prefs.edit().putBoolean("tile_added", true).apply()
                refresh()
            }
        }
    }

    // ------------------------------------------------------------ animation

    private fun startPulse() {
        if (pulse?.isRunning == true) return
        pulse = ObjectAnimator.ofPropertyValuesHolder(
            ring,
            PropertyValuesHolder.ofFloat(View.SCALE_X, 0.85f, 1.22f),
            PropertyValuesHolder.ofFloat(View.SCALE_Y, 0.85f, 1.22f),
            PropertyValuesHolder.ofFloat(View.ALPHA, 0.55f, 0f)
        ).apply {
            duration = 2200
            repeatCount = ObjectAnimator.INFINITE
            start()
        }
    }

    private fun stopPulse() {
        pulse?.cancel()
        pulse = null
        if (::ring.isInitialized) ring.alpha = 0f
    }

    // ------------------------------------------------------------- helpers

    private fun label(text: String, size: Float, color: Int, bold: Boolean = false) =
        TextView(this).apply {
            this.text = text
            textSize = size
            setTextColor(color)
            if (bold) setTypeface(null, Typeface.BOLD)
        }

    private fun primaryButton(text: String, onClick: () -> Unit) = Button(this).apply {
        this.text = text
        isAllCaps = false
        textSize = 14f
        setTextColor(onAccent)
        setTypeface(null, Typeface.BOLD)
        background = rounded(accent, 14)
        stateListAnimator = null
        setOnClickListener { onClick() }
    }

    private fun makeBadge() = label("", 14f, textMain, true).apply { gravity = Gravity.CENTER }

    private fun setBadge(b: TextView, number: String, done: Boolean) {
        b.text = if (done) "✓" else number
        b.setTextColor(if (done) onAccent else textMain)
        b.background = oval(if (done) ok else surface2)
    }

    private fun oval(color: Int) = GradientDrawable().apply {
        shape = GradientDrawable.OVAL
        setColor(color)
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
