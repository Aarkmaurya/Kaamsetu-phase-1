package com.nexora.kaamsetu.feature.technician.jobs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.nexora.kaamsetu.core.di.GenericViewModelFactory
import com.nexora.kaamsetu.core.di.LocalAppContainer
import com.nexora.kaamsetu.core.theme.InfoCard
import com.nexora.kaamsetu.core.theme.ScreenTitle
import com.nexora.kaamsetu.domain.model.JobRequestPublicView
import com.nexora.kaamsetu.domain.model.timingDisplay

@Composable
fun TechnicianJobRequestsScreen() {
    val container = LocalAppContainer.current
    val viewModel: TechnicianJobRequestsViewModel = viewModel(
        factory = GenericViewModelFactory { TechnicianJobRequestsViewModel(container.jobRepository) }
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
                subtitle = "Customer contact details are shared only after you're selected"
            )
        }

        if (openRequests.isEmpty()) {
            item { Text("No open requests nearby right now.") }
        }

        items(openRequests) { request ->
            JobRequestCard(
                request = request,
                onSendQuote = { price, eta -> viewModel.submitQuote(request.id, price, eta) }
            )
        }
    }
}

@Composable
private fun JobRequestCard(
    request: JobRequestPublicView,
    onSendQuote: (price: Double, etaMinutes: Int) -> Unit
) {
    var price by remember { mutableStateOf("") }
    var eta by remember { mutableStateOf("") }
    var quoteSent by remember { mutableStateOf(false) }

    InfoCard {
        Text(text = request.serviceName, style = MaterialTheme.typography.titleMedium)
        Text(text = request.problemDescription, style = MaterialTheme.typography.bodyMedium)
        Text(
            text = "${request.approximateArea} • ${request.timingDisplay()}",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        if (quoteSent) {
            Text(
                text = "Quote sent",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(top = 8.dp)
            )
        } else {
            Row(modifier = Modifier.padding(top = 8.dp)) {
                OutlinedTextField(
                    value = price,
                    onValueChange = { price = it },
                    label = { Text("Price ₹") },
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = eta,
                    onValueChange = { eta = it },
                    label = { Text("ETA (min)") },
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 8.dp)
                )
            }
            Button(
                onClick = {
                    val priceValue = price.toDoubleOrNull()
                    val etaValue = eta.toIntOrNull()
                    if (priceValue != null && etaValue != null) {
                        onSendQuote(priceValue, etaValue)
                        quoteSent = true
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            ) {
                Text("Send Quote")
            }
        }
    }
}

