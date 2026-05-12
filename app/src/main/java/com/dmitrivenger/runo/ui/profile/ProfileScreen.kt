package com.dmitrivenger.runo.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.dmitrivenger.runo.R
import com.dmitrivenger.runo.data.preferences.UserPreferences
import com.dmitrivenger.runo.domain.model.RunGoal
import com.dmitrivenger.runo.domain.model.UserProfile
import com.dmitrivenger.runo.ui.components.RunoPrimaryButton
import com.dmitrivenger.runo.ui.components.RunoTextField
import kotlinx.coroutines.launch

private val GOAL_EMOJI = mapOf(
    RunGoal.STAY_ACTIVE to "🏃",
    RunGoal.IMPROVE_PACE to "⚡",
    RunGoal.BUILD_ENDURANCE to "🏔",
    RunGoal.LOSE_WEIGHT to "🔥",
)

@Composable
fun ProfileScreen(
    preferences: UserPreferences,
    onOpenSettings: () -> Unit,
) {
    val profile by preferences.userProfile.collectAsState(initial = UserProfile())
    val scope = rememberCoroutineScope()
    val snackbarState = remember { SnackbarHostState() }
    var showGoalDialog by remember { mutableStateOf(false) }

    var name by remember(profile.name) { mutableStateOf(profile.name) }
    var age by remember(profile.age) { mutableStateOf(if (profile.age > 0) profile.age.toString() else "") }
    var height by remember(profile.heightCm) { mutableStateOf(if (profile.heightCm > 0f) profile.heightCm.toString() else "") }
    var weight by remember(profile.weightKg) { mutableStateOf(if (profile.weightKg > 0f) profile.weightKg.toString() else "") }

    val goalStayActive = stringResource(R.string.goal_stay_active)
    val goalImprovePace = stringResource(R.string.goal_improve_pace)
    val goalBuildEndurance = stringResource(R.string.goal_build_endurance)
    val goalLoseWeight = stringResource(R.string.goal_lose_weight)
    val goalLabels = remember(goalStayActive, goalImprovePace, goalBuildEndurance, goalLoseWeight) {
        mapOf(
            RunGoal.STAY_ACTIVE to goalStayActive,
            RunGoal.IMPROVE_PACE to goalImprovePace,
            RunGoal.BUILD_ENDURANCE to goalBuildEndurance,
            RunGoal.LOSE_WEIGHT to goalLoseWeight,
        )
    }
    val profileSavedMsg = stringResource(R.string.profile_saved)

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(bottom = 32.dp),
        ) {
            item {
                ProfileHeader(
                    profile = profile,
                    goalLabel = goalLabels[profile.goal],
                    onGoalClick = { showGoalDialog = true },
                )
            }

            item {
                Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                    SectionLabel(stringResource(R.string.edit_profile))
                    Spacer(Modifier.height(12.dp))

                    RunoTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = stringResource(R.string.field_name),
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Words,
                            keyboardType = KeyboardType.Text,
                        ),
                    )
                    Spacer(Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        RunoTextField(
                            value = age,
                            onValueChange = { age = it },
                            label = stringResource(R.string.field_age),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                        )
                        RunoTextField(
                            value = height,
                            onValueChange = { height = it },
                            label = stringResource(R.string.field_height),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.weight(1f),
                        )
                    }
                    Spacer(Modifier.height(12.dp))

                    RunoTextField(
                        value = weight,
                        onValueChange = { weight = it },
                        label = stringResource(R.string.field_weight),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    )
                    Spacer(Modifier.height(20.dp))

                    RunoPrimaryButton(
                        text = stringResource(R.string.save_changes),
                        onClick = {
                            scope.launch {
                                preferences.saveProfile(
                                    profile.copy(
                                        name = name.trim(),
                                        age = age.toIntOrNull() ?: profile.age,
                                        heightCm = height.toFloatOrNull() ?: profile.heightCm,
                                        weightKg = weight.toFloatOrNull() ?: profile.weightKg,
                                    )
                                )
                                snackbarState.showSnackbar(profileSavedMsg)
                            }
                        },
                        enabled = name.isNotBlank(),
                    )
                }
            }

            item { Spacer(Modifier.height(28.dp)) }

            item {
                Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                    SectionLabel(stringResource(R.string.section_app))
                    Spacer(Modifier.height(12.dp))
                    SettingsRow(
                        icon = {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant),
                                contentAlignment = Alignment.Center,
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Settings,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(18.dp),
                                )
                            }
                        },
                        title = stringResource(R.string.settings),
                        subtitle = stringResource(R.string.settings_subtitle),
                        onClick = onOpenSettings,
                    )
                }
            }
        }

        SnackbarHost(
            hostState = snackbarState,
            modifier = Modifier.align(Alignment.BottomCenter),
        )
    }

    if (showGoalDialog) {
        AlertDialog(
            onDismissRequest = { showGoalDialog = false },
            title = { Text(stringResource(R.string.select_goal)) },
            text = {
                Column {
                    RunGoal.entries.forEach { goal ->
                        val label = goalLabels[goal] ?: goal.label
                        val emoji = GOAL_EMOJI[goal] ?: ""
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    scope.launch { preferences.saveProfile(profile.copy(goal = goal)) }
                                    showGoalDialog = false
                                }
                                .padding(vertical = 12.dp, horizontal = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(text = emoji, style = MaterialTheme.typography.bodyMedium)
                            Spacer(Modifier.width(12.dp))
                            Text(
                                text = label,
                                style = MaterialTheme.typography.bodyLarge,
                                color = if (profile.goal == goal) MaterialTheme.colorScheme.primary
                                        else MaterialTheme.colorScheme.onSurface,
                            )
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showGoalDialog = false }) {
                    Text(stringResource(R.string.cancel))
                }
            },
        )
    }
}

@Composable
private fun ProfileHeader(
    profile: UserProfile,
    goalLabel: String?,
    onGoalClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(Modifier.height(32.dp))

        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = profile.name.take(1).uppercase().ifBlank { "R" },
                style = MaterialTheme.typography.displaySmall,
                color = MaterialTheme.colorScheme.primary,
            )
        }

        Spacer(Modifier.height(12.dp))

        Text(
            text = profile.name.ifBlank { stringResource(R.string.default_name) },
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
        )

        if (goalLabel != null) {
            val emoji = GOAL_EMOJI[profile.goal] ?: ""
            Spacer(Modifier.height(6.dp))
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(Color.Transparent)
                    .border(1.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(50))
                    .clickable(onClick = onGoalClick)
                    .padding(horizontal = 12.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(text = emoji, style = MaterialTheme.typography.bodyMedium)
                Spacer(Modifier.width(6.dp))
                Text(
                    text = goalLabel,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
        }

        Spacer(Modifier.height(28.dp))
        HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleLarge,
        color = MaterialTheme.colorScheme.onBackground,
    )
}

@Composable
private fun SettingsRow(
    icon: @Composable () -> Unit,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        icon()
        Spacer(Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(20.dp),
        )
    }
}
