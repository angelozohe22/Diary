package com.example.diary.ui.view.adapters

import com.example.diary.domain.model.Task

/**
 * Created by Angelo on 2/21/2021
 */
interface OnTaskClickListener {
    fun onTaskClicked(task: Task)
}