package com.nexora.kaamsetu.feature.customer.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nexora.kaamsetu.core.theme.ScreenTitle

private data class PopularService(val id: String, val label: String)

/**
 * Demo shortcut list mapped onto existing seeded category ids. "Mechanic" is a
 * deliberate MVP simplification pointing at bike_mechanic — the full Services
 * screen still lists Bike Mechanic and Car Mechanic separately.
 */
private val popularServices = listOf(
    PopularService("electrician", "Electrician"),
    PopularService("plumber", "Plumber"),
    PopularService("carpenter", "Carpenter"),
    PopularService("ac_repair", "AC Repair"),
    PopularService("mobile_repair", "Mobile Repair"),
    PopularService("bike_mechanic", "Mechanic"),
    PopularService("home_cleaning", "Cleaning"),
    PopularService("locksmith", "Locksmith")
)

@Composable
fun CustomerHomeScreen(
    onBrowseServices: () -> Unit,
    onServiceSelected: (categoryId: String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        ScreenTitle(title = "नमस्ते! आज आपको किस काम में मदद चाहिए?")

        // Search bar acts as an entry point into the Services screen, which
        // owns the actual dynamic search/filtering logic.
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onBrowseServices),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Filled.Search,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "कोई service खोजें...",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(start = 12.dp)
                )
            }
        }

        Text(text = "Popular Services", style = MaterialTheme.typography.titleMedium)

        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.height(180.dp)
        ) {
            items(popularServices) { service ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onServiceSelected(service.id) },
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp, horizontal = 4.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = service.label,
                            style = MaterialTheme.typography.labelLarge,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            }
        }

        OutlinedButton(
            onClick = onBrowseServices,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
        ) {
            Text("View All Services")
        }
    }
}
