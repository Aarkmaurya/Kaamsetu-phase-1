package com.nexora.kaamsetu.feature.technician.active

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
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
import com.nexora.kaamsetu.domain.model.JobStatus
import com.nexora.kaamsetu.domain.model.timingDisplay

@Composable
fun TechnicianActiveJobsScreen() {
    val container = LocalAppContainer.current
    val viewModel: TechnicianActiveJobsViewModel = viewModel(
        factory = GenericViewModelFactory { TechnicianActiveJobsViewModel(container.jobRepository) }
    )
    val activeJobs by viewModel.activeJobs.collectAsState()
    val selectedQuoteByJobId by viewModel.selectedQuoteByJobId.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item { ScreenTitle(title = "Active Jobs", subtitle = "Jobs you've been selected for") }

        if (activeJobs.isEmpty()) {
            item { Text("No active jobs yet.") }
        }

        items(activeJobs) { job ->
            InfoCard {
                Column {
                    Text(text = job.serviceName, style = MaterialTheme.typography.titleMedium)
                    Text(text = job.problemDescription, style = MaterialTheme.typography.bodyMedium)

                    // Unlocked only because this job's selectedTechnicianId is
                    // this technician — see JobRequest's class doc and
                    // JobRepository.observeJobsForSelectedTechnician.
                    Text(
                        text = "Customer: ${job.customerName}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "Address: ${job.exactAddress ?: "Visit Service Center"}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "Phone: ${job.customerPhone}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "Approximate Area: ${job.approximateArea}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Preferred Time: ${job.timingDisplay()}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    selectedQuoteByJobId[job.id]?.let { quote ->
                        Text(
                            text = "Agreed Price: ₹${quote.estimatedPrice.toInt()}",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }

                    Text(
                        text = "Status: ${job.status.name.replace('_', ' ')}",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(top = 4.dp, bottom = 8.dp)
                    )

                    val next: JobStatus? = nextStatusFor(job.status)
                    if (next != null) {
                        Button(
                            onClick = { viewModel.advanceStatus(job.id, next) },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Mark as ${next.name.replace('_', ' ')}")
                        }
                    }
                }
            }
        }
    }
}
