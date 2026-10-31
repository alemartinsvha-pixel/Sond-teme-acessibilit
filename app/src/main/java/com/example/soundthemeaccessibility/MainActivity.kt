package com.example.soundthemeaccessibility

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp

private const val PREFERENCES_NAME = "sound_theme_preferences"
private const val THEME_PREFERENCE_KEY = "selected_theme"

private val soundThemes = listOf(
    "default" to "Padrão",
    "prophetic" to "Profético",
    "soft" to "Suave",
    "piano" to "Piano",
    "pads" to "Pads",
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val preferences = getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE)
        val initialTheme = preferences.getString(THEME_PREFERENCE_KEY, "default") ?: "default"

        setContent {
            var selectedTheme by remember { mutableStateOf(initialTheme) }

            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .safeDrawingPadding()
                            .verticalScroll(rememberScrollState())
                            .padding(24.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                    ) {
                        Text(
                            text = "SoundTheme Accessibility",
                            style = MaterialTheme.typography.headlineSmall,
                        )
                        Text(
                            text = "Escolha um único pacote de sons para ativar:",
                            style = MaterialTheme.typography.bodyLarge,
                        )
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .selectableGroup(),
                        ) {
                            soundThemes.forEach { (key, label) ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .selectable(
                                            selected = selectedTheme == key,
                                            role = Role.RadioButton,
                                            onClick = {
                                            selectedTheme = key
                                            preferences.edit()
                                                .putString(THEME_PREFERENCE_KEY, key)
                                                .apply()
                                            },
                                        )
                                        .padding(vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    RadioButton(
                                        selected = selectedTheme == key,
                                        onClick = null,
                                    )
                                    Text(text = label)
                                }
                            }
                        }
                        Button(
                            onClick = {
                                startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
                            },
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Text("Ativar serviço de acessibilidade")
                        }
                    }
                }
            }
        }
    }
}
