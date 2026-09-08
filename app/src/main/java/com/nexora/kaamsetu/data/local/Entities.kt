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

@Entity(tableName = "job_requests")
data class JobRequestEntity(
    @PrimaryKey val id: String,
    val customerId: String,
    val categoryId: String,
    val problemDescription: String,
    val approximateArea: String,
    val timing: String,
    val preferredDateTime: String,
    val status: String,
    val customerPhone: String,
    val exactAddress: String,
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
