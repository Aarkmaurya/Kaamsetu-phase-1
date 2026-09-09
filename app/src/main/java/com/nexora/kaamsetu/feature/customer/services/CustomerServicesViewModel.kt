package com.nexora.kaamsetu.feature.customer.services

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexora.kaamsetu.data.repository.ServiceCatalogRepository
import com.nexora.kaamsetu.domain.model.ServiceCategory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class CustomerServicesViewModel(
    private val serviceCatalogRepository: ServiceCatalogRepository,
    initialQuery: String = ""
) : ViewModel() {

    private val _categories = MutableStateFlow<List<ServiceCategory>>(emptyList())
    val categories: StateFlow<List<ServiceCategory>> = _categories.asStateFlow()

    private val _searchQuery = MutableStateFlow(initialQuery)
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    /** Dynamically filtered by [searchQuery] — updates as the user types, per Phase 2 requirements. */
    private val _filteredCategories = MutableStateFlow<List<ServiceCategory>>(emptyList())
    val filteredCategories: StateFlow<List<ServiceCategory>> = _filteredCategories.asStateFlow()

    init {
        viewModelScope.launch {
            serviceCatalogRepository.observeCategories().collect { _categories.value = it }
        }
        viewModelScope.launch {
            _categories.combine(_searchQuery) { categories, query ->
                if (query.isBlank()) {
                    categories
                } else {
                    categories.filter { it.name.contains(query, ignoreCase = true) }
                }
            }.collect { _filteredCategories.value = it }
        }
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }
}

