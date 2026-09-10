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
     * Privacy-safe single-job lookup for a technician's Job Details screen —
     * the SAME guarantee as observeOpenRequestsPublicView(): the return type
     * itself cannot carry phone/exact address/customer name, so there is no
     * code path here that can leak them, regardless of the job's status.
     *
     * BACKEND TODO: the server-side equivalent must be served from a
     * technician-facing endpoint/query that strips private fields at the data
     * layer — never the same endpoint the owning customer uses.
     */
    fun observeOpenJobPublicViewById(jobRequestId: String): Flow<JobRequestPublicView?>

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

    /**
     * Assigns exactly one technician to a job and unlocks private customer
     * data to them (see JobRequest's class doc). Also marks the winning
     * quote SELECTED and every other quote on this job NOT_SELECTED.
     *
     * SAFETY: if the job already has a selectedTechnicianId, this is a no-op —
     * a job can only ever be assigned once, enforced here regardless of what
     * the UI allows the customer to tap.
     *
     * BACKEND TODO: this must be a single atomic server-side transaction
     * (assign + lock quotes) guarded by the same "already assigned?" check,
     * re-validated against the database, not client state.
     */
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

    /** Used to prefill Send/Update Quote and to decide which label to show. */
    suspend fun getMyQuoteForJob(jobRequestId: String, technicianId: String): Quote?

    /**
     * Creates a new quote, OR updates this technician's existing quote for
     * this job if one already exists — enforced by (jobRequestId, technicianId)
     * lookup inside the implementation, not by trusting the id the caller
     * passes in. This is what prevents duplicate quote records.
     *
     * Also advances job status from REQUEST_CREATED/FINDING_PROFESSIONALS to
     * QUOTES_RECEIVED on the first quote for a job.
     */
    suspend fun submitQuote(quote: Quote)
}

