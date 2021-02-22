package com.example.diary.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.diary.domain.DiaryRepository

/**
 * Created by Angelo on 2/21/2021
 */
class DiaryViewModelFactory(private val repository: DiaryRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        Log.e("asd", "entro en factory")
        return modelClass.getConstructor(DiaryRepository::class.java).newInstance(repository)
    }
}