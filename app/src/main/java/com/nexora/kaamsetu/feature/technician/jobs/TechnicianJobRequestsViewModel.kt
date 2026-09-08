package com.nexora.kaamsetu.feature.technician.jobs

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexora.kaamsetu.data.repository.JobRepository
import com.nexora.kaamsetu.domain.model.JobRequestPublicView
import com.nexora.kaamsetu.domain.model.Quote
import com.nexora.kaamsetu.feature.technician.DEMO_TECHNICIAN_ID
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class TechnicianJobRequestsViewModel(
    private val jobRepository: JobRepository
) : ViewModel() {

    private val _openRequests = MutableStateFlow<List<JobRequestPublicView>>(emptyList())
    val openRequests: StateFlow<List<JobRequestPublicView>> = _openRequests.asStateFlow()

    init {
        viewModelScope.launch {
            // Only ever exposes the public view — phone/exact address are not
            // part of JobRequestPublicView, enforcing the privacy rule.
            jobRepository.observeOpenRequestsPublicView().collect { _openRequests.value = it }
        }
    }

    fun submitQuote(jobRequestId: String, price: Double, etaMinutes: Int) {
        viewModelScope.launch {
            jobRepository.submitQuote(
                Quote(
                    id = "quote_${UUID.randomUUID()}",
                    jobRequestId = jobRequestId,
                    technicianId = DEMO_TECHNICIAN_ID,
                    price = price,
                    etaMinutes = etaMinutes
                )
            )
        }
    }
}
