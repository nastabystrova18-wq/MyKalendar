package com.example.mykalendar

import kotlinx.coroutines.flow.Flow

class TaskRepository(private val taskDao: TaskDao) {

    // Получить все задачи как Flow — данные будут обновляться автоматически
    val allTasks: Flow<List<Task>> = taskDao.getAllTasksFlow()

    // Получить задачи за конкретную дату
    fun getTasksByDate(date: String): Flow<List<Task>> = taskDao.getTasksByDateFlow(date)

    suspend fun insertTask(task: Task) = taskDao.insertTask(task)

    suspend fun updateTask(task: Task) = taskDao.updateTask(task)

    suspend fun deleteTask(task: Task) = taskDao.deleteTask(task)
}