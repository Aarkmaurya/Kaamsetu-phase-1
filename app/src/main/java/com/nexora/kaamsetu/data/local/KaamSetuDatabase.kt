package com.nexora.kaamsetu.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [
        ServiceCategoryEntity::class,
        TechnicianEntity::class,
        CustomerEntity::class,
        JobRequestEntity::class,
        QuoteEntity::class,
        UserAccountEntity::class
    ],
    version = 4,
    exportSchema = false
)
// Version bumped 3 -> 4 for Phase 4B.01's new user_accounts table (local auth
// foundation). Unlike prior bumps, this one ships a real Migration (below)
// instead of relying on fallbackToDestructiveMigration() — existing
// customer/technician/job/quote data must survive this update, per Phase
// 4B.01's explicit requirement. fallbackToDestructiveMigration() remains in
// the builder purely as a safety net for any *future, untested* version bump,
// not as a substitute for this one.
abstract class KaamSetuDatabase : RoomDatabase() {

    abstract fun serviceCategoryDao(): ServiceCategoryDao
    abstract fun technicianDao(): TechnicianDao
    abstract fun customerDao(): CustomerDao
    abstract fun jobRequestDao(): JobRequestDao
    abstract fun quoteDao(): QuoteDao
    abstract fun userAccountDao(): UserAccountDao

    companion object {

        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `user_accounts` (
                        `id` TEXT NOT NULL,
                        `phone` TEXT NOT NULL,
                        `passwordHash` TEXT NOT NULL,
                        `name` TEXT NOT NULL,
                        `role` TEXT NOT NULL,
                        `createdAt` INTEGER NOT NULL,
                        PRIMARY KEY(`id`)
                    )
                    """.trimIndent()
                )
                db.execSQL(
                    "CREATE UNIQUE INDEX IF NOT EXISTS `index_user_accounts_phone` ON `user_accounts` (`phone`)"
                )
            }
        }

        @Volatile
        private var INSTANCE: KaamSetuDatabase? = null

        fun getInstance(context: Context): KaamSetuDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    KaamSetuDatabase::class.java,
                    "kaamsetu.db"
                )
                    .addMigrations(MIGRATION_3_4)
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}


