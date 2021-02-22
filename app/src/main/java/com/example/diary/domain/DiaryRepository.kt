package com.example.diary.domain

import com.example.diary.domain.model.Comment
import com.example.diary.domain.model.Task

/**
 * Created by Angelo on 2/21/2021
 */
interface DiaryRepository {
    //All local request
    suspend fun getAllTasks(): List<Task>
    suspend fun getTaskById(idTask: Int): Task?
    suspend fun insertTask(task: Task)
    suspend fun updateTask(task: Task)
    suspend fun deleteTask(task: Task)

    suspend fun getAllCommentsById(idTask: Int): List<Comment>
    suspend fun insertComment(comment: Comment)
    suspend fun deleteAllCommentsById(idTask: Int)
}