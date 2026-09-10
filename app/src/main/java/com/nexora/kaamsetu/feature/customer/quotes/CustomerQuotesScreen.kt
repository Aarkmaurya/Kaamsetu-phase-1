package com.nexora.kaamsetu.feature.customer.quotes

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
import com.nexora.kaamsetu.domain.model.Quote

@Composable
fun CustomerQuotesScreen(
    jobId: String,
    onTechnicianSelected: () -> Unit
) {
    val container = LocalAppContainer.current
    val viewModel: CustomerQuotesViewModel = viewModel(
        factory = GenericViewModelFactory { CustomerQuotesViewModel(jobId, container.jobRepository) }
    )
    val job by viewModel.job.collectAsState()
    val quotes by viewModel.quotes.collectAsState()
    val alreadySelectedTechnicianId = job?.selectedTechnicianId

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            ScreenTitle(
                title = "Compare Quotes",
                subtitle = if (quotes.isEmpty()) "No quotes yet — check back soon" else "Newest first"
            )
        }

        items(quotes) { quote ->
            QuoteCard(
                quote = quote,
                isAlreadySelected = alreadySelectedTechnicianId != null,
                isThisOneSelected = alreadySelectedTechnicianId == quote.technicianId,
                onSelect = {
                    viewModel.selectTechnician(quote.technicianId, onDone = onTechnicianSelected)
                }
            )
        }
    }
}

@Composable
private fun QuoteCard(
    quote: Quote,
    isAlreadySelected: Boolean,
    isThisOneSelected: Boolean,
    onSelect: () -> Unit
) {
    InfoCard {
        Column {
            Text(text = quote.technicianName, style = MaterialTheme.typography.titleMedium)
            Text(
                text = "⭐ ${quote.technicianRating}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (quote.technicianVerified) {
                Text(
                    text = "✓ Verified Partner",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Text(
                text = "💰 ₹${quote.estimatedPrice.toInt()}",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(top = 4.dp)
            )
            Text(
                text = "⏱️ ${quote.estimatedArrivalTime}",
                style = MaterialTheme.typography.bodyMedium
            )
            quote.message?.let { message ->
                Text(
                    text = "\"$message\"",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            when {
                isThisOneSelected -> Text(
                    text = "✓ Selected",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(top = 8.dp)
                )
                isAlreadySelected -> Unit // another technician was chosen — no action shown here
                else -> Button(
                    onClick = onSelect,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                ) {
                    Text("Select Technician")
                }
            }
        }
    }
}

