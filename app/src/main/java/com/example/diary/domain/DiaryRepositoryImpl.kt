package com.example.diary.domain

import com.example.diary.data.local.source.LocalDataSource
import com.example.diary.domain.model.Comment
import com.example.diary.domain.model.Task

/**
 * Created by Angelo on 2/21/2021
 */
class DiaryRepositoryImpl(
        private val localDataSource: LocalDataSource
): DiaryRepository {
    override suspend fun getAllTasks(): List<Task> {
        return localDataSource.getAllTasks()
    }

    override suspend fun getTaskById(idTask: Int): Task? {
        return localDataSource.getTaskById(idTask)
    }

    override suspend fun insertTask(task: Task) {
        localDataSource.insertTask(task)
    }

    override suspend fun updateTask(task: Task) {
        localDataSource.updateTask(task)
    }

    override suspend fun deleteTask(task: Task) {
        localDataSource.deleteTask(task)
    }

    override suspend fun getAllCommentsById(idTask: Int): List<Comment> {
        return localDataSource.getAllCommentsById(idTask)
    }

    override suspend fun insertComment(comment: Comment) {
        localDataSource.insertComment(comment)
    }

    override suspend fun deleteAllCommentsById(idTask: Int) {
        localDataSource.deleteAllCommentsById(idTask)
    }
}