package com.nexora.kaamsetu.feature.technician.jobs

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexora.kaamsetu.data.repository.JobRepository
import com.nexora.kaamsetu.data.repository.TechnicianRepository
import com.nexora.kaamsetu.domain.model.JobRequestPublicView
import com.nexora.kaamsetu.feature.technician.DEMO_TECHNICIAN_ID
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

/**
 * Phase 3: shows only jobs whose requested service matches one of this
 * technician's skills — simple client-side matching for the offline MVP
 * (skills list vs job.serviceId). A real backend could do this matching
 * server-side (e.g. a SQL join or search index) without changing how this
 * ViewModel or its screen consume the result.
 */
class TechnicianJobRequestsViewModel(
    private val jobRepository: JobRepository,
    private val technicianRepository: TechnicianRepository
) : ViewModel() {

    private val _openRequests = MutableStateFlow<List<JobRequestPublicView>>(emptyList())
    val openRequests: StateFlow<List<JobRequestPublicView>> = _openRequests.asStateFlow()

    init {
        viewModelScope.launch {
            // Only ever exposes the public view — phone/exact address/customer
            // name are not part of JobRequestPublicView, enforcing the privacy
            // rule structurally rather than by convention.
            combine(
                jobRepository.observeOpenRequestsPublicView(),
                technicianRepository.observeAll()
            ) { openJobs, technicians ->
                val me = technicians.firstOrNull { it.id == DEMO_TECHNICIAN_ID }
                if (me == null) {
                    emptyList()
                } else {
                    openJobs.filter { job -> job.serviceId in me.skills }
                }
            }.collect { _openRequests.value = it }
        }
    }
}

