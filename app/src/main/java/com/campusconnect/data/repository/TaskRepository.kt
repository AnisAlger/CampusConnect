package com.campusconnect.data.repository

import com.campusconnect.data.model.Task
import kotlinx.coroutines.flow.Flow

class TaskRepository(private val taskDao: TaskDao) {
    val allTasks: Flow<List<Task>> = taskDao.getAllTasks()

    suspend fun insert(task: Task): Long = taskDao.insertTask(task) // ✅ retourne l'id
    suspend fun update(task: Task) = taskDao.updateTask(task)
    suspend fun delete(task: Task) = taskDao.deleteTask(task)
    suspend fun deleteAll() = taskDao.deleteAll()
}
