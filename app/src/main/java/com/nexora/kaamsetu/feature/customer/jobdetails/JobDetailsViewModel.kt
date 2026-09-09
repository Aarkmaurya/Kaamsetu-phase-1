package com.nexora.kaamsetu.feature.customer.jobdetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexora.kaamsetu.data.repository.JobRepository
import com.nexora.kaamsetu.domain.model.JobRequest
import com.nexora.kaamsetu.domain.model.JobStatus
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

    init {
        viewModelScope.launch {
            jobRepository.observeJobById(jobId).collect { _job.value = it }
        }
    }

    /**
     * Phase 2 only allows cancelling from REQUEST_CREATED, checked here for UX.
     * BACKEND TODO: this guard must be re-enforced server-side — see the
     * updateStatus() doc comment on JobRepository.
     */
    fun cancelRequest() {
        val current = _job.value ?: return
        if (current.status != JobStatus.REQUEST_CREATED) return
        viewModelScope.launch {
            jobRepository.updateStatus(current.id, JobStatus.CANCELLED)
        }
    }
}
