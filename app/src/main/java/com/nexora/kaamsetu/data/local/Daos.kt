package com.nexora.kaamsetu.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ServiceCategoryDao {
    @Query("SELECT * FROM service_categories ORDER BY `group`, name")
    fun observeAll(): Flow<List<ServiceCategoryEntity>>

    @Query("SELECT COUNT(*) FROM service_categories")
    suspend fun count(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(categories: List<ServiceCategoryEntity>)
}

@Dao
interface TechnicianDao {
    @Query("SELECT * FROM technicians")
    fun observeAll(): Flow<List<TechnicianEntity>>

    @Query("SELECT * FROM technicians WHERE status = 'APPROVED' AND isAvailable = 1")
    fun observeAvailableApproved(): Flow<List<TechnicianEntity>>

    @Query("SELECT * FROM technicians WHERE id = :id")
    suspend fun getById(id: String): TechnicianEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(technicians: List<TechnicianEntity>)

    @Update
    suspend fun update(technician: TechnicianEntity)
}

@Dao
interface CustomerDao {
    @Query("SELECT * FROM customers WHERE id = :id")
    suspend fun getById(id: String): CustomerEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(customers: List<CustomerEntity>)
}

@Dao
interface JobRequestDao {
    @Query("SELECT * FROM job_requests ORDER BY createdAt DESC")
    fun observeAll(): Flow<List<JobRequestEntity>>

    @Query("SELECT * FROM job_requests WHERE customerId = :customerId ORDER BY createdAt DESC")
    fun observeForCustomer(customerId: String): Flow<List<JobRequestEntity>>

    /** Used by the customer Job Details screen — reactive so a cancel() updates the screen in place. */
    @Query("SELECT * FROM job_requests WHERE id = :id LIMIT 1")
    fun observeById(id: String): Flow<JobRequestEntity?>

    @Query("SELECT * FROM job_requests WHERE status IN ('REQUEST_CREATED', 'FINDING_PROFESSIONALS', 'QUOTES_RECEIVED')")
    fun observeOpenRequests(): Flow<List<JobRequestEntity>>

    @Query("SELECT * FROM job_requests WHERE selectedTechnicianId = :technicianId ORDER BY createdAt DESC")
    fun observeForSelectedTechnician(technicianId: String): Flow<List<JobRequestEntity>>

    @Query("SELECT * FROM job_requests WHERE id = :id")
    suspend fun getById(id: String): JobRequestEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(requests: List<JobRequestEntity>)

    @Update
    suspend fun update(request: JobRequestEntity)
}

@Dao
interface QuoteDao {
    @Query("SELECT * FROM quotes WHERE jobRequestId = :jobRequestId")
    fun observeForJob(jobRequestId: String): Flow<List<QuoteEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(quote: QuoteEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(quotes: List<QuoteEntity>)
}
