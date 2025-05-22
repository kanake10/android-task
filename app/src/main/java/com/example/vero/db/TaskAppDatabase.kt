package com.example.vero.db

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.vero.db.dao.TaskDao
import com.example.vero.db.entity.TaskModel

@Database(
    entities = [TaskModel::class],
    version = 2,
    exportSchema = true,
    autoMigrations = [
        AutoMigration(from = 1, to = 2)
    ]
)
abstract class TaskDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao

    companion object {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE tasks RENAME TO tasks_old")

                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS tasks (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        task TEXT,
                        title TEXT,
                        description TEXT,
                        sort TEXT,
                        wageType TEXT,
                        BusinessUnitKey TEXT,
                        businessUnit TEXT,
                        parentTaskID TEXT,
                        preplanningBoardQuickSelect TEXT,
                        colorCode TEXT,
                        workingTime TEXT,
                        isAvailableInTimeTrackingKioskMode INTEGER NOT NULL
                    )
                """)

                database.execSQL("""
                    INSERT INTO tasks (id, task, title, description, sort, wageType, BusinessUnitKey, businessUnit, parentTaskID, preplanningBoardQuickSelect, colorCode, workingTime, isAvailableInTimeTrackingKioskMode)
                    SELECT id, task, title, description, sort, wageType, BusinessUnitKey, businessUnit, parentTaskID, preplanningBoardQuickSelect, colorCode, workingTime, isAvailableInTimeTrackingKioskMode
                    FROM tasks_old
                """)

                database.execSQL("DROP TABLE tasks_old")
            }
        }
    }
}