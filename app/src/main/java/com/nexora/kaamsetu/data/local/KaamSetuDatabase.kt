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
    version = 2,
    exportSchema = false
)
// Version bumped 1 -> 2 for Phase 2's expanded JobRequestEntity (serviceName,
// scheduledDateTime, serviceType, customerName, createdAt). No Migration class
// is written because fallbackToDestructiveMigration() is still enabled below —
// acceptable for this pre-release MVP; a real Migration is required once the
// app has real users with data worth preserving.
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

