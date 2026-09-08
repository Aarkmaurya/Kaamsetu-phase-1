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

enum class JobStatus {
    REQUEST_CREATED,
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

data class ServiceCategory(
    val id: String,
    val group: String,
    val name: String
)

/**
 * Full technician record. In the MVP this is demo/mock data only.
 * `phone` and `exactAddress`-equivalent fields on a job are intentionally
 * kept out of anything a non-selected technician can query — see
 * [com.nexora.kaamsetu.domain.model.JobRequest] for the privacy split.
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
 * A service request as it exists BEFORE a technician is selected.
 * Only the fields in [publicView] are ever shown to technicians at this stage.
 * `customerPhone` and `exactAddress` are only attached to a [Booking] after
 * `selectedTechnicianId` is set by the customer — enforcing the "no private
 * data before selection" rule at the model level, not just in the UI.
 */
data class JobRequest(
    val id: String,
    val customerId: String,
    val categoryId: String,
    val problemDescription: String,
    val approximateArea: String,
    val timing: ServiceTiming,
    val preferredDateTime: String,
    val status: JobStatus,
    val customerPhone: String,
    val exactAddress: String,
    val selectedTechnicianId: String? = null
) {
    /** What a technician is allowed to see before they are selected for this job. */
    fun publicView(): JobRequestPublicView = JobRequestPublicView(
        id = id,
        categoryId = categoryId,
        problemDescription = problemDescription,
        approximateArea = approximateArea,
        timing = timing,
        preferredDateTime = preferredDateTime,
        status = status
    )
}

data class JobRequestPublicView(
    val id: String,
    val categoryId: String,
    val problemDescription: String,
    val approximateArea: String,
    val timing: ServiceTiming,
    val preferredDateTime: String,
    val status: JobStatus
)

data class Quote(
    val id: String,
    val jobRequestId: String,
    val technicianId: String,
    val price: Double,
    val etaMinutes: Int
)

