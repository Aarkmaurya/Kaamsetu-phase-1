package com.nexora.kaamsetu.feature.customer.jobdetails

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import com.nexora.kaamsetu.domain.model.ServiceType
import com.nexora.kaamsetu.domain.model.timingDisplay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun JobDetailsScreen(
    jobId: String,
    onViewQuotes: (jobId: String) -> Unit = {}
) {
    val container = LocalAppContainer.current
    val viewModel: JobDetailsViewModel = viewModel(
        factory = GenericViewModelFactory { JobDetailsViewModel(jobId, container.jobRepository) }
    )
    val job by viewModel.job.collectAsState()
    val selectedQuote by viewModel.selectedQuote.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        val current = job
        if (current == null) {
            ScreenTitle(title = "Job Details")
            Text("Loading...")
            return@Column
        }

        ScreenTitle(title = current.serviceName, subtitle = "Requested on ${formatDate(current.createdAt)}")

        InfoCard {
            DetailRow("Problem", current.problemDescription)
            DetailRow("Preferred Time", current.timingDisplay())
            DetailRow(
                "Service Type",
                if (current.serviceType == ServiceType.HOME_VISIT) "Home Visit" else "Visit Service Center"
            )
            DetailRow("Approximate Area", current.approximateArea)
            if (current.serviceType == ServiceType.HOME_VISIT) {
                // Shown here because this is the OWNING customer's own detail
                // screen — see JobRequest's class doc for why this must stay
                // hidden from technicians until they are selected.
                DetailRow("Exact Address", current.exactAddress ?: "-")
            }
            DetailRow("Your Name", current.customerName)
            DetailRow("Your Phone", current.customerPhone)
            DetailRow("Status", current.status.name.replace('_', ' '))
        }

        if (current.status == JobStatus.QUOTES_RECEIVED) {
            Button(
                onClick = { onViewQuotes(jobId) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .padding(top = 16.dp)
            ) {
                Text("View Quotes")
            }
        }

        val quote = selectedQuote
        if (current.status == JobStatus.TECHNICIAN_SELECTED && quote != null) {
            InfoCard {
                Column {
                    Text(text = "Selected Technician", style = MaterialTheme.typography.titleMedium)
                    Text(text = quote.technicianName, style = MaterialTheme.typography.bodyLarge)
                    Text(
                        text = "⭐ ${quote.technicianRating}" + if (quote.technicianVerified) " • ✓ Verified Partner" else "",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Agreed Price: ₹${quote.estimatedPrice.toInt()}",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                    Text(
                        text = "Estimated Arrival: ${quote.estimatedArrivalTime}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        if (current.status == JobStatus.REQUEST_CREATED || current.status == JobStatus.FINDING_PROFESSIONALS) {
            Button(
                onClick = viewModel::cancelRequest,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .padding(top = 16.dp)
            ) {
                Text("Cancel Request")
            }
        }

        if (current.status == JobStatus.CANCELLED) {
            Text(
                text = "This request was cancelled.",
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 16.dp)
            )
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

private fun formatDate(epochMillis: Long): String =
    SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()).format(Date(epochMillis))
    
