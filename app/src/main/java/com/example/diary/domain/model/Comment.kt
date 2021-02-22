package com.example.diary.domain.model

/**
 * Created by Angelo on 2/21/2021
 */
data class Comment(
        val idComment   : Int,
        val image       : String,
        val message     : String,
        val _idTask     : Int
)