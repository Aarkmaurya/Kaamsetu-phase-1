package com.nexora.kaamsetu.feature.admin.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexora.kaamsetu.data.repository.JobRepository
import com.nexora.kaamsetu.data.repository.TechnicianRepository
import com.nexora.kaamsetu.domain.model.JobStatus
import com.nexora.kaamsetu.domain.model.TechnicianStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

data class DashboardCounts(
    val totalTechnicians: Int = 0,
    val pendingApplications: Int = 0,
    val activeJobs: Int = 0,
    val completedJobs: Int = 0
)

class AdminDashboardViewModel(
    technicianRepository: TechnicianRepository,
    jobRepository: JobRepository
) : ViewModel() {

    private val _counts = MutableStateFlow(DashboardCounts())
    val counts: StateFlow<DashboardCounts> = _counts.asStateFlow()

    init {
        viewModelScope.launch {
            technicianRepository.observeAll()
                .combine(jobRepository.observeAllJobs()) { technicians, jobs ->
                    DashboardCounts(
                        totalTechnicians = technicians.size,
                        pendingApplications = technicians.count { it.status == TechnicianStatus.PENDING },
                        activeJobs = jobs.count {
                            it.status != JobStatus.WORK_COMPLETED && it.status != JobStatus.CANCELLED
                        },
                        completedJobs = jobs.count { it.status == JobStatus.WORK_COMPLETED }
                    )
                }
                .collect { _counts.value = it }
        }
    }
}
