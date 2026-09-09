package com.nexora.kaamsetu.feature.customer.createrequest

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import com.nexora.kaamsetu.domain.model.ServiceTiming
import com.nexora.kaamsetu.domain.model.ServiceType

@Composable
fun CreateServiceRequestScreen(
    serviceId: String,
    onRequestCreated: () -> Unit,
    onBack: () -> Unit
) {
    val container = LocalAppContainer.current
    val viewModel: CreateServiceRequestViewModel = viewModel(
        factory = GenericViewModelFactory {
            CreateServiceRequestViewModel(serviceId, container.serviceCatalogRepository, container.jobRepository)
        }
    )
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(state.submitted) {
        if (state.submitted) onRequestCreated()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        ScreenTitle(title = state.serviceName.ifBlank { "Create Request" })

        OutlinedTextField(
            value = state.problemDescription,
            onValueChange = viewModel::onProblemDescriptionChange,
            label = { Text("Problem Description") },
            placeholder = { Text("मेरे घर का fan काम नहीं कर रहा है") },
            isError = state.errors.containsKey(RequestFormField.PROBLEM_DESCRIPTION),
            supportingText = errorText(state.errors[RequestFormField.PROBLEM_DESCRIPTION]),
            minLines = 3,
            modifier = Modifier.fillMaxWidth()
        )

        Text(text = "Preferred Time", style = MaterialTheme.typography.titleMedium)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(
                selected = state.preferredTime == ServiceTiming.NOW,
                onClick = { viewModel.onPreferredTimeChange(ServiceTiming.NOW) },
                label = { Text("Now") }
            )
            FilterChip(
                selected = state.preferredTime == ServiceTiming.TODAY,
                onClick = { viewModel.onPreferredTimeChange(ServiceTiming.TODAY) },
                label = { Text("Today") }
            )
            FilterChip(
                selected = state.preferredTime == ServiceTiming.SCHEDULED,
                onClick = { viewModel.onPreferredTimeChange(ServiceTiming.SCHEDULED) },
                label = { Text("Schedule for Later") }
            )
        }

        if (state.preferredTime == ServiceTiming.SCHEDULED) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = state.scheduledDate,
                    onValueChange = viewModel::onScheduledDateChange,
                    label = { Text("Date") },
                    placeholder = { Text("2026-09-10") },
                    isError = state.errors.containsKey(RequestFormField.SCHEDULED_DATE),
                    supportingText = errorText(state.errors[RequestFormField.SCHEDULED_DATE]),
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = state.scheduledTime,
                    onValueChange = viewModel::onScheduledTimeChange,
                    label = { Text("Time") },
                    placeholder = { Text("18:30") },
                    isError = state.errors.containsKey(RequestFormField.SCHEDULED_TIME),
                    supportingText = errorText(state.errors[RequestFormField.SCHEDULED_TIME]),
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Text(text = "Service Type", style = MaterialTheme.typography.titleMedium)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(
                selected = state.serviceType == ServiceType.HOME_VISIT,
                onClick = { viewModel.onServiceTypeChange(ServiceType.HOME_VISIT) },
                label = { Text("🏠 Home Visit") }
            )
            FilterChip(
                selected = state.serviceType == ServiceType.SERVICE_CENTER,
                onClick = { viewModel.onServiceTypeChange(ServiceType.SERVICE_CENTER) },
                label = { Text("🏪 Visit Service Center") }
            )
        }

        if (state.serviceType == ServiceType.HOME_VISIT) {
            OutlinedTextField(
                value = state.approximateArea,
                onValueChange = viewModel::onApproximateAreaChange,
                label = { Text("Approximate Area") },
                placeholder = { Text("Lambhua, Sultanpur") },
                isError = state.errors.containsKey(RequestFormField.APPROXIMATE_AREA),
                supportingText = errorText(state.errors[RequestFormField.APPROXIMATE_AREA]),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = state.exactAddress,
                onValueChange = viewModel::onExactAddressChange,
                label = { Text("Exact Address (kept private)") },
                isError = state.errors.containsKey(RequestFormField.EXACT_ADDRESS),
                supportingText = errorText(state.errors[RequestFormField.EXACT_ADDRESS]),
                minLines = 2,
                modifier = Modifier.fillMaxWidth()
            )
        }

        OutlinedTextField(
            value = state.customerName,
            onValueChange = viewModel::onCustomerNameChange,
            label = { Text("Your Name") },
            isError = state.errors.containsKey(RequestFormField.CUSTOMER_NAME),
            supportingText = errorText(state.errors[RequestFormField.CUSTOMER_NAME]),
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = state.customerPhone,
            onValueChange = viewModel::onCustomerPhoneChange,
            label = { Text("Phone Number (kept private)") },
            placeholder = { Text("98765 43210") },
            isError = state.errors.containsKey(RequestFormField.CUSTOMER_PHONE),
            supportingText = errorText(state.errors[RequestFormField.CUSTOMER_PHONE]),
            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Phone),
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = viewModel::submit,
            enabled = !state.isSubmitting,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Text(if (state.isSubmitting) "Submitting..." else "Find Professionals")
        }
    }
}

@Composable
private fun errorText(message: String?): (@Composable () -> Unit)? {
    if (message == null) return null
    return {
        Text(text = message, color = MaterialTheme.colorScheme.error)
    }
}

