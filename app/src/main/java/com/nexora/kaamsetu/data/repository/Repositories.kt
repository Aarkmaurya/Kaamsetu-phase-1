package com.nexora.kaamsetu.data.repository

import com.nexora.kaamsetu.domain.model.JobRequest
import com.nexora.kaamsetu.domain.model.JobRequestPublicView
import com.nexora.kaamsetu.domain.model.JobStatus
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
     * Single full job record, for the customer's own Job Details screen.
     *
     * BACKEND TODO: once a real API exists, this call must be authorized
     * server-side by checking the requester is either the job's customerId,
     * its selectedTechnicianId, or an admin — a client asking for a jobId it
     * doesn't own must be rejected by the server, not just "not shown" by the UI.
     */
    fun observeJobById(jobRequestId: String): Flow<JobRequest?>

    /**
     * Privacy-safe view for technicians who have NOT been selected yet.
     * This is the enforcement point for "no phone/address before selection".
     *
     * BACKEND TODO: the server-side equivalent of this query must be the ONLY
     * way an unselected technician's request can read job data — it must never
     * fall back to a full-record endpoint for convenience.
     */
    fun observeOpenRequestsPublicView(): Flow<List<JobRequestPublicView>>

    /**
     * Full record — only returned for jobs where this technician is the
     * selectedTechnicianId.
     *
     * BACKEND TODO: server must re-verify selectedTechnicianId == the
     * authenticated technician's id on every call, not just at selection time.
     */
    fun observeJobsForSelectedTechnician(technicianId: String): Flow<List<JobRequest>>

    suspend fun createJobRequest(request: JobRequest)
    suspend fun selectTechnician(jobRequestId: String, technicianId: String)

    /**
     * BACKEND TODO: status transitions must be validated server-side (e.g. a
     * customer can only move REQUEST_CREATED -> CANCELLED, a technician can only
     * advance BOOKING_CONFIRMED -> ... -> WORK_COMPLETED for jobs they were
     * selected for). The client-side checks in ViewModels are a UX convenience
     * only and must not be trusted as the actual authorization boundary.
     */
    suspend fun updateStatus(jobRequestId: String, status: JobStatus)

    fun observeQuotesForJob(jobRequestId: String): Flow<List<Quote>>
    suspend fun submitQuote(quote: Quote)
}

