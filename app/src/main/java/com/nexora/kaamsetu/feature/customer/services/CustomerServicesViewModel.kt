package com.nexora.kaamsetu.feature.customer.services

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexora.kaamsetu.data.repository.ServiceCatalogRepository
import com.nexora.kaamsetu.domain.model.ServiceCategory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CustomerServicesViewModel(
    private val serviceCatalogRepository: ServiceCatalogRepository
) : ViewModel() {

    private val _categories = MutableStateFlow<List<ServiceCategory>>(emptyList())
    val categories: StateFlow<List<ServiceCategory>> = _categories.asStateFlow()

    init {
        viewModelScope.launch {
            serviceCatalogRepository.observeCategories().collect { _categories.value = it }
        }
    }
}
