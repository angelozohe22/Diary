package com.example.kmmsharedmodule.domain.model

data class Task(
    val idTask      : Int,
    val name        : String,
    val description : String,
    val startDate   : String,
    val endDate     : String,
    val isFinished  : Int
)