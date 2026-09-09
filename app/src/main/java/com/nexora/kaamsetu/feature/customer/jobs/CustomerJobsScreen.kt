package com.nexora.kaamsetu.feature.customer.jobs

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.nexora.kaamsetu.core.di.GenericViewModelFactory
import com.nexora.kaamsetu.core.di.LocalAppContainer
import com.nexora.kaamsetu.core.theme.InfoCard
import com.nexora.kaamsetu.core.theme.ScreenTitle
import com.nexora.kaamsetu.domain.model.JobRequest
import com.nexora.kaamsetu.domain.model.timingDisplay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun CustomerJobsScreen(
    showSuccessBanner: Boolean = false,
    onBannerDismissed: () -> Unit = {},
    onJobClick: (jobId: String) -> Unit = {}
) {
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

        if (showSuccessBanner) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE6F4EA)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = onBannerDismissed)
                ) {
                    Text(
                        text = "आपकी service request सफलतापूर्वक बनाई गई है",
                        modifier = Modifier.padding(16.dp),
                        color = Color(0xFF1E4620)
                    )
                }
            }
        }

        if (jobs.isEmpty()) {
            item { Text("No requests yet. Create one from Services.") }
        }

        items(jobs) { job ->
            JobCard(job = job, onClick = { onJobClick(job.id) })
        }
    }
}

@Composable
private fun JobCard(job: JobRequest, onClick: () -> Unit) {
    InfoCard {
        Column(modifier = Modifier.clickable(onClick = onClick)) {
            Text(text = job.serviceName, style = MaterialTheme.typography.titleMedium)
            Text(
                text = job.problemDescription,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2
            )
            Text(
                text = job.approximateArea,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = job.timingDisplay(),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "Status: ${job.status.name.replace('_', ' ')}",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(top = 4.dp)
            )
            Text(
                text = formatDate(job.createdAt),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
    }
}

private fun formatDate(epochMillis: Long): String =
    SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()).format(Date(epochMillis))
    
