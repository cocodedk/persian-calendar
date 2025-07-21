package com.cocode.calendar

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [Event::class], version = 2)
@TypeConverters(EventConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun eventDao(): EventDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Add new columns for repetition features
                database.execSQL("ALTER TABLE event ADD COLUMN isRepeating INTEGER NOT NULL DEFAULT 0")
                database.execSQL("ALTER TABLE event ADD COLUMN repetitionType TEXT NOT NULL DEFAULT 'NONE'")
                database.execSQL("ALTER TABLE event ADD COLUMN originalDate TEXT")
                database.execSQL("ALTER TABLE event ADD COLUMN repetitionEndDate TEXT")
            }
        }

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "calendar_db"
                ).addMigrations(MIGRATION_1_2).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
