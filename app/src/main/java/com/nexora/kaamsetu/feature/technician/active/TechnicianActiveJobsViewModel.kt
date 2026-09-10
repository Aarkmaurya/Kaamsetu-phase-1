package com.nexora.kaamsetu.feature.technician.active

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexora.kaamsetu.data.repository.JobRepository
import com.nexora.kaamsetu.domain.model.JobRequest
import com.nexora.kaamsetu.domain.model.JobStatus
import com.nexora.kaamsetu.domain.model.Quote
import com.nexora.kaamsetu.feature.technician.DEMO_TECHNICIAN_ID
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TechnicianActiveJobsViewModel(
    private val jobRepository: JobRepository
) : ViewModel() {

    private val _activeJobs = MutableStateFlow<List<JobRequest>>(emptyList())
    val activeJobs: StateFlow<List<JobRequest>> = _activeJobs.asStateFlow()

    /** This technician's own (now-SELECTED) quote for each active job, keyed by jobId — used to show the agreed price. */
    private val _selectedQuoteByJobId = MutableStateFlow<Map<String, Quote>>(emptyMap())
    val selectedQuoteByJobId: StateFlow<Map<String, Quote>> = _selectedQuoteByJobId.asStateFlow()

    init {
        viewModelScope.launch {
            // Full record — safe here because the DAO query only returns jobs
            // where selectedTechnicianId == this technician.
            jobRepository.observeJobsForSelectedTechnician(DEMO_TECHNICIAN_ID).collect { jobs ->
                _activeJobs.value = jobs
                val quotesById = mutableMapOf<String, Quote>()
                jobs.forEach { job ->
                    jobRepository.getMyQuoteForJob(job.id, DEMO_TECHNICIAN_ID)?.let { quote ->
                        quotesById[job.id] = quote
                    }
                }
                _selectedQuoteByJobId.value = quotesById
            }
        }
    }

    fun advanceStatus(jobRequestId: String, next: JobStatus) {
        viewModelScope.launch {
            jobRepository.updateStatus(jobRequestId, next)
        }
    }
}

/**
 * Defines the forward path a technician can push a job through once selected.
 * Phase 3 only guarantees TECHNICIAN_SELECTED is reached correctly; the
 * ON_THE_WAY -> WORK_COMPLETED chain was already built in Phase 1 and is left
 * wired up here rather than removed, per "do not remove working functionality".
 */
fun nextStatusFor(current: JobStatus): JobStatus? = when (current) {
    JobStatus.TECHNICIAN_SELECTED -> JobStatus.ON_THE_WAY
    JobStatus.BOOKING_CONFIRMED -> JobStatus.ON_THE_WAY
    JobStatus.ON_THE_WAY -> JobStatus.ARRIVED
    JobStatus.ARRIVED -> JobStatus.WORK_STARTED
    JobStatus.WORK_STARTED -> JobStatus.WORK_COMPLETED
    else -> null
}

