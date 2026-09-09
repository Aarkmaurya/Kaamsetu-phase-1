package com.nexora.kaamsetu.feature.customer.services

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
fun CustomerServicesScreen(
    initialQuery: String = "",
    onCategorySelected: (categoryId: String) -> Unit
) {
    val container = LocalAppContainer.current
    val viewModel: CustomerServicesViewModel = viewModel(
        factory = GenericViewModelFactory {
            CustomerServicesViewModel(container.serviceCatalogRepository, initialQuery)
        }
    )
    val searchQuery by viewModel.searchQuery.collectAsState()
    val filteredCategories by viewModel.filteredCategories.collectAsState()

    // Grouped view when not searching (browsing), flat results while searching
    // (dynamic filtering reads better as a single ranked list than as groups).
    val grouped = if (searchQuery.isBlank()) filteredCategories.groupBy { it.group } else emptyMap()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        item {
            ScreenTitle(title = "Services", subtitle = "Choose a category to raise a request")
            OutlinedTextField(
                value = searchQuery,
                onValueChange = viewModel::onSearchQueryChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                singleLine = true,
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                placeholder = { Text("कोई service खोजें...") }
            )
        }

        if (searchQuery.isNotBlank()) {
            if (filteredCategories.isEmpty()) {
                item { Text("No services match \"$searchQuery\".") }
            }
            items(filteredCategories) { category ->
                ServiceRow(name = category.name, group = category.group) {
                    onCategorySelected(category.id)
                }
            }
        } else {
            grouped.forEach { (group, items) ->
                item {
                    Text(text = group, style = MaterialTheme.typography.titleMedium)
                }
                items(items) { category ->
                    ServiceRow(name = category.name, group = null) {
                        onCategorySelected(category.id)
                    }
                }
            }
        }
    }
}

@Composable
private fun ServiceRow(name: String, group: String?, onClick: () -> Unit) {
    InfoCard {
        Text(
            text = name,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
        )
        if (group != null) {
            Text(
                text = group,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

