package com.example.shared

import com.squareup.sqldelight.Query
import com.squareup.sqldelight.Transacter
import kotlin.Any
import kotlin.Long
import kotlin.String

interface DiaryQueries : Transacter {
  fun <T : Any> SelectAllTasks(mapper: (
    id_task: Long,
    name: String,
    description: String,
    date_start: String,
    date_end: String,
    is_finished: Long?
  ) -> T): Query<T>

  fun SelectAllTasks(): Query<Task>

  fun <T : Any> SelectTaskById(id_task: Long, mapper: (
    id_task: Long,
    name: String,
    description: String,
    date_start: String,
    date_end: String,
    is_finished: Long?
  ) -> T): Query<T>

  fun SelectTaskById(id_task: Long): Query<Task>

  fun <T : Any> SelectCommentListById(_id_task: Long, mapper: (
    id_comment: Long,
    image: String,
    message: String,
    _id_task: Long
  ) -> T): Query<T>

  fun SelectCommentListById(_id_task: Long): Query<Comment>

  fun InsertTask(
    name: String,
    description: String,
    date_start: String,
    date_end: String,
    is_finished: Long?
  )

  fun UpdateTask(
    name: String,
    description: String,
    date_start: String,
    date_end: String,
    is_finished: Long?,
    id_task: Long
  )

  fun DeleteTask(id_task: Long)

  fun InsertComment(
    image: String,
    message: String,
    _id_task: Long
  )

  fun DeleteAllCommentsById(_id_task: Long)
}
