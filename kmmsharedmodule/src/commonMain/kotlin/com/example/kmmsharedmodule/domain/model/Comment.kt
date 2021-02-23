package com.example.kmmsharedmodule.domain.model

data class Comment(
        val idComment   : Int,
        val image       : String,
        val message     : String,
        val _idTask     : Int
)