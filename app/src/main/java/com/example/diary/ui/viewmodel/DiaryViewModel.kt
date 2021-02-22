package com.example.diary.ui.viewmodel

import android.util.Log
import androidx.lifecycle.*
import com.example.diary.data.local.source.LocalDataSourceImpl
import com.example.diary.domain.DiaryRepository
import com.example.diary.domain.DiaryRepositoryImpl
import com.example.diary.domain.model.Comment
import com.example.diary.domain.model.Task
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Created by Angelo on 2/21/2021
 */
class DiaryViewModel private constructor(
        private val diaryRepository: DiaryRepository
): ViewModel() {

    private val _taskList = MutableLiveData<List<Task>>()
    val taskList: LiveData<List<Task>> get() = _taskList

    private val _task = MutableLiveData<Task>()
    val task: LiveData<Task> get() = _task

    private val _commentList = MutableLiveData<List<Comment>>()
    val commentList: LiveData<List<Comment>> get() = _commentList

    fun fetchTaskList() {
        viewModelScope.launch {
            _taskList.value = diaryRepository.getAllTasks()
        }
    }

    fun fetchTaskById(idTask: Int) {
        viewModelScope.launch {
            _task.value = diaryRepository.getTaskById(idTask)
        }
    }

    fun insertNewTask(task: Task){
        viewModelScope.launch {
            diaryRepository.insertTask(task)
            fetchTaskList()
        }
    }

    fun updateTask(task: Task){
        viewModelScope.launch {
            diaryRepository.updateTask(task)
            fetchTaskById(task.idTask)
            fetchTaskList()
        }
    }

    fun deleteTask(task: Task){
        viewModelScope.launch {
            diaryRepository.deleteTask(task)
            diaryRepository.deleteAllCommentsById(task.idTask)
        }
    }

    fun fetchAllCommentsById(idTask: Int){
        viewModelScope.launch {
            _commentList.value = diaryRepository.getAllCommentsById(idTask)
        }
    }

    fun insertComment(comment: Comment){
        viewModelScope.launch {
            diaryRepository.insertComment(comment)
            fetchAllCommentsById(comment._idTask)
        }
    }

    companion object{
        @Volatile private var instance: DiaryViewModel? = null
        fun getInstance(): DiaryViewModel =
                instance ?: DiaryViewModel(
                        DiaryRepositoryImpl(
                                LocalDataSourceImpl()
                        )
                ).also { instance = it }
        fun destroyInstance(){ instance = null }
    }

}