package com.nexora.kaamsetu.feature.technician.jobdetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexora.kaamsetu.data.repository.JobRepository
import com.nexora.kaamsetu.domain.model.JobRequestPublicView
import com.nexora.kaamsetu.feature.technician.DEMO_TECHNICIAN_ID
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TechnicianJobDetailsViewModel(
    jobId: String,
    private val jobRepository: JobRepository
) : ViewModel() {

    private val _job = MutableStateFlow<JobRequestPublicView?>(null)
    val job: StateFlow<JobRequestPublicView?> = _job.asStateFlow()

    private val _hasExistingQuote = MutableStateFlow(false)
    val hasExistingQuote: StateFlow<Boolean> = _hasExistingQuote.asStateFlow()

    init {
        viewModelScope.launch {
            // Privacy-safe by construction — see JobRepository.observeOpenJobPublicViewById.
            jobRepository.observeOpenJobPublicViewById(jobId).collect { _job.value = it }
        }
        viewModelScope.launch {
            _hasExistingQuote.value = jobRepository.getMyQuoteForJob(jobId, DEMO_TECHNICIAN_ID) != null
        }
    }
}
