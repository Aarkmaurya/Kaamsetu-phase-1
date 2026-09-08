package com.nexora.kaamsetu.feature.customer.services

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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

@Composable
fun CustomerServicesScreen(onCategorySelected: (categoryId: String) -> Unit) {
    val container = LocalAppContainer.current
    val viewModel: CustomerServicesViewModel = viewModel(
        factory = GenericViewModelFactory { CustomerServicesViewModel(container.serviceCatalogRepository) }
    )
    val categories by viewModel.categories.collectAsState()
    val grouped = categories.groupBy { it.group }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        item {
            ScreenTitle(title = "Services", subtitle = "Choose a category to raise a request")
        }
        grouped.forEach { (group, items) ->
            item {
                Text(text = group, style = MaterialTheme.typography.titleMedium)
            }
            items(items) { category ->
                InfoCard {
                    Text(
                        text = category.name,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onCategorySelected(category.id) }
                    )
                }
            }
        }
    }
}

