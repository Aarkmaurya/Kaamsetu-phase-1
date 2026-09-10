package com.nexora.kaamsetu.feature.technician.sendquote

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.nexora.kaamsetu.core.di.GenericViewModelFactory
import com.nexora.kaamsetu.core.di.LocalAppContainer
import com.nexora.kaamsetu.core.theme.ScreenTitle

@Composable
fun SendQuoteScreen(
    jobId: String,
    onQuoteSubmitted: () -> Unit
) {
    val container = LocalAppContainer.current
    val viewModel: SendQuoteViewModel = viewModel(
        factory = GenericViewModelFactory {
            SendQuoteViewModel(jobId, container.technicianRepository, container.jobRepository)
        }
    )
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(state.submitted) {
        if (state.submitted) onQuoteSubmitted()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        ScreenTitle(title = if (state.isUpdating) "Update Quote" else "Send Quote")

        OutlinedTextField(
            value = state.estimatedPrice,
            onValueChange = viewModel::onPriceChange,
            label = { Text("Estimated Price (₹)") },
            placeholder = { Text("500") },
            isError = state.errors.containsKey(SendQuoteField.PRICE),
            supportingText = state.errors[SendQuoteField.PRICE]?.let { message ->
                { Text(text = message, color = MaterialTheme.colorScheme.error) }
            },
            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        Text(text = "Estimated Arrival Time", style = MaterialTheme.typography.titleMedium)
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(ARRIVAL_TIME_OPTIONS) { option ->
                FilterChip(
                    selected = state.estimatedArrivalTime == option,
                    onClick = { viewModel.onArrivalTimeChange(option) },
                    label = { Text(option) }
                )
            }
        }

        OutlinedTextField(
            value = state.message,
            onValueChange = viewModel::onMessageChange,
            label = { Text("Message (optional)") },
            placeholder = { Text("I can reach your location in approximately 30 minutes.") },
            supportingText = { Text("${state.message.length}/$QUOTE_MESSAGE_MAX_LENGTH") },
            minLines = 2,
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = viewModel::submit,
            enabled = !state.isSubmitting,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Text(
                when {
                    state.isSubmitting -> "Submitting..."
                    state.isUpdating -> "Update Quote"
                    else -> "Send Quote"
                }
            )
        }
    }
}

