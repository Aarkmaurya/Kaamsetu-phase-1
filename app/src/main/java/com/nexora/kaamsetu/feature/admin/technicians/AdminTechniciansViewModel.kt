package com.nexora.kaamsetu.feature.admin.technicians

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexora.kaamsetu.data.repository.TechnicianRepository
import com.nexora.kaamsetu.domain.model.Technician
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AdminTechniciansViewModel(
    private val technicianRepository: TechnicianRepository
) : ViewModel() {

    private val _technicians = MutableStateFlow<List<Technician>>(emptyList())
    val technicians: StateFlow<List<Technician>> = _technicians.asStateFlow()

    init {
        viewModelScope.launch {
            technicianRepository.observeAll().collect { _technicians.value = it }
        }
    }

    fun approve(id: String) = viewModelScope.launch { technicianRepository.approve(id) }
    fun reject(id: String) = viewModelScope.launch { technicianRepository.reject(id) }
    fun suspend(id: String) = viewModelScope.launch { technicianRepository.suspend(id) }
}
