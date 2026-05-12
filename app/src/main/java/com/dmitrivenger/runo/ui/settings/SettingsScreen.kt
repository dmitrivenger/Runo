package com.dmitrivenger.runo.ui.settings

import android.app.Activity
import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.dmitrivenger.runo.R
import com.dmitrivenger.runo.data.preferences.UserPreferences
import com.dmitrivenger.runo.domain.model.UserProfile
import com.dmitrivenger.runo.ui.components.RunoToggle
import com.dmitrivenger.runo.ui.components.RunoTopBar
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(
    preferences: UserPreferences,
    onBack: () -> Unit,
) {
    val profile by preferences.userProfile.collectAsState(initial = UserProfile())
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    fun save(updated: UserProfile) {
        scope.launch { preferences.saveProfile(updated) }
    }

    fun switchLanguage(lang: String) {
        scope.launch { preferences.saveProfile(profile.copy(language = lang)) }
        context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
            .edit().putString("language", lang).apply()
        (context as? Activity)?.recreate()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding(),
    ) {
        RunoTopBar(onBack = onBack)

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .navigationBarsPadding(),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
        ) {
            item {
                Text(
                    text = stringResource(R.string.settings),
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(start = 4.dp, bottom = 28.dp),
                )
            }

            // ── Run tracking ─────────────────────────────────────────────────
            item {
                SettingsSection(title = stringResource(R.string.section_run_tracking)) {
                    Text(
                        text = stringResource(R.string.distance_units),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Spacer(Modifier.height(10.dp))
                    ChipRow(
                        options = listOf(
                            stringResource(R.string.unit_kilometres) to true,
                            stringResource(R.string.unit_miles) to false,
                        ),
                        selected = profile.useMetricUnits,
                        onSelect = { save(profile.copy(useMetricUnits = it)) },
                    )
                }
                Spacer(Modifier.height(16.dp))
            }

            // ── Voice coach ──────────────────────────────────────────────────
            item {
                SettingsSection(title = stringResource(R.string.section_voice_coach)) {
                    RunoToggle(
                        label = stringResource(R.string.voice_feedback),
                        description = stringResource(R.string.voice_feedback_desc),
                        checked = profile.voiceFeedbackEnabled,
                        onCheckedChange = { save(profile.copy(voiceFeedbackEnabled = it)) },
                    )
                    AnimatedVisibility(
                        visible = profile.voiceFeedbackEnabled,
                        enter = expandVertically(),
                        exit = shrinkVertically(),
                    ) {
                        Column {
                            HorizontalDivider(
                                modifier = Modifier.padding(vertical = 14.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant,
                            )
                            Text(
                                text = stringResource(R.string.announce_every),
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                            )
                            Spacer(Modifier.height(10.dp))
                            ChipRow(
                                options = listOf(
                                    stringResource(R.string.interval_500m) to 500,
                                    stringResource(R.string.interval_1km) to 1000,
                                    stringResource(R.string.interval_2km) to 2000,
                                    stringResource(R.string.interval_5km) to 5000,
                                ),
                                selected = profile.voiceIntervalMeters,
                                onSelect = { save(profile.copy(voiceIntervalMeters = it)) },
                            )
                        }
                    }
                }
                Spacer(Modifier.height(16.dp))
            }

            // ── Appearance ───────────────────────────────────────────────────
            item {
                SettingsSection(title = stringResource(R.string.section_appearance)) {
                    RunoToggle(
                        label = stringResource(R.string.dark_mode),
                        description = stringResource(R.string.dark_mode_desc),
                        checked = profile.darkMode,
                        onCheckedChange = { save(profile.copy(darkMode = it)) },
                    )
                }
                Spacer(Modifier.height(16.dp))
            }

            // ── Language ─────────────────────────────────────────────────────
            item {
                SettingsSection(title = stringResource(R.string.section_language)) {
                    ChipRow(
                        options = listOf(
                            stringResource(R.string.lang_english) to "en",
                            stringResource(R.string.lang_russian) to "ru",
                        ),
                        selected = profile.language,
                        onSelect = { lang -> switchLanguage(lang) },
                    )
                }
                Spacer(Modifier.height(32.dp))
            }

            // ── Version ──────────────────────────────────────────────────────
            item {
                val versionName = try {
                    context.packageManager.getPackageInfo(context.packageName, 0).versionName
                } catch (_: Exception) {
                    "1.0"
                }
                Text(
                    text = stringResource(R.string.version_label, versionName ?: "1.0"),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(start = 4.dp),
                )
            }
        }
    }
}

@Composable
private fun SettingsSection(title: String, content: @Composable () -> Unit) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(start = 4.dp, bottom = 10.dp),
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp),
        ) {
            content()
        }
    }
}

@Composable
private fun <T> ChipRow(
    options: List<Pair<String, T>>,
    selected: T,
    onSelect: (T) -> Unit,
) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        options.forEach { (label, value) ->
            val isSelected = value == selected
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                color = if (isSelected) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(
                        if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                        else MaterialTheme.colorScheme.surfaceVariant,
                    )
                    .border(
                        width = if (isSelected) 1.dp else 0.dp,
                        color = if (isSelected) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(50),
                    )
                    .clickable { onSelect(value) }
                    .padding(horizontal = 16.dp, vertical = 8.dp),
            )
        }
    }
}
