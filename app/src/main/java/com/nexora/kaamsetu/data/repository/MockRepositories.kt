package com.nexora.kaamsetu.data.repository

import com.nexora.kaamsetu.data.local.CustomerDao
import com.nexora.kaamsetu.data.local.JobRequestDao
import com.nexora.kaamsetu.data.local.JobRequestEntity
import com.nexora.kaamsetu.data.local.QuoteDao
import com.nexora.kaamsetu.data.local.QuoteEntity
import com.nexora.kaamsetu.data.local.ServiceCategoryDao
import com.nexora.kaamsetu.data.local.TechnicianDao
import com.nexora.kaamsetu.data.local.TechnicianEntity
import com.nexora.kaamsetu.domain.model.JobRequest
import com.nexora.kaamsetu.domain.model.JobRequestPublicView
import com.nexora.kaamsetu.domain.model.JobStatus
import com.nexora.kaamsetu.domain.model.Quote
import com.nexora.kaamsetu.domain.model.QuoteStatus
import com.nexora.kaamsetu.domain.model.ServiceCategory
import com.nexora.kaamsetu.domain.model.ServiceType
import com.nexora.kaamsetu.domain.model.ServiceTiming
import com.nexora.kaamsetu.domain.model.Technician
import com.nexora.kaamsetu.domain.model.TechnicianStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class MockServiceCatalogRepository(
    private val dao: ServiceCategoryDao
) : ServiceCatalogRepository {

    override fun observeCategories(): Flow<List<ServiceCategory>> =
        dao.observeAll().map { list -> list.map { ServiceCategory(it.id, it.group, it.name) } }
}

class MockTechnicianRepository(
    private val dao: TechnicianDao
) : TechnicianRepository {

    override fun observeAll(): Flow<List<Technician>> =
        dao.observeAll().map { list -> list.map { it.toDomain() } }

    override fun observeAvailableApproved(): Flow<List<Technician>> =
        dao.observeAvailableApproved().map { list -> list.map { it.toDomain() } }

    override suspend fun approve(technicianId: String) = setStatus(technicianId, "APPROVED")
    override suspend fun reject(technicianId: String) = setStatus(technicianId, "REJECTED")
    override suspend fun suspend(technicianId: String) = setStatus(technicianId, "SUSPENDED")

    override suspend fun setAvailability(technicianId: String, isAvailable: Boolean) {
        val current = dao.getById(technicianId) ?: return
        dao.update(current.copy(isAvailable = isAvailable))
    }

    private suspend fun setStatus(technicianId: String, status: String) {
        val current = dao.getById(technicianId) ?: return
        dao.update(current.copy(status = status))
    }

    private fun TechnicianEntity.toDomain() = Technician(
        id = id,
        name = name,
        phone = phone,
        status = TechnicianStatus.valueOf(status),
        skills = skillsCsv.split(",").filter { it.isNotBlank() },
        city = city,
        rating = rating,
        completedJobs = completedJobs,
        isVerified = isVerified,
        isAvailable = isAvailable
    )
}

