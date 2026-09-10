package com.nexora.kaamsetu.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        ServiceCategoryEntity::class,
        TechnicianEntity::class,
        CustomerEntity::class,
        JobRequestEntity::class,
        QuoteEntity::class
    ],
    version = 3,
    exportSchema = false
)
// Version bumped 2 -> 3 for Phase 3's expanded QuoteEntity (technician name/
// rating/verified, estimatedPrice, estimatedArrivalTime, message, status).
// Still relying on fallbackToDestructiveMigration() below, consistent with
// the migration strategy established in Phase 2 — acceptable for this
// pre-release MVP; a real Migration is required once real user data exists.
abstract class KaamSetuDatabase : RoomDatabase() {

    abstract fun serviceCategoryDao(): ServiceCategoryDao
    abstract fun technicianDao(): TechnicianDao
    abstract fun customerDao(): CustomerDao
    abstract fun jobRequestDao(): JobRequestDao
    abstract fun quoteDao(): QuoteDao

    companion object {
        @Volatile
        private var INSTANCE: KaamSetuDatabase? = null

        fun getInstance(context: Context): KaamSetuDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    KaamSetuDatabase::class.java,
                    "kaamsetu.db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}
