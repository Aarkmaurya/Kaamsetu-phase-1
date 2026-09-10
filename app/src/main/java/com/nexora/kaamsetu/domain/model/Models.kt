package com.nexora.kaamsetu.domain.model

/** The three roles supported by KaamSetu. Kept as an enum so navigation and RBAC checks share one source of truth. */
enum class UserRole {
    CUSTOMER,
    TECHNICIAN,
    ADMIN
}

enum class TechnicianStatus {
    PENDING,
    APPROVED,
    REJECTED,
    SUSPENDED
}

/**
 * Phase 2 adds FINDING_PROFESSIONALS between REQUEST_CREATED and QUOTES_RECEIVED.
 * Only REQUEST_CREATED (and cancellation out of it) is fully wired up in Phase 2;
 * the rest of the pipeline exists in the model for Phase 3 (technician quotes).
 */
enum class JobStatus {
    REQUEST_CREATED,
    FINDING_PROFESSIONALS,
    QUOTES_RECEIVED,
    TECHNICIAN_SELECTED,
    BOOKING_CONFIRMED,
    ON_THE_WAY,
    ARRIVED,
    WORK_STARTED,
    WORK_COMPLETED,
    CANCELLED
}

enum class ServiceTiming {
    NOW,
    TODAY,
    SCHEDULED
}

enum class ServiceType {
    HOME_VISIT,
    SERVICE_CENTER
}

data class ServiceCategory(
    val id: String,
    val group: String,
    val name: String
)

/**
 * Full technician record. In the MVP this is demo/mock data only.
 * `phone` and job-level private fields are intentionally kept out of anything
 * a non-selected technician can query — see [JobRequest] for the privacy split.
 */
data class Technician(
    val id: String,
    val name: String,
    val phone: String,
    val status: TechnicianStatus,
    val skills: List<String>,
    val city: String,
    val rating: Double,
    val completedJobs: Int,
    val isVerified: Boolean,
    val isAvailable: Boolean
)

data class Customer(
    val id: String,
    val name: String,
    val phone: String
)

/**
 * A customer service request.
 *
 * PRIVACY / FUTURE AUTHORIZATION NOTE:
 * `customerName`, `customerPhone`, and `exactAddress` must only ever reach a
 * technician after they are the `selectedTechnicianId` for this job. Right now
 * that's enforced by which repository method / view a screen is allowed to read
 * from (see [publicView] and JobRepository.observeOpenRequestsPublicView).
 * When a real backend exists, this must ALSO be enforced server-side — a client
 * omitting a field is a UX nicety, not a security boundary. The backend must
 * re-check `selectedTechnicianId == requestingTechnicianId` (or requester is the
 * owning customer, or requester is admin) before returning these fields, and
 * must reject any request for another technician's/customer's private data
 * regardless of what the client asks for.
 */
data class JobRequest(
    val id: String,
    val customerId: String,
    val serviceId: String,
    val serviceName: String,
    val problemDescription: String,
    val preferredTime: ServiceTiming,
    /** Only meaningful when [preferredTime] == SCHEDULED. Simple "yyyy-MM-dd HH:mm" style text for the MVP. */
    val scheduledDateTime: String? = null,
    val serviceType: ServiceType,
    val approximateArea: String,
    /** Null for SERVICE_CENTER requests. Private — see class doc. */
    val exactAddress: String? = null,
    val customerName: String,
    /** Private — see class doc. */
    val customerPhone: String,
    val status: JobStatus,
    val createdAt: Long,
    val selectedTechnicianId: String? = null
) {
    /** What a technician is allowed to see before they are selected for this job. */
    fun publicView(): JobRequestPublicView = JobRequestPublicView(
        id = id,
        serviceId = serviceId,
        serviceName = serviceName,
        problemDescription = problemDescription,
        approximateArea = approximateArea,
        preferredTime = preferredTime,
        scheduledDateTime = scheduledDateTime,
        serviceType = serviceType,
        status = status
    )
}

data class JobRequestPublicView(
    val id: String,
    val serviceId: String,
    val serviceName: String,
    val problemDescription: String,
    val approximateArea: String,
    val preferredTime: ServiceTiming,
    val scheduledDateTime: String?,
    val serviceType: ServiceType,
    val status: JobStatus
)

/** Human-friendly timing text shared by customer and technician screens. */
fun JobRequest.timingDisplay(): String = when (preferredTime) {
    ServiceTiming.NOW -> "Now"
    ServiceTiming.TODAY -> "Today"
    ServiceTiming.SCHEDULED -> "Scheduled: ${scheduledDateTime ?: "date/time TBD"}"
}

fun JobRequestPublicView.timingDisplay(): String = when (preferredTime) {
    ServiceTiming.NOW -> "Now"
    ServiceTiming.TODAY -> "Today"
    ServiceTiming.SCHEDULED -> "Scheduled: ${scheduledDateTime ?: "date/time TBD"}"
}

data class Quote(
    val id: String,
    val jobRequestId: String,
    val technicianId: String,
    /**
     * Technician name/rating/verified are captured onto the quote at submit
     * time (denormalized) so the customer's Compare Quotes screen can render
     * a card without joining against the technician table. Acceptable for
     * this MVP; a real backend may instead join live technician data.
     */
    val technicianName: String,
    val technicianRating: Double,
    val technicianVerified: Boolean,
    val estimatedPrice: Double,
    val estimatedArrivalTime: String,
    val message: String? = null,
    val status: QuoteStatus = QuoteStatus.PENDING,
    val createdAt: Long
)

enum class QuoteStatus {
    PENDING,
    SELECTED,
    NOT_SELECTED
}

