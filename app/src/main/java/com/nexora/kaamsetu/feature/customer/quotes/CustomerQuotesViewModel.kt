package com.nexora.kaamsetu.feature.customer.quotes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexora.kaamsetu.data.repository.JobRepository
import com.nexora.kaamsetu.domain.model.JobRequest
import com.nexora.kaamsetu.domain.model.Quote
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CustomerQuotesViewModel(
    private val jobId: String,
    private val jobRepository: JobRepository
) : ViewModel() {

    // Full record is appropriate here — this screen only ever runs for the
    // OWNING customer's own job (see JobDetailsScreen -> View Quotes).
    private val _job = MutableStateFlow<JobRequest?>(null)
    val job: StateFlow<JobRequest?> = _job.asStateFlow()

    // DAO already orders by createdAt DESC ("newest first"), per the spec.
    private val _quotes = MutableStateFlow<List<Quote>>(emptyList())
    val quotes: StateFlow<List<Quote>> = _quotes.asStateFlow()

    init {
        viewModelScope.launch {
            jobRepository.observeJobById(jobId).collect { _job.value = it }
        }
        viewModelScope.launch {
            jobRepository.observeQuotesForJob(jobId).collect { _quotes.value = it }
        }
    }

    /**
     * BACKEND TODO: as with JobRepository.selectTechnician, the server must
     * re-check the job hasn't already been assigned before honoring this —
     * this ViewModel just relays the tap.
     */
    fun selectTechnician(technicianId: String, onDone: () -> Unit) {
        viewModelScope.launch {
            jobRepository.selectTechnician(jobId, technicianId)
            onDone()
        }
    }
}
