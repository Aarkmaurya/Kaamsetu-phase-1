package com.nexora.kaamsetu.feature.customer.jobs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.nexora.kaamsetu.core.di.GenericViewModelFactory
import com.nexora.kaamsetu.core.di.LocalAppContainer
import com.nexora.kaamsetu.core.theme.InfoCard
import com.nexora.kaamsetu.core.theme.ScreenTitle

@Composable
fun CustomerJobsScreen() {
    val container = LocalAppContainer.current
    val viewModel: CustomerJobsViewModel = viewModel(
        factory = GenericViewModelFactory { CustomerJobsViewModel(container.jobRepository) }
    )
    val jobs by viewModel.jobs.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item { ScreenTitle(title = "My Jobs", subtitle = "Track your service requests") }

        if (jobs.isEmpty()) {
            item { Text("No requests yet. Create one from Services.") }
        }

        items(jobs) { job ->
            InfoCard {
                Column {
                    Text(text = job.problemDescription, style = MaterialTheme.typography.titleMedium)
                    Text(
                        text = job.approximateArea,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Status: ${job.status.name.replace('_', ' ')}",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }
    }
}

