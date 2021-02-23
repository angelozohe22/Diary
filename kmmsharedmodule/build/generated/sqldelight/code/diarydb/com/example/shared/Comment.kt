package com.example.shared

import kotlin.Long
import kotlin.String

data class Comment(
  val id_comment: Long,
  val image: String,
  val message: String,
  val _id_task: Long
) {
  override fun toString(): String = """
  |Comment [
  |  id_comment: $id_comment
  |  image: $image
  |  message: $message
  |  _id_task: $_id_task
  |]
  """.trimMargin()
}
