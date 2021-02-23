package com.example.kmmsharedmodule.viewmodel

import com.badoo.reaktive.observable.Observable
import com.badoo.reaktive.subject.publish.PublishSubject
import com.example.kmmsharedmodule.data.local.DbDriver
import com.example.kmmsharedmodule.data.local.SQLDelightLocal
import com.example.kmmsharedmodule.data.repository.DiaryRepository
import com.example.kmmsharedmodule.data.repository.DiaryRepositoryImpl
import com.example.kmmsharedmodule.domain.model.Comment
import com.example.kmmsharedmodule.domain.model.Task
import kotlin.native.concurrent.ThreadLocal

/**
 * Created by Angelo on 2/23/2021
 */

class DiaryViewModel private constructor(
    private val repository: DiaryRepository
) {

    private val _taskList = PublishSubject<List<Task>>()
    val taskList: Observable<List<Task>> = _taskList

    private val _task = PublishSubject<Task>()
    val task: Observable<Task> = _task

    private val _commentList = PublishSubject<List<Comment>>()
    val commentList: Observable<List<Comment>> = _commentList

    fun fetchTaskList(){
        val list = repository.getAllTasks()
        _taskList.onNext(list)
    }

    fun fetchTaskById(idTask: Int) {
        val task = repository.getTaskById(idTask)
        println(task.toString())
        _task.onNext(task)
    }

    fun insertTask(task: Task){
        repository.insertTask(task)
        fetchTaskList()
    }

    fun updateTask(task: Task) {
        repository.updateTask(task)
        fetchTaskById(task.idTask)
        fetchTaskList()
    }

    fun deleteTask(task: Task) {
        repository.deleteTask(task)
        repository.deleteAllCommentsById(task.idTask)
    }

    fun fetchAllCommentsById(idTask: Int){
        val commentList = repository.getAllCommentsById(idTask)
        _commentList.onNext(commentList)
    }

    fun insertComment(comment: Comment) {
        repository.insertComment(comment)
        fetchAllCommentsById(comment._idTask)
    }


    @ThreadLocal
    companion object{
        private var instance: DiaryViewModel? = null
        fun getInstance(dbDriver: DbDriver): DiaryViewModel =
            instance ?: DiaryViewModel(
                DiaryRepositoryImpl(SQLDelightLocal(dbDriver.createDriver()))
            ).also { instance = it }
        fun destroyInstance(){ instance = null }
    }
}
