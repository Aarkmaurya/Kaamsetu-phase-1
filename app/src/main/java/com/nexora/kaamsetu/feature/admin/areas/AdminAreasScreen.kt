package com.nexora.kaamsetu.feature.admin.areas

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nexora.kaamsetu.core.theme.InfoCard
import com.nexora.kaamsetu.core.theme.ScreenTitle

private val demoAreas = listOf("Lucknow", "Kanpur", "Gomti Nagar (Lucknow)", "Indira Nagar (Lucknow)")

@Composable
fun AdminAreasScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        ScreenTitle(title = "Areas", subtitle = "Service cities and areas (add/enable/disable — Phase 2)")
        demoAreas.forEach { area ->
            InfoCard {
                Text(text = area, style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
}

