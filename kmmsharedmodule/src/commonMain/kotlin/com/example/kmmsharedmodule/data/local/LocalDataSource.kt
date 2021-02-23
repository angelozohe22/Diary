package com.example.kmmsharedmodule.data.local

import com.example.kmmsharedmodule.domain.model.Comment
import com.example.kmmsharedmodule.domain.model.Task

/**
 * Created by Angelo on 2/23/2021
 */
interface LocalDataSource {
    //Task
    fun getAllTasks(): List<Task>
    fun getTaskById(idTask: Int): Task
    fun insertTask(task: Task)
    fun updateTask(task: Task)
    fun deleteTask(task: Task)
    //Comment
    fun getAllCommentsById(idTask: Int): List<Comment>
    fun insertComment(comment: Comment)
    fun deleteAllCommentsById(idTask: Int)
}