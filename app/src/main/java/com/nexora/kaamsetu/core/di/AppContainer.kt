package com.nexora.kaamsetu.core.di

import android.content.Context
import com.nexora.kaamsetu.data.local.KaamSetuDatabase
import com.nexora.kaamsetu.data.local.SeedData
import com.nexora.kaamsetu.data.repository.JobRepository
import com.nexora.kaamsetu.data.repository.MockJobRepository
import com.nexora.kaamsetu.data.repository.MockServiceCatalogRepository
import com.nexora.kaamsetu.data.repository.MockTechnicianRepository
import com.nexora.kaamsetu.data.repository.ServiceCatalogRepository
import com.nexora.kaamsetu.data.repository.TechnicianRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/**
 * Simple hand-rolled service locator for the MVP.
 * Holds one instance of each repository, backed by Room + demo seed data today.
 * When a real backend exists, only the repository implementations passed here change —
 * ViewModels and screens depend on the interfaces, not on this container's internals.
 */
class AppContainer(context: Context) {

    private val database = KaamSetuDatabase.getInstance(context)
    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    val serviceCatalogRepository: ServiceCatalogRepository =
        MockServiceCatalogRepository(database.serviceCategoryDao())

    val technicianRepository: TechnicianRepository =
        MockTechnicianRepository(database.technicianDao())

    val jobRepository: JobRepository =
        MockJobRepository(database.jobRequestDao(), database.quoteDao(), database.customerDao())

    init {
        appScope.launch { seedIfNeeded() }
    }

    private suspend fun seedIfNeeded() {
        database.serviceCategoryDao().insertAll(SeedData.categories())
        database.customerDao().insertAll(SeedData.customers())
        database.technicianDao().insertAll(SeedData.technicians())
        database.jobRequestDao().insertAll(SeedData.jobRequests())
        database.quoteDao().insertAll(SeedData.quotes())
    }
}

