package com.nexora.kaamsetu.data.repository

import com.nexora.kaamsetu.domain.model.JobRequest
import com.nexora.kaamsetu.domain.model.JobRequestPublicView
import com.nexora.kaamsetu.domain.model.Quote
import com.nexora.kaamsetu.domain.model.ServiceCategory
import com.nexora.kaamsetu.domain.model.Technician
import kotlinx.coroutines.flow.Flow

interface ServiceCatalogRepository {
    fun observeCategories(): Flow<List<ServiceCategory>>
}

interface TechnicianRepository {
    fun observeAll(): Flow<List<Technician>>
    fun observeAvailableApproved(): Flow<List<Technician>>
    suspend fun approve(technicianId: String)
    suspend fun reject(technicianId: String)
    suspend fun suspend(technicianId: String)
    suspend fun setAvailability(technicianId: String, isAvailable: Boolean)
}

interface JobRepository {
    /** Full records — used by the customer who owns the job and by admin. */
    fun observeJobsForCustomer(customerId: String): Flow<List<JobRequest>>
    fun observeAllJobs(): Flow<List<JobRequest>>

    /**
     * Privacy-safe view for technicians who have NOT been selected yet.
     * This is the enforcement point for "no phone/address before selection".
     */
    fun observeOpenRequestsPublicView(): Flow<List<JobRequestPublicView>>

    /** Full record — only returned for jobs where this technician is the selectedTechnicianId. */
    fun observeJobsForSelectedTechnician(technicianId: String): Flow<List<JobRequest>>

    suspend fun createJobRequest(request: JobRequest)
    suspend fun selectTechnician(jobRequestId: String, technicianId: String)
    suspend fun updateStatus(jobRequestId: String, status: com.nexora.kaamsetu.domain.model.JobStatus)

    fun observeQuotesForJob(jobRequestId: String): Flow<List<Quote>>
    suspend fun submitQuote(quote: Quote)
}
