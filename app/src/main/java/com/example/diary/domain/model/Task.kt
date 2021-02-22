package com.example.diary.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Created by Angelo on 2/20/2021
 */
@Parcelize
data class Task(
    val idTask      : Int,
    val name        : String,
    val description : String,
    val startDate   : String,
    val endDate     : String,
    val isFinished  : Int
): Parcelable
