package com.nexora.kaamsetu.feature.customer.jobs

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexora.kaamsetu.core.session.SessionManager
import com.nexora.kaamsetu.data.repository.JobRepository
import com.nexora.kaamsetu.domain.model.JobRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * The current customer id. Phase 4B.01: now sourced from the active login
 * session instead of a hardcoded value, falling back to the Phase 1-3 seeded
 * demo customer only if somehow no session is active.
 */
val DEMO_CUSTOMER_ID: String
    get() = SessionManager.currentUserId ?: "cust_demo"

class CustomerJobsViewModel(
    private val jobRepository: JobRepository
) : ViewModel() {

    private val _jobs = MutableStateFlow<List<JobRequest>>(emptyList())
    val jobs: StateFlow<List<JobRequest>> = _jobs.asStateFlow()

    init {
        viewModelScope.launch {
            jobRepository.observeJobsForCustomer(DEMO_CUSTOMER_ID).collect { _jobs.value = it }
        }
    }
}
