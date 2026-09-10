package com.nexora.kaamsetu.feature.customer.jobdetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexora.kaamsetu.data.repository.JobRepository
import com.nexora.kaamsetu.domain.model.JobRequest
import com.nexora.kaamsetu.domain.model.JobStatus
import com.nexora.kaamsetu.domain.model.Quote
import com.nexora.kaamsetu.domain.model.QuoteStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class JobDetailsViewModel(
    private val jobId: String,
    private val jobRepository: JobRepository
) : ViewModel() {

    private val _job = MutableStateFlow<JobRequest?>(null)
    val job: StateFlow<JobRequest?> = _job.asStateFlow()

    /** The winning quote, once one exists — drives the "Selected Technician" card in Job Details. */
    private val _selectedQuote = MutableStateFlow<Quote?>(null)
    val selectedQuote: StateFlow<Quote?> = _selectedQuote.asStateFlow()

    init {
        viewModelScope.launch {
            jobRepository.observeJobById(jobId).collect { _job.value = it }
        }
        viewModelScope.launch {
            jobRepository.observeQuotesForJob(jobId).collect { quotes ->
                _selectedQuote.value = quotes.firstOrNull { it.status == QuoteStatus.SELECTED }
            }
        }
    }

    /**
     * Phase 2 allowed cancelling only from REQUEST_CREATED. Phase 3 widens
     * this to also allow cancelling from FINDING_PROFESSIONALS — both are
     * "no quotes yet" states in this MVP (see JobRepository doc comments) —
     * but NOT once quotes exist or a technician is selected.
     * BACKEND TODO: this guard must be re-enforced server-side — see the
     * updateStatus() doc comment on JobRepository.
     */
    fun cancelRequest() {
        val current = _job.value ?: return
        if (current.status != JobStatus.REQUEST_CREATED && current.status != JobStatus.FINDING_PROFESSIONALS) return
        viewModelScope.launch {
            jobRepository.updateStatus(current.id, JobStatus.CANCELLED)
        }
    }
}

