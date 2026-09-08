package com.nexora.kaamsetu.feature.technician.active

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexora.kaamsetu.data.repository.JobRepository
import com.nexora.kaamsetu.domain.model.JobRequest
import com.nexora.kaamsetu.domain.model.JobStatus
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

    init {
        viewModelScope.launch {
            // Full record — safe here because the DAO query only returns jobs
            // where selectedTechnicianId == this technician.
            jobRepository.observeJobsForSelectedTechnician(DEMO_TECHNICIAN_ID).collect {
                _activeJobs.value = it
            }
        }
    }

    fun advanceStatus(jobRequestId: String, next: JobStatus) {
        viewModelScope.launch {
            jobRepository.updateStatus(jobRequestId, next)
        }
    }
}

/** Defines the forward path a technician can push a job through once selected. */
fun nextStatusFor(current: JobStatus): JobStatus? = when (current) {
    JobStatus.BOOKING_CONFIRMED -> JobStatus.ON_THE_WAY
    JobStatus.ON_THE_WAY -> JobStatus.ARRIVED
    JobStatus.ARRIVED -> JobStatus.WORK_STARTED
    JobStatus.WORK_STARTED -> JobStatus.WORK_COMPLETED
    else -> null
}
