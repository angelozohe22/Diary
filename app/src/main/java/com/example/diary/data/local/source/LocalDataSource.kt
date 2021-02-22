package com.example.diary.data.local.source

import com.example.diary.domain.model.Comment
import com.example.diary.domain.model.Task

interface LocalDataSource {
    //Task
    suspend fun getAllTasks(): List<Task>
    suspend fun getTaskById(idTask: Int): Task?
    suspend fun insertTask(task: Task)
    suspend fun updateTask(task: Task)
    suspend fun deleteTask(task: Task)
    //Comment
    suspend fun getAllCommentsById(idTask: Int): List<Comment>
    suspend fun insertComment(comment: Comment)
    suspend fun deleteAllCommentsById(idTask: Int)
}
