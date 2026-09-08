package com.nexora.kaamsetu.feature.admin.services

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.nexora.kaamsetu.feature.customer.services.CustomerServicesViewModel

@Composable
fun AdminServicesScreen() {
    val container = LocalAppContainer.current
    // Reuses the same read-only category stream as the customer Services screen;
    // admin-only add/edit/disable actions are planned for Phase 2.
    val viewModel: CustomerServicesViewModel = viewModel(
        factory = GenericViewModelFactory { CustomerServicesViewModel(container.serviceCatalogRepository) }
    )
    val categories by viewModel.categories.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        item {
            ScreenTitle(title = "Services", subtitle = "Manage the service catalog (add/edit — Phase 2)")
        }
        items(categories) { category ->
            InfoCard {
                Text(text = category.name, style = MaterialTheme.typography.bodyLarge)
                Text(
                    text = category.group,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

