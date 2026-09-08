package com.nexora.kaamsetu.feature.admin.technicians

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import com.nexora.kaamsetu.domain.model.Technician
import com.nexora.kaamsetu.domain.model.TechnicianStatus

@Composable
fun AdminTechniciansScreen() {
    val container = LocalAppContainer.current
    val viewModel: AdminTechniciansViewModel = viewModel(
        factory = GenericViewModelFactory { AdminTechniciansViewModel(container.technicianRepository) }
    )
    val technicians by viewModel.technicians.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item { ScreenTitle(title = "Technicians", subtitle = "Review applications and manage partners") }

        items(technicians) { technician ->
            TechnicianRow(
                technician = technician,
                onApprove = { viewModel.approve(technician.id) },
                onReject = { viewModel.reject(technician.id) },
                onSuspend = { viewModel.suspend(technician.id) }
            )
        }
    }
}

@Composable
private fun TechnicianRow(
    technician: Technician,
    onApprove: () -> Unit,
    onReject: () -> Unit,
    onSuspend: () -> Unit
) {
    InfoCard {
        Text(text = technician.name, style = MaterialTheme.typography.titleMedium)
        Text(
            text = "${technician.city} • ${technician.skills.joinToString()}",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = "Status: ${technician.status.name}",
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(top = 4.dp, bottom = 8.dp)
        )

        when (technician.status) {
            TechnicianStatus.PENDING -> Row(modifier = Modifier.fillMaxWidth()) {
                Button(onClick = onApprove, modifier = Modifier.weight(1f)) { Text("Approve") }
                OutlinedButton(
                    onClick = onReject,
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 8.dp)
                ) { Text("Reject") }
            }

            TechnicianStatus.APPROVED -> OutlinedButton(
                onClick = onSuspend,
                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                modifier = Modifier.fillMaxWidth()
            ) { Text("Suspend") }

            else -> Unit
        }
    }
}

