package com.example.kmmsharedmodule.data.local

import com.example.kmmsharedmodule.diarydb
import com.example.kmmsharedmodule.domain.model.Comment
import com.example.kmmsharedmodule.domain.model.Task
import com.squareup.sqldelight.db.SqlDriver

/**
 * Created by Angelo on 2/23/2021
 */
expect class DbDriver{
    fun createDriver(): SqlDriver
}

class SQLDelightLocal(sqlDriver: SqlDriver)
    : LocalDataSource {

    private val db by lazy { diarydb(sqlDriver) }

    override fun getAllTasks(): List<Task> {
        val taskList = db.diaryQueries.SelectAllTasks{ id_task, name, description, date_start, date_end, is_finished ->
            Task(id_task.toInt(), name, description, date_start, date_end, is_finished?.toInt() ?: 0)
        }.executeAsList()
        return taskList
    }

    override fun getTaskById(idTask: Int): Task {
        val task = db.diaryQueries.SelectTaskById(idTask.toLong()){ id_task, name, description, date_start, date_end, is_finished ->
            Task(id_task.toInt(), name, description, date_start, date_end, is_finished?.toInt() ?: 0)
        }.executeAsOne()
        return task
    }

    override fun insertTask(task: Task) {
        db.diaryQueries.InsertTask(
                task.name,
                task.description,
                task.startDate,
                task.endDate,
                task.isFinished.toLong())
    }

    override fun updateTask(task: Task) {
        db.diaryQueries.UpdateTask(task.name, task.description, task.startDate, task.endDate, task.isFinished.toLong(), task.idTask.toLong())
    }

    override fun deleteTask(task: Task) {
        db.diaryQueries.DeleteTask(task.idTask.toLong())
    }

    override fun getAllCommentsById(idTask: Int): List<Comment> {
        val commentList = db.diaryQueries.SelectCommentListById(idTask.toLong()){ id_comment, image, message, _id_task ->
            Comment(id_comment.toInt(), image, message, _id_task.toInt())
        }.executeAsList()
        return commentList
    }

    override fun insertComment(comment: Comment) {
        db.diaryQueries.InsertComment(comment.image, comment.message, comment._idTask.toLong())
    }

    override fun deleteAllCommentsById(idTask: Int) {
        db.diaryQueries.DeleteAllCommentsById(idTask.toLong())
    }
}