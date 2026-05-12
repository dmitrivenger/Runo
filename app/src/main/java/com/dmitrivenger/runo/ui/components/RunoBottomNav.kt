package com.dmitrivenger.runo.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector  // used by NavItem's icon parameter
import androidx.compose.ui.unit.dp
import com.dmitrivenger.runo.ui.theme.Brand_DeepGreen
import com.dmitrivenger.runo.ui.theme.Brand_White
import com.dmitrivenger.runo.ui.theme.Light_CardSurface

enum class BottomNavTab { HOME, STATS, ACTIVITY, PROFILE }

@Composable
fun RunoBottomNav(
    currentTab: BottomNavTab,
    onTabSelected: (BottomNavTab) -> Unit,
    onRunClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // Outer box — reports its height to Scaffold so content scrolls above the pill.
    // The run circle overflows upward via offset(); since it's in a higher z-layer than
    // the screen content, it renders on top without being hidden behind content.
    Box(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(start = 24.dp, end = 24.dp, bottom = 16.dp),
    ) {
        // ── Pill bar ─────────────────────────────────────────────────────────
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp),
            shape = RoundedCornerShape(40.dp),
            color = Light_CardSurface,
            shadowElevation = 8.dp,
            tonalElevation = 0.dp,
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                NavItem(
                    icon     = Icons.Filled.Home,
                    isActive = currentTab == BottomNavTab.HOME,
                    label    = "Home",
                    onClick  = { onTabSelected(BottomNavTab.HOME) },
                )
                NavItem(
                    icon     = Icons.Filled.BarChart,
                    isActive = currentTab == BottomNavTab.STATS,
                    label    = "Stats",
                    onClick  = { onTabSelected(BottomNavTab.STATS) },
                )
                // Empty centre slot — Run button floats above this space
                Spacer(Modifier.width(56.dp))
                NavItem(
                    icon     = Icons.Filled.Description,
                    isActive = currentTab == BottomNavTab.ACTIVITY,
                    label    = "Activity",
                    onClick  = { onTabSelected(BottomNavTab.ACTIVITY) },
                )
                NavItem(
                    icon     = Icons.Filled.Person,
                    isActive = currentTab == BottomNavTab.PROFILE,
                    label    = "Profile",
                    onClick  = { onTabSelected(BottomNavTab.PROFILE) },
                )
            }
        }

        // ── Run button — white circle floating half above the pill ────────────
        // offset(y = -28.dp): moves the circle up by its own radius so its
        // centre aligns with the pill's top edge.
        Box(
            modifier = Modifier
                .size(56.dp)
                .align(Alignment.TopCenter)
                .offset(y = (-28).dp)
                .shadow(elevation = 6.dp, shape = CircleShape)
                .clip(CircleShape)
                .background(Color.White)
                .clickable { onRunClick() },
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Filled.DirectionsRun,
                contentDescription = "Start Run",
                tint = Color(0xFF1A73E8),
                modifier = Modifier.size(28.dp),
            )
        }
    }
}

@Composable
private fun NavItem(
    icon: ImageVector,
    isActive: Boolean,
    label: String,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50.dp))
            .background(if (isActive) Brand_DeepGreen else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(
                horizontal = if (isActive) 16.dp else 12.dp,
                vertical = 10.dp,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (isActive) Brand_White else Brand_DeepGreen,
            modifier = Modifier
                .size(24.dp)
                .then(if (!isActive) Modifier.alpha(0.5f) else Modifier),
        )
    }
}
