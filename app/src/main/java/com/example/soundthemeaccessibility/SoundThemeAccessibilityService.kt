package com.example.soundthemeaccessibility

import android.accessibilityservice.AccessibilityService
import android.media.AudioAttributes
import android.media.SoundPool
import android.os.SystemClock
import android.view.accessibility.AccessibilityEvent

private const val PREFERENCES_NAME = "sound_theme_preferences"
private const val THEME_PREFERENCE_KEY = "selected_theme"

class SoundThemeAccessibilityService : AccessibilityService() {
    private lateinit var soundPool: SoundPool
    private val soundIds = mutableMapOf<Pair<String, Cue>, Int>()
    private val loadedSounds = mutableSetOf<Int>()
    private var lastScrollSoundAt = 0L

    override fun onServiceConnected() {
        super.onServiceConnected()

        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_ASSISTANCE_ACCESSIBILITY)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        soundPool = SoundPool.Builder()
            .setMaxStreams(4)
            .setAudioAttributes(audioAttributes)
            .build()
            .also { pool ->
                pool.setOnLoadCompleteListener { _, sampleId, status ->
                    if (status == 0) {
                        loadedSounds += sampleId
                    }
                }
            }

        SoundTheme.entries.forEach { theme ->
            Cue.entries.forEach { cue ->
                val resourceName = "${theme.resourcePrefix}_${cue.resourceName}"
                val resourceId = resources.getIdentifier(resourceName, "raw", packageName)
                if (resourceId != 0) {
                    val sampleId = soundPool.load(this, resourceId, 1)
                    soundIds[theme.id to cue] = sampleId
                }
            }
        }
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null || !::soundPool.isInitialized) return

        val cue = when (event.eventType) {
            AccessibilityEvent.TYPE_VIEW_ACCESSIBILITY_FOCUSED -> Cue.FOCUS
            AccessibilityEvent.TYPE_VIEW_CLICKED -> Cue.CLICK
            AccessibilityEvent.TYPE_VIEW_SCROLLED -> Cue.SCROLL
            AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED -> Cue.WINDOW
            else -> return
        }

        // Rolagem gera muitos eventos seguidos; limita para não "metralhar" o som.
        if (cue == Cue.SCROLL) {
            val now = SystemClock.elapsedRealtime()
            if (now - lastScrollSoundAt < 250) return
            lastScrollSoundAt = now
        }

        val selectedThemeId = getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE)
            .getString(THEME_PREFERENCE_KEY, SoundTheme.DEFAULT.id)
            ?: SoundTheme.DEFAULT.id
        val theme = SoundTheme.entries.firstOrNull { it.id == selectedThemeId }
            ?: SoundTheme.DEFAULT
        val sampleId = soundIds[theme.id to cue] ?: return

        if (sampleId in loadedSounds) {
            soundPool.play(sampleId, 1f, 1f, 1, 0, 1f)
        }
    }

    override fun onInterrupt() = Unit

    override fun onDestroy() {
        if (::soundPool.isInitialized) {
            soundPool.release()
            soundIds.clear()
            loadedSounds.clear()
        }
        super.onDestroy()
    }

    private enum class Cue(val resourceName: String) {
        FOCUS("focus"),
        CLICK("click"),
        SCROLL("scroll"),
        WINDOW("window"),
    }

    private enum class SoundTheme(val id: String, val resourcePrefix: String) {
        DEFAULT("default", "default"),
        PROPHETIC("prophetic", "prophetic"),
        SOFT("soft", "soft"),
        PIANO("piano", "piano"),
        PADS("pads", "pads"),
    }
}
