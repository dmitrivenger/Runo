package com.dmitrivenger.runo.ui.onboarding

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material.icons.outlined.Height
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dmitrivenger.runo.R
import com.dmitrivenger.runo.domain.model.RunGoal
import com.dmitrivenger.runo.domain.model.UserProfile
import com.dmitrivenger.runo.ui.components.RunoPrimaryButton
import com.dmitrivenger.runo.ui.theme.Brand_DeepGreen
import com.dmitrivenger.runo.ui.theme.Light_BackgroundCream
import com.dmitrivenger.runo.ui.theme.Light_MutedGray

private val GOAL_META = mapOf(
    RunGoal.STAY_ACTIVE     to Pair("🏃", "Stay Active"),
    RunGoal.IMPROVE_PACE    to Pair("⚡", "Improve Pace"),
    RunGoal.BUILD_ENDURANCE to Pair("🏔", "Build Endurance"),
    RunGoal.LOSE_WEIGHT     to Pair("🔥", "Lose Weight"),
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

    val totalSteps = 2

    val canProceed = when (step) {
        0 -> name.isNotBlank() &&
                age.toIntOrNull() != null &&
                height.toFloatOrNull() != null &&
                weight.toFloatOrNull() != null
        else -> true
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Mountain illustration — full-screen background
        Image(
            painter = painterResource(R.drawable.welcome_mountains),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
        )

        // Cream gradient overlay — covers top portion for text legibility
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.65f)
                .align(Alignment.TopCenter)
                .background(
                    brush = Brush.verticalGradient(
                        colorStops = arrayOf(
                            0f   to Light_BackgroundCream,
                            0.80f to Light_BackgroundCream,
                            1f   to Color.Transparent,
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 24.dp),
        ) {
            // ── Top bar ───────────────────────────────────────────────────────
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
                            tint = Brand_DeepGreen,
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

            Spacer(Modifier.height(32.dp))

            // ── Animated step content ─────────────────────────────────────────
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
                        0 -> ProfileStep(
                            name = name, age = age, height = height, weight = weight,
                            onNameChange = { name = it },
                            onAgeChange = { age = it },
                            onHeightChange = { height = it },
                            onWeightChange = { weight = it },
                        )
                        1 -> GoalStep(selected = goal, onSelect = { goal = it })
                    }
                }
            }

            // ── Continue / Start button ───────────────────────────────────────
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
                        if (isActive) Brand_DeepGreen
                        else Brand_DeepGreen.copy(alpha = 0.25f)
                    )
            )
        }
    }
}

// ── Step 0: Profile (name + body stats) ──────────────────────────────────────
@Composable
private fun ProfileStep(
    name: String, age: String, height: String, weight: String,
    onNameChange: (String) -> Unit,
    onAgeChange: (String) -> Unit,
    onHeightChange: (String) -> Unit,
    onWeightChange: (String) -> Unit,
) {
    Column {
        // Title
        Text(
            text = "Let's get to",
            style = MaterialTheme.typography.headlineLarge.copy(
                fontWeight = FontWeight.Normal,
                fontSize = 32.sp,
            ),
            color = Brand_DeepGreen,
        )
        Text(
            text = "know you",
            style = MaterialTheme.typography.displaySmall.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 40.sp,
            ),
            color = Brand_DeepGreen,
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = "This helps us personalize\nyour experience.",
            style = MaterialTheme.typography.bodyLarge,
            color = Light_MutedGray,
        )
        Spacer(Modifier.height(28.dp))

        ProfileFieldCard(
            icon = Icons.Outlined.Person,
            label = "Name",
            value = name,
            onValueChange = onNameChange,
            placeholder = "Your name",
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Words,
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next,
            ),
        )
        Spacer(Modifier.height(12.dp))
        ProfileFieldCard(
            icon = null,
            ageLabel = age,
            label = "Age",
            value = age,
            onValueChange = onAgeChange,
            placeholder = "Years",
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next,
            ),
        )
        Spacer(Modifier.height(12.dp))
        ProfileFieldCard(
            icon = Icons.Outlined.Height,
            label = "Height",
            value = height,
            onValueChange = onHeightChange,
            placeholder = "cm",
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Decimal,
                imeAction = ImeAction.Next,
            ),
        )
        Spacer(Modifier.height(12.dp))
        ProfileFieldCard(
            icon = Icons.Outlined.FitnessCenter,
            label = "Weight",
            value = weight,
            onValueChange = onWeightChange,
            placeholder = "kg",
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Decimal,
                imeAction = ImeAction.Done,
            ),
        )
    }
}

@Composable
private fun ProfileFieldCard(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    keyboardOptions: KeyboardOptions,
    icon: ImageVector? = null,
    ageLabel: String? = null,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .padding(horizontal = 16.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Left icon area
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Light_BackgroundCream),
            contentAlignment = Alignment.Center,
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Brand_DeepGreen,
                    modifier = Modifier.size(22.dp),
                )
            } else if (ageLabel != null) {
                // Age: circle with number
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape)
                        .border(1.5.dp, Brand_DeepGreen, CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = if (ageLabel.isNotBlank()) ageLabel.take(2) else "?",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                        color = Brand_DeepGreen,
                    )
                }
            }
        }

        Spacer(Modifier.width(14.dp))

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label, style = MaterialTheme.typography.labelMedium) },
            placeholder = { Text(placeholder, style = MaterialTheme.typography.bodyLarge, color = Light_MutedGray) },
            singleLine = true,
            keyboardOptions = keyboardOptions,
            modifier = Modifier.weight(1f),
            textStyle = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.SemiBold,
                color = Brand_DeepGreen,
            ),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent,
                focusedLabelColor = Light_MutedGray,
                unfocusedLabelColor = Light_MutedGray,
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
            ),
        )
    }
}

// ── Step 1: Goal ──────────────────────────────────────────────────────────────
@Composable
private fun GoalStep(selected: RunGoal, onSelect: (RunGoal) -> Unit) {
    Column {
        Text(
            text = "What's your",
            style = MaterialTheme.typography.headlineLarge.copy(
                fontWeight = FontWeight.Normal,
                fontSize = 32.sp,
            ),
            color = Brand_DeepGreen,
        )
        Text(
            text = "main goal?",
            style = MaterialTheme.typography.displaySmall.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 40.sp,
            ),
            color = Brand_DeepGreen,
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = "Pick what matters most to you\nright now. You can change it later.",
            style = MaterialTheme.typography.bodyLarge,
            color = Light_MutedGray,
        )
        Spacer(Modifier.height(28.dp))
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
                if (isSelected) Brand_DeepGreen.copy(alpha = 0.10f) else Color.White
            )
            .border(
                width = if (isSelected) 1.5.dp else 0.dp,
                color = if (isSelected) Brand_DeepGreen else Color.Transparent,
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
            color = if (isSelected) Brand_DeepGreen else Brand_DeepGreen.copy(alpha = 0.75f),
        )
    }
}
