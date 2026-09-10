package com.nexora.kaamsetu.feature.technician.jobs

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
import com.nexora.kaamsetu.domain.model.JobRequestPublicView
import com.nexora.kaamsetu.domain.model.ServiceType
import com.nexora.kaamsetu.domain.model.timingDisplay

@Composable
fun TechnicianJobRequestsScreen(onViewJob: (jobId: String) -> Unit) {
    val container = LocalAppContainer.current
    val viewModel: TechnicianJobRequestsViewModel = viewModel(
        factory = GenericViewModelFactory {
            TechnicianJobRequestsViewModel(container.jobRepository, container.technicianRepository)
        }
    )
    val openRequests by viewModel.openRequests.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            ScreenTitle(
                title = "Job Requests",
                subtitle = "Matched to your skills — customer contact details are shared only after you're selected"
            )
        }

        if (openRequests.isEmpty()) {
            item { Text("No matching requests nearby right now.") }
        }

        items(openRequests) { request ->
            JobRequestCard(request = request, onViewJob = { onViewJob(request.id) })
        }
    }
}

@Composable
private fun JobRequestCard(request: JobRequestPublicView, onViewJob: () -> Unit) {
    InfoCard {
        Column {
            Text(text = request.serviceName, style = MaterialTheme.typography.titleMedium)
            Text(text = request.problemDescription, style = MaterialTheme.typography.bodyMedium)
            Text(
                text = "${request.approximateArea} • ${request.timingDisplay()}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = if (request.serviceType == ServiceType.HOME_VISIT) "🏠 Home Visit" else "🏪 Visit Service Center",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "Status: ${request.status.name.replace('_', ' ')}",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(top = 4.dp, bottom = 8.dp)
            )
            Button(onClick = onViewJob, modifier = Modifier.fillMaxWidth()) {
                Text("View Job")
            }
        }
    }
}
