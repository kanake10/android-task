package com.example.vero.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.vero.db.entity.TaskModel
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTasks(tasks: List<TaskModel>)

    @Query("DELETE FROM tasks")
    suspend fun deleteAllTasks()

    @Query("""
    SELECT * FROM tasks WHERE
    task LIKE '%' || :query || '%' COLLATE NOCASE OR
    title LIKE '%' || :query || '%' COLLATE NOCASE OR
    description LIKE '%' || :query || '%' COLLATE NOCASE OR
    sort LIKE '%' || :query || '%' COLLATE NOCASE OR
    wageType LIKE '%' || :query || '%' COLLATE NOCASE OR
    BusinessUnitKey LIKE '%' || :query || '%' COLLATE NOCASE OR
    businessUnit LIKE '%' || :query || '%' COLLATE NOCASE OR
    parentTaskID LIKE '%' || :query || '%' COLLATE NOCASE OR
    preplanningBoardQuickSelect LIKE '%' || :query || '%' COLLATE NOCASE OR
    colorCode LIKE '%' || :query || '%' COLLATE NOCASE OR
    workingTime LIKE '%' || :query || '%' COLLATE NOCASE OR
    isAvailableInTimeTrackingKioskMode LIKE '%' || :query || '%' COLLATE NOCASE
""")
    fun searchTasks(query: String): Flow<List<TaskModel>>


    @Query("SELECT * FROM tasks")
    fun getAllTasks(): Flow<List<TaskModel>>
}
