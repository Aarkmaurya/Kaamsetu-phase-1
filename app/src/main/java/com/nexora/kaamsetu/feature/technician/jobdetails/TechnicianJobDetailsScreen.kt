package com.nexora.kaamsetu.feature.technician.jobdetails

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import com.nexora.kaamsetu.domain.model.ServiceType
import com.nexora.kaamsetu.domain.model.timingDisplay

@Composable
fun TechnicianJobDetailsScreen(
    jobId: String,
    onSendOrUpdateQuote: (jobId: String) -> Unit
) {
    val container = LocalAppContainer.current
    val viewModel: TechnicianJobDetailsViewModel = viewModel(
        factory = GenericViewModelFactory { TechnicianJobDetailsViewModel(jobId, container.jobRepository) }
    )
    val job by viewModel.job.collectAsState()
    val hasExistingQuote by viewModel.hasExistingQuote.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        val current = job
        if (current == null) {
            ScreenTitle(title = "Job Details")
            Text("This job is no longer available.")
            return@Column
        }

        ScreenTitle(
            title = current.serviceName,
            subtitle = "Customer contact details are shared only after you're selected"
        )

        InfoCard {
            DetailRow("Problem", current.problemDescription)
            DetailRow("Approximate Area", current.approximateArea)
            DetailRow("Preferred Time", current.timingDisplay())
            DetailRow(
                "Service Type",
                if (current.serviceType == ServiceType.HOME_VISIT) "🏠 Home Visit" else "🏪 Visit Service Center"
            )
            DetailRow("Status", current.status.name.replace('_', ' '))
        }

        Button(
            onClick = { onSendOrUpdateQuote(jobId) },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .padding(top = 16.dp)
        ) {
            Text(if (hasExistingQuote) "Update Quote" else "Send Quote")
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Text(text = label, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
    Text(
        text = value,
        style = MaterialTheme.typography.bodyLarge,
        modifier = Modifier.padding(bottom = 10.dp)
    )
}

