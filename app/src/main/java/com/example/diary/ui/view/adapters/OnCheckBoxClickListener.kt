package com.example.diary.ui.view.adapters

import com.example.kmmsharedmodule.domain.model.Task

/**
 * Created by Angelo on 2/22/2021
 */
interface OnCheckBoxClickListener {
    fun updateTaskFinished(task: Task)
}