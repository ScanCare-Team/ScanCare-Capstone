package com.tariapp.scancare

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object Migrasi {
    val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {
            // Misalnya, menambah kolom baru pada tabel `scancare_entity`
            database.execSQL("ALTER TABLE scancare_entity ADD COLUMN new_column INTEGER DEFAULT 0 NOT NULL")
        }
    }
}