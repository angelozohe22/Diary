package com.example.diary.data.local.source

import android.util.Log
import com.example.diary.core.ctx
import com.example.diary.data.local.source.db.DBHelper
import com.example.diary.domain.model.Comment
import com.example.diary.domain.model.Task

/**
 * Created by Angelo on 2/21/2021
 */
class LocalDataSourceImpl: LocalDataSource {

    private var _taskList = mutableListOf<Task>()
    private val db = DBHelper(ctx)

    override suspend fun getAllTasks(): List<Task> {
        val taskList = mutableListOf<Task>()
        val cursor = db.getTaskList()
        cursor?.let{
            if(cursor.count != 0) {
                while (cursor.moveToNext()){
                    val idTask = cursor.getInt(0)
                    val name = cursor.getString(1)
                    val description = cursor.getString(2)
                    val startDate = cursor.getString(3)
                    val endDate = cursor.getString(4)
                    val is_finished = cursor.getInt(5)
                    val task = Task(idTask, name, description, startDate, endDate, is_finished)
                    taskList.add(task)
                }
            }
        }
        _taskList = taskList
        return _taskList.toList()
    }

    override suspend fun getTaskById(idTask: Int): Task? {
        return _taskList.firstOrNull { it.idTask == idTask }
    }

    override suspend fun insertTask(task: Task) {
        db.insertTask(task)
    }

    override suspend fun updateTask(task: Task) {
        _taskList.forEachIndexed { index, currentTask ->
            if(currentTask.idTask == task.idTask) {
                db.updateTask(task)
                _taskList[index] = task
            }
        }
    }

    override suspend fun deleteTask(task: Task) {
        val contains = _taskList.contains(task)
        if(contains){
            db.deleteTask(task)
            _taskList.remove(task)
        }

    }

    override suspend fun getAllCommentsById(idTask: Int): List<Comment> {
        val currentList = mutableListOf<Comment>()
        val cursor = db.getCommentListById(idTask)
        cursor?.let{
            if(cursor.count != 0){
                while (cursor.moveToNext()){
                    val idComment = cursor.getInt(0)
                    val image = cursor.getString(1)
                    val message = cursor.getString(2)
                    val _id_task = cursor.getInt(3)
                    val comment = Comment(idComment, image, message, _id_task)
                    currentList.add(comment)
                }
            }
        }

        return currentList.toList()
    }

    override suspend fun insertComment(comment: Comment) {
        db.insertComment(comment)
    }

    override suspend fun deleteAllCommentsById(idTask: Int) {
        db.deleteAllCommentsById(idTask)
    }

}