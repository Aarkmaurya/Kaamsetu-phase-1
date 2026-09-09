package com.nexora.kaamsetu.data.local

import androidx.room.Entity
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

@Entity(tableName = "quotes")
data class QuoteEntity(
    @PrimaryKey val id: String,
    val jobRequestId: String,
    val technicianId: String,
    val price: Double,
    val etaMinutes: Int
)

