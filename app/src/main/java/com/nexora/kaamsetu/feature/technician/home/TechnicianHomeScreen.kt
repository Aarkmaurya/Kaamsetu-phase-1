package com.nexora.kaamsetu.feature.technician.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.nexora.kaamsetu.core.di.GenericViewModelFactory
import com.nexora.kaamsetu.core.di.LocalAppContainer
import com.nexora.kaamsetu.core.theme.InfoCard
import com.nexora.kaamsetu.core.theme.ScreenTitle

@Composable
fun TechnicianHomeScreen() {
    val container = LocalAppContainer.current
    val viewModel: TechnicianHomeViewModel = viewModel(
        factory = GenericViewModelFactory { TechnicianHomeViewModel(container.technicianRepository) }
    )
    val technician by viewModel.technician.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        ScreenTitle(title = "Welcome back", subtitle = technician?.name ?: "")

        InfoCard {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "Availability", style = MaterialTheme.typography.titleMedium)
                    Text(
                        text = if (technician?.isAvailable == true) "You are Online" else "You are Offline",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Switch(
                    checked = technician?.isAvailable == true,
                    onCheckedChange = { viewModel.toggleAvailability() },
                    enabled = technician?.status?.name == "APPROVED"
                )
            }
        }

        InfoCard {
            Text(text = "Rating: ${technician?.rating ?: "-"}", style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "Completed jobs: ${technician?.completedJobs ?: 0}",
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = "Status: ${technician?.status?.name ?: "-"}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