class MockJobRepository(
    private val jobDao: JobRequestDao,
    private val quoteDao: QuoteDao,
    private val customerDao: CustomerDao
) : JobRepository {

    override fun observeJobsForCustomer(customerId: String): Flow<List<JobRequest>> =
        jobDao.observeForCustomer(customerId).map { list -> list.map { it.toDomain() } }

    override fun observeAllJobs(): Flow<List<JobRequest>> =
        jobDao.observeAll().map { list -> list.map { it.toDomain() } }

    override fun observeOpenJobPublicViewById(jobRequestId: String): Flow<JobRequestPublicView?> =
        jobDao.observeById(jobRequestId).map { it?.toDomain()?.publicView() }

    override fun observeOpenRequestsPublicView(): Flow<List<JobRequestPublicView>> =
        jobDao.observeOpenRequests().map { list -> list.map { it.toDomain().publicView() } }

    override fun observeJobsForSelectedTechnician(technicianId: String): Flow<List<JobRequest>> =
        jobDao.observeForSelectedTechnician(technicianId).map { list -> list.map { it.toDomain() } }

    override suspend fun createJobRequest(request: JobRequest) {
        jobDao.insertAll(listOf(request.toEntity()))
    }

    override suspend fun selectTechnician(jobRequestId: String, technicianId: String) {
        val existing = jobDao.getById(jobRequestId) ?: return
        // A job can only ever be assigned once — ignore any further attempts,
        // regardless of what the UI allowed the customer to tap.
        if (existing.selectedTechnicianId != null) return

        jobDao.update(
            existing.copy(
                selectedTechnicianId = technicianId,
                status = JobStatus.TECHNICIAN_SELECTED.name
            )
        )

        val winningQuote = quoteDao.getByJobAndTechnician(jobRequestId, technicianId)
        if (winningQuote != null) {
            quoteDao.setStatus(winningQuote.id, QuoteStatus.SELECTED.name)
            quoteDao.setStatusForOthers(jobRequestId, winningQuote.id, QuoteStatus.NOT_SELECTED.name)
        }
    }

    override suspend fun updateStatus(jobRequestId: String, status: JobStatus) {
        val existing = jobDao.getById(jobRequestId) ?: return
        jobDao.update(existing.copy(status = status.name))
    }

    override fun observeQuotesForJob(jobRequestId: String): Flow<List<Quote>> =
        quoteDao.observeForJob(jobRequestId).map { list -> list.map { it.toDomain() } }
        
override fun observeJobById(jobRequestId: String): Flow<JobRequest?> =
    jobDao.observeById(jobRequestId).map { it?.toDomain() }
    
    override suspend fun getMyQuoteForJob(jobRequestId: String, technicianId: String): Quote? =
        quoteDao.getByJobAndTechnician(jobRequestId, technicianId)?.toDomain()

    override suspend fun submitQuote(quote: Quote) {
        // Enforce "one active quote per technician per job" by natural key
        // (jobRequestId, technicianId) — reuse the existing row's id/createdAt
        // if this technician has already quoted this job, so this is always
        // an update-in-place rather than a second row.
        val existing = quoteDao.getByJobAndTechnician(quote.jobRequestId, quote.technicianId)
        val toSave = if (existing != null) {
            quote.copy(id = existing.id, createdAt = existing.createdAt)
        } else {
            quote
        }
        quoteDao.upsert(toSave.toEntity())

        val job = jobDao.getById(quote.jobRequestId)
        if (job != null &&
            (job.status == JobStatus.REQUEST_CREATED.name || job.status == JobStatus.FINDING_PROFESSIONALS.name)
        ) {
            jobDao.update(job.copy(status = JobStatus.QUOTES_RECEIVED.name))
        }
    }

    private fun JobRequestEntity.toDomain() = JobRequest(
        id = id,
        customerId = customerId,
        serviceId = serviceId,
        serviceName = serviceName,
        problemDescription = problemDescription,
        preferredTime = ServiceTiming.valueOf(preferredTime),
        scheduledDateTime = scheduledDateTime,
        serviceType = ServiceType.valueOf(serviceType),
        approximateArea = approximateArea,
        exactAddress = exactAddress,
        customerName = customerName,
        customerPhone = customerPhone,
        status = JobStatus.valueOf(status),
        createdAt = createdAt,
        selectedTechnicianId = selectedTechnicianId
    )

    private fun JobRequest.toEntity() = JobRequestEntity(
        id = id,
        customerId = customerId,
        serviceId = serviceId,
        serviceName = serviceName,
        problemDescription = problemDescription,
        preferredTime = preferredTime.name,
        scheduledDateTime = scheduledDateTime,
        serviceType = serviceType.name,
        approximateArea = approximateArea,
        exactAddress = exactAddress,
        customerName = customerName,
        customerPhone = customerPhone,
        status = status.name,
        createdAt = createdAt,
        selectedTechnicianId = selectedTechnicianId
    )

    private fun QuoteEntity.toDomain() = Quote(
        id = id,
        jobRequestId = jobRequestId,
        technicianId = technicianId,
        technicianName = technicianName,
        technicianRating = technicianRating,
        technicianVerified = technicianVerified,
        estimatedPrice = estimatedPrice,
        estimatedArrivalTime = estimatedArrivalTime,
        message = message,
        status = QuoteStatus.valueOf(status),
        createdAt = createdAt
    )

    private fun Quote.toEntity() = QuoteEntity(
        id = id,
        jobRequestId = jobRequestId,
        technicianId = technicianId,
        technicianName = technicianName,
        technicianRating = technicianRating,
        technicianVerified = technicianVerified,
        estimatedPrice = estimatedPrice,
        estimatedArrivalTime = estimatedArrivalTime,
        message = message,
        status = status.name,
        createdAt = createdAt
    )
}
