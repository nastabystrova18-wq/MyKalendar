package com.example.mykalendar

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

    @Insert
    suspend fun insertTask(task: Task)

    @Update
    suspend fun updateTask(task: Task)

    @Delete
    suspend fun deleteTask(task: Task)

    // Flow автоматически уведомляет об изменениях в таблице
    @Query("SELECT * FROM tasks ORDER BY date ASC, time ASC")
    fun getAllTasksFlow(): Flow<List<Task>>

    @Query("SELECT * FROM tasks WHERE date = :date")
    fun getTasksByDateFlow(date: String): Flow<List<Task>>

    // Оставим и обычные suspend-методы для точечных запросов
    @Query("SELECT * FROM tasks ORDER BY date ASC, time ASC")
    suspend fun getAllTasks(): List<Task>

    @Query("SELECT * FROM tasks WHERE date = :date")
    suspend fun getTasksByDate(date: String): List<Task>
}