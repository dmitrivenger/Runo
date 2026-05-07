package com.dmitrivenger.runo.ui.onboarding

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.dmitrivenger.runo.domain.model.RunGoal
import com.dmitrivenger.runo.domain.model.UserProfile
import com.dmitrivenger.runo.ui.components.RunoPrimaryButton
import com.dmitrivenger.runo.ui.components.RunoTextField

private val GOAL_META = mapOf(
    RunGoal.STAY_ACTIVE    to Pair("🏃", "Stay Active"),
    RunGoal.IMPROVE_PACE   to Pair("⚡", "Improve Pace"),
    RunGoal.BUILD_ENDURANCE to Pair("🏔", "Build Endurance"),
    RunGoal.LOSE_WEIGHT    to Pair("🔥", "Lose Weight"),
)

@Composable
fun OnboardingScreen(onComplete: (UserProfile) -> Unit) {
    var step by remember { mutableIntStateOf(0) }
    var goingForward by remember { mutableStateOf(true) }

    var name   by remember { mutableStateOf("") }
    var age    by remember { mutableStateOf("") }
    var height by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }
    var goal   by remember { mutableStateOf(RunGoal.STAY_ACTIVE) }

    val totalSteps = 3

    val canProceed = when (step) {
        0 -> name.isNotBlank()
        1 -> age.toIntOrNull() != null &&
                height.toFloatOrNull() != null &&
                weight.toFloatOrNull() != null
        else -> true
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 24.dp),
    ) {
        // ── Top bar ───────────────────────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (step > 0) {
                IconButton(
                    onClick = {
                        goingForward = false
                        step--
                    },
                    modifier = Modifier.size(40.dp),
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.onBackground,
                    )
                }
            } else {
                Spacer(Modifier.size(40.dp))
            }
            Spacer(Modifier.weight(1f))
            StepDots(current = step, total = totalSteps)
            Spacer(Modifier.weight(1f))
            Spacer(Modifier.size(40.dp))
        }

        Spacer(Modifier.height(40.dp))

        // ── Animated step content ─────────────────────────────────────────────
        AnimatedContent(
            targetState = step,
            transitionSpec = {
                if (goingForward) {
                    slideInHorizontally(tween(300)) { it } togetherWith
                            slideOutHorizontally(tween(300)) { -it }
                } else {
                    slideInHorizontally(tween(300)) { -it } togetherWith
                            slideOutHorizontally(tween(300)) { it }
                }
            },
            label = "stepContent",
            modifier = Modifier.weight(1f),
        ) { currentStep ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
            ) {
                when (currentStep) {
                    0 -> NameStep(name = name, onNameChange = { name = it })
                    1 -> BodyStep(
                        age = age, height = height, weight = weight,
                        onAgeChange = { age = it },
                        onHeightChange = { height = it },
                        onWeightChange = { weight = it },
                    )
                    2 -> GoalStep(selected = goal, onSelect = { goal = it })
                }
            }
        }

        // ── Button ────────────────────────────────────────────────────────────
        RunoPrimaryButton(
            text = if (step < totalSteps - 1) "Continue" else "Start Running",
            onClick = {
                if (step < totalSteps - 1) {
                    goingForward = true
                    step++
                } else {
                    onComplete(
                        UserProfile(
                            name = name.trim(),
                            age = age.toIntOrNull() ?: 0,
                            heightCm = height.toFloatOrNull() ?: 0f,
                            weightKg = weight.toFloatOrNull() ?: 0f,
                            goal = goal,
                        )
                    )
                }
            },
            enabled = canProceed,
        )
        Spacer(Modifier.height(36.dp))
    }
}

// ── Step dots indicator ───────────────────────────────────────────────────────
@Composable
private fun StepDots(current: Int, total: Int) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        repeat(total) { index ->
            val isActive = index == current
            val width by animateFloatAsState(
                targetValue = if (isActive) 24f else 8f,
                animationSpec = tween(300),
                label = "dotWidth$index",
            )
            Box(
                modifier = Modifier
                    .size(width.dp, 8.dp)
                    .clip(RoundedCornerShape(50))
                    .background(
                        if (isActive) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.surfaceVariant
                    )
            )
        }
    }
}

// ── Step 1: Name ──────────────────────────────────────────────────────────────
@Composable
private fun NameStep(name: String, onNameChange: (String) -> Unit) {
    Column {
        Text(
            text = "Let's get to\nknow you",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onBackground,
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = "We'll personalise your experience\naround you.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(Modifier.height(36.dp))
        RunoTextField(
            value = name,
            onValueChange = onNameChange,
            label = "Your name",
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Words,
                keyboardType = KeyboardType.Text,
            ),
        )
    }
}

// ── Step 2: Body stats ────────────────────────────────────────────────────────
@Composable
private fun BodyStep(
    age: String, height: String, weight: String,
    onAgeChange: (String) -> Unit,
    onHeightChange: (String) -> Unit,
    onWeightChange: (String) -> Unit,
) {
    Column {
        Text(
            text = "About you",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onBackground,
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = "Used to calculate accurate calories\nand personalised stats.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(Modifier.height(36.dp))

        // Age — full width
        RunoTextField(
            value = age,
            onValueChange = onAgeChange,
            label = "Age",
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        )
        Spacer(Modifier.height(16.dp))

        // Height + Weight — side by side
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            RunoTextField(
                value = height,
                onValueChange = onHeightChange,
                label = "Height (cm)",
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.weight(1f),
            )
            RunoTextField(
                value = weight,
                onValueChange = onWeightChange,
                label = "Weight (kg)",
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.weight(1f),
            )
        }
    }
}

// ── Step 3: Goal ──────────────────────────────────────────────────────────────
@Composable
private fun GoalStep(selected: RunGoal, onSelect: (RunGoal) -> Unit) {
    Column {
        Text(
            text = "Your goal",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onBackground,
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = "Pick what matters most to you\nright now. You can change it later.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(Modifier.height(32.dp))
        RunGoal.entries.forEach { runGoal ->
            GoalCard(
                runGoal = runGoal,
                isSelected = runGoal == selected,
                onSelect = onSelect,
            )
            Spacer(Modifier.height(12.dp))
        }
    }
}

@Composable
private fun GoalCard(runGoal: RunGoal, isSelected: Boolean, onSelect: (RunGoal) -> Unit) {
    val meta = GOAL_META[runGoal] ?: Pair("🏃", runGoal.label)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(
                if (isSelected)
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                else
                    MaterialTheme.colorScheme.surfaceVariant
            )
            .border(
                width = if (isSelected) 1.5.dp else 0.dp,
                color = if (isSelected)
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(16.dp),
            )
            .clickable { onSelect(runGoal) }
            .padding(horizontal = 20.dp, vertical = 18.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(text = meta.first, style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.width(16.dp))
        Text(
            text = meta.second,
            style = MaterialTheme.typography.titleMedium,
            color = if (isSelected)
                MaterialTheme.colorScheme.primary
            else
                MaterialTheme.colorScheme.onSurface,
        )
    }
}
