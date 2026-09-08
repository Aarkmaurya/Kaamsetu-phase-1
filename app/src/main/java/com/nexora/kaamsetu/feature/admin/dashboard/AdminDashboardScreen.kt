package com.nexora.kaamsetu.feature.admin.dashboard

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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

@Composable
fun AdminDashboardScreen() {
    val container = LocalAppContainer.current
    val viewModel: AdminDashboardViewModel = viewModel(
        factory = GenericViewModelFactory {
            AdminDashboardViewModel(container.technicianRepository, container.jobRepository)
        }
    )
    val counts by viewModel.counts.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        ScreenTitle(title = "Dashboard")

        DashboardStat("Total Technicians", counts.totalTechnicians)
        DashboardStat("Pending Applications", counts.pendingApplications)
        DashboardStat("Active Jobs", counts.activeJobs)
        DashboardStat("Completed Jobs", counts.completedJobs)
    }
}

@Composable
private fun DashboardStat(label: String, value: Int) {
    InfoCard {
        Text(text = label, style = MaterialTheme.typography.bodyMedium)
        Text(
            text = value.toString(),
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

