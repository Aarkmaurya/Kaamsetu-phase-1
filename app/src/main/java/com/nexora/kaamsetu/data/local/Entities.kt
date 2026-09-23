package com.nexora.kaamsetu.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "service_categories")
data class ServiceCategoryEntity(
    @PrimaryKey val id: String,
    val group: String,
    val name: String
)

@Entity(tableName = "technicians")
data class TechnicianEntity(
    @PrimaryKey val id: String,
    val name: String,
    val phone: String,
    val status: String,
    val skillsCsv: String,
    val city: String,
    val rating: Double,
    val completedJobs: Int,
    val isVerified: Boolean,
    val isAvailable: Boolean
)

@Entity(tableName = "customers")
data class CustomerEntity(
    @PrimaryKey val id: String,
    val name: String,
    val phone: String
)

/**
 * Phase 4B.01: local demo account store for the auth foundation. `id` is
 * shared with the matching CustomerEntity/TechnicianEntity row created at
 * registration time (see LocalAuthRepository.register) so existing
 * repositories keep working against a real logged-in user's id unchanged.
 * `passwordHash` is never plain text — see PasswordHasher — but this whole
 * table is still local-only demo auth, not production security.
 */
@Entity(tableName = "user_accounts", indices = [Index(value = ["phone"], unique = true)])
data class UserAccountEntity(
    @PrimaryKey val id: String,
    val phone: String,
    val passwordHash: String,
    val name: String,
    val role: String,
    val createdAt: Long
)

/**
 * Phase 2: expanded to match the full JobRequest domain model (service name,
 * scheduling, service type, customer name, createdAt). Room version was bumped
 * in KaamSetuDatabase along with this change; fallbackToDestructiveMigration()
 * is still in place for the MVP so this does not require a Migration class.
 */
@Entity(tableName = "job_requests")
data class JobRequestEntity(
    @PrimaryKey val id: String,
    val customerId: String,
    val serviceId: String,
    val serviceName: String,
    val problemDescription: String,
    val preferredTime: String,
    val scheduledDateTime: String? = null,
    val serviceType: String,
    val approximateArea: String,
    val exactAddress: String? = null,
    val customerName: String,
    val customerPhone: String,
    val status: String,
    val createdAt: Long,
    val selectedTechnicianId: String? = null
)

/**
 * Phase 3: expanded to carry technician name/rating/verified (denormalized —
 * see Quote's class doc), a human-readable arrival estimate, an optional
 * message, and a quote status (PENDING / SELECTED / NOT_SELECTED). Room
 * version bumped accordingly in KaamSetuDatabase.
 */
@Entity(tableName = "quotes")
data class QuoteEntity(
    @PrimaryKey val id: String,
    val jobRequestId: String,
    val technicianId: String,
    val technicianName: String,
    val technicianRating: Double,
    val technicianVerified: Boolean,
    val estimatedPrice: Double,
    val estimatedArrivalTime: String,
    val message: String? = null,
    val status: String,
    val createdAt: Long
)

