package com.nexora.kaamsetu.feature.customer.jobs

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexora.kaamsetu.data.repository.JobRepository
import com.nexora.kaamsetu.domain.model.JobRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/** Demo customer id — replaced by the real logged-in user id once real auth exists. */
const val DEMO_CUSTOMER_ID = "cust_demo"

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
