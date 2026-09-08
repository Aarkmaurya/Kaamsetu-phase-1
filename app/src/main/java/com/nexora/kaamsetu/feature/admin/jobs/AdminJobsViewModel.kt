package com.nexora.kaamsetu.feature.admin.jobs

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexora.kaamsetu.data.repository.JobRepository
import com.nexora.kaamsetu.domain.model.JobRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AdminJobsViewModel(
    jobRepository: JobRepository
) : ViewModel() {

    private val _jobs = MutableStateFlow<List<JobRequest>>(emptyList())
    val jobs: StateFlow<List<JobRequest>> = _jobs.asStateFlow()

    init {
        viewModelScope.launch {
            jobRepository.observeAllJobs().collect { _jobs.value = it }
        }
    }
}
