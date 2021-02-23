package com.example.kmmsharedmodule.data.repository

import com.example.kmmsharedmodule.data.local.LocalDataSource
import com.example.kmmsharedmodule.domain.model.Comment
import com.example.kmmsharedmodule.domain.model.Task

/**
 * Created by Angelo on 2/23/2021
 */
class DiaryRepositoryImpl(
    private val localDataSource: LocalDataSource
    ): DiaryRepository{

    override fun getAllTasks(): List<Task> {
        return localDataSource.getAllTasks()
    }

    override fun getTaskById(idTask: Int): Task {
        return localDataSource.getTaskById(idTask)
    }

    override fun insertTask(task: Task) {
        localDataSource.insertTask(task)
    }

    override fun updateTask(task: Task) {
        localDataSource.updateTask(task)
    }

    override fun deleteTask(task: Task) {
        localDataSource.deleteTask(task)
    }

    override fun getAllCommentsById(idTask: Int): List<Comment> {
        return localDataSource.getAllCommentsById(idTask)
    }

    override fun insertComment(comment: Comment) {
        localDataSource.insertComment(comment)
    }

    override fun deleteAllCommentsById(idTask: Int) {
        localDataSource.deleteAllCommentsById(idTask)
    }
}