package com.nexora.kaamsetu.feature.technician.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexora.kaamsetu.data.repository.TechnicianRepository
import com.nexora.kaamsetu.domain.model.Technician
import com.nexora.kaamsetu.feature.technician.DEMO_TECHNICIAN_ID
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TechnicianHomeViewModel(
    private val technicianRepository: TechnicianRepository
) : ViewModel() {

    private val _technician = MutableStateFlow<Technician?>(null)
    val technician: StateFlow<Technician?> = _technician.asStateFlow()

    init {
        viewModelScope.launch {
            technicianRepository.observeAll().collect { all ->
                _technician.value = all.firstOrNull { it.id == DEMO_TECHNICIAN_ID }
            }
        }
    }

    fun toggleAvailability() {
        val current = _technician.value ?: return
        viewModelScope.launch {
            technicianRepository.setAvailability(DEMO_TECHNICIAN_ID, !current.isAvailable)
        }
    }
}
